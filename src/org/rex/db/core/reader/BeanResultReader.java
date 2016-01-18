package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;

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
		if(rowNum > 0) {
			throw new DBException("DB-Q10022");
		}
		results.add(row2Bean(rs, rowNum++, ps, originalKey));
	}

	public List<T> getResults() {
		return results;
	}

	/**
	 * OR映射
	 */
	protected T row2Bean(ResultSet rs, int rowNum, Ps ps, boolean originalKey) throws DBException {
			try {
				return orUtil.rs2Object(rs, resultBean, originalKey);
			} catch (SQLException e) {
				throw new DBException("DB-Q10010", e);
			}
	}
}
