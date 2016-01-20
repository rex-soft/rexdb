package org.rex;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

/**
 * 方便调用的数据库操作接口类
 * @author zhouwei
 */
public class DB {
	
	private static DataSourceManager getDataSourceManager() throws DBException{
		return Configuration.getCurrentConfiguration().getDataSourceManager();
	}
	
	private static DialectManager getDialectManager() throws DBException{
		return Configuration.getCurrentConfiguration().getDialectManager();
	}

	// --------------------------------------------- 0.1 数据源、数据库连接0
	public static DataSource getDefaultDataSource() throws DBException {
		DataSource defaultDataSource = getDataSourceManager().getDefault();
		if(defaultDataSource == null)
			throw new DBException("DB-C10035");
		return defaultDataSource;
	}
	
	public static DataSource getDataSource(String dataSourceId) throws DBException {
		DataSource dataSource = getDataSourceManager().get(dataSourceId);
		if(dataSource == null)
			throw new DBException("DB-C10036", dataSourceId);
		return dataSource;
	}
	
	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection() throws DBException, SQLException{
		return getDefaultDataSource().getConnection();
	}

	/**
	 * 获取数据库连接
	 */
	public static Connection getConnection(String dataSource)
			throws DBException, SQLException{
		return getDataSource(dataSource).getConnection();
	}

	// --------------------------------------------- 0.2 方言

	/**
	 * 获取方言实现
	 */
	public static Dialect getDialect() throws DBException {
		return getDialectManager().getDialect(getDefaultDataSource());
	}

	/**
	 * 获取方言实现
	 */
	public static Dialect getDialect(DataSource dataSource) throws DBException {
		return getDialectManager().getDialect(dataSource);
	}
	
	/**
	 * 获取方言实现
	 */
	public static Dialect getDialect(String dataSourceId) throws DBException {
		return getDialectManager().getDialect(getDataSource(dataSourceId));
	}

	// --------------------------------------------- 查询
	/**
	 * 获取DBQuery对象
	 * @param sql SQL语句
	 * @return DBQuery对象
	 * @throws DBException
	 */
	protected static DBQuery getQuery(String sql) throws DBException{
		return getQuery(getDefaultDataSource(), sql);
	}
	
	/**
	 * 获取DBQuery对象
	 * @param dataSource 数据源ID
	 * @param sql SQL语句
	 * @return DBQuery对象
	 * @throws DBException
	 */
	protected static DBQuery getQuery(String dataSourceId, String sql) throws DBException{
		return getQuery(getDataSource(dataSourceId), sql);
	}
	
	/**
	 * 获取DBQuery对象
	 * @param dataSource 数据源
	 * @param sql SQL语句
	 * @return DBQuery对象
	 * @throws DBException
	 */
	protected static DBQuery getQuery(DataSource dataSource, String sql) throws DBException{
		return new DBQuery(dataSource, sql);
	}
	
