package org.rex.db.util;

import java.text.MessageFormat;

public class StringUtil {

	/**
	 * 判断是否是空字符串
	 * @param s 字符串
	 */
	public static boolean isEmptyString(String s){
		return s == null || "".equals(s.trim());
	}
	
	/**
	 * 格式化显示字符串
	 * 例如：format("Hello {0}!", "World"); -> Hello World
	 * @param message
	 * @param args
	 * @return
	 */
	public static String format(String message, Object... params) {
		return MessageFormat.format(message, params);
	}
}
