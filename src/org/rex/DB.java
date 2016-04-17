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
package org.rex;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.DBCall;
import org.rex.db.DBQuery;
import org.rex.db.DBTransaction;
import org.rex.db.DBUpdate;
import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.datasource.DataSourceManager;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;
import org.rex.db.transaction.DefaultDefinition;

/**
 * Database operations.
 * 
 * @author z
 * @version 1.0.0, 2016-04-17
 * @since 1.0
 */
public class DB {

	// --------------------------------------------- delegates
	// -------------update
	/**
	 * Gets <tt>DBUpdate</tt> instance for the default dataSource.
	 * 
	 * @return DBUpdate instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBUpdate instance.
	 */
	private static DBUpdate getDBUpdate() throws DBException {
		return DBUpdate.getInstance(getDefaultDataSource());
	}

	/**
	 * Gets <tt>DBUpdate</tt> instance by the specified dataSource id.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @return DBUpdate instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBUpdate instance.
	 */
	private static DBUpdate getDBUpdate(String dataSourceId) throws DBException {
		return DBUpdate.getInstance(getDataSource(dataSourceId));
	}

	// -------------query
	/**
	 * Gets <tt>DBQuery</tt> instance for the default dataSource.
	 * 
	 * @return DBQuery instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBQuery instance.
	 */
	private static DBQuery getDBQuery() throws DBException {
		return DBQuery.getInstance(getDefaultDataSource());
	}

	/**
	 * Gets <tt>DBQuery</tt> instance by the specified dataSource id.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @return DBQuery instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBQuery instance.
	 */
	private static DBQuery getDBQuery(String dataSourceId) throws DBException {
		return DBQuery.getInstance(getDataSource(dataSourceId));
	}

	// -------------call
	/**
	 * Gets <tt>DBCall</tt> instance for the default dataSource.
	 * 
	 * @return DBCall instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBCall instance.
	 */
	private static DBCall getDBCall() throws DBException {
		return DBCall.getInstance(getDefaultDataSource());
	}

	/**
	 * Gets <tt>DBCall</tt> instance by the specified dataSource id.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @return DBCall instance.
	 * @throws DBException couldn't find dataSource, couldn't initialize DBCall instance.
	 */
	private static DBCall getDBCall(String dataSourceId) throws DBException {
		return DBCall.getInstance(getDataSource(dataSourceId));
	}

	// --------------------------------------------- data sources and
	private static DataSourceManager getDataSourceManager() throws DBException {
		return Configuration.getCurrentConfiguration().getDataSourceManager();
	}

	public static DataSource getDefaultDataSource() throws DBException {
		DataSource defaultDataSource = getDataSourceManager().getDefault();
		if (defaultDataSource == null)
			throw new DBException("DB-00002");
		return defaultDataSource;
	}

	public static DataSource getDataSource(String dataSourceId) throws DBException {
		DataSource dataSource = getDataSourceManager().get(dataSourceId);
		if (dataSource == null)
			throw new DBException("DB-00003", dataSourceId);
		return dataSource;
	}

	/**
	 * Gets Connection from the default dataSource.
	 * 
	 * @return an idle Connection from the default dataSource.
	 */
	public static Connection getConnection() throws DBException {
		try {
			return getDefaultDataSource().getConnection();
		} catch (SQLException e) {
			throw new DBException("DB-00004", e);
		}
	}

	/**
	 * Gets Connection from specified dataSource.
	 * 
	 * @return an idle Connection from specified dataSource.
	 */
	public static Connection getConnection(String dataSourceId) throws DBException {
		try {
			return getDataSource(dataSourceId).getConnection();
		} catch (SQLException e) {
			throw new DBException("DB-00004", e);
		}
	}

	// --------------------------------------------- dialects
	private static DialectManager getDialectManager() throws DBException {
		return Configuration.getCurrentConfiguration().getDialectManager();
	}

	/**
	 * Gets Dialect instance for the default dataSource.
	 * 
	 * @return dialect for the default dataSource.
	 */
	public static Dialect getDialect() throws DBException {
		return getDialectManager().getDialect(getDefaultDataSource());
	}

	/**
	 * Gets Dialect instance for the specified dataSource.
	 * 
	 * @param dataSource the specified dataSource.
	 * @return dialect for the specified dataSource.
	 */
	public static Dialect getDialect(DataSource dataSource) throws DBException {
		return getDialectManager().getDialect(dataSource);
	}

	/**
	 * Gets Dialect instance for the specified dataSource.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @return dialect for the specified dataSource.
	 */
	public static Dialect getDialect(String dataSourceId) throws DBException {
		return getDialectManager().getDialect(getDataSource(dataSourceId));
	}

