package org.rex.db.sql.analyzer;

import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class MapStatementSetter implements StatementSetter{

	private Ps ps = null;
	private Map<?, ?> bean = null;

	/**
	 * 构造函数
	 * 
	 * @param ps 预编译对象
	 * @param params Map参数对象
	 */
	public MapStatementSetter(String sql, Map<?, ?> bean) {
		this.ps = new Ps();
		this.bean = bean;
	}

	//---implements method
	public String setParameter(String parameterName, int index) throws DBException {
		ps.add(bean.get(parameterName));
		return "?";
	}
	
	public Ps getParsedPs() {
		return ps;
	}

}
