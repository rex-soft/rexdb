package org.rex.db;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.rex.WMap;
import org.rex.db.core.DBOperation;
import org.rex.db.core.DBTemplate;
import org.rex.db.core.reader.BeanResultReader;
import org.rex.db.core.reader.ClassResultReader;
import org.rex.db.core.reader.MapResultReader;
import org.rex.db.core.reader.ResultReader;
import org.rex.db.exception.DBException;
import org.rex.db.sql.SqlParser;

/**
 * 处理查询
 * 
 * @author Z
 */
public class DBQuery extends DBOperation {

	// ---------------------------------------构造函数
	/**
	 * 构造函数
	 * 
	 * @param dataSource 数据源
	 * @param sql sql语句
	 * @throws SQLException 当SQL语句方言翻译错误时抛出异常
	 */
	public DBQuery(DataSource dataSource, String sql) throws DBException {
		setDataSource(dataSource);
		// setSql(translateSql(sql));
		setSql(sql);
	}

	// ---------------------------------------与方言有关的内部方法

	/**
	 * 执行SQL翻译，主要针对SQL语句中的方法
	 * 
	 * @param sql 原SQL语句
	 * @return 翻译后的SQL
	 * @throws SQLException 翻译SQL时发生了错误
	 */
	// protected String translateSql(String sql) throws W11DBException{
	// return getDialect().translateSql(sql);
	// }

	/**
	 * 获取分页SQL
	 * 
	 * @param sql sql语句
	 * @param limit 结果条目
	 * @return 分页SQL语句
	 */
	protected String getLimitSql(String sql, int limit) throws DBException {
		return getDialect().getLimitSql(sql, limit);
	}

	/**
	 * 获取分页SQL
	 * 
	 * @param sql sql语句
	 * @param offset 偏移
	 * @param limit 结果条目
	 * @return 分页SQL语句
	 */
	protected String getLimitSql(String sql, int offset, int limit) throws DBException {
		return getDialect().getLimitSql(sql, offset, limit);
	}

	/**
	 * 获取分页后的预编译参数
	 * 
	 * @param ps 预编译参数
	 * @param limit 分页sql条目
	 * @return
	 * @throws SQLException
	 */
	protected Ps getLimitPs(Ps ps, int limit) throws DBException {
		return getDialect().getLimitPs(ps, limit);
	}

	/**
	 * 获取分页后的预编译参数
	 * 
	 * @param ps 预编译参数
	 * @param offset 偏移
	 * @param limit 分页sql条目
	 * @return
	 * @throws SQLException
	 */
	protected Ps getLimitPs(Ps ps, int offset, int limit) throws DBException {
		return getDialect().getLimitPs(ps, offset, limit);
	}

	/**
	 * 解析带有EL标记
	 * 
	 * @param param 封装了预编译参数的对象
	 * @return Ps
	 */
	protected Ps parseSqlEl(Object param) {
		Object[] result = SqlParser.parse(getSql(), param);
		setSql((String) result[0]);
		return (Ps) result[1];
	}

	// ---------------------------------------私有内部方法

	/**
	 * 根据数组对象类型生成PS对象
	 * 
	 * @param params 预编译参数数组
	 * @return PS对象
	 */
	protected Ps getPs(Object[] params) {
		return new Ps(params);
	}

	// ---------------------------------------与查询有关的内部方法
	/**
	 * 执行查询
	 */
	private <T> List<T> execute(ResultReader<T> rr, Ps ps) throws DBException {
		DBTemplate template = getTemplate();
		if (ps == null)
			template.query(getSql(), rr);
		else {
			template.query(getSql(), ps, rr);
		}
		return rr.getResults();
	}

	// ----------------查询多条记录
	private <T> List<T> queryList(Ps ps, Class<T> resultClass, boolean originalKey) throws DBException {
		ResultReader<T> rr = new ClassResultReader<T>(ps, originalKey, resultClass);
		return execute(rr, ps);
	}

	private <T> List<T> queryList(Ps ps, T bean, boolean originalKey) throws DBException {
		ResultReader<T> rr = new BeanResultReader<T>(ps, originalKey, bean);
		return execute(rr, ps);
	}

	private List<WMap> queryList(Ps ps, boolean originalKey) throws DBException {
		ResultReader<WMap> rr = new MapResultReader(ps, originalKey);
		return execute(rr, ps);
	}

	// ----------------查询1条记录
	private <T> T query(Ps ps, Class<T> resultClass, boolean originalKey) throws DBException {
		List<T> result = queryList(ps, resultClass, originalKey);
		return fetchOne(result);
	}

