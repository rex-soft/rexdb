package org.rex.db.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.datasource.DataSourceManager;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;
import org.rex.db.listener.DBListener;
import org.rex.db.listener.ListenerManager;

public class Configuration {
	
	public Configuration(){
		variables = new Properties();
		dataSourceManager = new DataSourceManager();
		listenerManager = new ListenerManager();
		dialectManager = new DialectManager();
	}
	
	//-----------------------------static: load configuration
	
	private static final String DEFAULT_CONFIG_PATH = "rexdb.xml";
	
	/**
	 * 以单例模式运行
	 */
	private static Configuration instance;
	
	static{
		try {
			loadDefaultConfiguration();
		} catch (DBException e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 获取当前配置
	 */
	public static Configuration getCurrentConfiguration() throws DBException{
		if(instance == null){
			loadDefaultConfiguration();
		}
		
		if(instance == null)
			throw new DBException("DB-C10051");
			
		return instance;
	}
	
	public synchronized static void loadDefaultConfiguration() throws DBException{
		if(instance != null)
			throw new DBException("DB-C10052", DEFAULT_CONFIG_PATH);
		
		instance = new XMLConfigurationLoader().loadFromClasspath(DEFAULT_CONFIG_PATH);
	}
	
	public synchronized static void loadConfigurationFromClasspath(String path) throws DBException{
		if(instance != null)
			throw new DBException("DB-C10052", path);
		instance = new XMLConfigurationLoader().loadFromClasspath(path);
	}
	
	public  synchronized static void loadConfigurationFromFileSystem(String path) throws DBException{
		if(instance != null)
			throw new DBException("DB-C10052", path);
		instance = new XMLConfigurationLoader().loadFromFileSystem(path);
	}

	
	//-----------------------------configuration
	/**
	 * 配置变量
	 */
	private Properties variables;
	
	/**
	 * 语言：zh-cn、en
	 */
	private String lang;
	
	/**
	 * 选用的日志包，可选log4j，log4j2，slf4j，jdk
	 */
	private String logger;
	
	private String loggerFactory;
	
	/**
	 * 数据源
	 */
	private DataSourceManager dataSourceManager;
	
	/**
	 * 监听
	 */
	private ListenerManager listenerManager;
	
	/**
	 * 方言
	 */
	private DialectManager dialectManager;
	
	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public Properties getVariables() {
		return variables;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public String getLoggerFactory() {
		return loggerFactory;
	}

	public void setLoggerFactory(String loggerFactory) {
		this.loggerFactory = loggerFactory;
	}

	public void setDefaultDataSource(DataSource dataSource){
		dataSourceManager.addDefault(dataSource);
	}
	
	public void setDataSource(String id, DataSource dataSource){
		dataSourceManager.add(id, dataSource);
	}

	public DataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}
	
	public void addListener(DBListener listener){
		listenerManager.registe(listener);
	}

	public ListenerManager getListenerManager() {
		return listenerManager;
	}
	
	public void addDialect(DataSource dataSource, Dialect dialect){
		dialectManager.setDialect(dataSource, dialect);;
	}

	public DialectManager getDialectManager() {
		return dialectManager;
	}

	public String toString() {
		return "Configuration [variables=" + variables + ", lang=" + lang + ", dataSourceManager=" + dataSourceManager + ", listenerManager="
				+ listenerManager + ", dialectManager=" + dialectManager + "]";
	}
	
}
