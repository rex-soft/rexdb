package org.rex.db.core.statement.batch;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.exception.DBException;

public interface BatchStatementCreator {

	public Statement createBatchStatement(Connection conn, String[] sql) throws DBException, SQLException;
}
