package org.rex.db.datasource;

import java.util.Properties;

public class ConnectionProperties {
	
	//----------simple pool
	/**
	 * 获取数据库连接配置
	 */
	public static Properties getSimpleProperties(){
		Properties properties = new Properties();
		properties.put("driverClassName","com.mysql.jdbc.Driver");
		properties.put("url","jdbc:mysql://localhost:3306/test_db");
		properties.put("username","root");
		properties.put("password","12345678");
		return properties;
	}

	/**
	 * 获取数据库连接配置
	 */
	public static Properties getSimpleProperties(Properties args){
		Properties properties = getSimpleProperties();
		if(args!=null) properties.putAll(args);
		return properties;
	}
	
	//-----------pool
	public static Properties getPoolProperties(){
		Properties properties = getSimpleProperties();
		properties.put("class","org.apache.commons.dbcp.BasicDataSource");
		return properties;
	}
}
