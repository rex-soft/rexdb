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
 * Loads XML configuration.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class XMLConfigurationLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLConfigurationLoader.class);

	/**
	 * Loads configuration from the file system.
	 * 
	 * @param path absolute path.
	 * @return configuration.
	 * @throws DBException could not read or parse the file.
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
	 * Loads configuration from ClassPath.
	 * 
	 * @param path file path from ClassPath.
	 * @return configuration.
	 * @throws DBException could not read or parse the file.
	 */
	public Configuration loadFromClasspath(String path) throws DBException {
		InputStream inputStream = ResourceUtil.getResourceAsStream(path);
		return load(inputStream, null);
	}

	/**
	 * Loads configuration from InputStream.
	 * 
	 * @param inputStream file InputStream.
	 * @return configuration.
	 * @throws DBException could not read or parse the InputStream.
	 */
	public Configuration load(InputStream inputStream) throws DBException {
		return load(inputStream, null);
	}

	protected Configuration load(InputStream inputStream, Properties properties) throws DBException {
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
