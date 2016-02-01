package org.rex.db.configuration;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Iterator;
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
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.ResourceUtil;
import org.rex.db.util.StringUtil;

public class XMLConfigParser {

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
		this.configuration.setVariables(props);
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
			parsePropertiesNode(root.evalNode("properties"));
			parseSettingsNode(root.evalNode("settings"));
			configuration.applySettings();
			
			parseDataSource(root.evalNode("dataSource"));
			parseListener(root.evalNode("listener"));
	}

	/**
	 * 解析properties节点
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
	private void parseSettingsNode(XNode context) throws DBException {
		if (context == null)
			return;
		
		Properties props = context.getChildrenAsProperties();
		Map<String, Method> writers = ReflectUtil.getWriteableMethods(Configuration.class);
		for (Iterator<?> iterator = props.keySet().iterator(); iterator.hasNext();) {
			String key = String.valueOf(iterator.next());
			if(!writers.containsKey(key)){
				throw new DBException("DB-F0005", "settings", key);
			}
		}

		ReflectUtil.setProperties(configuration, props, true);
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
			factory = new JndiDataSourceFactory(props);
		}else if (hasClass){// 自定指定数据源
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
	private void parseListener(XNode context) throws DBException {
		if (context == null)
			return;

		String clazz = context.getStringAttribute("class");
		Properties props = context.getChildrenAsProperties();
		if (StringUtil.isEmptyString(clazz)) {
			throw new DBException("DB-F0006", "listener", "class");
		}
		
		DBListener listener = ReflectUtil.instance(clazz, DBListener.class);
		ReflectUtil.setProperties(listener, props, true);

		configuration.addListener(listener);
	}

}
