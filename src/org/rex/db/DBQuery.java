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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.RMap;
import org.rex.db.core.DBOperation;
import org.rex.db.core.reader.ClassResultReader;
import org.rex.db.core.reader.MapResultReader;
import org.rex.db.core.reader.ResultReader;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBException;

/**
 * Database querying operation.
 * 
 * @author z
 * @version 1.0.0, 2016-04-17
 * @since 1.0
 */
public class DBQuery extends DBOperation {

	// ------instances
	private volatile static Map<DataSource, DBQuery> querys = new HashMap<DataSource, DBQuery>();

	public static DBQuery getInstance(DataSource dataSource) throws DBException {
		if (!querys.containsKey(dataSource)) {
			querys.put(dataSource, new DBQuery(dataSource));
		}
		return querys.get(dataSource);
	}

	// -------constructors
	public DBQuery(DataSource dataSource) throws DBException {
		super(dataSource);
	}

	// =========public methods
	// ---------------query one row for java bean
	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that JDBC ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public <T> T get(String sql, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, null, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public <T> T get(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public <T> T get(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public <T> T get(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public <T> T get(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}

	// ---------------query one row for RMap

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return Map that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public RMap<String, ?> getMap(String sql) throws DBException {
		return templateMapQueryForOneRow(sql, null);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public RMap<String, ?> getMap(String sql, Ps parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return Map that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public RMap<String, ?> getMap(String sql, Object[] parameterArray) throws DBException {
		return templateMapQueryForOneRow(sql, parameterArray);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public RMap<String, ?> getMap(String sql, Object parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public RMap<String, ?> getMap(String sql, Map<?, ?> parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	// ---------------query a list of java bean
	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, null, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	// ---------------query a list of RMap
	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql) throws DBException {
		return templateMapQuery(sql, null);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Ps parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Object[] parameterArray) throws DBException {
		return templateMapQuery(sql, parameterArray);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Object parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Map<?, ?> parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}

	// ---------------query a limit list of java bean
	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class, the given SQL is
	 * automatically wrapped to paging SQL for the database.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, null, resultClass, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class, the given SQL is
	 * automatically wrapped to paging SQL for the database.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class, the given SQL is
	 * automatically wrapped to paging SQL for the database.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameterArray, resultClass, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class, the given SQL is
	 * automatically wrapped to paging SQL for the database.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class, the given SQL is
	 * automatically wrapped to paging SQL for the database.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}

	// ---------------query a limit list of RMap

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped to paging SQL for the
	 * database.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, int offset, int rows) throws DBException {
		return templateMapQuery(sql, null, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped to paging SQL for the
	 * database.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Ps parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped to paging SQL for the
	 * database.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameterArray, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped to paging SQL for the
	 * database.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Object parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped to paging SQL for the
	 * database.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public List<RMap> getMapList(String sql, Map<?, ?> parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}

	// =========private methods

	// ---------------tempalte query for java bean
	protected <T> T templateClassQueryForOneRow(String sql, Object parameters, Class<T> resultClass) throws DBException {
		List<T> list = templateClassQuery(sql, parameters, resultClass, null);
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			throw new DBException("DB-00006", list.size());
		return list.get(0);
	}

	protected <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, null);
	}

	protected <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		LimitHandler limitHandler = getDialect().getLimitHandler(offset, rows);
		return templateClassQuery(sql, parameters, resultClass, limitHandler);
	}

	private <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass, LimitHandler limitHandler) throws DBException {
		ResultReader<T> resultReader = new ClassResultReader<T>(resultClass);
		if (parameters == null && limitHandler == null)
			getTemplate().query(sql, resultReader);
		else
			getTemplate().query(sql, parameters, limitHandler, resultReader);
		return resultReader.getResults();
	}

	// ---------------template query for RMap
	protected RMap<String, ?> templateMapQueryForOneRow(String sql, Object parameters) throws DBException {
		List<RMap> list = templateMapQuery(sql, parameters, null);
		if (list.size() == 0)
			return null;
		if (list.size() > 1)
			throw new DBException("DB-00006", list.size());

		return list.get(0);
	}

	protected List<RMap> templateMapQuery(String sql, Object parameters) throws DBException {
		return templateMapQuery(sql, parameters, null);
	}

	protected List<RMap> templateMapQuery(String sql, Object parameters, int offset, int rows) throws DBException {
		LimitHandler limitHandler = getDialect().getLimitHandler(offset, rows);
		return templateMapQuery(sql, parameters, limitHandler);
	}

	private List<RMap> templateMapQuery(String sql, Object parameters, LimitHandler limitHandler) throws DBException {
		MapResultReader resultReader = new MapResultReader();
		if (parameters == null && limitHandler == null)
			getTemplate().query(sql, resultReader);
		else
			getTemplate().query(sql, parameters, limitHandler, resultReader);
		return resultReader.getResults();
	}

}
