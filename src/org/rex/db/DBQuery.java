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
 * 查询
 * @author z
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

	//=========public methods

	//---------------query one row for java bean
	/**
	 * 执行查询, 获取一条记录
	 */
	public <T> T get(String sql, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, null, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public <T> T get(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 */
	public <T> T get(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameterArray, resultClass);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 */
	public <T> T get(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public <T> T get(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return templateClassQueryForOneRow(sql, parameters, resultClass);
	}
	
	//---------------query one row for RMap

	/**
	 * 执行查询, 获取一条记录
	 */
	public RMap<String, ?> getMap(String sql) throws DBException {
		return templateMapQueryForOneRow(sql, null);
	}
	
	/**
	 * 执行查询, 获取一条记录
	 */
	public RMap<String, ?> getMap(String sql, Ps parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public RMap<String, ?> getMap(String sql, Object[] parameterArray) throws DBException {
		return templateMapQueryForOneRow(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public RMap<String, ?> getMap(String sql, Object parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	/**
	 * 执行查询, 获取一条记录
	 */
	public RMap<String, ?> getMap(String sql, Map<?, ?> parameters) throws DBException {
		return templateMapQueryForOneRow(sql, parameters);
	}

	//---------------query a list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, null, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameterArray, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass);
	}

	//---------------query a list of RMap
	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql) throws DBException {
		return templateMapQuery(sql, null);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Ps parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Object[] parameterArray) throws DBException {
		return templateMapQuery(sql, parameterArray);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Object parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Map<?, ?> parameters) throws DBException {
		return templateMapQuery(sql, parameters);
	}
	
	//---------------query a limit list of java bean
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, null, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Ps parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}
	
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Object[] parameterArray, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameterArray, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public <T> List<T> getList(String sql, Map<?, ?> parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, offset, rows);
	}
	
	//---------------query a limit list of RMap
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, int offset, int rows) throws DBException {
		return templateMapQuery(sql, null, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Ps parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Object[] parameterArray, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameterArray, offset, rows);
	}

	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Object parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}
	
	/**
	 * 执行查询, 获取多条记录
	 */
	public List<RMap> getMapList(String sql, Map<?, ?> parameters, int offset, int rows) throws DBException {
		return templateMapQuery(sql, parameters, offset, rows);
	}
	
	
	//=========private methods
	
	//---------------tempalte query for java bean
	protected <T> T templateClassQueryForOneRow(String sql, Object parameters, Class<T> resultClass) throws DBException {
		List<T> list = templateClassQuery(sql, parameters, resultClass, null);
		if(list.size() == 0) return null;
		if(list.size() > 1)
			throw new DBException("DB-00005", list.size());
		return list.get(0);
	}
	
	protected <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass) throws DBException {
		return templateClassQuery(sql, parameters, resultClass, null);
	}
	
	protected <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass, int offset, int rows) throws DBException {
		LimitHandler limitHandler = getDialect().getLimitHandler(offset, rows);
		return templateClassQuery(sql, parameters, resultClass, limitHandler);
	}
	
	/**
	 * query for a list of java beans
	 * 
	 * @param sql a query SQL that may contain one or more '?', or '#{
	 *            <i>parameter name</i>}' IN parameter placeholders
	 * @param parameters the prepared parameters
	 * @param resultClass java bean that the SQL query for
	 * @return a list of java beans
	 * @throws DBException
	 */
	private <T> List<T> templateClassQuery(String sql, Object parameters, Class<T> resultClass, LimitHandler limitHandler) throws DBException {
		ResultReader<T> resultReader = new ClassResultReader<T>(resultClass);
		if (parameters == null && limitHandler == null)
			getTemplate().query(sql, resultReader);
		else
			getTemplate().query(sql, parameters, limitHandler, resultReader);
		return resultReader.getResults();
	}

	//---------------template query for RMap
	protected RMap<String, ?> templateMapQueryForOneRow(String sql, Object parameters) throws DBException {
		List<RMap> list = templateMapQuery(sql, parameters, null);
		if(list.size() == 0) return null;
		if(list.size() > 1)
			throw new DBException("DB-00005", list.size());
		
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