	// --------------------------------------------- QUERY
	// ------------specified dataSource
	// ---------------query one row for java bean
	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that JDBC ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String dataSourceId, String sql, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String dataSourceId, String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String dataSourceId, String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	// ---------------query one row for RMap

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameterArray);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	// ---------------query a list of java bean
	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	// ---------------query a list of RMap
	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameterArray);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	// ---------------query a limit list of java bean
	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class,
	 * the given SQL is automatically wrapped to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, resultClass, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class,
	 * the given SQL is automatically wrapped to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class,
	 * the given SQL is automatically wrapped to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameterArray, resultClass, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class,
	 * the given SQL is automatically wrapped to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object parameters, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of object that instanced from the given class,
	 * the given SQL is automatically wrapped to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	// ---------------query a limit list of RMap

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped
	 * to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped
	 * to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Ps parameters, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped
	 * to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameterArray, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped
	 * to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object parameters, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters, offset, rows);
	}

	/**
	 * Executes SQL with the specified dataSource and maps JDBC ResultSet to a List of Map, the given SQL is automatically wrapped
	 * to paging SQL for the database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param offset specified how many rows to skip.
	 * @param rows limits the number of rows returned by the query.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Map<?, ?> parameters, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters, offset, rows);
	}

	// ------------default dataSource
	// ---------------query one row for java bean
	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that JDBC ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String sql, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to an object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that the ResultSet should be mapped to.
	 * @return object that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static <T> T get(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, parameters, resultClass);
	}

	// ---------------query one row for RMap

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String sql) throws DBException {
		return getDBQuery().getMap(sql);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String sql, Ps parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String sql, Object[] parameterArray) throws DBException {
		return getDBQuery().getMap(sql, parameterArray);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String sql, Object parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return Map that mapped from a row.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, the ResultSet contains
	 *             more than one row, etc.
	 */
	public static RMap<String, ?> getMap(String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	// ---------------query a list of java bean
	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameterArray, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of object that instanced from the given class.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @param resultClass a class that each row of the ResultSet should be mapped to.
	 * @return a list of object that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	// ---------------query a list of RMap
	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SELECT SQL.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql) throws DBException {
		return getDBQuery().getMapList(sql);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Ps parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Object[] parameterArray) throws DBException {
		return getDBQuery().getMapList(sql, parameterArray);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Object parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
	}

	/**
	 * Executes SQL and maps JDBC ResultSet to a List of Map.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return a list of Map that mapped from JDBC ResultSet.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, resultClass, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameterArray, resultClass, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Ps parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameterArray, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Object parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
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
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static List<RMap> getMapList(String sql, Map<?, ?> parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
	}

	// --------------------------------------------- UPDATE
	// ------------specified dataSource

	/**
	 * Executes the given SQL to the specified database, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing,
	 * such as an SQL DDL.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, such as INSERT, UPDATE, DELETE or DDL.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String dataSourceId, String sql) throws DBException {
		return getDBUpdate(dataSourceId).update(sql);
	}

	/**
	 * Executes the given SQL to the specified database, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing,
	 * such as an SQL DDL.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameterArray);
	}

	/**
	 * Executes the given SQL to the specified database, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing,
	 * such as an SQL DDL.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameters);
	}

	/**
	 * Executes the given SQL to the specified database, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing,
	 * such as an SQL DDL.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameters);
	}

	/**
	 * Executes the given SQL to the specified database, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing,
	 * such as an SQL DDL.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameters);
	}

	/**
	 * Executes a batch of SQLs to the specified database. If all commands execute successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql SQLs to be sent to the database.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQLs, etc.
	 */
	public static int[] batchUpdate(String dataSourceId, String[] sqls) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sqls);
	}

	/**
	 * Executes sql with a batch of parameters to the specified database. if the sql execute successfully, returns an array of
	 * affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a batch of Ps.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Ps[] parameters) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameters);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the specified database. If the SQL executes successfully, returns an
	 * array of affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArrays a batch of parameter arrays.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[][] parameterArrays) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterArrays);
	}

	/**
	 * Executes sql with a batch of parameters to the specified database. if the sql execute successfully, returns an array of
	 * affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterMaps a batch of Map.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterMaps);
	}

	/**
	 * Executes sql with a batch of parameters to the specified database. if the sql execute successfully, returns an array of
	 * affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 *
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterBeans a batch of Java bean.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[] parameterBeans) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterBeans);
	}

	// ------------default dataSource
	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL to be sent to the database, such as INSERT, UPDATE, DELETE or DDL.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String sql) throws DBException {
		return getDBUpdate().update(sql);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String sql, Object[] parameterArray) throws DBException {
		return getDBUpdate().update(sql, parameterArray);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String sql, Ps parameters) throws DBException {
		return getDBUpdate().update(sql, parameters);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String sql, Map<?, ?> parameters) throws DBException {
		return getDBUpdate().update(sql, parameters);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int update(String sql, Object parameters) throws DBException {
		return getDBUpdate().update(sql, parameters);
	}

	/**
	 * Executes a batch of SQLs to the database. If all commands execute successfully, returns an array of affected row counts, or
	 * value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql SQLs to be sent to the database.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQLs, etc.
	 */
	public static int[] batchUpdate(String[] sql) throws DBException {
		return getDBUpdate().batchUpdate(sql);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of
	 * affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArrays a batch of parameter arrays.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String sql, Object[][] parameterArrays) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterArrays);
	}

	/**
	 * Executes sql with a batch of parameters to the database. if the sql execute successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a batch of Ps.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String sql, Ps[] parameters) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameters);
	}

	/**
	 * Executes sql with a batch of parameters to the database. if the sql execute successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterMaps a batch of Map
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterMaps);
	}

	/**
	 * Executes sql with a batch of parameters to the database. if the sql execute successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterBeans a batch of Java bean
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static int[] batchUpdate(String sql, Object[] parameterBeans) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterBeans);
	}

	// --------------------------------------------- 存储过程
	// ------------使用指定数据源
	/**
	 * Executes a stored procedure or a function to the specified database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL to be sent to the database, typically a static SQL.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql) throws DBException {
		return getDBCall(dataSourceId).call(sql);
	}

	/**
	 * Executes a stored procedure or a function to the specified database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameterArray);
	}

	/**
	 * Executes a stored procedure or a function to the specified database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function to the specified database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters a Map that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function to the specified database.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	// ------------使用默认数据源
	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL to be sent to the database, typically a static SQL.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String sql) throws DBException {
		return getDBCall().call(sql);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String sql, Object[] parameterArray) throws DBException {
		return getDBCall().call(sql, parameterArray);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '?' parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String sql, Ps parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters a Map that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String sql, Map<?, ?> parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	/**
	 * Executes a stored procedure or a function.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return a Map that may contain OUT, INOUT parameters and return results.
	 * @throws DBException if configuration wasn't loaded, could not access database, couldn't execute SQL, etc.
	 */
	public static RMap<String, ?> call(String sql, Object parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	// --------------------------------------------- TRANSACTION
	private static void beginTransaction(DataSource dataSource, DefaultDefinition definition) throws DBException {
		DBTransaction.begin(dataSource, definition);
	}

	private static void commit(DataSource dataSource) throws DBException {
		DBTransaction.commit(dataSource);
	}

	private static void rollback(DataSource dataSource) throws DBException {
		DBTransaction.rollback(dataSource);
	}

	private static Connection getTransactionConnection(DataSource dataSource) throws DBException {
		return DBTransaction.getTransactionConnection(dataSource);
	}

	// ---------------------specified dataSource
	/**
	 * Begins a new transaction for the specified dataSource. The framework will get a new connection from the specified
	 * dataSource, set auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same thread
	 * are using this connection until committing or rollback.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public static void beginTransaction(String dataSourceId) throws DBException {
		beginTransaction(getDataSource(dataSourceId), null);
	}

	/**
	 * Begins a new transaction with definition for the specified dataSource. The framework will get a new connection from the
	 * specified dataSource, set auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same
	 * thread are using this connection until committing or rollback.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param definition transaction definition.
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public static void beginTransaction(String dataSourceId, DefaultDefinition definition) throws DBException {
		beginTransaction(getDataSource(dataSourceId), definition);
	}

	/**
	 * Commits transaction for the specified dataSource. The framework will get connection from ThreadLocal and commit it.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void commit(String dataSourceId) throws DBException {
		commit(getDataSource(dataSourceId));
	}

	/**
	 * Rollback transaction for the specified dataSource. The framework will get connection from ThreadLocal and rollback it.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void rollback(String dataSourceId) throws DBException {
		rollback(getDataSource(dataSourceId));
	}

	// ---------------------default dataSource
	/**
	 * Begins a new transaction for the default dataSource. The framework will get a new connection from the specified dataSource,
	 * set auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same thread are using this
	 * connection until committing or rollback.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public static void beginTransaction() throws DBException {
		beginTransaction(getDefaultDataSource(), null);
	}

	/**
	 * Begins a new transaction with definition for the default dataSource. The framework will get a new connection from the
	 * specified dataSource, set auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same
	 * thread are using this connection until committing or rollback.
	 * 
	 * @param dataSourceId dataSource id that configured in the configuration XML.
	 * @param definition transaction definition.
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public static void beginTransaction(DefaultDefinition definition) throws DBException {
		beginTransaction(getDefaultDataSource(), definition);
	}

	/**
	 * Commits transaction for the dataSource.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void commit() throws DBException {
		commit(getDefaultDataSource());
	}

	/**
	 * Rollback transaction for the dataSource.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void rollback() throws DBException {
		rollback(getDefaultDataSource());
	}

	// ---------------------jta
	/**
	 * Begins a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not begin transaction etc.
	 */
	public static void beginJtaTransaction() throws DBException {
		DBTransaction.beginJta();
	}

	/**
	 * Commits a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not commit transaction etc.
	 */
	public static void commitJta() throws DBException {
		DBTransaction.commitJta();
	}

	/**
	 * Rollback a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not commit transaction etc.
	 */
	public static void rollbackJta() throws DBException {
		DBTransaction.rollbackJta();
	}

	// --------------------transaction connection
	/**
	 * Gets connection from ThreadLocal for the default dataSource.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static Connection getTransactionConnection() throws DBException {
		return getTransactionConnection(getDefaultDataSource());
	}

	/**
	 * Gets connection from ThreadLocal for the default dataSource.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static Connection getTransactionConnection(String dataSourceId) throws DBException {
		return DBTransaction.getTransactionConnection(getDataSource(dataSourceId));
	}
}
