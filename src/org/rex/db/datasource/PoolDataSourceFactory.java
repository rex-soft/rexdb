package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.StringUtil;

/**
 * 自定义连接池数据源
 */
public class PoolDataSourceFactory implements DataSourceFactory {
	
	private volatile DataSource dataSource;
	
	public static final String DATA_SOURCE_CLASS = "class";
	
	public PoolDataSourceFactory(){
	}
	
	public PoolDataSourceFactory(Properties props) throws DBException{
		setProperties(props);
	}
	
	public synchronized void setProperties(Properties properties) throws DBException{
		String dataSourceClazz = properties.getProperty(DATA_SOURCE_CLASS);
		
		if(StringUtil.isEmptyString(dataSourceClazz))
			throw new DBException("DB-D0003", DATA_SOURCE_CLASS, DataSourceUtil.hiddenPassword(properties));
		
		dataSource = ReflectUtil.instance(dataSourceClazz, DataSource.class);
		
		properties.remove(DATA_SOURCE_CLASS);
		ReflectUtil.setProperties(dataSource, properties);
	}

	public synchronized DataSource getDataSource() {
		return dataSource;
	}
}
