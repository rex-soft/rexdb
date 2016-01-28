package org.rex.db.datasource.pool;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectFactory;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.StringUtil;

/**
 * 简单的连接池，提供基本的数据库连接管理、获取、超时处理等功能
 */
public class SimpleConnectionPool {

	private static final boolean IS_JDK5 = System.getProperty("java.version").contains("1.5."); // 当前JDK版本是否是1.5

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConnectionPool.class);

	// -----------config
	private String driverClassName;
	private String url;
	private String username;
	private String password;

	private int initSize = 1;// 初始化时创建的连接数
	private int minSize = 3; // 连接池保持的最小连接数
	private int maxSize = 10; // 连接池最大连接数
	private int increment = 3; // 每次增长的连接数

	private int retries = 3; // 获取数据库连接失败后的重试次数
	private int retryInterval = 750; // 增长连接失败后重试间隔

	private int getConnectionTimeout = 5000; // 连接超时时间（毫秒）
	private int inactiveTimeout = 600000; // 允许的连接空闲时间，超出时将被关闭
	private int maxLifetime = 1800000; // 允许的连接最长时间，超出时将被重置

	private boolean testConnection = true; // 使用JDBC接口测试连接（1.6以上版本有效）
	private String testSql; // 测试连接有效性SQL
	private int testTimeout = 500;// 测试连接有效性的超时时间

	// ---------runtime
	private final Timer timer;

	private final LinkedBlockingQueue<ConnectionProxy> inactiveConnections;
	private final AtomicInteger totalConnectionsCount;
	private final AtomicInteger inactiveConnectionCount;

	private volatile Throwable latestException;

	/**
	 * 初始化连接池
	 * 
	 * @param properties
	 * @throws DBException
	 */
	public SimpleConnectionPool(Properties properties) throws DBException  {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("starting simple connection pool with properties {0}", DataSourceUtil.hiddenPassword(properties));
		}

		extractProperties(properties);
		validateConfig();

		totalConnectionsCount = new AtomicInteger();
		inactiveConnectionCount = new AtomicInteger();
		inactiveConnections = new LinkedBlockingQueue<ConnectionProxy>();
		timer = new Timer("AutoCleanInactiveConnections", true);

		if (inactiveTimeout > 0 || maxLifetime > 0) {
			timer.scheduleAtFixedRate(new PoolTimerTask(inactiveTimeout, maxLifetime), TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS.toMillis(30));
		}

		initDriverManager();
		initConnectionPool();

		LOGGER.info("simple connection pool for database {0}@{1} has been started {2}.", username, url, latestException==null?"":"with errors");
	}

	// -----------property
	/**
	 * 获取配置
	 * 
	 * @throws DBException
	 */
	private void extractProperties(Properties properties) throws DBException {
		if (properties == null)
			throw new DBException("DB-D0006");
		
		Field[] fields = this.getClass().getDeclaredFields();
		for (Enumeration<?> en = properties.propertyNames(); en.hasMoreElements();) {
			Object key =  en.nextElement();
			Object value = properties.get(key);
			boolean hasField = false;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals(key)) {
					overrideProperty(fields[i], String.valueOf(value));
					hasField = true;
					continue;
				}
			}
			if (!hasField) {
				LOGGER.warn("simple connection pool property [{0}: {1}] not support, ignore.", key, value);
			}
		}
	}

	/**
	 * 覆盖默认值
	 * 
	 * @param field
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void overrideProperty(Field field, String value) {
		try {
			String fieldType = field.getType().getName();
			if ("java.lang.String".equals(fieldType)) {
				field.set(this, value);
			} else if ("int".equals(fieldType)) {
				try {
					field.setInt(this, Integer.parseInt(value));
				} catch (NumberFormatException e) {
					LOGGER.warn("value of simple connection pool property [{0}: {1}] is not a number, ignore.", field.getName(), value);
				}
			} else if ("boolean".equals(fieldType)) {
				field.setBoolean(this, Boolean.parseBoolean(value));
			}
		} catch (Exception e) {
			LOGGER.warn("couldn't set simple connection pool property [{0}: {1}]: {2}, ignore.", field.getName(), value, e.getMessage());
		}
	}

	/**
	 * 检查各项参数是否正确
	 */
	private void validateConfig() throws DBException {
		// --not null
		throwExceptionIfNull("driverClassName", driverClassName);
		throwExceptionIfNull("url", url);
		throwExceptionIfNull("username", username);

		// ignore others
	}

	private void throwExceptionIfNull(String key, String value) throws DBException {
		if (StringUtil.isEmptyString(url))
			throw new DBException("DB-D0007", key);
	}

	// -------------pool
	/**
	 * 初始化驱动管理
	 */
	public void initDriverManager() throws DBException {
		try {
			Class.forName(this.driverClassName, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new DBException("DB-D0008", e, this.driverClassName);
		}
	}

	/**
	 * 获取连接
	 */
	public Connection getConnection() throws SQLException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("get connection from simple connection pool [{0}], current idle pool size: {1}/{2} ", this.hashCode(),
					inactiveConnectionCount.get(), totalConnectionsCount.get());
		}

		try {
			int timeout = this.getConnectionTimeout;
			long start = System.currentTimeMillis();
			do {
				if (inactiveConnectionCount.get() == 0) {
					addConnections();
				}

				ConnectionProxy connectionProxy = inactiveConnections.poll(timeout, TimeUnit.MILLISECONDS);
				if (connectionProxy == null) {
					throw new SQLException("couldn't get connection from simple pool "+username + '@' + url+", current idle pool size: "+
							inactiveConnectionCount.get()+"/"+totalConnectionsCount.get()+"，the latest exception is: "+(latestException == null ? "" : latestException.getMessage()));
				}

				inactiveConnectionCount.decrementAndGet();

				int maxLifetime = this.maxLifetime;
				if (maxLifetime > 0 && start - connectionProxy.getCreationTime() > maxLifetime) {
					closeConnection(connectionProxy);
					timeout -= (System.currentTimeMillis() - start);
					continue;
				}

				connectionProxy.unclose();

				Connection connection = (Connection) connectionProxy;
				if (!isConnectionAlive(connection)) {
					closeConnection(connectionProxy);
					timeout -= (System.currentTimeMillis() - start);
					continue;
				}

				return connection;
			} while (timeout > 0);
			
			throw new SQLException("couldn't get connection from simple pool "+username + '@' + url+", current idle pool size: "+
					inactiveConnectionCount.get()+"/"+totalConnectionsCount.get()+"，the latest exception is: "+(latestException == null ? "" : latestException.getMessage()));
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * 释放连接
	 */
	public void releaseConnection(ConnectionProxy connectionProxy) {
		if (!connectionProxy.isForceClosed()) {
			connectionProxy.markLastAccess();
			inactiveConnectionCount.incrementAndGet();
			try {
				inactiveConnections.put(connectionProxy);
			} catch (InterruptedException e) {
				closeConnection(connectionProxy);
				LOGGER.warn("release connection failed, connection pool queue interrupted: {0}. connection {1} has already closed.", e.getMessage(),
						connectionProxy.hashCode());
			}
		} else {
			closeConnection(connectionProxy);
		}
	}

	/**
	 * 获取活动连接
	 */
	public int getActiveConnections() {
		return Math.min(this.maxSize, totalConnectionsCount.get() - inactiveConnectionCount.get());
	}

	/**
	 * 获取空闲连接
	 */
	public int getInactiveConnections() {
		return inactiveConnectionCount.get();
	}

	/**
	 * 获取所有连接
	 */
	public int gettotalConnectionsCount() {
		return totalConnectionsCount.get();
	}

	public void closeInactiveConnections() {
		for (int i = 0; i < inactiveConnectionCount.get(); i++) {
			ConnectionProxy connectionProxy = inactiveConnections.poll();
			if (connectionProxy == null) {
				break;
			}

			inactiveConnectionCount.decrementAndGet();
			closeConnection(connectionProxy);
		}
	}

	// ----private
	/**
	 * 初始化连接池
	 */
	private void initConnectionPool() {
		for (int i = 0; i < initSize; i++)
			addConnection();

		if (totalConnectionsCount.get() < initSize) {
			LOGGER.error("init simple connection pool for database {0}@{1} failed, the last exception is: ", latestException, username, url);
			// throw new DBRuntimeException("初始化连接池失败", lastException);
		}
	}

	/**
	 * 向连接池中增加预设的连接数，但不超过最大连接数
	 * XXX:一次性增加若干个连接，会不会影响获取连接的性能？
	 */
	private synchronized void addConnections() {
		for (int i = 0; totalConnectionsCount.get() < maxSize && i < increment; i++) {
			addConnection();
		}
	}

	/**
	 * 向连接池中增加1个连接
	 */
	private void addConnection() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("adding connection for simple pool {0}.", this.hashCode());
		}

		int retries = 0;
		while (true) {
			try {
				ConnectionProxy connection = newConnection();
				boolean alive = isConnectionAlive(connection);
				if (alive) {
					inactiveConnectionCount.incrementAndGet();
					totalConnectionsCount.incrementAndGet();
					inactiveConnections.add(connection);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("getting connection [{0}] from database has been added to idle queue. current idle pool size: {1}/{2} ",
								connection.hashCode(), inactiveConnectionCount.get(), totalConnectionsCount.get());
					}
					break;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("conection is not alive, will retest in {0} ms. current idle pool size: {1}/{2} ", connection.hashCode(),
								this.retryInterval, inactiveConnectionCount.get(), totalConnectionsCount.get());
					}

					Thread.sleep(this.retryInterval);
				}
			} catch (Exception e) {
				LOGGER.warn("getting connection from database failed: {0}, current idle pool size: {1}/{2} ", e.getMessage(),
						inactiveConnectionCount.get(), totalConnectionsCount.get());

				latestException = e;
				if (retries++ >= this.retries - 1) {
					LOGGER.warn("reached maximum number of retries: {0}, stop trying to get this connection. current idle pool size: {1}/{2} ", this.retries,
							inactiveConnectionCount.get(), totalConnectionsCount.get());
					break;
				}

				try {
					Thread.sleep(this.retryInterval);
				} catch (InterruptedException e1) {
					break;
				}
			}
		}
	}

	/**
	 * 创建一个新的数据库连接
	 */
	private ConnectionProxy newConnection() throws SQLException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getting connection from database {0}@{1}", username, url);
		}

		Connection conn = DriverManager.getConnection(url, username, password);
		SimpleConnectionProxy proxy = new SimpleConnectionProxy();
		proxy.setConnectionPool(this);
		return proxy.bind(conn);
	}

	/**
	 * 测试连接是否可用
	 * 
	 * @param connection 连接
	 * @param timeoutMs 超时时间
	 * @return 是否可用
	 */
	private boolean isConnectionAlive(Connection connection) {
		if (!this.testConnection)
			return true;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("testing connection[{0}]", connection.hashCode());
		}

		int timeout = (int) Math.ceil(testTimeout / 1000);
		try {
			if (IS_JDK5) {
				if (testSql == null)
					testSql = getTestSqlFromDialect(connection);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("detect the JDK version is 1.5, testing connection with sql: {0}", testSql);
				}

				// 执行查询测试连接
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(timeout);
				try {
					statement.executeQuery(testSql);
				} finally {
					statement.close();
				}
				return true;
			} else {
				return connection.isValid(timeout);// jdk6及以上版本，使用jdbc自带接口测试连接有效性
			}
		} catch (DBException e) {
			LOGGER.warn("exception occurred while getting dialect: {0}", e.getMessage());
			return false;
		} catch (SQLException e) {
			LOGGER.warn("exception occurred while testing connection: {0}", e.getMessage());
			return false;
		}
	}

	private String getTestSqlFromDialect(Connection connection) throws DBException {
		Dialect dialect = DialectFactory.resolveDialect(connection);
		return dialect.getTestSql();
	}

	private void closeConnection(ConnectionProxy connectionProxy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("closing connection[{0}] from database", connectionProxy.hashCode());
		}

		try {
			totalConnectionsCount.decrementAndGet();
			connectionProxy.closeConnection();
		} catch (SQLException e) {
			LOGGER.warn("exception occurred while closing connection: {0}", e.getMessage());
			return;
		}
	}

	/**
	 * 用于定期清理空闲连接，并在必要时重建连接
	 */
	private class PoolTimerTask extends TimerTask {

		private int inactiveTimeout;
		private int maxLifetime;

		public PoolTimerTask(int inactiveTimeout, int maxLifetime) {
			this.inactiveTimeout = inactiveTimeout;
			this.maxLifetime = maxLifetime;

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("timer task for {0}@{1} enabled.", username, url);
			}
		}

		public void run() {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("timer task started.");
			}

			timer.purge();// 移除所有任务

			long now = System.currentTimeMillis();
			int inactiveCount = inactiveConnectionCount.get();

			for (int i = 0; i < inactiveCount; i++) {
				ConnectionProxy connectionProxy = inactiveConnections.poll();
				if (connectionProxy == null) {
					break;
				}

				inactiveConnectionCount.decrementAndGet();

				if ((inactiveTimeout > 0 && now > connectionProxy.getLastAccess() + inactiveTimeout)
						|| (maxLifetime > 0 && now > connectionProxy.getCreationTime() + maxLifetime)) {
					closeConnection(connectionProxy);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("connection[{0}] reached max lifetime, closed.", connectionProxy.hashCode());
					}
				} else {
					inactiveConnectionCount.incrementAndGet();
					inactiveConnections.add(connectionProxy);
				}
			}

			if (totalConnectionsCount.get() < minSize) {
				addConnections();
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("timer task ended.");
			}
		}
	}
}
