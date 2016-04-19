/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.StringUtil;

/**
 * A Simple Connection pool
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public class SimpleConnectionPool {

	private static final boolean IS_JDK5 = System.getProperty("java.version").contains("1.5."); // 当前JDK版本是否是1.5

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConnectionPool.class);

	// -----------config
	private String driverClassName;
	private String url;
	private String username;
	private String password;

	private int initSize = 1;// initial number of connections
	private int minSize = 3; // minimum number of connections
	private int maxSize = 10; // maximum number of connections
	private int increment = 1; // when there are no idle connections, the number of connections are created at one-time

	private int retries = 2; // retry count after failed to get connection
	private int retryInterval = 750; // retry intervals after failed to get connection

	private int getConnectionTimeout = 5000; // connection timeout (ms)
	private int inactiveTimeout = 600000; //  timeout (ms) for idle connections, idle connections are closed after timeout
	private int maxLifetime = 1800000; // timeout (ms) for connections, connections are closed after timeout

	private boolean testConnection = true; // test connection alive after created ? 
	private String testSql; // SQL for testing connection alive
	private int testTimeout = 500;// timeout (ms) for testing connection alive

	// ---------runtime
	private final Timer timer;

	private final LinkedBlockingQueue<ConnectionProxy> inactiveConnections;
	private final AtomicInteger totalConnectionsCount;
	private final AtomicInteger inactiveConnectionCount;

	private volatile Throwable latestException;


	public SimpleConnectionPool(Properties properties) throws DBException  {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("starting simple connection pool[{0}] of properties {1}.", this.hashCode(), DataSourceUtil.hiddenPassword(properties));
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

		LOGGER.info("simple connection pool[{0}] started{3}.", this.hashCode(), username, url, latestException==null?"":" with errors");
	}

	// -----------property
	/**
	 * Applies Settings
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
				LOGGER.warn("simple connection pool[{0}] dose not support property [{1}: {2}], the property has been ignored.", this.hashCode(), key, value);
			}
		}
	}

	/**
	 * Overrides setting
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
					LOGGER.warn("property [{0}: {1}] for simple connection pool[{2}] is not a number, ignore.", field.getName(), value, this.hashCode());
				}
			} else if ("boolean".equals(fieldType)) {
				field.setBoolean(this, Boolean.parseBoolean(value));
			}
		} catch (Exception e) {
			LOGGER.warn("could not set property [{0}: {1}] for simple connection pool[{2}]: {3}, ignore.", field.getName(), value, this.hashCode(), e.getMessage());
		}
	}

	/**
	 * Validates settings
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
	 * Initializes JDBC Driver
	 */
	public void initDriverManager() throws DBException {
		try {
			Class.forName(this.driverClassName, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new DBException("DB-D0008", e, this.driverClassName);
		}
	}

	/**
	 * Returns connection
	 */
	public Connection getConnection() throws SQLException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getting connection from simple pool[{0}], current idle pool size is {1}/{2}.", this.hashCode(),
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
					throw new SQLException("couldn't get connection from simple pool "+username + '@' + url+", current idle pool size is "+
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
//				if (!isConnectionAlive(connection)) {
//					closeConnection(connectionProxy);
//					timeout -= (System.currentTimeMillis() - start);
//					continue;
//				}
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("connection[{0}] has been obtained.", connection.hashCode());
				}
				return connection;
			} while (timeout > 0);
			
			throw new SQLException("couldn't get connection from simple pool "+username + '@' + url+", current idle pool size is "+
					inactiveConnectionCount.get()+"/"+totalConnectionsCount.get()+"，the latest exception is: "+(latestException == null ? "" : latestException.getMessage()));
		} catch (InterruptedException e) {
			return null;
		}
	}

	/**
	 * Releases connection
	 */
	public void releaseConnection(ConnectionProxy connectionProxy) {
		if (!connectionProxy.isForceClosed()) {
			connectionProxy.markLastAccess();
			inactiveConnectionCount.incrementAndGet();
			try {
				inactiveConnections.put(connectionProxy);
			} catch (InterruptedException e) {
				closeConnection(connectionProxy);
				LOGGER.warn("could not release connection, connection pool[{0}] queue interrupted, {1}. the connection[{2}] has bean closed.", this.hashCode(), 
						e.getMessage(), connectionProxy.hashCode());
			}
		} else {
			closeConnection(connectionProxy);
		}
	}

	/**
	 * Returns active connections
	 */
	public int getActiveConnections() {
		return Math.min(this.maxSize, totalConnectionsCount.get() - inactiveConnectionCount.get());
	}

	/**
	 * Returns idle connections
	 */
	public int getInactiveConnections() {
		return inactiveConnectionCount.get();
	}

	/**
	 * Returns all connections
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
	 * Initializes connection pool
	 */
	private void initConnectionPool() {
		for (int i = 0; i < initSize; i++)
			addConnection();

		if (totalConnectionsCount.get() < initSize) {
			LOGGER.error("init simple connection pool[{0}] for database {1}@{2} failed, the last exception has been recorded.", latestException,
					String.valueOf(this.hashCode()), username, url);
		}
	}

	/**
	 * Increases connections
	 */
	private synchronized void addConnections() {
		for (int i = 0; totalConnectionsCount.get() < maxSize && i < increment; i++) {
			addConnection();
		}
	}

	/**
	 * Increases a connection
	 */
	private void addConnection() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("adding connection to simple pool[{0}].", this.hashCode());
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
						LOGGER.debug("connection [{0}] has been added to simple pool[{1}], current idle pool size is {2}/{3}.",
								this.hashCode(), connection.hashCode(), inactiveConnectionCount.get(), totalConnectionsCount.get());
					}
					break;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("conection is not alive, which will retest in {0} ms, current idle pool size is{1}/{2}.", connection.hashCode(),
								this.retryInterval, inactiveConnectionCount.get(), totalConnectionsCount.get());
					}

					Thread.sleep(this.retryInterval);
				}
			} catch (Exception e) {
				LOGGER.warn("could not get connection from database, {0}, current idle pool size is {1}/{2}.", e.getMessage(),
						inactiveConnectionCount.get(), totalConnectionsCount.get());

				latestException = e;
				if (retries++ >= this.retries - 1) {
					LOGGER.warn("reached maximum number of retries {0}, now stop trying getting this connection, current idle pool size is {1}/{2}.", this.retries,
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
	 * Creates a new connection from database
	 */
	private ConnectionProxy newConnection() throws SQLException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getting connection from database {0}@{1}.", username, url);
		}

		Connection conn = DriverManager.getConnection(url, username, password);
		SimpleConnectionProxy proxy = new SimpleConnectionProxy();
		proxy.setConnectionPool(this);
		return proxy.bind(conn);
	}

	/**
	 * Tests connection alive
	 */
	private boolean isConnectionAlive(Connection connection) {
		if (!this.testConnection)
			return true;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("testing connection[{0}].", connection.hashCode());
		}

		int timeout = (int) Math.ceil(testTimeout / 1000);
		boolean isAlive = false;
		try {
			if (IS_JDK5) {
				if (testSql == null)
					testSql = getTestSqlFromDialect(connection);
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("current JDK version is 1.5, testing connection with sql: {0}.", testSql);
				}

				Statement statement = connection.createStatement();
				statement.setQueryTimeout(timeout);
				try {
					statement.executeQuery(testSql);
				} finally {
					statement.close();
				}
				
				isAlive = true;
			} else {
				// jdk6 or higher
//				isAlive = connection.isValid(timeout);
				isAlive = (Boolean)ReflectUtil.invokeMethod(connection, Connection.class, "isValid", new Class[]{int.class}, new Object[]{timeout});
			}
		} catch (DBException e) {
			LOGGER.warn("could not get dialect, {0}.", e.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("exception occurred while testing the connection, {0}.", e.getMessage());
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("connection[{0}] is {1}.", connection.hashCode(), isAlive ? "alive" : "dead");
		}
		
		return isAlive;
	}

	private String getTestSqlFromDialect(Connection connection) throws DBException {
		Dialect dialect = DialectFactory.resolveDialect(connection);
		return dialect.getTestSql();
	}

	private void closeConnection(ConnectionProxy connectionProxy) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("closing connection[{0}].", connectionProxy.hashCode());
		}

		try {
			totalConnectionsCount.decrementAndGet();
			connectionProxy.closeConnection();
		} catch (SQLException e) {
			LOGGER.warn("could not close connection, {0}.", e.getMessage());
			return;
		}
	}

	/**
	 * Cleans and rebuilds connections
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

			timer.purge();

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
						LOGGER.debug("connection[{0}] has reached max lifetime, which has been closed.", connectionProxy.hashCode());
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
