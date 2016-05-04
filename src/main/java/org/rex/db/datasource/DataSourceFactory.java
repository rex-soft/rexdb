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

import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.DataSourceUtil;

/**
 * DataSource factory.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public abstract class DataSourceFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceFactory.class);
	
	private final Properties properties;
	
	private volatile DataSource dataSource;
	
	public DataSourceFactory(Properties properties) throws DBException{
		validateProperties(properties);
		this.properties = properties;
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("new datasource factory of properties {0} has been created.", DataSourceUtil.hiddenPassword(properties));
	}
	
	protected void validateProperties(Properties properties) throws DBException{
		if(properties == null || properties.size() == 0)
			throw new DBException("DB-D0001");
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public synchronized DataSource getDataSource() throws DBException {
		if(dataSource == null) {
			dataSource = createDataSource();
			
			LOGGER.info("new datasource[{0}] of datasource factory {1} has been created.", dataSource.hashCode(), DataSourceUtil.hiddenPassword(properties));
		}
		return dataSource;
	}
	
	public abstract DataSource createDataSource() throws DBException;
}
