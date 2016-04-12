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
	 * execute an SQL stored procedure or function.
	 * @param sql an SQL to be sent to the database, typically a static SQL
	 * 
	 * @return
	 */
	public RMap<String, ?> call(String sql) throws DBException{
		return templateCall(sql, null);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public RMap<String, ?> call(String sql, Object[] parameterArray) throws DBException{
		return templateCall(sql, parameterArray);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public RMap<String, ?> call(String sql, Ps ps) throws DBException{
		return templateCall(sql, ps);
	}

	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public RMap<String, ?> call(String sql, Map<?, ?> parameterMap) throws DBException{
		return templateCall(sql, parameterMap);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public RMap<String, ?> call(String sql, Object parameterBean) throws DBException{
		return templateCall(sql, parameterBean);
	}
	
	// -------private methods
	private RMap<String, ?> templateCall(String sql, Object parameters) throws DBException{
		return getTemplate().call(sql, parameters);
	}
}
