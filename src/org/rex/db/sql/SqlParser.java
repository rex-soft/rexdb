package org.rex.db.sql;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.sql.parser.SqlTokenParser;
import org.rex.db.sql.parser.TokenHandler;
import org.rex.db.util.JdbcUtil;

/**
 * 处理带有标记的参数
 * XXX: 语法分析性能有待提高
 */
public class SqlParser {
	
	public static final String PREFIX_MARK = "#{";
	public static final String SUFFIX_MARK = "}";

//	//main
//	public static void main(String[] args) {
//		String sql = "select * from country where ? id < #{id} ddd ?";
//		
//		Map m =new HashMap();
//		m.put("id", "111");
//		
//		Ps ps= new Ps();
//		
//		ps.add("111");
//		ps.add("2222");
//		
//		SqlParser parser = new SqlParser();
//		
//		parser.parse(sql, m, ps);
//		
//		System.out.println("SQL====>"+sql);
//		System.out.println("PS====>"+ps);
//	}
	
	/**
	 * 解析SQL语句
	 * @param sql 待解析的SQL语句
	 * @param ps 解析出的参数，注意如果该参数已经有值，在解析出标记后，不会覆盖原有参数，而是根据SQL中已存在的?，插入到相应的位置
	 * @param params 参数所在对象，支持Map、POJO
	 * @return [翻译后的SQL，预编译参数]
	 */
	public static Object[] parse(String sql, Object params){
		return parse(sql, params, null);
	}
	/**
	 * 解析SQL语句
	 * @param sql 待解析的SQL语句
	 * @param ps 解析出的参数，注意如果该参数已经有值，在解析出标记后，不会覆盖原有参数，而是根据SQL中已存在的?，插入到相应的位置
	 * @param params 参数所在对象，支持Map、POJO
	 * @return [翻译后的SQL，预编译参数]
	 */
	public static Object[] parse(String sql, Object params, Ps ps){
		//检查已经设定的预编译参数个数
		if(ps != null && ps.getParameters().size() > 0){
			int holderSize = JdbcUtil.countParameterPlaceholders(sql, '?', '\'');
			if(ps.getParameters().size() != holderSize)
				throw new DBRuntimeException("DB-C10007", sql, holderSize, ps.getParameters().size());
		}
		
		//解析SQL
		ParameterTokenHandler handler = new ParameterTokenHandler(sql, ps, params);
		SqlTokenParser parser = new SqlTokenParser(PREFIX_MARK, SUFFIX_MARK, handler);

		return new Object[]{parser.parse(sql), handler.getPs()};
	}

	/**
	 * 处理SQL中的参数
	 */
	private static class ParameterTokenHandler implements TokenHandler {
		
		private String sql = null;
		private Ps ps = null;
		private Object params = null;
		PropertyDescriptor[] props = null;
		
		/**
		 * 构造函数
		 * @param ps 预编译对象
		 * @param params 参数对象，支持Map、POJO类型
		 */
		public ParameterTokenHandler(String sql, Object params){
			this.sql = sql;
			this.ps = new Ps();
			this.params = params;
		}
		
		/**
		 * 构造函数
		 * @param ps 预编译对象
		 * @param params 参数对象，支持Map、POJO类型
		 */
		public ParameterTokenHandler(String sql, Ps ps, Object params){
			this.sql = sql;
			this.ps = ps == null ? new Ps() : ps;
			this.params = params;
		}

		public String handleToken(String content, String parsedPrefix, int index) {
			int paramsIndex = JdbcUtil.countParameterPlaceholders(parsedPrefix, '?', '\'');
			ps.insert(paramsIndex + 1, getParamValue(content));
			return "?";
		}
		
		/**
		 * 从params参数中取值，查找不到参数时设置为null
		 * @param key 参数名，大小写敏感
		 * @throws DBRuntimeException 
		 */
		protected Object getParamValue(String key){
			if(params instanceof Map){
				return ((Map)params).get(key);
			}else{
				if(props == null){
					BeanInfo bean = null;
					try {
						bean = Introspector.getBeanInfo(params.getClass());
					} catch (IntrospectionException e) {
						throw new DBRuntimeException("DB-S10001", e, sql, params.getClass().getName());
					}
					props = bean.getPropertyDescriptors();
				}
				
				for (int i = 0; i < props.length; i++) {
					if(props[i].getName().equals(key)){
						try {
							Method getMethod = props[i].getReadMethod();
							//参数不为空或没有返回值时，跳过
//							if(getMethod.getParameterTypes().length > 0 || getMethod.getReturnType() == null)
//								continue;
							
							return getMethod.invoke(params, null);
						} catch (IllegalAccessException e) {
							throw new DBRuntimeException("DB-S10002", e, sql, params.getClass().getName(), key);
						} catch (IllegalArgumentException e) {
							throw new DBRuntimeException("DB-S10003", e, sql, params.getClass().getName(), key);
						} catch (InvocationTargetException e) {
							throw new DBRuntimeException("DB-S10004", e, sql, params.getClass().getName(), key);
						}
					}
				}
				//未找到合适的get方法
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

}
