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
			case Types.ROWID: return "ROWID";
			case Types.NCHAR: return "NCHAR";
			case Types.NVARCHAR: return "NVARCHAR";
			case Types.LONGNVARCHAR: return "LONGNVARCHAR";
			case Types.NCLOB: return "NCLOB";
			case Types.SQLXML: return "SQLXML";
			default: return "Unknown";
		}
	}
}
