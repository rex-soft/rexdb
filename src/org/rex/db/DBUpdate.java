package org.rex.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.core.DBOperation;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

/**
 * Database updating operation, such as INSERT, UPDATE, DELETE, etc.
 * @author z
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
	 * Executes the given SQL, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing, such as an SQL DDL
	 * statement.
	 * 
	 * @param sql a SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int update(String sql) throws DBException {
		return templateUpdate(sql, null);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing, such as an SQL DDL
	 * statement.
	 * 
	 * @param sql a SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL uses '?'
	 *            representing prepared parameters, and sets parameters
	 *            according to the parameter array elements order
	 * @param parameterArray parameter <tt>array</tt> contains prepared
	 *            parameters, such as a String array<br/>
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int update(String sql, Object[] parameterArray) throws DBException {
		return templateUpdate(sql, parameterArray);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing, such as an SQL DDL
	 * statement.
	 * 
	 * @param sql a SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL uses '?'
	 *            representing prepared parameters, and sets parameters
	 *            according to the parameter ps built-in order
	 * @param ps a <tt>Ps</tt> object contains prepared parameters
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int update(String sql, Ps ps) throws DBException {
		return templateUpdate(sql, ps);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing, such as an SQL DDL
	 * statement.
	 * 
	 * @param sql a SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL uses '#{
	 *            <i>parameter name</i>}' representing prepared parameters(
	 *            <i>parameter name</i> is a Map's entry key, and the entry
	 *            value is the corresponding prepared parameter value). if the
	 *            Map doesn't contains key <i>parameter name</i>, the prepared
	 *            parameter value will be set to <tt>null</tt>
	 * @param parameterMap a <tt>Map</tt> contains prepared parameters
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int update(String sql, Map<?, ?> parameterMap) throws DBException {
		return templateUpdate(sql, parameterMap);
	}

	/**
	 * Executes the given SQL, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing, such as an SQL DDL
	 * statement.
	 * 
	 * @param sql a SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL uses '#{
	 *            <i>parameter name</i>}' representing prepared parameters(
	 *            <i>parameter name</i> is a java bean property, which has a
	 *            readable method), if the java bean doesn't have property
	 *            <i>parameter name</i>, or the property couldn't be read, the
	 *            prepared parameter value will be set to <tt>null</tt>
	 * @param parameterBean a java bean contains prepared parameters
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int update(String sql, Object parameterBean) throws DBException {
		return templateUpdate(sql, parameterBean);
	}

	// -----------batch update
	/**
	 * Executes a batch of SQLs to the database. if all commands execute
	 * successfully, returns an array of affected row counts. The int elements
	 * of the array that is returned are ordered to correspond to the commands
	 * in the batch, which are ordered according to the order in which they were
	 * added to the batch. The elements in the array returned by the method
	 * executeBatch may be one of the following:
	 * <ol>
	 * <li>A number greater than or equal to zero -- indicates that the command
	 * was processed successfully and is an update count giving the number of
	 * rows in the database that were affected by the command's execution
	 * <li/>
	 * <li>A value of SUCCESS_NO_INFO -- indicates that the command was
	 * processed successfully but that the number of rows affected is unknown
	 * </li>
	 * <li>A value of EXECUTE_FAILED -- indicates that the command failed to
	 * execute successfully and occurs only if a driver continues to process
	 * commands after a command fails</li>
	 * </ol>
	 * 
	 * @param sql SQLs to be Execute
	 * @return an array of update counts containing one element for each command
	 *         in the batch.
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	public int[] batchUpdate(String[] sql) throws DBException {
		return templateBatchUpdate(sql);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public int[] batchUpdate(String sql, Object[][] parameterArrays) throws DBException {
		return templateBatchUpdate(sql, (Object[]) parameterArrays);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public int[] batchUpdate(String sql, Ps[] parameters) throws DBException {
		return templateBatchUpdate(sql, parameters);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public int[] batchUpdate(String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return templateBatchUpdate(sql, parameterMaps);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public int[] batchUpdate(String sql, Object[] parameterBeans) throws DBException {
		return templateBatchUpdate(sql, parameterBeans);
	}
	
	//---list parameter
	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public int[] batchUpdate(String sql, List<?> parameterList) throws DBException {
		if(parameterList == null)
			templateBatchUpdate(sql, null);
		
		Class<?> clazz = validateListElementsType(parameterList);
		if(clazz == Ps.class)
			return templateBatchUpdate(sql, parameterList.toArray(new Ps[parameterList.size()]));
		else if(clazz.isInstance(Map.class))
			return templateBatchUpdate(sql, parameterList.toArray(new Map[parameterList.size()]));
		else if(clazz.isArray())
			return templateBatchUpdate(sql, parameterList.toArray(new Object[parameterList.size()][]));
		else
			return templateBatchUpdate(sql, parameterList.toArray(new Object[parameterList.size()]));
	}
	
	private Class<?> validateListElementsType(List<?> parameterList){
		Class<?> clazz = null;
		for (int i = 0; i < parameterList.size(); i++) {
			Object el = parameterList.get(i);
			if(el == null) continue;
			if(clazz == null)
				clazz = el.getClass();
			else{
				if(el.getClass() != clazz)
					throw new DBRuntimeException("DB-00004", clazz.getClass().getName(), el.getClass().getName());
			}
		}
		return clazz;
	}

	// ----------------------private methods
	/**
	 * Executes the SQL statement using <tt>DBTemplate</tt>. the given parameter
	 * 'parameters' could be <tt>null</tt>, <tt>Ps</tt>, <tt>Array</tt> ,
	 * <tt>Map</tt>, or a java bean. <br/>
	 * (1) if the given parameter 'parameters' is <tt>null</tt>, SQL will be
	 * executed in no-prepared <tt>Statement</tt> object, otherwise in
	 * <tt>PreparedStatement</tt> object.<br/>
	 * (2) if the given parameter 'parameters' is a <tt>Ps</tt> object, SQL uses
	 * '?' representing prepared parameters, and sets parameters according to
	 * the Ps built-in order.<br/>
	 * (3) if the given parameter 'parameters' is an <tt>Array</tt>, such as a
	 * String array, SQL uses '?' representing prepared parameters, and sets
	 * parameters according to the array elements order.<br/>
	 * (4) if the given parameter 'parameters' is a <tt>Map</tt>, SQL uses '#{
	 * <i>parameter name</i>}' representing prepared parameters('parameter name'
	 * is a Map's entry key, and the entry value is the corresponding prepared
	 * parameter value). if the Map doesn't contains key 'parameter name', the
	 * prepared parameter value will be set to <tt>null</tt>.<br/>
	 * (5) if the given parameter 'parameters' is a java bean, SQL uses '#{
	 * <i>parameter name</i>}' representing prepared parameters('parameter name'
	 * is a java bean property, which has a readable method), if java bean
	 * doesn't have property 'parameter name', or the property couldn't be read,
	 * the prepared parameter value will be set to <tt>null</tt>.<br/>
	 * 
	 * @param sql an SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL that may contain one
	 *            or more '?', or '#{ <i>parameter name</i>}' IN parameter
	 *            placeholders
	 * @param parameters the prepared parameters
	 * @return either (1) the affected row count for SQL Data Manipulation
	 *         Language (DML) statements or (2) 0 for SQL statements that return
	 *         nothing
	 * @throws DBException either rexdb configuration wasn't loaded, or database
	 *             access error occurs, or couldn't execute SQL, etc. the
	 *             specific reason will be included in the exception message.
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
	 * @return an array of update counts containing one element for each command
	 *         in the batch.
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	private int[] templateBatchUpdate(String[] sql) throws DBException {
		return getTemplate().batchUpdate(sql);
	}

	/**
	 * Executes the given SQL with a set of parameters to the database.
	 * 
	 * @param sql an SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement. the SQL that may contain one
	 *            or more '?', or '#{ <i>parameter name</i>}' IN parameter
	 *            placeholders
	 * @param parametersArray parameters to update. the given parameter could be
	 *            a set of <tt>Ps</tt>, <tt>Array</tt> , <tt>Map</tt>, or a java
	 *            bean. <br/>
	 * @return an array of update counts containing one element for each command
	 *         in the batch.
	 * @throws DBException if rexdb configuration wasn't loaded, database access
	 *             error occurs, couldn't execute SQL, etc. the specific reason
	 *             will be included in the exception message.
	 */
	private int[] templateBatchUpdate(String sql, Object[] parametersArray) throws DBException {
		if (parametersArray == null || parametersArray.length == 0) {
			return new int[] { templateUpdate(sql, null) };
		}
		return getTemplate().batchUpdate(sql, parametersArray);
	}

}
