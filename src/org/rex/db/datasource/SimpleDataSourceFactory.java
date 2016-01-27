package org.rex.db.datasource;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.StringUtil;

/**
 * 简易连接池工厂
 */
public class SimpleDataSourceFactory implements DataSourceFactory {

	private static final String KEY_DRIVER = "driverClassName";
	private static final String KEY_URL = "url";
	private static final String KEY_USERNAME = "username";

	protected DataSource dataSource;

	public SimpleDataSourceFactory() {
	}
	
	public SimpleDataSourceFactory(Properties properties) throws DBException {
		setProperties(properties);
	}

	public void setProperties(Properties properties) throws DBException {
		checkProperties(properties);
		try {
			dataSource = new SimpleDataSource(properties);
		} catch (SQLException e) {
			throw new DBException("DB-C10065", e, e.getMessage());
		}
	}
	
	/**
	 * 检查必要的数据完整性
	 */
	public void checkProperties(Properties properties){
		throwExceptionIfNullProperty(properties.getProperty(KEY_DRIVER));
		throwExceptionIfNullProperty(properties.getProperty(KEY_URL));
		throwExceptionIfNullProperty(properties.getProperty(KEY_USERNAME));
	}
	
	private void throwExceptionIfNullProperty(String value){
		if(StringUtil.isEmptyString(value))
			throw new DBRuntimeException("dataSource property "+KEY_DRIVER+" cannot be null.");
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
