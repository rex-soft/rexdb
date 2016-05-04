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
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.StringUtil;

/**
 * Pooled DataSource Factory for DBCP, C3P0 etc.
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public class PoolDataSourceFactory extends DataSourceFactory {
	
	public static final String DATA_SOURCE_CLASS = "class";
	
	public PoolDataSourceFactory(Properties properties) throws DBException {
		super(properties);
	}
	
	public DataSource createDataSource() throws DBException {
		Properties properties = (Properties)getProperties().clone();
		
		String dataSourceClazz = properties.getProperty(DATA_SOURCE_CLASS);
		if(StringUtil.isEmptyString(dataSourceClazz))
			throw new DBException("DB-D0003", DATA_SOURCE_CLASS, DataSourceUtil.hiddenPassword(properties));
		
		DataSource dataSource = ReflectUtil.instance(dataSourceClazz, DataSource.class);
		properties.remove(DATA_SOURCE_CLASS);
		ReflectUtil.setProperties(dataSource, properties);
		return dataSource;
	}
}
