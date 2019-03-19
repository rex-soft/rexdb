package org.rex.db.datasource;

import java.util.Properties;

import org.rex.db.UsingH2;

public class ConnectionProperties extends UsingH2{
	
	//----------simple pool
	/**
	 * test conn
	 */
	public static Properties getSimpleProperties(){
		Properties properties = new Properties();
		properties.put("driverClassName","org.h2.Driver");
		properties.put("url","jdbc:h2:tcp://127.0.0.1/~/rexdb");
		properties.put("username","sa");
		properties.put("password","");
		return properties;
	}

	/**
	 *test props
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
