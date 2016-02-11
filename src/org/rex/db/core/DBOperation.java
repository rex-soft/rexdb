package org.rex.db.core;

import javax.sql.DataSource;

import org.rex.db.configuration.Configuration;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;

/**
 * Base DB Operation
 */
public class DBOperation {
	
	private DBTemplate template;
	
	public DBOperation(DataSource dataSource) throws DBException{
		if(dataSource == null)
			throw new DBException("DB-C0008");
		
		template = new DBTemplate(dataSource);
	}
	
	/**
	 * get <tt>DBTemplate</tt> for this instance 
	 * @return DBTemplate
	 */
	protected DBTemplate getTemplate(){
		return template;
	}
	
	/**
	 * get <tt>Dialect</tt> for this instance 
	 * @return Dialect
	 * @throws DBException 
	 */
	protected Dialect getDialect() throws DBException{
		DialectManager manager = Configuration.getCurrentConfiguration().getDialectManager();
		return manager.getDialect(template.getDataSource());
	}
	
//	
//	/**
//	 * get <tt>DBTemplate</tt> for the specified <tt>DataSource</tt> 
//	 * @param dataSourceId the data source configured in the configuration xml.
//	 * @return DBTemplate a new <tt>DBTemplate</tt> for the data source
//	 * @throws DBException couldn't find the data source, or couldn't initialize DB template.
//	 */
//	protected DBTemplate getTemplate(String dataSourceId) throws DBException{
//		return getTemplate(getDataSource(dataSourceId));
//	}
//	
//	/**
//	 * get <tt>DBTemplate</tt> for the specified <tt>DataSource</tt> 
//	 * @param dataSource specified <tt>DataSource</tt> object
//	 * @return DBTemplate a new <tt>DBTemplate</tt> for the data source
//	 * @throws DBException couldn't initialize DB template.
//	 */
//	protected DBTemplate getTemplate(DataSource dataSource) throws DBException{
//		return new DBTemplate(dataSource);
//	}
//	
//	//---------DataSources and Dialects
//	private static DataSourceManager getDataSourceManager() throws DBException{
//		return Configuration.getCurrentConfiguration().getDataSourceManager();
//	}
//	
//	protected static DataSource getDataSource() throws DBException {
//		DataSource defaultDataSource = getDataSourceManager().getDefault();
//		if(defaultDataSource == null)
//			throw new DBException("DB-00002");
//		return defaultDataSource;
//	}
//	
//	protected static DataSource getDataSource(String dataSourceId) throws DBException {
//		DataSource dataSource = getDataSourceManager().get(dataSourceId);
//		if(dataSource == null)
//			throw new DBException("DB-00003", dataSourceId);
//		return dataSource;
//	}
	
//	private static DialectManager getDialectManager() throws DBException{
//		return Configuration.getCurrentConfiguration().getDialectManager();
//	}
}
