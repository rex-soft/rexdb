package org.rex.db.sql.analyzer;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * 解析SQL中的参数 
 * XXX:目前存在问题：不能自动排除引号之内的关键字，需要在前面加上\注释掉
 */
public class TokenAnalyzer {

	/**
	 * 参数起始标记
	 */
	private static final String PARAMETER_PREFIX = "#{";
	
	/**
	 * 参数结束标记
	 */
	private static final String PARAMETER_SUFFIX = "}";
	
	/**
	 * SQL
	 */
	private String sql;
	
	/**
	 * Parsed SQL
	 */
	private String parsedSql;
	
	/**
	 * 参数处理接口
	 */
	private StatementSetter statementSetter;

	public TokenAnalyzer(String sql, StatementSetter statementSetter) {
		this.sql = sql;
		this.statementSetter = statementSetter;
	}

	public String parse() throws DBException {
		StringBuilder builder = new StringBuilder();
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

						builder.append(statementSetter.setParameter(content, start));
						offset = end + PARAMETER_SUFFIX.length();
					}
				}
				start = sql.indexOf(PARAMETER_PREFIX, offset);
			}
			if (offset < src.length) {
				builder.append(src, offset, src.length - offset);
			}
		}
		
		parsedSql = builder.toString();
		return parsedSql;
	}
	
	//---getters
	public String getParsedSql(){
		return parsedSql;
	}
	
	public Ps getParsedPs(){
		return statementSetter.getParsedPs();
	}
}
