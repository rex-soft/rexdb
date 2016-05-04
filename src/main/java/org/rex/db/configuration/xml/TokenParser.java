/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.rex.db.configuration.xml;

import java.util.Properties;

import org.rex.db.util.StringUtil;

/**
 * Token parser.
 *
 * @version 1.0, 2016-04-26
 * @since Rexdb-1.0
 */
public class TokenParser {
	
	public static final String OPEN_TOKEN = "${";
	public static final String CLOSE_TOKEN = "}";
	
	private Properties variables;
	
	public TokenParser(Properties variables){
		this.variables = variables;
	}
	
	public void setVariables(Properties variables) {
		this.variables = variables;
	}
	
	public void addVariables(Properties variables) {
		if(this.variables == null) 
			this.variables = variables;
		else
			this.variables.putAll(variables);
	}

	public String parse(String text) {
		if(StringUtil.isEmptyString(text))
			return "";
		
		StringBuilder builder = new StringBuilder();
		StringBuilder token = new StringBuilder();
		
		char[] chars = text.toCharArray();
		int openLen = OPEN_TOKEN.length(), closeLen = CLOSE_TOKEN.length();
		int start = text.indexOf(OPEN_TOKEN), offset = 0;
		while (start > -1) {
			if (start > 0 && chars[start - 1] == '\\') {
				builder.append(chars, offset, start - offset - 1).append(OPEN_TOKEN);
				offset = start + openLen;
				
			} else {
				token.setLength(0);
				builder.append(chars, offset, start - offset);
				offset = start + openLen;
				int end = text.indexOf(CLOSE_TOKEN, offset);
				
				while (end > -1) {
					if (end > offset && chars[end - 1] == '\\') {
						token.append(chars, offset, end - offset - 1).append(CLOSE_TOKEN);
						offset = end + closeLen;
						end = text.indexOf(CLOSE_TOKEN, offset);
					} else {
						token.append(chars, offset, end - offset);
						offset = end + closeLen;
						break;
					}
				}
				if (end == -1) {
					builder.append(chars, start, chars.length - start);
					offset = chars.length;
				} else {
					String key = token.toString();
					if (variables != null && variables.containsKey(key)) {
						builder.append(variables.getProperty(key));
					}
					
					offset = end + closeLen;
				}
			}
			start = text.indexOf(OPEN_TOKEN, offset);
		}
		
		if (offset < chars.length) {
			builder.append(chars, offset, chars.length - offset);
		}
		
		return builder.toString();
	}
}
