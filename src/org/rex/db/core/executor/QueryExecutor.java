package org.rex.db.core.executor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL执行接口
 */
public interface QueryExecutor {

	/**
	 * 执行查询
	 */
	ResultSet executeQuery(Statement stmt, String sql) throws SQLException;

	/**
	 * 执行预编译查询
	 */
	ResultSet executeQuery(PreparedStatement ps) throws SQLException;
	
	/**
	 * 执行调用
	 */
	boolean execute(CallableStatement statement) throws SQLException;

	/**
	 * 执行批处理
	 */
	int[] executeBatch(Statement statement) throws SQLException;
	
	/**
	 * 执行更新
	 */
	int executeUpdate(PreparedStatement ps) throws SQLException;
	
	/**
	 * 执行更新
	 */
	int executeUpdate(Statement stmt, String sql) throws SQLException;
}
