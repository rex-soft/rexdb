package org.rex.db.sql.parser;

import org.rex.db.exception.DBException;

/**
 * 解析SQL中的参数 
 * XXX:目前存在问题：不能自动排除引号之内的关键字，需要在前面加上\注释掉
 */
public class SqlTokenParser {

	/**
	 * 参数起始标记
	 */
	private String openToken;
	/**
	 * 参数结束标记
	 */
	private String closeToken;
	/**
	 * 参数处理接口
	 */
	private TokenHandler handler;

	public SqlTokenParser(String openToken, String closeToken, TokenHandler handler) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		this.handler = handler;
	}

	public String parse(String text) throws DBException {
		StringBuilder builder = new StringBuilder();
		if (text != null && text.length() > 0) {
			char[] src = text.toCharArray();
			int offset = 0;
			int start = text.indexOf(openToken, offset);
			while (start > -1) {
				if (start > 0 && src[start - 1] == '\\') {
					builder.append(src, offset, start - offset - 1).append(openToken);
					offset = start + openToken.length();
				} else {
					int end = text.indexOf(closeToken, start);
					if (end == -1) {
						builder.append(src, offset, src.length - offset);
						offset = src.length;
					} else {
						builder.append(src, offset, start - offset);
						offset = start + openToken.length();
						String content = new String(src, offset, end - offset);

						builder.append(handler.handleToken(content, builder.toString(), start));
						offset = end + closeToken.length();
					}
				}
				start = text.indexOf(openToken, offset);
			}
			if (offset < src.length) {
				builder.append(src, offset, src.length - offset);
			}
		}
		return builder.toString();
	}

}
