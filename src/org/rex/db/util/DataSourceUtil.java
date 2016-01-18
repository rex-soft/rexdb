package org.rex.db.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.transaction.ThreadConnectionHolder;

/**
 * 数据源通用类
 */
public abstract class DataSourceUtil {

	private static final Log logger = LogFactory.getLog(DataSourceUtil.class);

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
	 * 设置查询超时时间
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
//		if (!(ds instanceof SmartDataSource) || ((SmartDataSource) ds).shouldClose(con)) {
		try {
			con.close();
		}catch (SQLException e) {
			throw new DBException("DB-C10002", e, ds, con);
		}
//		}
	}


}
