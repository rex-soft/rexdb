package org.rex.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.rex.db.datasource.pool.SimpleConnectionPool;

/**
 * 一个简易数据源，用于程序测试
 */
public class SimpleDataSource implements DataSource {

	private SimpleConnectionPool pool;
	
	public SimpleDataSource(Properties properties) throws SQLException {
		pool = new SimpleConnectionPool(properties);
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return pool.getConnection();
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new UnsupportedOperationException("setLoginTimeout");
	}

	public PrintWriter getLogWriter() {
		throw new UnsupportedOperationException("getLogWriter");
	}

	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new UnsupportedOperationException("setLogWriter");
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
