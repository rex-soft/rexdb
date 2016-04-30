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
package org.rex.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.RMap;
import org.rex.db.core.DBOperation;
import org.rex.db.exception.DBException;

/**
 * Database calling operation for storage procedures and functions.
 * 
 * @author z
 * @version 1.0, 2016-04-17
 * @since Rexdb-1.0
 */
public class DBCall extends DBOperation {

	// ------instances
	private volatile static Map<DataSource, DBCall> calls = new HashMap<DataSource, DBCall>();

	public static DBCall getInstance(DataSource dataSource) throws DBException {
		if (!calls.containsKey(dataSource)) {
			calls.put(dataSource, new DBCall(dataSource));
		}
		return calls.get(dataSource);
	}

	// -------constructors
	public DBCall(DataSource dataSource) throws DBException {
		super(dataSource);
	}

	// -------public methods
	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SQL.
	 * 
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the given SQL, etc.
	 */
	public RMap<String, ?> call(String sql) throws DBException {
		return templateCall(sql, null);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters.
	 * 
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the given SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Object[] parameterArray) throws DBException {
		return templateCall(sql, parameterArray);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * 
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the given SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Ps parameters) throws DBException {
		return templateCall(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters a Map that contains prepared parameters.
	 * 
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the given SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Map<?, ?> parameters) throws DBException {
		return templateCall(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * 
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the given SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Object parameters) throws DBException {
		return templateCall(sql, parameters);
	}

	// -------private methods
	private RMap<String, ?> templateCall(String sql, Object parameters) throws DBException {
		return getTemplate().call(sql, parameters);
	}
}
