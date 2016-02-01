package org.rex.db.core.reader;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.ReflectUtil;

/**
 * 读取单条结果集，进行OR映射
 */
public class BeanResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Ps ps;
	boolean originalKey;

	private T resultBean;
	private List<T> results;

	private int rowNum = 0;
	private Method cloneMethod = null;

	/**
	 * 创建结果集读取类，适用于普通查询
	 * 
	 * @param ps 查询参数
	 * @param originalKey 是否按照结果集原始键处理
	 * @param resultPojo
	 */
	public BeanResultReader(Ps ps, boolean originalKey, T resultBean) {
		this.results = new LinkedList<T>();
		this.ps = ps;
		this.originalKey = originalKey;
		this.resultBean = resultBean;
	}

	public void setPs(Ps ps) {
		this.ps = ps;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		if (resultBean == null)
			throw new DBException("DB-C0003");
		
		T clone = resultBean;
		if (rowNum > 0) {
			Method cloneMethod = getCloneMethod();
			if(cloneMethod == null)
				throw new DBException("DB-C0004", resultBean.getClass().getName());
			
			clone = (T)ReflectUtil.invokeMethod(resultBean, cloneMethod);
		}
		results.add(row2Bean(rs, rowNum++, ps, clone, originalKey));
	}
	
	private Method getCloneMethod() throws DBException{
		if(cloneMethod == null)
			cloneMethod = ReflectUtil.getCloneMethod(resultBean);
		return cloneMethod;
	}

	public List<T> getResults() {
		return results;
	}

	/**
	 * OR映射
	 */
	protected T row2Bean(ResultSet rs, int rowNum, Ps ps, T bean, boolean originalKey) throws DBException {
		return orUtil.rs2Object(rs, bean, originalKey);
	}
	

}
