package org.rex.db.core.executor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DefaultQueryExecutor implements QueryExecutor {

	public ResultSet executeQuery(Statement stmt, String sql) throws SQLException {
		return stmt.executeQuery(sql);
	}

	public ResultSet executeQuery(PreparedStatement ps) throws SQLException {
		return ps.executeQuery();
	}

	public boolean execute(CallableStatement statement) throws SQLException {
		return statement.execute();
	}
	
	public int[] executeBatch(Statement statement) throws SQLException {
		return statement.executeBatch();
	}

	public int executeUpdate(PreparedStatement ps) throws SQLException {
		return ps.executeUpdate();
	}
	
	public int executeUpdate(Statement stmt, String sql) throws SQLException {
		return stmt.executeUpdate(sql);
	}
}
