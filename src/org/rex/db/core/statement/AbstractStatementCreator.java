package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.exception.DBException;

/**
 * Base statement creator
 */
public abstract class AbstractStatementCreator implements StatementCreator{

	//----------Statement
	public Statement createStatement(Connection conn) throws DBException, SQLException{
		return conn.createStatement();
	}
	
	//----------Callable Statement
	public CallableStatement createCallableStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareCall(sql);
	}
	
	//----------Batch Statement
	public Statement createBatchStatement(Connection conn, String[] sql) throws SQLException {
		
		Statement stmt = conn.createStatement();
		for (int i = 0; i < sql.length; i++) {
			stmt.addBatch(sql[i]);
		}
		return stmt;
	}

}
