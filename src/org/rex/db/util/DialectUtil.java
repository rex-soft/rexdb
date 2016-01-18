package org.rex.db.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialectUtil {

	/**
	 * 替换字符串（仅替换一次）
	 * @param template 原字符串
	 * @param placeholder 要替换的字符
	 * @param replacement 新字符
	 * @return 新字符串
	 */
	public static String replaceOnce(String template, String placeholder, String replacement) {
		if ( template == null ) {
			return template;
		}
        int loc = template.indexOf( placeholder );
		if ( loc < 0 ) {
			return template;
		}
		else {
			return new StringBuffer( template.substring( 0, loc ) )
					.append( replacement )
					.append( template.substring( loc + placeholder.length() ) )
					.toString();
		}
	}
	
	/**
	 * 分割SQL函数中的参数（注意当参数中出现了转意的单引号(')、逗号（,）时，将会截取错误），示例：
	 * 		<code>100,'$1,563',50 --> 100、'$1,563'、50<br/>
	 * 		100,'',50 --> 100、''、50</code>
	 * 	
	 * @param params 函数中的参数字符串
	 * @return 参数List
	 * @throws Exception 参数字符串有错误
	 * 
	 * XXX:此方法有问题，未解决参数中有引号、逗号的问题，必须解决
	 */
	public static List splitSQLFunctionParams(String params) {
		
		if(params==null || params.trim().equals(""))
			return new ArrayList();
		
		return Arrays.asList(params.trim().split(","));
	}
}
