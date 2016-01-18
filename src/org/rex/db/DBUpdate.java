package org.rex.db;

import javax.sql.DataSource;

import org.rex.db.core.DBOperation;
import org.rex.db.exception.DBException;
import org.rex.db.sql.SqlParser;

/**
 * 用于数据库的增删改操作
 * @author z
 */
public class DBUpdate extends DBOperation{
	
	//---------------------------------------构造函数
	
	/**
	 * 构造函数
	 * @param dataSource 数据源
	 * @throws DBException 当SQL语句方言翻译错误时抛出异常
	 */
	public DBUpdate(DataSource dataSource) throws DBException{
		setDataSource(dataSource);
	}
	
	/**
	 * 构造函数
	 * @param dataSource 数据源
	 * @param sql sql语句
	 * @throws DBException 当SQL语句方言翻译错误时抛出异常
	 */
	public DBUpdate(DataSource dataSource, String sql) throws DBException{
		setDataSource(dataSource);
//		setSql(translateSql(sql));//对SQL执行基于方言的翻译
		setSql(sql);
	}
	
	//---------------------------------------与方言有关的内部方法
	/**
	 * 执行SQL翻译，主要针对SQL语句中的方法
	 * @param sql 原SQL语句
	 * @return 翻译后的SQL
	 * @throws W11DBException 翻译SQL时发生了错误
	 */
//	protected String translateSql(String sql) throws W11DBException{
//		return getDialect().translateSql(sql);
//	}
	
	//---------------------------------------执行操作，对外接口
	
	/**
	 * 执行更新
	 * @throws DBException 
	 */
	public int update() throws DBException{
		return getTemplate().update(getSql());
	}
	
	/**
	 * 执行更新
	 * @param ps 预编译参数
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public int update(Ps ps) throws DBException{
		if(ps==null) return update();
		return getTemplate().update(getSql(), ps);
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数，按照SQL中预编译参数顺序排列
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public int update(Object[] params) throws DBException{
		return update(new Ps(params));
	}
	
	/**
	 * 执行更新
	 * @param params 预编译参数所在的Map或POJO对象
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public int update(Object params) throws DBException{
		Object[] result = SqlParser.parse(getSql(), params);
		
		setSql((String)result[0]);
		return update((Ps)result[1]);
	}
	
	/**
	 * 执行批量更新
	 * @param sql sql语句
	 * @return 受影响记录条数
	 * @throws DBException 
	 */
	public int[] batchUpdate(String[] sql) throws DBException{
		return getTemplate().batchUpdate(sql);
	}
	
	/**
	 * 执行批量更新
	 * @param ps 预编译参数
	 * @return 受影响条数
	 */
	public int[] batchUpdate(Ps[] ps) throws DBException{
		if(ps==null || ps.length==0) {
//			int r = update();
//			return new int[]{r};
			return new int[]{0};
		}
		return getTemplate().batchUpdate(getSql(), ps);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public int[] batchUpdate(Object[][] params) throws DBException{
		if(params==null || params.length==0) {
			int r = update();
			return new int[]{r};
		}
		
		Ps[] ps = new Ps[params.length];
		for(int i=0;i<params.length;i++){
			ps[i] = new Ps(params[i]);
		}
		return batchUpdate(ps);
	}
	
	/**
	 * 执行批量更新
	 * @param params 预编译参数
	 * @return 受影响条数
	 * @throws DBException 
	 */
	public int[] batchUpdate(Object[] params) throws DBException{
		if(params==null || params.length==0) {
			int r = update();
			return new int[]{r};
		}
		Ps[] ps = new Ps[params.length];
		for(int i=0;i<params.length;i++){
			Object[] result = SqlParser.parse(getSql(), params[i]);
			ps[i] = (Ps)result[1];
			if(i == params.length - 1) setSql((String)result[0]);
		}
		return batchUpdate(ps);
	}
}
