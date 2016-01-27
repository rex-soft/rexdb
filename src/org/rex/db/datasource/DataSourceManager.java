package org.rex.db.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.exception.DBRuntimeException;

/**
 * 用于管理已配置的数据源
 */
public class DataSourceManager {
	
	private volatile DataSource defaultDataSource;

	private final Map<String, DataSource> dataSourses;
	
	public DataSourceManager(){
		dataSourses = new HashMap<String, DataSource>();
	}

	public void addDefault(DataSource dataSource){
		if(dataSource == null)
			throw new DBRuntimeException("待增加的默认数据源为空");
		defaultDataSource = dataSource;
	}
	
	public void add(String id, DataSource dataSource){
		if(dataSourses.containsKey(id)){//XXX
		}
		if(dataSource == null)
			throw new DBRuntimeException("待增加的数据源"+dataSource+"为空");
		
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
