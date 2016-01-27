package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.StringUtil;

/**
 * 自定义连接池数据源
 */
public class PoolDataSourceFactory implements DataSourceFactory {
	
	private volatile DataSource dataSource;
	
	public static final String DATA_SOURCE = "class";
	
	public PoolDataSourceFactory(){
	}
	
	public PoolDataSourceFactory(Properties props) throws DBException{
		setProperties(props);
	}
	
	public synchronized void setProperties(Properties props) throws DBException{
		String dataSourceClazz = props.getProperty(DATA_SOURCE);
		if(StringUtil.isEmptyString(dataSourceClazz))
			throw new DBException("DB-C10063", DATA_SOURCE);
		
		try {
			dataSource = (DataSource)Class.forName(dataSourceClazz).newInstance();
		} catch (Exception e) {
			throw new DBException("DB-C10064", e, e.getMessage());
		}
		
		ReflectUtil.setProperties(dataSource, props);
	}

	public synchronized DataSource getDataSource() {
		return dataSource;
	}
}
