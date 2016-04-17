/**
 * Copyright 2016 the original author or authors.
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

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.configuration.xml.XMLEntityResolver;
import org.rex.db.configuration.xml.XNode;
import org.rex.db.configuration.xml.XPathParser;
import org.rex.db.datasource.DataSourceFactory;
import org.rex.db.datasource.JndiDataSourceFactory;
import org.rex.db.datasource.PoolDataSourceFactory;
import org.rex.db.datasource.SimpleDataSourceFactory;
import org.rex.db.dialect.Dialect;
import org.rex.db.exception.DBException;
import org.rex.db.listener.DBListener;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.ResourceUtil;
import org.rex.db.util.StringUtil;

public class XMLConfigParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLConfigParser.class);

	private XPathParser parser;
	protected final Configuration configuration;

	// ----------------constructs
	public XMLConfigParser(Reader reader) {
		this(reader, null);
	}

	public XMLConfigParser(Reader reader, Properties props) {
		this(new XPathParser(reader, true, props, new XMLEntityResolver()), props);
	}

	public XMLConfigParser(InputStream inputStream) {
		this(inputStream, null);
	}

	public XMLConfigParser(InputStream inputStream, Properties props) {
		this(new XPathParser(inputStream, true, props, new XMLEntityResolver()), props);
	}

	private XMLConfigParser(XPathParser parser, Properties props) {
		this.configuration = new Configuration();
		if(props != null)
			this.configuration.addVariables(props);
		this.parser = parser;
	}

	/**
	 * 解析配置文件根节点
	 */
	public Configuration parse() throws DBException {
		parseConfiguration(parser.evalNode("/configuration"));
		return configuration;
	}

	/**
	 * 解析XML
	 */
	private void parseConfiguration(XNode root) throws DBException {
			parsePropertiesNodes(root.evalNodes("properties"));
			parseSettingsNodes(root.evalNodes("settings"));
			configuration.applySettings();
			
			parseDataSources(root.evalNodes("dataSource"));
			parseListeners(root.evalNodes("listener"));
	}
	
	/**
	 * 解析所有properties节点
	 * @param context
	 * @throws DBException
	 */
	private void parsePropertiesNodes(List<XNode> nodes) throws DBException {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			try{
				parsePropertiesNode(xNode);
			}catch(Exception e){
				LOGGER.error("could not read configration properties, {0} ignored.", e, e.getMessage());
			}
		}
	}

	/**
	 * 解析一个properties节点
	 */
	private void parsePropertiesNode(XNode context) throws DBException {
		if (context == null)
			return;

		String path = context.getStringAttribute("path");
		String url = context.getStringAttribute("url");
		boolean hasPath = !StringUtil.isEmptyString(path), 
				hasUrl = !StringUtil.isEmptyString(url);

		if (!hasPath && !hasUrl)
			throw new DBException("DB-F0003", "properties", "path, url");
		
		if (hasPath && hasUrl)
			throw new DBException("DB-F0004", "properties", "path, url");
			
		Properties properties = context.getChildrenAsProperties();
		if (hasPath) {
			properties.putAll(ResourceUtil.getResourceAsProperties(path));
		} else {
			properties.putAll(ResourceUtil.getUrlAsProperties(url));
		}
		
		parser.addVariables(properties);
		configuration.addVariables(properties);
	}
	
	/**
	 * 解析Settings节点
	 */
	private void parseSettingsNodes(List<XNode> nodes) throws DBException {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			parseSettingsNode(xNode);
		}
	}

	/**
	 * 解析Settings节点
	 */
	private void parseSettingsNode(XNode context) throws DBException {
		if (context == null)
			return;
		
		Properties props = context.getChildrenAsProperties();
		Map<String, Method> writers = ReflectUtil.getWriteableMethods(Configuration.class);
		for (Iterator<?> iterator = props.keySet().iterator(); iterator.hasNext();) {
			String key = String.valueOf(iterator.next());
			if(!writers.containsKey(key)){
				LOGGER.error("configuration {0} unexpected, property {1} is not supported, ignored.", "settings", key);
//				throw new DBException("DB-F0005", "settings", key);
			}
		}

		ReflectUtil.setProperties(configuration, props, true, true);
	}
	
	/**
	 * 解析dataSource节点
	 */
	private void parseDataSources(List<XNode> nodes) throws DBException  {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			try{
				parseDataSource(xNode);
			}catch(Exception e){
				LOGGER.error("could not load data source, {0} ignored.", e, e.getMessage());
			}
		}
	}
	
	/**
	 * 解析dataSource节点
	 */
	private void parseDataSource(XNode context) throws DBException  {
		if (context == null)
			return;
		
		Properties props = context.getChildrenAsProperties();
		String id = context.getStringAttribute("id"),
			clazz = context.getStringAttribute("class"),
			jndi = context.getStringAttribute("jndi"),
			dialect = context.getStringAttribute("dialect");
		
		boolean hasJndi = !StringUtil.isEmptyString(jndi),
				hasClass = !StringUtil.isEmptyString(clazz);

		if (hasJndi && hasClass)
			throw new DBException("DB-F0004", "dataSource", "jndi, class");

		DataSourceFactory factory;
		if (hasJndi) {// 有jndi参数，使用JNDI
			props.put(JndiDataSourceFactory.JNDI_NAME, jndi);
			factory = new JndiDataSourceFactory(props);
		}else if (hasClass){// 自定指定数据源
			props.put(PoolDataSourceFactory.DATA_SOURCE_CLASS, clazz);
			factory = new PoolDataSourceFactory(props);
		}else{// 不指定class和jndi时，使用自带简易数据源
			factory = new SimpleDataSourceFactory(props);
		}
		
		DataSource dataSource = factory.getDataSource();
		if (!StringUtil.isEmptyString(dialect)) {// 处理方言
			Dialect instance = (Dialect) ReflectUtil.instance(dialect, Dialect.class);
			configuration.addDialect(dataSource, instance);
		}
		
		if (StringUtil.isEmptyString(id))// 设置数据源
			configuration.setDefaultDataSource(dataSource);
		else
			configuration.setDataSource(id, dataSource);
	}

	/**
	 * 解析listener节点
	 */
	private void parseListeners(List<XNode> nodes) throws DBException {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			try{
				parseListener(xNode);
			}catch(Exception e){
				LOGGER.error("could not initialize listener, {0} ignored.", e, e.getMessage());
			}
		}
	}
	
	/**
	 * 解析listener节点
	 */
	private void parseListener(XNode context) throws DBException {
		if (context == null)
			return;

		String clazz = context.getStringAttribute("class");
		Properties props = context.getChildrenAsProperties();
		if (StringUtil.isEmptyString(clazz)) {
			throw new DBException("DB-F0006", "listener", "class");
		}
		
		DBListener listener = ReflectUtil.instance(clazz, DBListener.class);
		ReflectUtil.setProperties(listener, props, true, true);

		configuration.addListener(listener);
	}

}
