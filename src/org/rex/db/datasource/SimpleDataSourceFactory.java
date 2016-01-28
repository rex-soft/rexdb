package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;

/**
 * 简易连接池工厂
 */
public class SimpleDataSourceFactory implements DataSourceFactory {

	protected volatile DataSource dataSource;

	public SimpleDataSourceFactory() {
	}
	
	public SimpleDataSourceFactory(Properties properties) throws DBException {
		setProperties(properties);
	}

	public synchronized void setProperties(Properties properties) throws DBException {
		if(properties == null || properties.size() == 0)
			throw new DBException("");
			
		dataSource = new SimpleDataSource(properties);
	}

	public synchronized DataSource getDataSource() {
		return dataSource;
	}

}
