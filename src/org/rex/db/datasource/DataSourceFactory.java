package org.rex.db.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;

public interface DataSourceFactory {
	
	public abstract void setProperties(Properties properties) throws DBException;
	
	public abstract DataSource getDataSource() throws Exception;
}