	private <T> T query(Ps ps, T bean, boolean originalKey) throws DBException {
		List<T> result = queryList(ps, bean, originalKey);
		return fetchOne(result);
	}

	private WMap query(Ps ps, boolean originalKey) throws DBException {
		List<WMap> result = queryList(ps, originalKey);
		return fetchOne(result);
	}

	/**
	 * 从结果集列表中获取一条记录，如果超过一条，抛出异常
	 * 
	 * @param list 结果集列表
	 * @return 一条记录
	 * @throws DBException
	 */
	private <T> T fetchOne(List<T> list) throws DBException {
		if (list == null || list.size() == 0)
			return null;
		if (list.size() > 1)
			throw new DBException("DB-Q10001", list.size());
		return list.get(0);
	}

	// --------返回POJO实例，POJO实例作为参数
	/**
	 * 执行查询，获取一条记录
	 * 
	 * @param resultPojo 查询结果集实例
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T get(T bean, boolean originalKey) throws DBException {
		return query(null, bean, originalKey);
	}
	
	protected <T> T get(Ps ps, T bean, boolean originalKey) throws DBException {
		return query(ps, bean, originalKey);
	}

	/**
	 * 执行查询，获取一条记录
	 * 
	 * @param params 预编译参数（数组形式）
	 * @param resultPojo 查询结果集实例
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T getByParamArray(Object[] params, T bean, boolean originalKey) throws DBException {
		return query(getPs(params), bean, originalKey);
	}

	/**
	 * 执行查询，获取一条记录
	 * 
	 * @param param 预编译参数（对象形式）
	 * @param resultPojo 查询结果集实例
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T getByEl(Object param, T bean, boolean originalKey) throws DBException {
		return query(parseSqlEl(param), bean, originalKey);
	}

	// --------返回POJO实例，Class作为参数
	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param resultPojoClass 查询结果集类
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T get(Class<T> resultClass, boolean originalKey) throws DBException {
		return query(null, resultClass, originalKey);
	}
	
	protected <T> T get(Ps ps, Class<T> resultClass, boolean originalKey) throws DBException {
		return query(ps, resultClass, originalKey);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param params 预编译参数
	 * @param resultPojoClass 查询结果集类
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T getByParamArray(Object[] params, Class<T> resultClass, boolean originalKey) throws DBException {
		return query(getPs(params), resultClass, originalKey);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param ps 预编译参数
	 * @param resultPojoClass 查询结果集类
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected <T> T getByEl(Object param, Class<T> resultClass, boolean originalKey) throws DBException {
		return query(parseSqlEl(param), resultClass, originalKey);
	}

	// --------返回Map
	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected WMap getMap(boolean originalKey) throws DBException {
		return query(null, originalKey);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param ps 预编译参数
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected WMap getMap(Ps ps, boolean originalKey) throws DBException {
		return query(ps, originalKey);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param ps 预编译参数
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected WMap getMapByParamArray(Object[] params, boolean originalKey) throws DBException {
		return query(getPs(params), originalKey);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @param ps 预编译参数
	 * @param originalKey 结果集实例的参数是否与列名完全相同（false则转换为java风格）
	 * @return 结果集（未查询到记录时为null）
	 * @throws DBException
	 */
	protected WMap getMapByEl(Object param, boolean originalKey) throws DBException {
		return query(parseSqlEl(param), originalKey);
	}

	// ----------------结果集是多条记录
	// --------返回POJO实例
	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param resultPojoClass 结果类
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws DBException
	 */
	protected <T> List<T> getList(Class<T> resultClass, boolean originalKey) throws DBException {
		return queryList(null, resultClass, originalKey);
	}
	
