package org.rex.db.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.rex.db.exception.DBException;
import org.rex.db.util.ResourceUtil;

public class XMLConfigurationLoader {

	/**
	 * 从类路径中加载配置
	 * 
	 * @param path classpath中的路径
	 * @return 已解析的配置
	 * @throws DBException 加载失败时抛出异常
	 */
	public Configuration loadFromFileSystem(String path) throws DBException {
		File xml = new File(path);
		if (!xml.isFile() || !xml.canRead()) {
			throw new DBException("DB-C10042", path);
		}

		try {
			FileInputStream fis = new FileInputStream(xml);
			return load(fis, null);
		} catch (IOException e) {
			throw new DBException("DB-C10043", e, path);
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
		InputStream inputStream;
		try {
			inputStream = ResourceUtil.getResourceAsStream(path);
			if (inputStream == null)
				throw new DBException("DB-C10042", path);

			return load(inputStream, null);
		} catch (IOException e) {
			throw new DBException("DB-C10043", e, path);
		}
	}

	/**
	 * 加载配置
	 * 
	 * @param inputStream 文件流
	 * @return 已解析的配置
	 */
	public Configuration load(InputStream inputStream) {
		return load(inputStream, null);
	}

	/**
	 * 加载配置
	 * 
	 * @param inputStream 文件流
	 * @param properties 已有配置
	 * @return 已解析的配置
	 */
	public Configuration load(InputStream inputStream, Properties properties) {
		XMLConfigParser parser = new XMLConfigParser(inputStream, properties);
		Configuration configuration = parser.parse();
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
		return configuration;
	}

}
