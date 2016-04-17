/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
