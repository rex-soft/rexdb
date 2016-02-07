package org.rex.db.sql.analyzer;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * SQL中参数处理接口
 */
public interface StatementSetter {

	/**
	 * 处理参数
	 * @param content 关键字内容
	 * @param parsedPrefix 已解析出的字符串
	 * @param index 起始字符串位置
	 * @return
	 */
	String setParameter(String parameterName, int index) throws DBException;
	
	/**
	 * 获取解析结果
	 * @return
	 * @throws DBException
	 */
	Ps getParsedPs();
}