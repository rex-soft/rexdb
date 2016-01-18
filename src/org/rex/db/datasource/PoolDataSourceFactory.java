package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.ReflectUtil;

/**
 * 自定义连接池数据源
 */
public class PoolDataSourceFactory implements DataSourceFactory {
	
	private DataSource dataSource;
	
	public static final String DATA_SOURCE = "class";
	
	public PoolDataSourceFactory(){
	}
	
	public PoolDataSourceFactory(Properties props) throws DBException{
		setProperties(props);
	}
	
	public void setProperties(Properties props) throws DBException{
		String dataSourceClazz = props.getProperty(DATA_SOURCE);
		try {
			dataSource = (DataSource)Class.forName(dataSourceClazz).newInstance();
		} catch (Exception e) {
			throw new DBException("无法创建数据源实例");
		}
		
		ReflectUtil.setProperties(dataSource, props);
	}

	public DataSource getDataSource() throws Exception {
		return dataSource;
	}
}
