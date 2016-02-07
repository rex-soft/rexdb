package org.rex.db.sql.analyzer;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ReflectUtil;

/**
* 处理SQL中的参数
*/
public class ReflectedStatementSetter implements StatementSetter{

	private Ps ps = null;
	private Object bean = null;

	/**
	 * 构造函数
	 * 
	 * @param ps 预编译对象
	 * @param params 参数对象，支持Map、POJO类型
	 */
	public ReflectedStatementSetter(String sql, Object bean) {
		this.ps = new Ps();
		this.bean = bean;
	}

	//---implements method
	public String setParameter(String parameterName, int index) throws DBException {
		ps.add(getParamValue(parameterName));
		return "?";
	}
	
	public Ps getParsedPs() {
		return ps;
	}

	//---private methods
	/**
	 * 从params参数中取值，查找不到参数时设置为null
	 * 
	 * @param key 参数名，大小写敏感
	 * @throws DBException 
	 */
	private Object getParamValue(String key) throws DBException {
		Map<String, Method> readers = ReflectUtil.getReadableMethods(bean.getClass());
		for (Iterator<?> iterator = readers.keySet().iterator(); iterator.hasNext();) {
			String paramName = (String)iterator.next();
			Method reader = readers.get(paramName);
			if(paramName.equals(key)){
				return ReflectUtil.invokeMethod(bean, reader);
			}
		}
		return null;
	}

}
