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
package org.rex.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * JDBC utilities.
 * 
 * @version 1.0, 2016-02-01
 * @since Rexdb-1.0
 */
public class JdbcUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

	/**
	 * Retrieves warnings reported by calls on the Connection, Statement and ResultSet object.
	 */
	public static void checkWarnings(Connection connection, Statement statement, ResultSet resultSet){
		if(connection != null) checkConnectionWarning(connection);
		if(statement != null) checkStatementWarning(statement);
		if(resultSet != null) checkResultSetWarning(resultSet);
	}
	
	/**
	 * Retrieves the first warning reported by calls on this Connection object.
	 */
	public static void checkConnectionWarning(Connection connection) {
		try {
			SQLWarning warning = connection.getWarnings();
			if (warning != null) 
				LOGGER.warn("connection Warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("could not get connection warning, {0}.", e, e.getMessage());
		}
	}
	
	/**
	 * Retrieves the first warning of this Statement object.
	 */
	public static void checkStatementWarning(Statement statement) {
		try {
			SQLWarning warning = statement.getWarnings();
			if (warning != null) 
				LOGGER.warn("statement warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("could not get statement warning, {0}.", e, e.getMessage());
		}
	}
	
	/**
	 * Retrieves the first warning of this ResultSet object.
	 */
	public static void checkResultSetWarning(ResultSet resultSet) {
		try {
			SQLWarning warning = resultSet.getWarnings();
			if (warning != null) 
				LOGGER.warn("result set warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("could not get result set warning, {0}.", e, e.getMessage());
		}
	}
}
