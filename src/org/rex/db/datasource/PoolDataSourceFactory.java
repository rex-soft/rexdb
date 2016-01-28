package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.StringUtil;

/**
 * 用于加载第三方数据源的工厂，例如DBCP、C3P0
 */
public class PoolDataSourceFactory extends DataSourceFactory {
	
	public static final String DATA_SOURCE_CLASS = "class";
	
	public PoolDataSourceFactory(Properties properties) throws DBException {
		super(properties);
	}
	
	public DataSource createDataSource() throws DBException {
		Properties properties = (Properties)getProperties().clone();
		
		String dataSourceClazz = properties.getProperty(DATA_SOURCE_CLASS);
		if(StringUtil.isEmptyString(dataSourceClazz))
			throw new DBException("DB-D0003", DATA_SOURCE_CLASS, DataSourceUtil.hiddenPassword(properties));
		
		DataSource dataSource = ReflectUtil.instance(dataSourceClazz, DataSource.class);
		properties.remove(DATA_SOURCE_CLASS);
		ReflectUtil.setProperties(dataSource, properties);
		return dataSource;
	}
}
