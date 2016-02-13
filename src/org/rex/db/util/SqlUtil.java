package org.rex.db.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

public class SqlUtil {
	
	private static final String PARAMETER_PREFIX = "#{";
	
	private static final String PARAMETER_SUFFIX = "}";
	
	private static final char PARAMETER = '?';
	
	private static final Map<String, String[]> sqlCache = new HashMap<String, String[]>();
	
	/**
	 * 对SQL执行基本的校验，防止错误查询被发送到数据库。校验不通过时直接抛出异常
	 * @param sql 待校验的SQL语句
	 * @throws DBException 
	 */
	public static void validate(String sql, Ps ps) throws DBException{
		validate(sql, ps == null ? 0 : ps.getParameters().size());
	}
	
	public static void validate(String sql, int expectedParameterSize) throws DBException{
		int holderSize = SqlUtil.countParameterPlaceholders(sql, PARAMETER, '\'');
		if (holderSize != expectedParameterSize)
			throw new DBException("DB-S0001", sql, holderSize, expectedParameterSize);
	}
	
	
	/**
	 * set null for PreparedStatement
	 */
	public static void setNull(PreparedStatement preparedStatement, int index) throws SQLException{
		preparedStatement.setObject(index, null, Types.NULL);
	}
	
	
	/**
	 * set parameters for PreparedStatement
	 */
	public static void setParameter(PreparedStatement preparedStatement, int index, Object value) throws SQLException{
		setParameter(preparedStatement, index, value, getSqlType(value));
	}
	
	/**
	 * set parameters for PreparedStatement
	 */
	public static void setParameter(PreparedStatement preparedStatement, int index, Object value, int sqlType) throws SQLException{
		if (value == null) {
			preparedStatement.setNull(index, sqlType);
		}else {
			switch (sqlType) {
				case Types.VARCHAR : 
					preparedStatement.setString(index, (String) value);
					break;

				default : 
					preparedStatement.setObject(index, value, sqlType);
					break;
			}
		}
	}
	
	/**
	 * parse the sql with tags
	 * @param sql sql to parse
	 * @return string array, array[0] is parsed sql, array[1] until the last element is parsed parameters
	 */
	public static String[] parse(String sql) {
		if(!sqlCache.containsKey(sql)){
			StringBuilder builder = new StringBuilder();
			List<String> all = new ArrayList<String>();
			if (sql != null && sql.length() > 0) {
				char[] src = sql.toCharArray();
				int offset = 0;
				int start = sql.indexOf(PARAMETER_PREFIX, offset);
				while (start > -1) {
					if (start > 0 && src[start - 1] == '\\') {
						builder.append(src, offset, start - offset - 1).append(PARAMETER_PREFIX);
						offset = start + PARAMETER_PREFIX.length();
					} else {
						int end = sql.indexOf(PARAMETER_SUFFIX, start);
						if (end == -1) {
							builder.append(src, offset, src.length - offset);
							offset = src.length;
						} else {
							builder.append(src, offset, start - offset);
							offset = start + PARAMETER_PREFIX.length();
							String content = new String(src, offset, end - offset);
							all.add(content);
							builder.append(PARAMETER);
							offset = end + PARAMETER_SUFFIX.length();
						}
					}
					start = sql.indexOf(PARAMETER_PREFIX, offset);
				}
				if (offset < src.length) {
					builder.append(src, offset, src.length - offset);
				}
			}
			all.add(0, builder.toString());
			String[] parsed = all.toArray(new String[all.size()]);
			sqlCache.put(sql, parsed);
			return parsed;
		}
		return sqlCache.get(sql);
	}


