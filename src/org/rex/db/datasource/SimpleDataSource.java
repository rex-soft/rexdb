package org.rex.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.datasource.pool.SimpleConnectionPool;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

/**
 * 一个简易数据源，用于程序测试
 */
public class SimpleDataSource implements DataSource {

	private final SimpleConnectionPool pool;
	
	public SimpleDataSource(Properties properties) throws DBException {
		pool = new SimpleConnectionPool(properties);
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		throw new DBRuntimeException("DB-C10066", "getConnection");
	}

	public int getLoginTimeout() throws SQLException {
		throw new DBRuntimeException("DB-C10066", "getLoginTimeout");
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new DBRuntimeException("DB-C10066", "setLoginTimeout");
	}

	public PrintWriter getLogWriter() {
		throw new DBRuntimeException("DB-C10066", "getLogWriter");
	}

	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new DBRuntimeException("DB-C10066", "setLogWriter");
	}

}
