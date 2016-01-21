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

import com.zaxxer.hikari.javassist.ClassFile;
import com.zaxxer.hikari.javassist.CtClass;
import com.zaxxer.hikari.javassist.CtConstructor;

public class SimplePool {

	// -----------config
	private String driverClassName = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/rexdb";
	private String username = "root";
	private String password = "12345678";

	private volatile int acquireIncrement;
	private volatile int acquireRetries;
	private volatile long acquireRetryDelay;
	private volatile long connectionTimeout;
	private volatile long idleTimeout;
	private volatile long maxLifetime;
	private volatile int minPoolSize;
	private volatile int maxPoolSize;

	private String connectionTestQuery;
	private final boolean jdbc4ConnectionTest;
	private boolean autoCommit;

	// ---------runtime
	private final LinkedTransferQueue<ConnectionProxy> idleConnections;
	private final AtomicInteger totalConnections;
	private final AtomicInteger idleConnectionCount;

	private Timer timer;

	public SimplePool() {
		acquireIncrement = 5;
		acquireRetries = 3;
		acquireRetryDelay = 750;
		connectionTimeout = 5000;
		idleTimeout = TimeUnit.MINUTES.toMillis(10);
		autoCommit = true;
		jdbc4ConnectionTest = true;
		minPoolSize = 30;
		maxPoolSize = 10;
		maxLifetime = TimeUnit.MINUTES.toMillis(30);

		this.totalConnections = new AtomicInteger();
		this.idleConnectionCount = new AtomicInteger();
		this.idleConnections = new LinkedTransferQueue<ConnectionProxy>();
	}

	public SimplePool(Properties properties) throws DBException {
		this();
		extractProperties(properties);

		timer = new Timer("SimplePoolTimer", true);
		if (idleTimeout > 0 || maxLifetime > 0) {
			timer.scheduleAtFixedRate(new PoolTimerTask(idleTimeout, maxLifetime), TimeUnit.SECONDS.toMillis(30), TimeUnit.SECONDS.toMillis(30));
		}

		initDriverManager();
		fillPool();
	}

	public static void main(String[] args) throws DBException, SQLException {
		SimplePool pool = new SimplePool(null);
		Connection conn = pool.getConnection();
		System.out.println(conn);
	}

	/**
	 * 获取配置
	 */
	private void extractProperties(Properties properties) {

	}

	public void initDriverManager() throws DBException {
		try {
			Class.forName(this.driverClassName, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException ex) {
			throw new DBException("DB-C10015", ex, this.driverClassName);
		}
	}

	public Connection getConnection() throws SQLException {

		try {
			long timeout = this.connectionTimeout;
			final long start = System.currentTimeMillis();
			do {
				if (idleConnectionCount.get() == 0) {
					addConnections();
				}

				ConnectionProxy connectionProxy = idleConnections.poll(timeout, TimeUnit.MILLISECONDS);
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

	public void releaseConnection(ConnectionProxy connectionProxy) {
		if (!connectionProxy.isBrokenConnection()) {
			connectionProxy.markLastAccess();
			idleConnectionCount.incrementAndGet();
			idleConnections.put(connectionProxy);
		} else {
			closeConnection(connectionProxy);
		}
	}

	public int getActiveConnections() {
		return Math.min(this.maxPoolSize, totalConnections.get() - idleConnectionCount.get());
	}

	public int getIdleConnections() {
		return idleConnectionCount.get();
	}

	public int getTotalConnections() {
		return totalConnections.get();
	}

	public int getThreadsAwaitingConnection() {
		return idleConnections.getWaitingConsumerCount();
	}

	public void closeIdleConnections() {
		final int idleCount = idleConnectionCount.get();
		for (int i = 0; i < idleCount; i++) {
			ConnectionProxy connectionProxy = idleConnections.poll();
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

	private synchronized void addConnections() {
		final int max = this.maxPoolSize;
		final int increment = this.acquireIncrement;
		for (int i = 0; totalConnections.get() < max && i < increment; i++) {
			addConnection();
		}
	}

	private void addConnection() {
		int retries = 0;
		while (true) {
			try {
				Connection connection = newConnection();
				System.out.println("-----------------");
				boolean alive = isConnectionAlive(connection, this.connectionTimeout);
				if (alive) {
					connection.setAutoCommit(this.autoCommit);
					idleConnectionCount.incrementAndGet();
					totalConnections.incrementAndGet();
					
					ConnectionProxy proxyConnection = new ConnectionProxy(connection);
					proxyConnection.setParentPool(this);
					idleConnections.add(proxyConnection);
					break;
				} else {
					Thread.sleep(this.acquireRetryDelay);
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	private Connection newConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	private byte[] transformConnection(ClassFile classFile) throws Exception {
		String className = classFile.getName();
		CtClass target = classPool.getCtClass(className);

		CtClass intf = classPool.get("com.zaxxer.hikari.proxy.IHikariConnectionProxy");
		target.addInterface(intf);
		LOGGER.debug("Added interface {} to {}", intf.getName(), className);

		CtClass proxy = classPool.get("com.zaxxer.hikari.proxy.ConnectionProxy");

		copyFields(proxy, target);
		copyMethods(proxy, target, classFile);
		mergeClassInitializers(proxy, target, classFile);
		specialConnectionInjectCloseCheck(target);
		injectTryCatch(target);

		for (CtConstructor constructor : target.getConstructors()) {
			constructor.insertAfter("__init();");
		}

		return target.toBytecode();
	}

	private boolean isConnectionAlive(final Connection connection, long timeoutMs) {

		System.out.println("==========isConnectionAlive");
		
		if (timeoutMs < 500) {
			timeoutMs = 500;
		}

		try {
			if (jdbc4ConnectionTest) {
				return connection.isValid((int) timeoutMs * 1000);
			}

			Statement statement = connection.createStatement();
			try {
				statement.executeQuery(this.connectionTestQuery);
			} finally {
				statement.close();
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void closeConnection(ConnectionProxy connectionProxy) {
		try {
			totalConnections.decrementAndGet();
			connectionProxy.__close();
		} catch (SQLException e) {
			return;
		}
	}

	private class PoolTimerTask extends TimerTask {

		private long idleTimeout;
		private long maxLifetime;

		public PoolTimerTask(long idleTimeout, long maxLifetime) {
			this.idleTimeout = idleTimeout;
			this.maxLifetime = maxLifetime;
		}

		public void run() {
			timer.purge();

			final long now = System.currentTimeMillis();
			final long idleTimeout = this.idleTimeout;
			final long maxLifetime = this.maxLifetime;
			final int idleCount = idleConnectionCount.get();

			for (int i = 0; i < idleCount; i++) {
				ConnectionProxy connectionProxy = idleConnections.poll();
				if (connectionProxy == null) {
					break;
				}

				idleConnectionCount.decrementAndGet();

				if ((idleTimeout > 0 && now > connectionProxy.getLastAccess() + idleTimeout)
						|| (maxLifetime > 0 && now > connectionProxy.getCreationTime() + maxLifetime)) {
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
