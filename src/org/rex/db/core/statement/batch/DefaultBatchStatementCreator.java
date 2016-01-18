package org.rex.db.core.statement.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.exception.DBException;

/**
 * 用于执行批处理
 */
public class DefaultBatchStatementCreator implements BatchStatementCreator {

	public Statement createBatchStatement(Connection conn, String[] sql) throws DBException, SQLException {
		
		Statement stmt = conn.createStatement();
		for (int i = 0; i < sql.length; i++) {
			stmt.addBatch(sql[i]);
		}
		return stmt;
	}
	
//	public String toString() {
//		StringBuffer sbuf = new StringBuffer()
//			.append("type = BatchStatement");
//		return sbuf.toString();
//	}
}
