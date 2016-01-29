package org.rex.db.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 管理已加载的数据源，包括1个默认数据源，以及多个指定ID的数据源
 */
public class DataSourceManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceManager.class);
	
	private volatile DataSource defaultDataSource;

	private final Map<String, DataSource> dataSourses;
	
	public DataSourceManager(){
		dataSourses = new HashMap<String, DataSource>();
	}

	public void setDefault(DataSource dataSource){
		defaultDataSource = dataSource;
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Default datasource[{0}] registed.", dataSource.hashCode());
	}
	
	public void add(String id, DataSource dataSource){
		dataSourses.put(id, dataSource);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("Datasource[{0}] of id {1} registed.", dataSource.hashCode(), id);
	}
	
	public DataSource get(String id){
		return dataSourses.get(id);
	}
	
	public DataSource getDefault(){
		return defaultDataSource;
	}
	
	public boolean has(String id){
		return dataSourses.containsKey(id);
	}
	
	public boolean hasDefault(){
		return defaultDataSource == null;
	}
}
