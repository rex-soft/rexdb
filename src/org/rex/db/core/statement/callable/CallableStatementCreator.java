package org.rex.db.core.statement.callable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public interface CallableStatementCreator {

	public CallableStatement createCallableStatement(Connection conn) throws DBException, SQLException;
	
	public CallableStatement createCallableStatement(Connection conn, Ps ps) throws DBException, SQLException;
}
