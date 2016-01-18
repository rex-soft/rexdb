package org.rex.db.configuration;

import java.io.InputStream;
import java.io.Reader;
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
import org.rex.db.exception.DBRuntimeException;
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
	public Configuration parse() {
		parseConfiguration(parser.evalNode("/configuration"));
		return configuration;
	}

	/**
	 * 解析XML
	 */
	private void parseConfiguration(XNode root) {
		try {
			parsePropertiesNode(root.evalNode("properties"));
			parseSettingsNode(root.evalNode("settings"));
			parseDataSource(root.evalNode("dataSource"));
			parseListener(root.evalNode("listener"));
		} catch (Exception e) {
			throw new DBRuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
		}
	}

	/**
	 * 解析properties节点
	 */
	private void parsePropertiesNode(XNode context) throws Exception {
		if (context == null)
			return;

		Properties properties = context.getChildrenAsProperties();
		String path = context.getStringAttribute("path");
		String url = context.getStringAttribute("url");

		boolean noPath = StringUtil.isEmptyString(path), noUrl = StringUtil.isEmptyString(url);

		if (!noPath && !noUrl)
			throw new DBRuntimeException("DB-C10045", "properties", "path, url");

		if (noPath && noUrl)
			throw new DBException("DB-C10044", "properties", "path, url");
			

		if (noUrl) {
			properties.putAll(ResourceUtil.getResourceAsProperties(path));
		} else {
			properties.putAll(ResourceUtil.getUrlAsProperties(url));
		}

		Properties vars = configuration.getVariables();
		if (vars != null)
			properties.putAll(vars);
		parser.setVariables(properties);
		configuration.setVariables(properties);
	}

	/**
	 * 解析Properties
	 */
	private void parseSettingsNode(XNode context) throws DBException {
		if (context == null)
			return;

		Properties props = context.getChildrenAsProperties();
		for (Object key : props.keySet()) {
			if (!ReflectUtil.hasSetter(Configuration.class, String.valueOf(key))) {
				throw new DBException("DB-C10046", "settings", "key");
			}
		}

		ReflectUtil.setProperties(configuration, props);
	}

	/**
	 * 解析数据源
	 */
	private void parseDataSource(XNode context) throws Exception {
		if (context == null)
			return;

		String id = context.getStringAttribute("id");
		String clazz = context.getStringAttribute("class");
		String jndi = context.getStringAttribute("jndi");
		String dialect = context.getStringAttribute("dialect");
		Properties props = context.getChildrenAsProperties();

		DataSourceFactory factory;

		if (!StringUtil.isEmptyString(jndi) && !StringUtil.isEmptyString(clazz))
			throw new DBException("DB-C10045", "dataSource", "jndi, class");

		// 不指定class和jndi时，使用自带简易数据源
		else if (StringUtil.isEmptyString(clazz) && StringUtil.isEmptyString(jndi)) {
			factory = new SimpleDataSourceFactory();
		}
		// 有jndi参数，使用JNDI
		else if (!StringUtil.isEmptyString(jndi)) {
			factory = new JndiDataSourceFactory();
		}
		// 自定指定数据源
		else {
			factory = new PoolDataSourceFactory();
		}
		factory.setProperties(props);
		DataSource dataSource = factory.getDataSource();

		// 处理方言
		if (!StringUtil.isEmptyString(dialect)) {
			Dialect instance = (Dialect) ReflectUtil.instance(dialect, Dialect.class);
			configuration.addDialect(dataSource, instance);
		}

		// 设置数据源
		if (StringUtil.isEmptyString(id))
			configuration.setDefaultDataSource(dataSource);
		else
			configuration.setDataSource(id, dataSource);
	}

	/**
	 * 解析listener
	 */
	private void parseListener(XNode context) throws DBException {
		if (context == null)
			return;

		String clazz = context.getStringAttribute("class");
		if (StringUtil.isEmptyString(clazz)) {
			throw new DBException("DB-C10047", "listener", "class");
		}

		Class listenerClass = null;
		try {
			listenerClass = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new DBException("DB-C10048", clazz);
		}

		if (!DBListener.class.isAssignableFrom(listenerClass)) {
			throw new DBException("DB-C10049", clazz, DBListener.class.getName());
		}

		DBListener listener = null;
		try {
			listener = (DBListener) listenerClass.newInstance();
		} catch (InstantiationException e) {
			throw new DBException("DB-C10050", e, clazz, e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DBException("DB-C10050", e, clazz, e.getMessage());
		}

		configuration.addListener(listener);
	}

}
