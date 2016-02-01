package org.rex.db.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.transaction.ThreadConnectionHolder;

/**
 * 数据源通用类
 */
public abstract class DataSourceUtil {

	/**
	 * 从数据源中获取连接
	 */
	public static Connection getConnection(DataSource ds) throws DBException {
		if(ds == null)
			throw new DBException("DB-C10003");
		
		ConnectionHolder holder = (ConnectionHolder) ThreadConnectionHolder.get(ds);
		if (holder != null) {
			return holder.getConnection();
		}
		else {
			try {
				return ds.getConnection();
			}
			catch (SQLException e) {
				throw new DBException("DB-C10001", e, e.getMessage());
			}
		}
	}

	/**
	 * 设置事物超时时间
	 */
	public static void applyTransactionTimeout(Statement stmt, DataSource ds) throws DBException {
		ConnectionHolder holder = (ConnectionHolder) ThreadConnectionHolder.get(ds);
		if (holder != null && holder.getDeadline() != null) {
			try {
				stmt.setQueryTimeout(holder.getTimeToLiveInSeconds());
			} catch (SQLException e) {
				throw new DBException("DB-C10014", e);
			}
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public static void closeConnection(Connection con, DataSource ds) throws DBException {
		if (con == null || ThreadConnectionHolder.has(ds)) {
			return;
		}
		try {
			con.close();
		}catch (SQLException e) {
			throw new DBException("DB-C10002", e, ds, con);
		}
	}


	/**
	 * Make a copy and hidden password parameters in the datasource configuration
	 * @param properties datasource configuration
	 */
	public static Properties hiddenPassword(Properties properties){
		Properties clone = null;
		if(properties != null){
			clone = (Properties)properties.clone();
			if(clone.containsKey("password"))
				clone.put("password", "******(hidden)");
		}
		return clone;
	}
}
