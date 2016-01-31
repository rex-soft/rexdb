package org.rex.db.sql.parser;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.SqlUtil;

/**
* 处理SQL中的参数
*/
public class ParameterTokenHandler implements TokenHandler{

	private String sql = null;
	private Ps ps = null;
	private Object params = null;

	/**
	 * 构造函数
	 * 
	 * @param ps 预编译对象
	 * @param params 参数对象，支持Map、POJO类型
	 */
	public ParameterTokenHandler(String sql, Object params) {
		this.sql = sql;
		this.ps = new Ps();
		this.params = params;
	}

	/**
	 * 构造函数
	 * 
	 * @param ps 预编译对象
	 * @param params 参数对象，支持Map、POJO类型
	 */
	public ParameterTokenHandler(String sql, Ps ps, Object params) {
		this.sql = sql;
		this.ps = ps == null ? new Ps() : ps;
		this.params = params;
	}

	public String handleToken(String content, String parsedPrefix, int index) throws DBException {
		int paramsIndex = SqlUtil.countParameterPlaceholders(parsedPrefix, '?', '\'');
		ps.insert(paramsIndex + 1, getParamValue(content));
		return "?";
	}

	/**
	 * 从params参数中取值，查找不到参数时设置为null
	 * 
	 * @param key 参数名，大小写敏感
	 * @throws DBException 
	 */
	protected Object getParamValue(String key) throws DBException {
		if (params instanceof Map) {
			return ((Map<?,?>) params).get(key);
		} else {
			Map<String, Method> readers = ReflectUtil.getReadableParams(params.getClass());
			for (Iterator<?> iterator = readers.keySet().iterator(); iterator.hasNext();) {
				String paramName = (String)iterator.next();
				Method reader = readers.get(paramName);
				if(paramName.equals(key)){
					return ReflectUtil.invokeMethod(params, reader, null);
				}
			}
			return null;
		}
	}

	public Ps getPs() {
		return ps;
	}

	public void setPs(Ps ps) {
		this.ps = ps;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}
}
