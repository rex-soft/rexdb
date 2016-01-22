package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.rex.db.exception.DBException;


public class SimpleConnectionPool {

	private static boolean isJdk5;			//当前JDK版本是否是1.5
	static{
		isJdk5 = System.getProperty("java.version").contains("1.5.");
	}
	
	// -----------config
	private String driverClassName = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/rexdb";
	private String username = "root";
	private String password = "12345678";

	
	private volatile int minPoolSize = 10;			//连接池最小连接数
	private volatile int maxPoolSize = 30;			//连接池最大连接数
	
	private volatile int acquireIncrement = 5;		//每次增长的连接数
	private volatile int acquireRetries = 3;		//获取数据库连接失败后的重试次数
	private volatile long acquireRetryDelay = 750;	//增长连接失败后重试间隔
	
	private volatile long connectionTimeout = 5000;	//连接超时时间（毫秒）
	private volatile long idleTimeout = 600000;		//允许的连接空闲时间，超出时将被关闭
	private volatile long maxLifetime = 1800000;	//允许的连接最长时间，超出时将被关闭

	private boolean testConnectionByJdbc = true;	//使用JDBC接口测试连接（1.6以上版本有效）
	private String connectionTestQuery;				//测试连接有效性SQL
	
	private boolean autoCommit = true;				//连接是否设置为自动提交

	// ---------runtime
	private Timer timer;
	
	private final LinkedTransferQueue<IConnectionProxy> idleConnections;
	private final AtomicInteger totalConnections;
	private final AtomicInteger idleConnectionCount;

	public SimpleConnectionPool() {
		this.totalConnections = new AtomicInteger();
		this.idleConnectionCount = new AtomicInteger();
		this.idleConnections = new LinkedTransferQueue<IConnectionProxy>();
	}

	/**
	 * 初始化连接池
	 * @param properties
	 * @throws DBException
	 */
	public SimpleConnectionPool(Properties properties) throws DBException {
		this();
		extractProperties(properties);

		timer = new Timer("AutoCleanIdleConnections", true);
		if (idleTimeout > 0 || maxLifetime > 0) {
			timer.scheduleAtFixedRate(new PoolTimerTask(idleTimeout, maxLifetime), TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS.toMillis(30));
		}

		initDriverManager();
		fillPool();
	}

