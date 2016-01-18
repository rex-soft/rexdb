package org.rex.db.core.statement.prepared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * 创建PreparedStatement并设置参数
 */
public interface PreparedStatementCreator {
	
	public PreparedStatement createPreparedStatement(Connection conn, Ps ps) throws DBException, SQLException;
}
