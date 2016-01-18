package org.rex.db.core.statement.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public interface BatchPreparedStatementCreator {

	public PreparedStatement createBatchPreparedStatement(Connection conn, Ps[] ps) throws DBException, SQLException;
}
