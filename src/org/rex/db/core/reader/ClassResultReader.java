package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.rex.db.configuration.Configuration;
import org.rex.db.dynamic.javassist.BeanConvertor;
import org.rex.db.dynamic.javassist.BeanConvertorManager;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.ReflectUtil;

/**
 * 读取单条结果集，进行OR映射
 */
public class ClassResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Class<T> resultClass;
	private List<T> results;

	private int rowNum = 0;

	
	//----------settings
	/**
	 * user dynamic class
	 * @throws DBException 
	 */
	private static boolean isDynamic() throws DBException{
		return Configuration.getCurrentConfiguration().isDynamicClass();
	}
	

	//--------construct
	/**
	 * 创建结果集读取类，适用于普通查询
	 * 
	 * @param ps 查询参数
	 * @param originalKey 是否按照结果集原始键处理
	 * @param resultPojo
	 */
	public ClassResultReader(Class<T> resultClass) {
		this.results = new LinkedList<T>();
		this.resultClass = resultClass;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		results.add(row2Bean(rs, rowNum++));
	}

	public List<T> getResults() {
		return results;
	}

	// --------private parameter
	int[] columnsCodeCacheForDynamic = null;

	// --------private methods
	/**
	 * OR映射
	 */
	private T row2Bean(ResultSet rs, int rowNum) throws DBException {
		if (resultClass == null)
			throw new DBException("DB-C0003");
		
		if(isDynamic()){
			BeanConvertor setter = BeanConvertorManager.getConvertor(resultClass);
			String[] rsLabelsRenamed = orUtil.getResultSetLabelsRenamed(rs);
			if(columnsCodeCacheForDynamic == null)
				columnsCodeCacheForDynamic = setter.getColumnCodes(rsLabelsRenamed);

			try {
				return (T)setter.readResultSet(rs, orUtil, columnsCodeCacheForDynamic);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DBException(e);
			}
		}else{
			T bean = ReflectUtil.instance(resultClass);
			return orUtil.rs2Object(rs, bean);
		}
		
	}
}
