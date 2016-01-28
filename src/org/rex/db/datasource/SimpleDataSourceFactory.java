package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;

/**
 * 框架内置的简单数据源工厂，用于创建一个简单数据源
 */
public class SimpleDataSourceFactory extends DataSourceFactory {
	
	public SimpleDataSourceFactory(Properties properties) throws DBException {
		super(properties);
	}

	public DataSource createDataSource() throws DBException{
		return new SimpleDataSource(getProperties());
	}
}
