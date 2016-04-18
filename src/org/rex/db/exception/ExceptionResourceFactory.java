/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.exception;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.StringUtil;

/**
 * 加载异常配置文件
 */
public class ExceptionResourceFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResourceFactory.class);

	//--异常资源配置文件
	private static final String PROPERTIES_ENCODING = "UTF-8";
			
	//--支持的语言，方便用户设置
	public static final String LANG_ZH_CN = "zh-cn";
	public static final String LANG_EN = "en";
	
	//--异常资源配置文件
	private static final Map<String, String> PROPERTIES = new HashMap<String, String>(){
		{
			put(LANG_ZH_CN, "exception.db.zh-cn.properties");
			put(LANG_EN, "exception.db.en.properties");
		}
	};

	//--当前使用的语言
	private volatile String lang = LANG_EN;
	
	//所有资源
	private final Map<String, ExceptionResource> resources;
	
	//单例
	private static final ExceptionResourceFactory instance;

	
	static {
		instance = new ExceptionResourceFactory();
	}

	public static ExceptionResourceFactory getInstance() {
		return instance;
	}

	protected ExceptionResourceFactory() {
		resources = new HashMap<String, ExceptionResource>();
		init();
	}
	
	/**
	 * 加载配置
	 */
	private void init(){
		for (Iterator iterator = PROPERTIES.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
			try{
				resources.put(entry.getKey(), new ExceptionResource(loadProperties(entry.getValue(), PROPERTIES_ENCODING)));
			}catch(Exception e){
				LOGGER.warn("could not load exception resource {0}, {1}.", entry.getValue(), e.getMessage());
			}
		}
	}
	
	//--------PUBLIC
	/**
	 * 设置异常语言
	 */
	public void setLang(String lang){
		if(!LANG_ZH_CN.equals(lang) && !LANG_EN.equals(lang))
			throw new RuntimeException("Language "+ lang +" not support, " + LANG_ZH_CN + " or " + LANG_EN + " required.");
		
		this.lang = lang;
	}
	
	/**
	 * 获取异常消息
	 */
	public String translate(String code){
		return translate(code, null);
	}
	
	/**
	 * 获取异常消息
	 */
	public String translate(String code, Object... params){
		
		ExceptionResource resource = resources.get(lang);
		String message = resource.getMessage(code);
		
		//对应错误代码
		if(message == null)
			return code;
		else{
			return "(" + code + ") " + StringUtil.format(message, params);
		}
	}
	
	/**
	 * 读取一个配置文件
	 */
	private Properties loadProperties(String path, String encode) {
		Properties props = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(path);
			if (inputStream == null) {
				return null;
			}
			props.load(inputStream);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to locate file " + path, ex);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		
		if(encode != null){
			for (Iterator<?> iterator = props.keySet().iterator(); iterator.hasNext();) {
				String key = (String)iterator.next();
				String value = props.getProperty(key);
				
				try {
					props.put(key, new String(value.getBytes("ISO-8859-1"), encode));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException("Error transforming coding for properties " + path + ", " + e.getMessage());
				}
			}
		}
		
		return props;
	}
}
