package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBException;

/**
 * 对象创建器
 */
public interface StatementCreator {

	//----------Statement
	public Statement createStatement(Connection connection) throws DBException, SQLException;
	
	//----------PreparedStatement
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException;
	
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters, LimitHandler limitHandler) throws DBException, SQLException;
	
	//----------CallableStatement
	public CallableStatement createCallableStatement(Connection connection, String sql) throws DBException, SQLException;
	
	public CallableStatement createCallableStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException;
	
	//----------BatchStatement
	public Statement createBatchStatement(Connection connection, String[] sql) throws DBException, SQLException;
	
	//----------BatchPreparedStatement
	public PreparedStatement createBatchPreparedStatement(Connection connection, String sql, Object[] parametersArray) throws DBException, SQLException;
}