	protected <T> List<T> getList(Ps ps, Class<T> resultClass, boolean originalKey) throws DBException {
		return queryList(ps, resultClass, originalKey);
	}


	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param ps 预编译参数
	 * @param resultPojoClass 结果类
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws DBException
	 */
	protected <T> List<T> getListByParamArray(Object[] params, Class<T> resultClass, boolean originalKey) throws DBException {
		return queryList(getPs(params), resultClass, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param param 预编译参数
	 * @param resultPojoClass 结果类
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws DBException
	 */
	protected <T> List<T> getListByEl(Object param, Class<T> resultClass, boolean originalKey) throws DBException {
		return queryList(parseSqlEl(param), resultClass, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param resultPojoClass 结果类
	 * @param limit 记录条数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 */
	protected <T> List<T> getList(Class<T> resultClass, int limit, boolean originalKey) throws DBException {
		return getLimitList(null, resultClass, -1, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param resultPojoClass 结果类
	 * @param offset 记录偏移数
	 * @param limit 记录条数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 */
	protected <T> List<T> getList(Class<T> resultClass, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(null, resultClass, offset, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param ps 预编译参数
	 * @param resultPojoClass 结果类
	 * @param offset 记录偏移数, 该值小于0时忽略
	 * @param limit 记录条数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 * @throws DBException
	 */
	protected <T> List<T> getLimitList(Ps ps, Class<T> resultClass, int offset, int limit, boolean originalKey) throws DBException {
		return queryList(wrapLimitPs(ps, offset, limit), resultClass, originalKey);
	}
	
	protected List<WMap> getLimitList(Ps ps, int offset, int limit, boolean originalKey) throws DBException {
		return queryList(wrapLimitPs(ps, offset, limit), originalKey);
	}

	private Ps wrapLimitPs(Ps ps, int offset, int limit) throws DBException{
		String limitSql = getLimitSql(getSql(), offset, limit);
		Ps limitPs = offset < 0 ? getLimitPs(ps, limit) : getLimitPs(ps, offset, limit);// 小于0的offset无视
		setSql(limitSql);
		return limitPs;
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param resultPojoClass 结果类
	 * @param offset 记录偏移数, 该值小于0时忽略
	 * @param limit 记录条数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 */
	protected <T> List<T> getListByParamArray(Object[] params, Class<T> resultClass, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(getPs(params), resultClass, offset, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param param 预编译参数
	 * @param resultPojoClass 结果类
	 * @param offset 记录偏移数, 该值小于0时忽略
	 * @param limit 记录条数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 */
	protected <T> List<T> getListByEl(Object param, Class<T> resultClass, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(parseSqlEl(param), resultClass, offset, limit, originalKey);
	}

	// --------返回Map对象
	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws DBException
	 */
	protected List<WMap> getMapList(boolean originalKey) throws DBException {
		return queryList(null, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws SQLException
	 * @throws DBException
	 */
	protected List<WMap> getMapList(int limit, boolean originalKey) throws DBException {
		return getLimitList(null, -1, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param offset 结果集偏移数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return 结果记录
	 * @throws DBException
	 */
	protected List<WMap> getMapList(int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(null, offset, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param ps 预编译参数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapList(Ps ps, boolean originalKey) throws DBException {
		return queryList(ps, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param ps 预编译参数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapList(Ps ps, int limit, boolean originalKey) throws DBException {
		return getLimitList(ps, -1, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param ps 预编译参数
	 * @param offset 结果集偏移数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapList(Ps ps, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(ps, offset, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByParamArray(Object[] params, boolean originalKey) throws DBException {
		return queryList(getPs(params), originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByEl(Object param, boolean originalKey) throws DBException {
		return queryList(parseSqlEl(param), originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByParamArray(Object[] params, int limit, boolean originalKey) throws DBException {
		return getLimitList(getPs(params), -1, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByEl(Object param, int limit, boolean originalKey) throws DBException {
		return getLimitList(parseSqlEl(param), -1, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param offset 结果集偏移数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByParamArray(Object[] params, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(getPs(params), offset, limit, originalKey);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @param params 预编译参数
	 * @param offset 结果集偏移数
	 * @param limit 数据条目
	 * @param originalKey 结果类属性的命名方式是否使用数据库风格
	 * @return
	 * @throws DBException
	 */
	protected List<WMap> getMapListByEl(Object param, int offset, int limit, boolean originalKey) throws DBException {
		return getLimitList(parseSqlEl(param), offset, limit, originalKey);
	}

	// ---------------------------------------执行查询，对外接口
	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(T bean) throws DBException {
		return get(bean, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(T bean) throws DBException {
		return get(bean, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Ps ps, T bean) throws DBException {
		return get(ps, bean, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Object[] params, T bean) throws DBException {
		return getByParamArray(params, bean, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Object param, T bean) throws DBException {
		return getByEl(param, bean, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Ps ps, T bean) throws DBException {
		return get(ps, bean, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Object[] params, T bean) throws DBException {
		return getByParamArray(params, bean, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Object param, T bean) throws DBException {
		return getByEl(param, bean, true);
	}

	
	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Class<T> resultClass) throws DBException {
		return get(null, resultClass, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Class<T> resultClass) throws DBException {
		return get(resultClass, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Ps ps, Class<T> resultClass) throws DBException {
		return get(ps, resultClass, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Object[] params, Class<T> resultClass) throws DBException {
		return getByParamArray(params, resultClass, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T get(Object param, Class<T> resultClass) throws DBException {
		return getByEl(param, resultClass, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Ps ps, Class<T> resultClass) throws DBException {
		return get(ps, resultClass, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Object[] params, Class<T> resultClass) throws DBException {
		return getByParamArray(params, resultClass, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public <T> T getOriginal(Object param, Class<T> resultClass) throws DBException {
		return getByEl(param, resultClass, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMap() throws DBException {
		return (WMap) getMap(false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMapOriginal() throws DBException {
		return (WMap) getMap(true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMap(Ps ps) throws DBException {
		return (WMap) getMap(ps, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMap(Object[] params) throws DBException {
		return (WMap) getMapByParamArray(params, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMap(Object param) throws DBException {
		return (WMap) getMapByEl(param, false);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMapOriginal(Ps ps) throws DBException {
		return (WMap) getMap(ps, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMapOriginal(Object[] params) throws DBException {
		return (WMap) getMapByParamArray(params, true);
	}

	/**
	 * 执行查询, 获取一条记录
	 * 
	 * @throws DBException
	 */
	public WMap getMapOriginal(Object param) throws DBException {
		return (WMap) getMapByEl(param, true);
	}

	// --------------------多条
	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getList(Class<T> resultClass) throws DBException {
		return getList(resultClass, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getListOriginal(Class<T> resultClass) throws DBException {
		return getList(resultClass, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getList(Ps ps, Class<T> resultClass) throws DBException {
		return getList(ps, resultClass, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getList(Object[] params, Class<T> resultClass) throws DBException {
		return getListByParamArray(params, resultClass, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getList(Object param, Class<T> resultClass) throws DBException {
		return getListByEl(param, resultClass, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getListOriginal(Ps ps, Class<T> resultClass) throws DBException {
		return getList(ps, resultClass, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getListOriginal(Object[] params, Class<T> resultClass) throws DBException {
		return getListByParamArray(params, resultClass, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getListOriginal(Object param, Class<T> resultClass) throws DBException {
		return getListByEl(param, resultClass, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public <T> List<T> getList(Class<T> resultClass, int offset, int limit) throws DBException {
		return getList(resultClass, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getListOriginal(Class<T> resultClass, int offset, int limit) throws DBException {
		return getList(resultClass, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(Ps ps, Class<T> resultClass, int offset, int limit) throws DBException {
		return getLimitList(ps, resultClass, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(Object[] params, Class<T> resultClass, int offset, int limit) throws DBException {
		return getListByParamArray(params, resultClass, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(Object param, Class<T> resultClass, int offset, int limit) throws DBException {
		return getListByEl(param, resultClass, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getListOriginal(Ps ps, Class<T> resultClass, int offset, int limit) throws DBException {
		return getLimitList(ps, resultClass, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getListOriginal(Object[] params, Class<T> resultClass, int offset, int limit) throws DBException {
		return getListByParamArray(params, resultClass, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getListOriginal(Object param, Class<T> resultClass, int offset, int limit) throws DBException {
		return getListByEl(param, resultClass, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList() throws DBException {
		return getMapList(false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal() throws DBException {
		return getMapList(true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Ps ps) throws DBException {
		return getMapList(ps, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Object[] params) throws DBException {
		return getMapListByParamArray(params, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Object param) throws DBException {
		return getMapListByEl(param, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Ps ps) throws DBException {
		return getMapList(ps, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Object[] params) throws DBException {
		return getMapListByParamArray(params, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Object param) throws DBException {
		return getMapListByEl(param, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(int offset, int limit) throws DBException {
		return getMapList(offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(int offset, int limit) throws DBException {
		return getMapList(offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Ps ps, int offset, int limit) throws DBException {
		return getMapList(ps, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Object[] params, int offset, int limit) throws DBException {
		return getMapListByParamArray(params, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapList(Object param, int offset, int limit) throws DBException {
		return getMapListByEl(param, offset, limit, false);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Ps ps, int offset, int limit) throws DBException {
		return getMapList(ps, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Object[] params, int offset, int limit) throws DBException {
		return getMapListByParamArray(params, offset, limit, true);
	}

	/**
	 * 执行查询, 获取多条记录
	 * 
	 * @throws DBException
	 */
	public List<WMap> getMapListOriginal(Object param, int offset, int limit) throws DBException {
		return getMapListByEl(param, offset, limit, true);
	}

}