	//------------使用指定数据源
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, T bean) throws DBException{
		return getQuery(dataSourceId, sql).get(bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, T bean) throws DBException{
		return getQuery(dataSourceId, sql).get(bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Ps ps, T bean) throws DBException{
		return getQuery(dataSourceId, sql).get(ps, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Object[] params, T bean) throws DBException{
		return getQuery(dataSourceId, sql).get(params, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Object param, T bean) throws DBException{
		return getQuery(dataSourceId, sql).get(param, bean);
	}	
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Ps ps, T bean) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(ps, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Object[] params, T bean) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(params, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Object param, T bean) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(param, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).get(resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).get(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).get(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String dataSourceId, String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).get(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String dataSourceId, String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getOriginal(param, resultClass);
	}
	
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String dataSourceId, String sql) throws DBException{
		return getQuery(dataSourceId, sql).getMap();
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String dataSourceId, String sql) throws DBException{
		return getQuery(dataSourceId, sql).getMapOriginal();
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String dataSourceId, String sql, Ps ps) throws DBException{
		return getQuery(dataSourceId, sql).getMap(ps);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String dataSourceId, String sql, Object[] params) throws DBException{
		return getQuery(dataSourceId, sql).getMap(params);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String dataSourceId, String sql, Object param) throws DBException{
		return getQuery(dataSourceId, sql).getMap(param);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String dataSourceId, String sql, Ps ps) throws DBException{
		return getQuery(dataSourceId, sql).getMapOriginal(ps);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String dataSourceId, String sql, Object[] params) throws DBException{
		return getQuery(dataSourceId, sql).getMapOriginal(params);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String dataSourceId, String sql, Object param) throws DBException{
		return getQuery(dataSourceId, sql).getMapOriginal(param);
	}

	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getList(resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getList(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getList(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getList(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getList(resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Ps ps, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getList(ps, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object[] params, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getList(params, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String dataSourceId, String sql, Object param, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getList(param, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Ps ps, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(ps, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Object[] params, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(params, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String dataSourceId, String sql, Object param, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getListOriginal(param, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql) throws DBException{
		return getQuery(dataSourceId, sql).getMapList();
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal();
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Ps ps) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(ps);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Object[] params) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(params);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Object param) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(param);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Ps ps) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(ps);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Object[] params) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(params);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Object param) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(param);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Ps ps, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(ps, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Object[] params, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(params, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String dataSourceId, String sql, Object param, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapList(param, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Ps ps, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(ps, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Object[] params, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(params, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String dataSourceId, String sql, Object param, int offset, int rows) throws DBException{
		return getQuery(dataSourceId, sql).getMapListOriginal(param, offset, rows);
	}

	//------------使用默认数据源
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, T bean) throws DBException{
		return getQuery(sql).get(bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, T bean) throws DBException{
		return getQuery(sql).get(bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Ps ps, T bean) throws DBException{
		return getQuery(sql).get(ps, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Object[] params, T bean) throws DBException{
		return getQuery(sql).get(params, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Object param, T bean) throws DBException{
		return getQuery(sql).get(param, bean);
	}	
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Ps ps, T bean) throws DBException{
		return getQuery(sql).getOriginal(ps, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Object[] params, T bean) throws DBException{
		return getQuery(sql).getOriginal(params, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Object param, T bean) throws DBException{
		return getQuery(sql).getOriginal(param, bean);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Class<T> resultClass) throws DBException{
		return getQuery(sql).get(resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Class<T> resultClass) throws DBException{
		return getQuery(sql).getOriginal(resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(sql).get(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(sql).get(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T get(String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(sql).get(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(sql).getOriginal(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(sql).getOriginal(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static <T> T getOriginal(String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(sql).getOriginal(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String sql) throws DBException{
		return getQuery(sql).getMap();
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String sql) throws DBException{
		return getQuery(sql).getMapOriginal();
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String sql, Ps ps) throws DBException{
		return getQuery(sql).getMap(ps);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String sql, Object[] params) throws DBException{
		return getQuery(sql).getMap(params);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMap(String sql, Object param) throws DBException{
		return getQuery(sql).getMap(param);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String sql, Ps ps) throws DBException{
		return getQuery(sql).getMapOriginal(ps);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String sql, Object[] params) throws DBException{
		return getQuery(sql).getMapOriginal(params);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 * @throws DBException 
	 */
	public static WMap getMapOriginal(String sql, Object param) throws DBException{
		return getQuery(sql).getMapOriginal(param);
	}

	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass) throws DBException{
		return getQuery(sql).getList(resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String sql, Class<T> resultClass) throws DBException{
		return getQuery(sql).getListOriginal(resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(sql).getList(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(sql).getList(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(sql).getList(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String sql, Ps ps, Class<T> resultClass) throws DBException{
		return getQuery(sql).getListOriginal(ps, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String sql, Object[] params, Class<T> resultClass) throws DBException{
		return getQuery(sql).getListOriginal(params, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getListOriginal(String sql, Object param, Class<T> resultClass) throws DBException{
		return getQuery(sql).getListOriginal(param, resultClass);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static <T> List<T> getList(String sql, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getList(resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String sql, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getListOriginal(resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Ps ps, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getList(ps, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object[] params, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getList(params, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getList(String sql, Object param, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getList(param, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String sql, Ps ps, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getListOriginal(ps, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String sql, Object[] params, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getListOriginal(params, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public static <T> List<T> getListOriginal(String sql, Object param, Class<T> resultClass, int offset, int rows) throws DBException{
		return getQuery(sql).getListOriginal(param, resultClass, offset, rows);
	}
	
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql) throws DBException{
		return getQuery(sql).getMapList();
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql) throws DBException{
		return getQuery(sql).getMapListOriginal();
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Ps ps) throws DBException{
		return getQuery(sql).getMapList(ps);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Object[] params) throws DBException{
		return getQuery(sql).getMapList(params);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Object param) throws DBException{
		return getQuery(sql).getMapList(param);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Ps ps) throws DBException{
		return getQuery(sql).getMapListOriginal(ps);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Object[] params) throws DBException{
		return getQuery(sql).getMapListOriginal(params);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Object param) throws DBException{
		return getQuery(sql).getMapListOriginal(param);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, int offset, int rows) throws DBException{
		return getQuery(sql).getMapList(offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, int offset, int rows) throws DBException{
		return getQuery(sql).getMapListOriginal(offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Ps ps, int offset, int rows) throws DBException{
		return getQuery(sql).getMapList(ps, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Object[] params, int offset, int rows) throws DBException{
		return getQuery(sql).getMapList(params, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapList(String sql, Object param, int offset, int rows) throws DBException{
		return getQuery(sql).getMapList(param, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Ps ps, int offset, int rows) throws DBException{
		return getQuery(sql).getMapListOriginal(ps, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Object[] params, int offset, int rows) throws DBException{
		return getQuery(sql).getMapListOriginal(params, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 * @throws DBException 
	 */
	public static List<WMap> getMapListOriginal(String sql, Object param, int offset, int rows) throws DBException{
		return getQuery(sql).getMapListOriginal(param, offset, rows);
	}
	
	// --------------------------------------------- 更新
	
	/**
	 * 获取DBUpdate对象
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBUpdate getUpdate() throws DBException{
		return new DBUpdate(getDefaultDataSource());
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBUpdate getUpdate(String sql) throws DBException{
		return new DBUpdate(getDefaultDataSource(), sql);
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSourceId 数据源编号
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBUpdate getUpdateByDataSourceId(String dataSourceId) throws DBException{
		return new DBUpdate(getDataSource(dataSourceId));
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSourceId 数据源编号
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBUpdate getUpdate(String dataSourceId, String sql) throws DBException{
		return new DBUpdate(getDataSource(dataSourceId), sql);
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSource 数据源
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBUpdate getUpdate(DataSource dataSource, String sql) throws DBException{
		return new DBUpdate(dataSource, sql);
	}
	
	//------------使用指定数据源
	/**
	 * 执行更新
	 * @throws DBException 
	 */
	public static int update(String dataSourceId, String sql) throws DBException{
		return getUpdate(dataSourceId, sql).update();
	}
	
	/**
	 * 执行更新
	 * @param ps 预编译参数
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String dataSourceId, String sql, Ps ps) throws DBException{
		return getUpdate(dataSourceId, sql).update(ps);
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数，按照SQL中预编译参数顺序排列
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String dataSourceId, String sql, Object[] params) throws DBException{
		return getUpdate(dataSourceId, sql).update(params);
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数所在的Map或POJO对象
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String dataSourceId, String sql, Object params) throws DBException{
		return getUpdate(dataSourceId, sql).update(params);
	}
	
	/**
	 * 执行批量更新
	 * @param sql sql语句
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String dataSourceId, String[] sql) throws DBException{
		return getUpdateByDataSourceId(dataSourceId).batchUpdate(sql);
	}
	
	/**
	 * 执行批量更新
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Ps[] ps) throws DBException{
		return getUpdate(dataSourceId, sql).batchUpdate(ps);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[][] params) throws DBException{
		return getUpdate(dataSourceId, sql).batchUpdate(params);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String dataSourceId, String sql, Object[] params) throws DBException{
		return getUpdate(dataSourceId, sql).batchUpdate(params);
	}
	
	//------------使用默认数据源
	/**
	 * 执行更新
	 * @throws DBException 
	 */
	public static int update(String sql) throws DBException{
		return getUpdate(sql).update();
	}
	
	/**
	 * 执行更新
	 * @param ps 预编译参数
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String sql, Ps ps) throws DBException{
		return getUpdate(sql).update(ps);
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数，按照SQL中预编译参数顺序排列
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String sql, Object[] params) throws DBException{
		return getUpdate(sql).update(params);
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数所在的Map或POJO对象
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int update(String sql, Object params) throws DBException{
		return getUpdate(sql).update(params);
	}
	
	/**
	 * 执行批量更新
	 * @param sql sql语句
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String[] sql) throws DBException{
		return getUpdate().batchUpdate(sql);
	}
	
	/**
	 * 执行批量更新
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public static int[] batchUpdate(String sql, Ps[] ps) throws DBException{
		return getUpdate(sql).batchUpdate(ps);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String sql, Object[][] params) throws DBException{
		return getUpdate(sql).batchUpdate(params);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public static int[] batchUpdate(String sql, Object[] params) throws DBException{
		return getUpdate(sql).batchUpdate(params);
	}

	// --------------------------------------------- 存储过程
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSourceId 数据源编号
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBCall getCall(String sql) throws DBException{
		return new DBCall(getDefaultDataSource(), sql);
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSourceId 数据源编号
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBCall getCall(String dataSourceId, String sql) throws DBException{
		return new DBCall(getDataSource(dataSourceId), sql);
	}
	
	/**
	 * 获取DBUpdate对象
	 * @param dataSource 数据源
	 * @param sql SQL语句
	 * @return DBUpdate对象
	 * @throws DBException
	 */
	protected static DBCall getCall(DataSource dataSource, String sql) throws DBException{
		return new DBCall(dataSource, sql);
	}
	
	//------------使用指定数据源
	/**
	 * 调用存储过程
	 * @throws DBException 
	 */
	public static WMap call(DataSource dataSource, String sql) throws DBException{
		return getCall(dataSource, sql).call();
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(DataSource dataSource, String sql, Ps ps) throws DBException{
		return getCall(dataSource, sql).call(ps);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(DataSource dataSource, String sql, Object[] params) throws DBException{
		return getCall(dataSource, sql).call(params);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(DataSource dataSource, String sql, Object param) throws DBException{
		return getCall(dataSource, sql).call(param);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(DataSource dataSource, String sql) throws DBException{
		return getCall(dataSource, sql).callOriginal();
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(DataSource dataSource, String sql, Ps ps) throws DBException{
		return getCall(dataSource, sql).callOriginal(ps);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(DataSource dataSource, String sql, Object[] params) throws DBException{
		return getCall(dataSource, sql).callOriginal(params);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(DataSource dataSource, String sql, Object param) throws DBException{
		return getCall(dataSource, sql).callOriginal(param);
	}
	
	//------------使用默认数据源
	/**
	 * 调用存储过程
	 * @throws DBException 
	 */
	public static WMap call(String sql) throws DBException{
		return getCall(sql).call();
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(String sql, Ps ps) throws DBException{
		return getCall(sql).call(ps);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(String sql, Object[] params) throws DBException{
		return getCall(sql).call(params);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap call(String sql, Object param) throws DBException{
		return getCall(sql).call(param);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(String sql) throws DBException{
		return getCall(sql).callOriginal();
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(String sql, Ps ps) throws DBException{
		return getCall(sql).callOriginal(ps);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(String sql, Object[] params) throws DBException{
		return getCall(sql).callOriginal(params);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public static WMap callOriginal(String sql, Object param) throws DBException{
		return getCall(sql).callOriginal(param);
	}

	// --------------------------------------------- 事务
	
	//---普通事物
	/**
	 * 开始事物
	 */
	public static void beginTransaction() throws DBException{
		beginTransaction(getDefaultDataSource());
	}
	
	/**
	 * 开始事物
	 */
	public static void beginTransaction(String dataSourceId) throws DBException{
		beginTransaction(getDataSource(dataSourceId));
	}
	
	/**
	 * 开始事物
	 */
	public static void beginTransaction(DataSource dataSource) throws DBException{
		DBTransaction.begin(dataSource, null);
	}
	
	/**
	 * 提交事物
	 */
	public static void commit() throws DBException{
		commit(getDefaultDataSource());
	}

	/**
	 * 提交事物
	 */
	public static void commit(String dataSourceId) throws DBException{
		commit(getDataSource(dataSourceId));
	}
	
	/**
	 * 提交事物
	 */
	public static void commit(DataSource dataSource) throws DBException{
		DBTransaction.commit(dataSource);
	}
	
	/**
	 * 回滚事务
	 */
	public static void rollback() throws DBException{
		rollback(getDefaultDataSource());
	}

	/**
	 * 回滚事务
	 */
	public static void rollback(String dataSourceId) throws DBException{
		rollback(getDataSource(dataSourceId));
	}

	/**
	 * 回滚事务
	 */
	public static void rollback(DataSource dataSource) throws DBException{
		DBTransaction.rollback(dataSource);
	}
	
	/**
	 * 获取事物中的连接
	 */
	public static Connection getTransactionConnection() throws DBException{
		return getTransactionConnection(getDefaultDataSource());
	}
	
	/**
	 * 获取事物中的连接
	 */
	public static Connection getTransactionConnection(String dataSourceId) throws DBException{
		return getTransactionConnection(getDataSource(dataSourceId));
	}
	
	/**
	 * 获取事物中的连接
	 */
	public static Connection getTransactionConnection(DataSource dataSource) throws DBException{
		return DBTransaction.getTransactionConnection(dataSource);
	}
	
	//---jta
	/**
	 * 开始事物
	 */
	public static void beginJtaTransaction() throws DBException{
		DBTransaction.beginJta();
	}
	
	/**
	 * 提交事物
	 */
	public static void commitJta() throws DBException{
		DBTransaction.commitJta();
	}
	
	/**
	 * 回滚事务
	 */
	public static void rollbackJta() throws DBException{
		DBTransaction.rollbackJta();
	}
}
