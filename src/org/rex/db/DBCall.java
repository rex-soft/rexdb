package org.rex.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.RMap;
import org.rex.db.core.DBOperation;
import org.rex.db.exception.DBException;

/**
 * Database call operation for stored procedures and functions.
 * @author z
 */
public class DBCall extends DBOperation{
	
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
	 * execute a stored procedure or a function.
	 * @param sql an SQL to be sent to the database, typically a static SQL
	 * 
	 * @return results contains return results
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, etc.
	 */
	public RMap<String, ?> call(String sql) throws DBException{
		return templateCall(sql, null);
	}
	
	/**
	 * execute a stored procedure or a function.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders
	 * @param parameterArray an object array that contains prepared parameters in order
	 * 
	 * @return results contains return results
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Object[] parameterArray) throws DBException{
		return templateCall(sql, parameterArray);
	}
	
	/**
	 * execute a stored procedure or a function.
	 * @param sql an SQL that may contain one or more '?' IN parameter placeholders
	 * @param parameters a Ps object that contains prepared parameters
	 * 
	 * @return results contains return results, out parameters, INOUT parameters
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Ps parameters) throws DBException{
		return templateCall(sql, parameters);
	}

	/**
	 * execute a stored procedure or a function.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders 
	 * @param parameters a map that contains prepared parameters
	 * 
	 * @return results contains return results
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Map<?, ?> parameters) throws DBException{
		return templateCall(sql, parameters);
	}
	
	/**
	 * execute a stored procedure or a function.
	 * @param sql an SQL that may contain one or more '#{...}' IN parameter placeholders 
	 * @param parameters an object that contains prepared parameters
	 * 
	 * @return results contains return results
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, etc.
	 */
	public RMap<String, ?> call(String sql, Object parameters) throws DBException{
		return templateCall(sql, parameters);
	}
	
	// -------private methods
	private RMap<String, ?> templateCall(String sql, Object parameters) throws DBException{
		return getTemplate().call(sql, parameters);
	}
}
