package org.rex.db.core.reader;

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
public class ClassResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Ps ps;
	boolean originalKey;

	private Class<T> resultClass;
	private List<T> results;

	private int rowNum = 0;

	public ClassResultReader(boolean originalKey, Class<T> resultClass) {
		this.results = new LinkedList<T>();
		this.originalKey = originalKey;
		this.resultClass = resultClass;
	}

	/**
	 * 创建结果集读取类，适用于普通查询
	 * 
	 * @param ps 查询参数
	 * @param originalKey 是否按照结果集原始键处理
	 * @param resultPojo
	 */
	public ClassResultReader(Ps ps, boolean originalKey, Class<T> resultClass) {
		this(originalKey, resultClass);
		this.ps = ps;

	}

	public void setPs(Ps ps) {
		this.ps = ps;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		results.add(row2Bean(rs, rowNum++, ps, originalKey));
	}

	public List<T> getResults() {
		return results;
	}

	/**
	 * OR映射
	 */
	protected T row2Bean(ResultSet rs, int rowNum, Ps ps, boolean originalKey) throws DBException {
		if (resultClass == null)
			throw new DBException("DB-C0003");

		T bean = ReflectUtil.instance(resultClass);
		return orUtil.rs2Object(rs, bean, originalKey);
	}

	public static <T> T createInstance(Class<T> cls) {
		T obj = null;
		try {
			obj = cls.newInstance();
		} catch (Exception e) {
			obj = null;
		}
		return obj;
	}
}
