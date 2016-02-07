package org.rex.db.sql.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.sql.dynamic.Bean2Ps;
import org.rex.db.sql.dynamic.ConvertorManager;

public class DynamicStatementSetter implements StatementSetter{
	
	private Ps ps = null;
	private Object bean = null;
	
	private List<String> parameterNames = new ArrayList<String>();
	
	public DynamicStatementSetter(String sql, Object bean) {
		this.bean = bean;
	}
	
	//---implements
	public String setParameter(String parameterName, int index) throws DBException {
		parameterNames.add(parameterName);
		return "?";
	}

	public Ps getParsedPs() {
		if(ps == null){
			ps = createPs();
		}
		return ps;
	}
	
	//---
	private Ps createPs(){
		Bean2Ps bean2Ps = ConvertorManager.getConvertor(bean.getClass());
		return bean2Ps.toPs(bean, parameterNames.toArray(new String[parameterNames.size()]));
	}
}
