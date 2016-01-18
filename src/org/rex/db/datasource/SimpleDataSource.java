package org.rex.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rex.db.exception.DBException;

/**
 * 创建一个简易数据源，只有单个连接，仅用于程序测试
 */
public class SimpleDataSource implements DataSource {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private String driverClassName = "";

	private String url = "";

	private String username = "";

	private String password = "";

	public SimpleDataSource() {
	}

	public SimpleDataSource(String driverClassName, String url, String username, String password)
	    throws DBException {
		setDriverClassName(driverClassName);
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}

	public void setDriverClassName(String driverClassName) throws DBException {
		this.driverClassName = driverClassName;
		try {
			Class.forName(this.driverClassName, true, Thread.currentThread().getContextClassLoader());
			logger.debug("Loaded JDBC driver: " + this.driverClassName);
		}
		catch (ClassNotFoundException ex) {
			throw new DBException("DB-C10015", ex, this.driverClassName);
		}
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Connection getConnection() throws SQLException {
		return getConnectionFromDriverManager();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return getConnectionFromDriverManager(this.url, username, password);
	}
	
	protected Connection getConnectionFromDriverManager() throws SQLException {
		return getConnectionFromDriverManager(this.url, this.username, this.password);
	}

	protected Connection getConnectionFromDriverManager(String url, String username, String password) throws SQLException {
		logger.debug("Creating new JDBC connection to [" + url + "]");
		return DriverManager.getConnection(url, username, password);
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