	/**
	 * 获取配置
	 */
	private void extractProperties(Properties properties) {
		
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
		try {
			long timeout = this.connectionTimeout;
			final long start = System.currentTimeMillis();
			do {
				if (idleConnectionCount.get() == 0) {
					addConnections();
				}

				IConnectionProxy connectionProxy = idleConnections.poll(timeout, TimeUnit.MILLISECONDS);
				if (connectionProxy == null) {
					throw new SQLException("Timeout of encountered waiting for connection");
				}

				idleConnectionCount.decrementAndGet();

				final long maxLifetime = this.maxLifetime;
				if (maxLifetime > 0 && start - connectionProxy.getCreationTime() > maxLifetime) {
					closeConnection(connectionProxy);
					timeout -= (System.currentTimeMillis() - start);
					continue;
				}

				connectionProxy.unclose();

				Connection connection = (Connection) connectionProxy;
				if (!isConnectionAlive(connection, timeout)) {
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
	public void releaseConnection(IConnectionProxy connectionProxy) {
		if (!connectionProxy.isBrokenConnection()) {
			connectionProxy.markLastAccess();
			idleConnectionCount.incrementAndGet();
			idleConnections.put(connectionProxy);
		} else {
			closeConnection(connectionProxy);
		}
	}
	
	/**
	 * 获取活动连接
	 */
	public int getActiveConnections() {
		return Math.min(this.maxPoolSize, totalConnections.get() - idleConnectionCount.get());
	}

	/**
	 * 获取空闲连接
	 */
	public int getIdleConnections() {
		return idleConnectionCount.get();
	}

	/**
	 * 获取所有连接
	 */
	public int getTotalConnections() {
		return totalConnections.get();
	}

	public int getThreadsAwaitingConnection() {
		return idleConnections.getWaitingConsumerCount();
	}

	public void closeIdleConnections() {
		final int idleCount = idleConnectionCount.get();
		for (int i = 0; i < idleCount; i++) {
			IConnectionProxy connectionProxy = idleConnections.poll();
			if (connectionProxy == null) {
				break;
			}

			idleConnectionCount.decrementAndGet();
			closeConnection(connectionProxy);
		}
	}

	// ----private
	private void fillPool() {
		int maxIters = (this.minPoolSize / this.acquireIncrement) + 1;
		while (totalConnections.get() < this.minPoolSize && maxIters-- > 0) {
			addConnections();
		}
	}

	/**
	 * 向连接池中增加预设的连接数，但不超过最大连接数
	 */
	private synchronized void addConnections() {
		final int max = this.maxPoolSize;
		final int increment = this.acquireIncrement;
		for (int i = 0; totalConnections.get() < max && i < increment; i++) {
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
				IConnectionProxy connection = newConnection();
				boolean alive = isConnectionAlive(connection, this.connectionTimeout);
				if (alive) {
					connection.setAutoCommit(this.autoCommit);
					idleConnectionCount.incrementAndGet();
					totalConnections.incrementAndGet();
					idleConnections.add(connection);
					break;
				} else {
					Thread.sleep(this.acquireRetryDelay);
				}
			} catch (Exception e) {
				if (retries++ > this.acquireRetries) {
					break;
				}

				try {
					Thread.sleep(this.acquireRetryDelay);
				} catch (InterruptedException e1) {
					break;
				}
			}
		}
	}

	/**
	 * 创建一个新的数据库连接
	 */
	private IConnectionProxy newConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		ConnectionProxy proxy = new ConnectionProxy();
		proxy.setParentPool(this);
		return proxy.bind(conn);
	}

	/**
	 * 测试连接是否可用
	 * @param connection 连接
	 * @param timeoutMs	超时时间
	 * @return 是否可用
	 */
	private boolean isConnectionAlive(final Connection connection, long timeoutMs) {
		if (timeoutMs < 500) {
			timeoutMs = 500;
		}

		try {
			//使用jdbc自带接口测试连接有效性
			if (!isJdk5 && this.testConnectionByJdbc) {
				return connection.isValid((int)Math.ceil(timeoutMs / 1000));
			}
			
			//无法执行自定义测试时，不进行测试
			if(connectionTestQuery == null)
				return true;

			//执行查询测试连接
			Statement statement = connection.createStatement();
			try {
				statement.executeQuery(this.connectionTestQuery);
			} finally {
				statement.close();
			}

			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	

	private void closeConnection(IConnectionProxy connectionProxy) {
		try {
			totalConnections.decrementAndGet();
			connectionProxy.__close();
		} catch (SQLException e) {
			return;
		}
	}

	/**
	 * 用于定期清理空闲连接，并在必要时重建连接
	 */
	private class PoolTimerTask extends TimerTask {

		private long idleTimeout;
		private long maxLifetime;

		public PoolTimerTask(long idleTimeout, long maxLifetime) {
			this.idleTimeout = idleTimeout;
			this.maxLifetime = maxLifetime;
		}

		public void run() {
			timer.purge();//移除所有任务

			final long now = System.currentTimeMillis();
			final int idleCount = idleConnectionCount.get();

			for (int i = 0; i < idleCount; i++) {
				IConnectionProxy connectionProxy = idleConnections.poll();
				if (connectionProxy == null) {
					break;
				}

				idleConnectionCount.decrementAndGet();

				if ((idleTimeout > 0 && now > connectionProxy.getLastAccess() + idleTimeout)
						|| (maxLifetime > 0 && now > connectionProxy.getCreationTime() + maxLifetime)) {
					System.out.println("--清理连接："+connectionProxy);
					closeConnection(connectionProxy);
				} else {
					idleConnectionCount.incrementAndGet();
					idleConnections.add(connectionProxy);
				}
			}

			addConnections();
		}
	}
}
