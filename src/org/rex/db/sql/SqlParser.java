package org.rex.db.sql;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.sql.parser.ParameterTokenHandler;
import org.rex.db.sql.parser.SqlTokenParser;
import org.rex.db.util.SqlUtil;

/**
 * 处理带有标记的参数 
 * XXX: SQL分析性能有待提高
 */
public class SqlParser {

	public static final String PARAMETER_PREFIX = "#{";
	public static final String PARAMETER_SUFFIX = "}";

	/**
	 * 解析SQL语句
	 * 
	 * @param sql 待解析的SQL语句
	 * @param ps 解析出的参数，注意如果该参数已经有值，在解析出标记后，不会覆盖原有参数，而是根据SQL中已存在的?，插入到相应的位置
	 * @param params 参数所在对象，支持Map、POJO
	 * @return [翻译后的SQL，预编译参数]
	 * @throws DBException 
	 */
	public static Object[] parse(String sql, Object params) throws DBException {
		return parse(sql, params, null);
	}
	
	/**
	 * 对SQL执行基本的校验，防止错误查询被发送到数据库。校验不通过时直接抛出异常
	 * @param sql 待校验的SQL语句
	 * @throws DBException 
	 */
	public static void validate(String sql, Ps ps) throws DBException{
		// 检查已经设定的预编译参数个数
		int holderSize = SqlUtil.countParameterPlaceholders(sql, '?', '\'');
		int paramSize = ps == null ? 0 : ps.getParameters().size();
		
		if (holderSize != paramSize)
			throw new DBException("DB-S0001", sql, holderSize, paramSize);
	}

	/**
	 * 解析SQL语句
	 * 
	 * @param sql 待解析的SQL语句
	 * @param ps 解析出的参数，注意如果该参数已经有值，在解析出标记后，不会覆盖原有参数，而是根据SQL中已存在的?，插入到相应的位置
	 * @param params 参数所在对象，支持Map、POJO
	 * @return [翻译后的SQL，预编译参数]
	 * @throws DBException 
	 */
	public static Object[] parse(String sql, Object params, Ps ps) throws DBException {
		ParameterTokenHandler handler = new ParameterTokenHandler(sql, ps, params);
		SqlTokenParser parser = new SqlTokenParser(PARAMETER_PREFIX, PARAMETER_SUFFIX, handler);
		return new Object[] { parser.parse(sql), handler.getPs() };
	}

}