	/**
	 * 检查文本中字符个数
	 * @param str 文本
	 * @param marker 待检查字符串
	 * @param delim 引用符号，在引用符号中的字符串不计入统计
	 * @return
	 */
	public static int countParameterPlaceholders(String str, char marker, char delim) {
		int count = 0;
		if (str == null || "".equals(str) || '\0' == marker || '\0' == delim)
			return count;

		final int stateStart = 0;
		final int stateNormalChar = 1;
		final int stateMarker = 2;
		final int stateInDelim = 3;
		final int stateError = 4;

		int len = str.length();
		int index = 0;
		char ch;
		char lookahead = 0;

		int state = stateStart;
		while (index < len) {
			ch = 0 == index ? str.charAt(0) : index < len - 1 ? lookahead : str.charAt(index);
			lookahead = index < len - 1 ? str.charAt(index + 1) : 0;
			switch (state) {
				case stateStart :
					if (ch == delim)
						state = stateInDelim;
					else if (ch == marker && (index == len - 1 || Character.isWhitespace(str.charAt(index + 1)))) {
						state = stateMarker;
					}
					else
						state = stateNormalChar;
					break;
				case stateNormalChar :
					if (ch == delim) {
						state = stateInDelim;
					}
					else if (index < len - 1 && lookahead == marker) {
						state = stateMarker;
					}
					break;
				case stateMarker :
					++count;
					if (index < len - 1 && !Character.isWhitespace(lookahead) && lookahead != ',' && lookahead != ')')
						state = stateError;
					else
						state = stateNormalChar;
					break;
				case stateInDelim :
					if (index == len - 1)
						state = stateError;
					else if (ch == delim) {
						if (index < len - 1 && delim == lookahead) {
							if (index > len - 2)
								throw new DBRuntimeException("DB-USQ01", str);
							else {
								index += 1;
							}
						}
						else
							state = stateNormalChar;
					}
					break;
				case stateError :
					throw new DBRuntimeException("DB-USQ02", str);
				default :
					throw new DBRuntimeException("DB-USQ03", str);
			}
			++index;
		}

		return count;
	}
	
	/**
	 * 根据java对象类型，获取sql type
	 * 
	 * 1. String - Types.VARCHAR
	 * 2. int|Integer - Types.INTEGER
	 * 3. BigDecimal - Types.NUMERIC
	 * 4. long|Long - Types.BIGINT
	 * 5. float|Float - Types.FLOAT
	 * 6. double|Double - Types.DOUBLE
	 * 7. Date - Types.DATE
	 * 8. Time - Types.TIME

	 */
	public static int getSqlType(Object param){
		int type;
		if(param==null) {
			type = Types.NULL;
		}
		else{
			String paramClassName=param.getClass().getName();//参数类名称
			if("java.lang.String".equals(paramClassName)) 
				type = Types.VARCHAR;
			else if("int".equals(paramClassName) || "java.lang.Integer".equals(paramClassName)) 
				type = Types.INTEGER;
			else if("java.math.BigDecimal".equals(paramClassName)) 
				type = Types.NUMERIC;
			else if("long".equals(paramClassName) || "java.lang.Long".equals(paramClassName)) 
				type = Types.BIGINT;
			else if("float".equals(paramClassName) || "java.lang.Float".equals(paramClassName)) 
				type = Types.FLOAT;
			else if("double".equals(paramClassName) || "java.lang.Double".equals(paramClassName)) 
				type = Types.DOUBLE;
			else if("java.util.Date".equals(paramClassName) || "java.sql.Date".equals(paramClassName)) 
				//type = Types.DATE;
				type = Types.TIMESTAMP;
			else if("java.sql.Time".equals(paramClassName)) 
				//type = Types.TIME;
				type = Types.TIMESTAMP;
			else if("java.sql.Timestamp".equals(paramClassName)) 
				type = Types.TIMESTAMP;
			else if("java.sql.Blob".equals(paramClassName)) //不确定BLOB/CLOB是否可用
				type = Types.BLOB;
			else if("java.sql.Clob".equals(paramClassName)) 
				type = Types.CLOB;
			else
				type = Types.VARCHAR;//其余一概作为varchar处理
		}

		return type;
	}
	
