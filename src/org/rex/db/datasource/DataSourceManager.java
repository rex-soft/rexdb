/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Manage DataSources, including a default dataSource.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
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
			LOGGER.debug("default datasource[{0}] registed.", dataSource.hashCode());
	}
	
	public void add(String id, DataSource dataSource){
		dataSourses.put(id, dataSource);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("datasource[{0}] {1} registed.", dataSource.hashCode(), id);
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
