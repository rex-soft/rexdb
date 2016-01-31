package org.rex.db.sql.parser;

import org.rex.db.exception.DBException;

/**
 * SQL中参数处理接口
 */
public interface TokenHandler {

	/**
	 * 处理参数
	 * @param content 关键字内容
	 * @param parsedPrefix 已解析出的字符串
	 * @param index 起始字符串位置
	 * @return
	 */
	String handleToken(String content, String parsedPrefix, int index) throws DBException;
}