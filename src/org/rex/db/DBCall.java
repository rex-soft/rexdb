package org.rex.db;

import javax.sql.DataSource;

import org.rex.WMap;
import org.rex.db.core.DBOperation;
import org.rex.db.core.DBTemplate;
import org.rex.db.core.reader.MapResultReader;
import org.rex.db.core.reader.ResultReader;
import org.rex.db.exception.DBException;
import org.rex.db.sql.SqlParser;

/**
 * 调用存储过程
 * @author z
 */
public class DBCall extends DBOperation{
	
	//---------------------------------------构造函数
	/**
	 * 构造函数
	 * @param dataSource 数据源
	 */
	public DBCall(DataSource dataSource, String spName){
		setDataSource(dataSource);
		setSql(spName);
	}

	//---------------------------------------内部接口
	/**
	 * 执行存储过程调用
	 */
	protected WMap execute(Ps ps, boolean originalKey) throws DBException {
		DBTemplate template = getTemplate();
		return template.call(getSql(), ps, originalKey);
	}
	
	/**
	 * 根据数组对象类型生成PS对象
	 * @param params 预编译参数数组
	 * @return PS对象
	 */
	protected Ps getPs(Object[] params){
		return new Ps(params);
	}
	
	/**
	 * 解析带有EL标记
	 * @param param 封装了预编译参数的对象
	 * @return Ps
	 * @throws DBException 
	 */
	protected Ps parseSqlEl(Object param) throws DBException{
		Object[] result = SqlParser.parse(getSql(), param);
		setSql((String)result[0]);
		return (Ps)result[1];
	}
	
	//---------------------------------------对外接口，调用存储过程时，仅返回Map对象
	/**
	 * 调用存储过程
	 * @throws DBException 
	 */
	public WMap call() throws DBException{
		return execute(null, false);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap call(Ps ps) throws DBException{
		return execute(ps, false);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap call(Object[] params) throws DBException{
		return execute(getPs(params), false);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap call(Object param) throws DBException{
		return execute(parseSqlEl(param), false);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap callOriginal() throws DBException{
		return execute(null, true);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap callOriginal(Ps ps) throws DBException{
		return execute(ps, true);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap callOriginal(Object[] params) throws DBException{
		return execute(getPs(params), true);
	}
	
	/**
	 * 调用存储过程
	 * @param ps 参数
	 * @throws DBException 
	 */
	public WMap callOriginal(Object param) throws DBException{
		return execute(parseSqlEl(param), true);
	}
}
