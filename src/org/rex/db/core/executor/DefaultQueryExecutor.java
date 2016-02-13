package org.rex.db.core.executor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;


public class DefaultQueryExecutor implements QueryExecutor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQueryExecutor.class);

	public ResultSet executeQuery(Statement stmt, String sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing sql {0} of statement[{1}].", sql, stmt.hashCode());
		
		return stmt.executeQuery(sql);
	}

	public ResultSet executeQuery(PreparedStatement ps) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing query of statement[{0}].", ps.hashCode());
		
		return ps.executeQuery();
	}

	public boolean execute(CallableStatement statement) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing statement[{0}].", statement.hashCode());
		
		return statement.execute();
	}
	
	public int[] executeBatch(Statement statement) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing batch of statement[{0}].", statement.hashCode());
		
		return statement.executeBatch();
	}

	public int executeUpdate(PreparedStatement ps) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing update of statement[{0}].", ps.hashCode());
		
		return ps.executeUpdate();
	}
	
	public int executeUpdate(Statement stmt, String sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("executing sql {0} of statement[{1}].", sql, stmt.hashCode());
		
		return stmt.executeUpdate(sql);
	}
}