	/**
	 * 根据对象数组声明参数<br/>
	 * 1. String - Types.VARCHAR
	 * 2. int|Integer - Types.INTEGER
	 * 3. BigDecimal - Types.NUMERIC
	 * 4. long|Long - Types.BIGINT
	 * 5. float|Float - Types.FLOAT
	 * 6. double|Double - Types.DOUBLE
	 * 7. Date - Types.DATE
	 * 8. Time - Types.TIME
	 * @param params 参数数组，与SQL语句中的‘?’一一对应
	 */
	public static int[] getSqlTypes(Object[] params){
		if(params==null) return null;
		
		int[] paramTypes = new int[params.length];
		for(int i=0;i<params.length;i++){
			paramTypes[i] = getSqlType(params[i]);
		}
		return paramTypes;
	}
	

	/**
	 * 检查SQL参数类型是否是数字
	 */
	public static boolean isNumeric(int sqlType) {
		return Types.BIT == sqlType
			|| Types.BIGINT == sqlType
			|| Types.DECIMAL == sqlType
			|| Types.DOUBLE == sqlType
			|| Types.FLOAT == sqlType
			|| Types.INTEGER == sqlType
			|| Types.NUMERIC == sqlType
			|| Types.REAL == sqlType
			|| Types.SMALLINT == sqlType
			|| Types.TINYINT == sqlType;
	}

	/**
	 * 简化SQL参数类型，只使用其中几类
	 */
	public static int translateType(int sqlType) {

		int retType = sqlType;
		if (Types.BIT == sqlType || Types.TINYINT == sqlType || Types.SMALLINT == sqlType || Types.INTEGER == sqlType)
			retType = Types.INTEGER;
		else if (Types.CHAR == sqlType || Types.VARCHAR == sqlType)
			retType = Types.VARCHAR;
		else if (
			Types.DECIMAL == sqlType
				|| Types.DOUBLE == sqlType
				|| Types.FLOAT == sqlType
				|| Types.NUMERIC == sqlType
				|| Types.REAL == sqlType)
			retType = Types.NUMERIC;

		return retType;
	}
	
	/**
	 * 根据数值返回SQL类型
	 * @param sqlType
	 * @return
	 */
	public static String getNameByType(int sqlType){
		switch(sqlType){
			case Types.BIT: return "BIT";
			case Types.BIGINT: return "BIGINT";
			case Types.DECIMAL: return "DECIMAL";
			case Types.DOUBLE: return "DOUBLE";
			case Types.FLOAT: return "FLOAT";
			case Types.INTEGER: return "INTEGER";
			case Types.NUMERIC: return "NUMERIC";
			case Types.REAL: return "REAL";
			case Types.SMALLINT: return "SMALLINT";
			case Types.TINYINT: return "TINYINT";
			case Types.CHAR: return "CHAR";
			case Types.VARCHAR: return "VARCHAR";
			case Types.LONGVARCHAR: return "LONGVARCHAR";
			case Types.DATE: return "DATE";
			case Types.TIME: return "TIME";
			case Types.TIMESTAMP: return "TIMESTAMP";
			case Types.BINARY: return "BINARY";
			case Types.VARBINARY: return "VARBINARY";
			case Types.LONGVARBINARY: return "LONGVARBINARY";
			case Types.NULL: return "NULL";
			case Types.OTHER: return "OTHER";
			case Types.JAVA_OBJECT: return "JAVA_OBJECT";
			case Types.DISTINCT: return "DISTINCT";
			case Types.STRUCT: return "STRUCT";
			case Types.ARRAY: return "ARRAY";
			case Types.BLOB: return "BLOB";
			case Types.CLOB: return "CLOB";
			case Types.REF: return "REF";
			case Types.DATALINK: return "DATALINK";
			case Types.BOOLEAN: return "BOOLEAN";
//			case Types.ROWID: return "ROWID";
//			case Types.NCHAR: return "NCHAR";
//			case Types.NVARCHAR: return "NVARCHAR";
//			case Types.LONGNVARCHAR: return "LONGNVARCHAR";
//			case Types.NCLOB: return "NCLOB";
//			case Types.SQLXML: return "SQLXML";
			default: return "Unsupported";
		}
	}
}
