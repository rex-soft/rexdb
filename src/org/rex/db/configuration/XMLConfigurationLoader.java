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
package org.rex.db.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.ResourceUtil;

/**
 * 用于读取框架配置文件
 */
public class XMLConfigurationLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLConfigurationLoader.class);

	/**
	 * 从类路径中加载配置
	 * 
	 * @param path classpath中的路径
	 * @return 已解析的配置
	 * @throws DBException 加载失败时抛出异常
	 */
	public Configuration loadFromFileSystem(String path) throws DBException {
		File xml = new File(path);
		if (!xml.isFile() || !xml.canRead()) 
			throw new DBException("DB-F0001", path);

		try {
			FileInputStream fis = new FileInputStream(xml);
			return load(fis, null);
		} catch (IOException e) {
			throw new DBException("DB-F0002", e, path, e.getMessage());
		}
	}

	/**
	 * 从类路径中加载配置
	 * 
	 * @param path classpath中的路径
	 * @return 已解析的配置
	 * @throws DBException 加载失败时抛出异常
	 */
	public Configuration loadFromClasspath(String path) throws DBException {
		InputStream inputStream = ResourceUtil.getResourceAsStream(path);
		return load(inputStream, null);
	}

	/**
	 * 加载配置
	 * 
	 * @param inputStream 文件流
	 * @return 已解析的配置
	 * @throws DBException 
	 */
	public Configuration load(InputStream inputStream) throws DBException {
		return load(inputStream, null);
	}

	/**
	 * 加载配置
	 * 
	 * @param inputStream 文件流
	 * @param properties 已有配置
	 * @return 已解析的配置
	 * @throws DBException 
	 */
	public Configuration load(InputStream inputStream, Properties properties) throws DBException {
		XMLConfigParser parser = new XMLConfigParser(inputStream, properties);
		Configuration configuration = parser.parse();
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOGGER.warn("could not close input stream of xml configuration, {0}.", e.getMessage());
			}
		}
		return configuration;
	}

}
