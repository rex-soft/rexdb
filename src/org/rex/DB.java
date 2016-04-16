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
		try{
			return getDefaultDataSource().getConnection();
		}catch(SQLException e){
			throw new DBException("DB-00004", e);
		}
	}

	/**
	 * Gets Connection from specified dataSource.
	 * 
	 * @return an idle Connection from specified dataSource.
	 */
	public static Connection getConnection(String dataSourceId) throws DBException {
		try{
			return getDataSource(dataSourceId).getConnection();
		}catch(SQLException e){
			throw new DBException("DB-00004", e);
		}
	}

	// --------------------------------------------- dialects
	private static DialectManager getDialectManager() throws DBException {
		return Configuration.getCurrentConfiguration().getDialectManager();
	}

	/**
	 * Gets Dialect instance for the default dataSource.
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

	// --------------------------------------------- 查询
	// ------------specified dataSource
	// ---------------query one row for java bean
	/**
	 * 执行查询, 获取一条记录
	 */
	public static <T> T get(String dataSourceId, String sql, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static <T> T get(String dataSourceId, String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static <T> T get(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameterArray, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static <T> T get(String dataSourceId, String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static <T> T get(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).get(sql, parameters, resultClass);
	}

	// ---------------query one row for RMap

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery(dataSourceId).getMap(sql, parameters);
	}

	// ---------------query a list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameterArray, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass);
	}

	// ---------------query a list of RMap
	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters);
	}

	// ---------------query a limit list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameterArray, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object parameters, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows)
			throws DBException {
		return getDBQuery(dataSourceId).getList(sql, parameters, resultClass, offset, rows);
	}

	// ---------------query a limit list of RMap

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Ps parameters, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameterArray, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String dataSourceId, String sql, Object parameters, int offset, int rows) throws DBException {
		return getDBQuery(dataSourceId).getMapList(sql, parameters, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
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
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, the ResultSet contains more than one row, etc.
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
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, the ResultSet contains more than one row, etc.
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
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, the ResultSet contains more than one row, etc.
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
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, the ResultSet contains more than one row, etc.
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
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database,
	 * 			couldn't execute SQL, the ResultSet contains more than one row, etc.
	 */
	public static <T> T get(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().get(sql, parameters, resultClass);
	}

	// ---------------query one row for RMap

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String sql) throws DBException {
		return getDBQuery().getMap(sql);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String sql, Ps parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String sql, Object[] parameterArray) throws DBException {
		return getDBQuery().getMap(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String sql, Object parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public static RMap<String, ?> getMap(String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery().getMap(sql, parameters);
	}

	// ---------------query a list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameterArray, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass);
	}

	// ---------------query a list of RMap
	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql) throws DBException {
		return getDBQuery().getMapList(sql);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Ps parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Object[] parameterArray) throws DBException {
		return getDBQuery().getMapList(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Object parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Map<?, ?> parameters) throws DBException {
		return getDBQuery().getMapList(sql, parameters);
	}

	// ---------------query a limit list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameterArray, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return getDBQuery().getList(sql, parameters, resultClass, offset, rows);
	}

	// ---------------query a limit list of RMap
	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Ps parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameterArray, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Object parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public static List<RMap> getMapList(String sql, Map<?, ?> parameters, int offset, int rows) throws DBException {
		return getDBQuery().getMapList(sql, parameters, offset, rows);
	}

	// --------------------------------------------- 更新
	// ------------使用指定数据源

	/**
	 * 执行更新
	 * 
	 * @throws DBException
	 */
	public static int update(String dataSourceId, String sql) throws DBException {
		return getDBUpdate(dataSourceId).update(sql);
	}

	/**
	 * 执行更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String dataSourceId, String sql, Ps ps) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, ps);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数，按照SQL中预编译参数顺序排列
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameterArray);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数所在的Map或POJO对象
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String dataSourceId, String sql, Map<?, ?> parameterMap) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameterMap);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数所在的Map或POJO对象
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String dataSourceId, String sql, Object parameterBean) throws DBException {
		return getDBUpdate(dataSourceId).update(sql, parameterBean);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param sql sql语句
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String dataSourceId, String[] sqls) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sqls);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Ps[] pss) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, pss);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[][] parameterArrays) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterArrays);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterMaps);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[] parameterBeans) throws DBException {
		return getDBUpdate(dataSourceId).batchUpdate(sql, parameterBeans);
	}

	// ------------使用默认数据源
	/**
	 * 执行更新
	 * 
	 * @throws DBException
	 */
	public static int update(String sql) throws DBException {
		return getDBUpdate().update(sql);
	}

	/**
	 * 执行更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String sql, Ps ps) throws DBException {
		return getDBUpdate().update(sql, ps);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数，按照SQL中预编译参数顺序排列
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String sql, Object[] parameterArray) throws DBException {
		return getDBUpdate().update(sql, parameterArray);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数所在的Map对象
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String sql, Map<?, ?> parameterMap) throws DBException {
		return getDBUpdate().update(sql, parameterMap);
	}

	/**
	 * 执行更新
	 * 
	 * @param params 预编译参数所在的POJO对象
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int update(String sql, Object parameterBean) throws DBException {
		return getDBUpdate().update(sql, parameterBean);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param sql sql语句
	 * @return 受影响记录条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String[] sql) throws DBException {
		return getDBUpdate().batchUpdate(sql);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public static int[] batchUpdate(String sql, Ps[] pss) throws DBException {
		return getDBUpdate().batchUpdate(sql, pss);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String sql, Object[][] parameterArrays) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterArrays);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String sql, Map<?, ?>[] parameterMaps) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterMaps);
	}

	/**
	 * 执行批量更新
	 * 
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException
	 */
	public static int[] batchUpdate(String sql, Object[] parameterBeans) throws DBException {
		return getDBUpdate().batchUpdate(sql, parameterBeans);
	}

	// --------------------------------------------- 存储过程
	// ------------使用指定数据源
	/**
	 * 调用存储过程
	 * 
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql) throws DBException {
		return getDBCall(dataSourceId).call(sql);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Object[] parameterArray) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameterArray);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Ps parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Map<?, ?> parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String dataSourceId, String sql, Object parameters) throws DBException {
		return getDBCall(dataSourceId).call(sql, parameters);
	}

	// ------------使用默认数据源
	/**
	 * 调用存储过程
	 * 
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String sql) throws DBException {
		return getDBCall().call(sql);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String sql, Object[] parameterArray) throws DBException {
		return getDBCall().call(sql, parameterArray);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String sql, Ps parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String sql, Map<?, ?> parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	/**
	 * 调用存储过程
	 * 
	 * @param ps 参数
	 * @throws DBException
	 */
	public static RMap<String, ?> call(String sql, Object parameters) throws DBException {
		return getDBCall().call(sql, parameters);
	}

	// --------------------------------------------- 事务

	// ---普通事物
	/**
	 * 开始事物
	 */
	public static void beginTransaction() throws DBException {
		beginTransaction(getDefaultDataSource(), null);
	}

	/**
	 * 开始事物
	 */
	public static void beginTransaction(String dataSourceId) throws DBException {
		beginTransaction(getDataSource(dataSourceId), null);
	}
	
	/**
	 * 开始事物
	 */
	public static void beginTransaction(DefaultDefinition definition) throws DBException {
		beginTransaction(getDefaultDataSource(), definition);
	}

	/**
	 * 开始事物
	 */
	public static void beginTransaction(String dataSourceId, DefaultDefinition definition) throws DBException {
		beginTransaction(getDataSource(dataSourceId), definition);
	}

	/**
	 * 开始事物
	 */
	private static void beginTransaction(DataSource dataSource, DefaultDefinition definition) throws DBException {
		DBTransaction.begin(dataSource, definition);
	}

	/**
	 * 提交事物
	 */
	public static void commit() throws DBException {
		commit(getDefaultDataSource());
	}

	/**
	 * 提交事物
	 */
	public static void commit(String dataSourceId) throws DBException {
		commit(getDataSource(dataSourceId));
	}

	/**
	 * 提交事物
	 */
	private static void commit(DataSource dataSource) throws DBException {
		DBTransaction.commit(dataSource);
	}

	/**
	 * 回滚事务
	 */
	public static void rollback() throws DBException {
		rollback(getDefaultDataSource());
	}

	/**
	 * 回滚事务
	 */
	public static void rollback(String dataSourceId) throws DBException {
		rollback(getDataSource(dataSourceId));
	}

	/**
	 * 回滚事务
	 */
	private static void rollback(DataSource dataSource) throws DBException {
		DBTransaction.rollback(dataSource);
	}

	/**
	 * 获取事物中的连接
	 */
	public static Connection getTransactionConnection() throws DBException {
		return getTransactionConnection(getDefaultDataSource());
	}

	/**
	 * 获取事物中的连接
	 */
	public static Connection getTransactionConnection(String dataSourceId) throws DBException {
		return DBTransaction.getTransactionConnection(getDataSource(dataSourceId));
	}

	/**
	 * 获取事物中的连接
	 */
	private static Connection getTransactionConnection(DataSource dataSource) throws DBException {
		return DBTransaction.getTransactionConnection(dataSource);
	}

	// ---jta
	/**
	 * 开始事物
	 */
	public static void beginJtaTransaction() throws DBException {
		DBTransaction.beginJta();
	}

	/**
	 * 提交事物
	 */
	public static void commitJta() throws DBException {
		DBTransaction.commitJta();
	}

	/**
	 * 回滚事务
	 */
	public static void rollbackJta() throws DBException {
		DBTransaction.rollbackJta();
	}
}
