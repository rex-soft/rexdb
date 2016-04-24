/**
 * Copyright 2016 the Rex-Soft Group.
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

/**
 * Query executor.
 * 
 * @version 1.0, 2016-01-18
 * @since Rexdb-1.0
 */
public interface QueryExecutor {

	/**
	 * Executes query.
	 */
	ResultSet executeQuery(Statement stmt, String sql) throws SQLException;

	/**
	 * Executes query.
	 */
	ResultSet executeQuery(PreparedStatement ps) throws SQLException;
	
	/**
	 * Executes update.
	 */
	int executeUpdate(PreparedStatement ps) throws SQLException;
	
	/**
	 * Executes update.
	 */
	int executeUpdate(Statement stmt, String sql) throws SQLException;
	
	/**
	 * Executes batch update.
	 */
	int[] executeBatch(Statement statement) throws SQLException;
	
	/**
	 * Executes call.
	 */
	boolean execute(CallableStatement statement) throws SQLException;
}
