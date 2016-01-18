package org.rex.db.util;

public class StringUtil {

	/**
	 * 判断是否是空字符串
	 * @param s 字符串
	 */
	public static boolean isEmptyString(String s){
		return s == null || "".equals(s.trim());
	}
}
