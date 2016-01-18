package org.rex.db.util;

import java.sql.Types;

import org.rex.db.exception.DBRuntimeException;

/**
 * 检查和执行SQL时的通用方法
 */
public class JdbcUtil {
	
//	public static void main(String[] args){
//		String sql="select * from T_X t where t.a = ? and t.b != ?";
//		System.out.println(countParameterPlaceholders(sql, '?', '\''));
//	}
	
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
								throw new DBRuntimeException("DB-C10032", str);
							else {
								index += 1;
							}
						}
						else
							state = stateNormalChar;
					}
					break;
				case stateError :
					throw new DBRuntimeException("DB-C10033", str);
				default :
					throw new RuntimeException("DB-C10034");
			}
			++index;
		}

		return count;
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
			case Types.BIT: return "Types.BIT";
			case Types.BIGINT: return "Types.BIGINT";
			case Types.DECIMAL: return "Types.DECIMAL";
			case Types.DOUBLE: return "Types.DOUBLE";
			case Types.FLOAT: return "Types.FLOAT";
			case Types.INTEGER: return "Types.INTEGER";
			case Types.NUMERIC: return "Types.NUMERIC";
			case Types.REAL: return "Types.REAL";
			case Types.SMALLINT: return "Types.SMALLINT";
			case Types.TINYINT: return "Types.TINYINT";
			case Types.CHAR: return "Types.CHAR";
			case Types.VARCHAR: return "Types.VARCHAR";
			case Types.LONGVARCHAR: return "Types.LONGVARCHAR";
			case Types.DATE: return "Types.DATE";
			case Types.TIME: return "Types.TIME";
			case Types.TIMESTAMP: return "Types.TIMESTAMP";
			case Types.BINARY: return "Types.BINARY";
			case Types.VARBINARY: return "Types.VARBINARY";
			case Types.LONGVARBINARY: return "Types.LONGVARBINARY";
			case Types.NULL: return "Types.NULL";
			case Types.OTHER: return "Types.OTHER";
			case Types.JAVA_OBJECT: return "Types.JAVA_OBJECT";
			case Types.DISTINCT: return "Types.DISTINCT";
			case Types.STRUCT: return "Types.STRUCT";
			case Types.ARRAY: return "Types.ARRAY";
			case Types.BLOB: return "Types.BLOB";
			case Types.CLOB: return "Types.CLOB";
			case Types.REF: return "Types.REF";
			case Types.DATALINK: return "Types.DATALINK";
			case Types.BOOLEAN: return "Types.BOOLEAN";
			case Types.ROWID: return "Types.ROWID";
			case Types.NCHAR: return "Types.NCHAR";
			case Types.NVARCHAR: return "Types.NVARCHAR";
			case Types.LONGNVARCHAR: return "Types.LONGNVARCHAR";
			case Types.NCLOB: return "Types.NCLOB";
			case Types.SQLXML: return "Types.SQLXML";
			default: return "Unknown";
		}
	}
}
