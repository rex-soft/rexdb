package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * 预编译对象创建器
 */
public interface StatementCreator {

	//----------Statement
	public Statement createStatement(Connection conn) throws DBException, SQLException;
	
	//----------PreparedStatement
	public PreparedStatement createPreparedStatement(Connection conn, String sql, Ps ps) throws DBException, SQLException;
	
	//----------CallableStatement
	public CallableStatement createCallableStatement(Connection conn, String sql) throws DBException, SQLException;
	
	public CallableStatement createCallableStatement(Connection conn, String sql, Ps ps) throws DBException, SQLException;
	
	//----------BatchStatement
	public Statement createBatchStatement(Connection conn, String[] sql) throws DBException, SQLException;
	
	//----------BatchPreparedStatement
	public PreparedStatement createBatchPreparedStatement(Connection conn, String sql, Ps[] ps) throws DBException, SQLException;
}
