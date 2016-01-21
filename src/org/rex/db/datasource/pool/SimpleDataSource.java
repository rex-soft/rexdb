package org.rex.db.datasource.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SimpleDataSource implements DataSource {

	private int loginTimeout;

	private SimplePool pool;

	public SimpleDataSource(PoolConfig configuration) {
		pool = new SimplePool(configuration);
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return pool.getConnection();
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeout = seconds;
	}

	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return (this.getClass().isAssignableFrom(iface));
	}
}
