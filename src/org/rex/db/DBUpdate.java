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

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.configuration.Configuration;
import org.rex.db.core.DBOperation;
import org.rex.db.core.DBTemplate;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.transaction.DefaultDefinition;

/**
 * Database updating operation, such as INSERT, UPDATE, DELETE, etc.
 * 
 * @author z
 * @version 1.0, 2016-04-17
 * @since Rexdb-1.0
 */
public class DBUpdate extends DBOperation {

	// ------instances
	private volatile static Map<DataSource, DBUpdate> updates = new HashMap<DataSource, DBUpdate>();

	public static DBUpdate getInstance(DataSource dataSource) throws DBException {
		if (!updates.containsKey(dataSource)) {
			updates.put(dataSource, new DBUpdate(dataSource));
		}
		return updates.get(dataSource);
	}

	// -------constructors
	public DBUpdate(DataSource dataSource) throws DBException {
		super(dataSource);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL to be sent to the database, such as INSERT, UPDATE, DELETE or DDL.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int update(String sql) throws DBException {
		return templateUpdate(sql, null);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArray an object array that contains prepared parameters in order.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * 
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int update(String sql, Object[] parameterArray) throws DBException {
		return templateUpdate(sql, parameterArray);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a Ps object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * 
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int update(String sql, Ps parameters) throws DBException {
		return templateUpdate(sql, parameters);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters an object that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * 
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int update(String sql, Object parameters) throws DBException {
		return templateUpdate(sql, parameters);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, DELETE or an SQL that returns nothing, such as an SQL DDL.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameters a map that contains prepared parameters.
	 * @return either (1) the affected row count or (2) 0 for SQL statements that return nothing.
	 * 
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int update(String sql, Map<?, ?> parameters) throws DBException {
		return templateUpdate(sql, parameters);
	}

	// -----------batch update
	/**
	 * Executes a batch of SQLs to the database. If all commands execute successfully, returns an array of affected row counts, or
	 * value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql SQLs to be sent to the database.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if configuration wasn't loaded, could not access the database, couldn't execute SQLs, etc.
	 */
	public int[] batchUpdate(String[] sql) throws DBException {
		return templateBatchUpdate(sql);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of
	 * affected row counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameterArrays a batch of parameter arrays.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int[] batchUpdate(String sql, Object[][] parameterArrays) throws DBException {
		return templateBatchUpdate(sql, (Object[]) parameterArrays);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders.
	 * @param parameters a batch of Ps.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int[] batchUpdate(String sql, Ps[] parameters) throws DBException {
		return templateBatchUpdate(sql, parameters);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterMaps a batch of Map
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int[] batchUpdate(String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return templateBatchUpdate(sql, parameterMaps);
	}

	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders.
	 * @param parameterBeans a batch of Java bean
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int[] batchUpdate(String sql, Object[] parameterBeans) throws DBException {
		return templateBatchUpdate(sql, parameterBeans);
	}

	// ---list parameter
	/**
	 * Executes the given SQL with a batch of parameters to the database. If the SQL executes successfully, returns an array of affected row
	 * counts, or value of SUCCESS_NO_INFO, or value of EXECUTE_FAILED.
	 * 
	 * @param sql an SQL that may contain one or more '?' or '#{...}' IN parameter placeholders.
	 * @param parameterList the parameter list.
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if the configuration wasn't loaded, could not access the database, couldn't execute the SQL, etc.
	 */
	public int[] batchUpdate(String sql, List<?> parameterList) throws DBException {
		if (parameterList == null)
			templateBatchUpdate(sql, null);

		Class<?> clazz = validateListElementsType(parameterList);
		if (clazz == Ps.class)
			return templateBatchUpdate(sql, parameterList.toArray(new Ps[parameterList.size()]));
		else if (clazz.isInstance(Map.class))
			return templateBatchUpdate(sql, parameterList.toArray(new Map[parameterList.size()]));
		else if (clazz.isArray())
			return templateBatchUpdate(sql, parameterList.toArray(new Object[parameterList.size()][]));
		else
			return templateBatchUpdate(sql, parameterList.toArray(new Object[parameterList.size()]));
	}

	private Class<?> validateListElementsType(List<?> parameterList) {
		Class<?> clazz = null;
		for (int i = 0; i < parameterList.size(); i++) {
			Object el = parameterList.get(i);
			if (el == null)
				continue;
			if (clazz == null)
				clazz = el.getClass();
			else {
				if (el.getClass() != clazz)
					throw new DBRuntimeException("DB-00005", clazz.getClass().getName(), el.getClass().getName());
			}
		}
		return clazz;
	}

	// ----------------------private methods
	/**
	 * Executes the SQL statement using <tt>DBTemplate</tt>. the given parameter 'parameters' could be <tt>null</tt>, <tt>Ps</tt>,
	 * <tt>Array</tt> , <tt>Map</tt>, or java bean. <br/>
	 * (1) if the given parameter 'parameters' is <tt>null</tt>, the SQL will be executed in no-prepared <tt>Statement</tt>,
	 * otherwise in <tt>PreparedStatement</tt> object.<br/>
	 * (2) if the given parameter 'parameters' is a <tt>Ps</tt> object, the SQL uses '?' representing prepared parameters, and sets
	 * parameters in order.<br/>
	 * (3) if the given parameter 'parameters' is an <tt>Array</tt>, such as a String array, the SQL uses '?' representing prepared
	 * parameters, and sets parameters in order.<br/>
	 * (4) if the given parameter 'parameters' is a <tt>Map</tt>, the SQL uses '#{ <i>parameter name</i>}' representing prepared
	 * parameters('parameter name' is Map's entry key, and the entry value is the corresponding prepared parameter value). if
	 * the Map contains nothing for 'parameter name', the prepared parameter value will be set to <tt>null</tt>.<br/>
	 * (5) if the given parameter 'parameters' is a java bean, the SQL uses '#{ <i>parameter name</i>}' representing prepared
	 * parameters('parameter name' is java bean's property, which has a readable method), if the java bean has no property
	 * 'parameter name', or the property couldn't be read, the prepared parameter value will be set to <tt>null</tt>.<br/>
	 * 
	 * @param sql an SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL statement that
	 *            returns nothing, such as a DDL statement. The SQL that may contain one or more '?', or '#{ <i>parameter name</i>
	 *            }' IN parameter placeholders
	 * @param parameters the prepared parameters
	 * @return either (1) the affected row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements
	 *         that return nothing
	 * @throws DBException either rexdb configuration wasn't loaded, or database error occurs, or couldn't execute SQL, etc.
	 */
	private int templateUpdate(String sql, Object parameters) throws DBException {
		if (parameters == null)
			return getTemplate().update(sql);
		else
			return getTemplate().update(sql, parameters);
	}

	/**
	 * Executes a batch of SQLs to the database.
	 * 
	 * @param sql SQLs to be Execute
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if rexdb configuration wasn't loaded, database access error occurs, couldn't execute SQL, etc. 
	 */
	private int[] templateBatchUpdate(String[] sql) throws DBException {
		return getTemplate().batchUpdate(sql);
	}

	/**
	 * Executes the given SQL with a set of parameters to the database.
	 * 
	 * @param sql an SQL Data Manipulation Language (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL statement that
	 *            returns nothing, such as a DDL statement. the SQL that may contain one or more '?', or '#{ <i>parameter name</i>
	 *            }' IN parameter placeholders
	 * @param parametersArray parameters to update. the given parameter could be a set of <tt>Ps</tt>, <tt>Array</tt> ,
	 *            <tt>Map</tt>, or a java bean. <br/>
	 * @return an array of update counts containing one element for each command in the batch.
	 * @throws DBException if rexdb configuration wasn't loaded, database access error occurs, couldn't execute SQL, etc.
	 */
	private int[] templateBatchUpdate(String sql, Object[] parametersArray) throws DBException {
		if (parametersArray == null || parametersArray.length == 0) {
			return new int[] { templateUpdate(sql, null) };
		}

		boolean autoTransaction = Configuration.getCurrentConfiguration().isBatchTransaction();

		int[] ri;
		DBTemplate template = getTemplate();
		DataSource dataSource = template.getDataSource();
		Connection connection = DBTransaction.getTransactionConnection(dataSource);
		if (autoTransaction && connection == null) {
			try {
				DBTransaction.begin(dataSource, new DefaultDefinition());
				ri = getTemplate().batchUpdate(sql, parametersArray);
				DBTransaction.commit(dataSource);
			} catch (DBException e) {
				DBTransaction.rollback(dataSource);
				throw e;
			}
		} else
			ri = getTemplate().batchUpdate(sql, parametersArray);

		return ri;
	}

}
