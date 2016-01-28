package org.rex.db.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * 管理已加载的数据源，包括1个默认数据源，以及多个指定ID的数据源
 */
public class DataSourceManager {
	
	private volatile DataSource defaultDataSource;

	private final Map<String, DataSource> dataSourses;
	
	public DataSourceManager(){
		dataSourses = new HashMap<String, DataSource>();
	}

	public void setDefault(DataSource dataSource){
		defaultDataSource = dataSource;
	}
	
	public void add(String id, DataSource dataSource){
		dataSourses.put(id, dataSource);
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
