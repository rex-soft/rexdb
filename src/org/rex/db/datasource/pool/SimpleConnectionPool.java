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
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.StringUtil;

/**
 * 简易的连接池
 */
public class SimpleConnectionPool {

	private static final boolean IS_JDK5 = System.getProperty("java.version").contains("1.5."); // 当前JDK版本是否是1.5

	// -----------config
	private String driverClassName;
	private String url;
	private String username;
	private String password;

	private int initSize = 1;//初始化时创建的连接数
	private volatile int minSize = 3; // 连接池保持的最小连接数
	private volatile int maxSize = 10; // 连接池最大连接数
	private volatile int increment = 3; // 每次增长的连接数

	private volatile int retries = 3; // 获取数据库连接失败后的重试次数
	private volatile int retryInterval = 750; // 增长连接失败后重试间隔

	private volatile int connectionTimeout = 5000; // 连接超时时间（毫秒）
	private volatile int inactiveTimeout = 600000; // 允许的连接空闲时间，超出时将被关闭
	private volatile int maxLifetime = 1800000; // 允许的连接最长时间，超出时将被重置

	private boolean testConnection = true; // 使用JDBC接口测试连接（1.6以上版本有效）
	private String testSql; // 测试连接有效性SQL
	private int testTimeout = 500;// 测试连接有效性的超时时间

	// ---------runtime
	private Timer timer;

	private LinkedTransferQueue<ConnectionProxy> inactiveConnections;
	private AtomicInteger totalConnectionsCount;
	private AtomicInteger inactiveConnectionCount;
	
//	private Throwable lastException;

	public void init() {
		this.totalConnectionsCount = new AtomicInteger();
		this.inactiveConnectionCount = new AtomicInteger();
		this.inactiveConnections = new LinkedTransferQueue<ConnectionProxy>();
	}

	/**
	 * 初始化连接池
	 * 
	 * @param properties
	 * @throws DBException
	 */
	public SimpleConnectionPool(Properties properties) throws DBException {
		init();
		extractProperties(properties);
		validateConfig();

		timer = new Timer("AutoCleanInactiveConnections", true);
		if (inactiveTimeout > 0 || maxLifetime > 0) {
			timer.scheduleAtFixedRate(new PoolTimerTask(inactiveTimeout, maxLifetime), TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS.toMillis(30));
		}

		initDriverManager();
		initConnectionPool();
	}

	/**
	 * 获取配置
	 * @throws DBException 
	 */
	private void extractProperties(Properties properties) throws DBException {
		if(properties == null)
			throw new DBException("参数不能为空");
		Field[] fields = this.getClass().getDeclaredFields();
		for (Enumeration<?> en = properties.propertyNames(); en.hasMoreElements();) {
			String key = (String)en.nextElement();
			
			for (int i = 0; i < fields.length; i++) {
				if(fields[i].getName().equals(key)){
					try{
						overrideProperty(fields[i], properties.getProperty(key));
					}catch(Exception e){
						//log.warn("参数赋值出错");
					}
					continue;
				}
				//
				//log.warn("不支持参数");
			}
		}
	}
	
	/**
	 * 覆盖默认值
	 * @param field
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void overrideProperty(Field field, String value){
		try {
			String fieldType = field.getType().getName();
			if("java.lang.String".equals(fieldType)){
				field.set(this, value);
			}else if("int".equals(fieldType)){
				try{
					field.setInt(this, Integer.parseInt(value));
				}catch(NumberFormatException e){
					throw new DBRuntimeException(field.getName()+"的值不是数字");
				}
			}else if("boolean".equals(fieldType)){
				field.setBoolean(this, Boolean.parseBoolean(value));
			}
		} catch (IllegalArgumentException e) {
			throw new DBRuntimeException(field.getName()+"赋值出错");
		} catch (IllegalAccessException e) {
			throw new DBRuntimeException(field.getName()+"赋值出错");
		}
	}

	/**
	 * 检查各项参数是否正确
	 * @throws DBException 
	 */
	private void validateConfig() throws DBException{
		if(StringUtil.isEmptyString(driverClassName) || StringUtil.isEmptyString(url) || StringUtil.isEmptyString(username))
			throw new DBException("连接参数不能为空");
	}
	
	/**
	 * 初始化驱动管理
	 */
	public void initDriverManager() throws DBException {
		try {
			Class.forName(this.driverClassName, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException ex) {
			throw new DBException("DB-C10015", ex, this.driverClassName);
		}
	}

	/**
	 * 获取连接
	 */
	public Connection getConnection() throws SQLException {
		System.out.println("connection pool inactive: "+inactiveConnectionCount.get() + ", total: "+totalConnectionsCount.get());
		try {
			int timeout = this.connectionTimeout;
			long start = System.currentTimeMillis();
			do {
				if (inactiveConnectionCount.get() == 0) {
					addConnections();
				}

				ConnectionProxy connectionProxy = inactiveConnections.poll(timeout, TimeUnit.MILLISECONDS);
				if (connectionProxy == null) {
					throw new SQLException("Timeout of encountered waiting for connection");
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

			throw new SQLException("Timeout of encountered waiting for connection");
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
			inactiveConnections.put(connectionProxy);
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

	public int getThreadsAwaitingConnection() {
		return inactiveConnections.getWaitingConsumerCount();
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
		for(int i = 0; i < initSize; i++)
			addConnection();
		
		if(totalConnectionsCount.get() <  minSize){
			//throw new DBRuntimeException("初始化连接池失败", lastException);
		}
	}

	/**
	 * 向连接池中增加预设的连接数，但不超过最大连接数
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
		int retries = 0;
		while (true) {
			try {
				ConnectionProxy connection = newConnection();
				boolean alive = isConnectionAlive(connection);
				if (alive) {
					inactiveConnectionCount.incrementAndGet();
					totalConnectionsCount.incrementAndGet();
					inactiveConnections.add(connection);
					break;
				} else {
					Thread.sleep(this.retryInterval);
				}
			} catch (Exception e) {
//				e.printStackTrace();
//				lastException = e;
				
				if (retries++ >= this.retries - 1) {
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
		int timeout = (int) Math.ceil(testTimeout / 1000);
		try {
			if (IS_JDK5) {
				if (testSql == null)
					testSql = getTestSqlFromDialect(connection);

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
			return false;
		} catch (SQLException e) {
			return false;
		}
	}

	private String getTestSqlFromDialect(Connection connection) throws DBException {
		Dialect dialect = DialectManager.getDialect(connection);
		return dialect.getTestSql();
	}

	private void closeConnection(ConnectionProxy connectionProxy) {
		try {
			totalConnectionsCount.decrementAndGet();
			connectionProxy.closeConnection();
		} catch (SQLException e) {
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
		}

		public void run() {
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
				} else {
					inactiveConnectionCount.incrementAndGet();
					inactiveConnections.add(connectionProxy);
				}
			}

			if(totalConnectionsCount.get() < minSize)
				addConnections();
		}
	}
}
