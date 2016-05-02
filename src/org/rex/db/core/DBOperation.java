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
package org.rex.db.core;

import javax.sql.DataSource;

import org.rex.db.configuration.Configuration;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;

/**
 * Basic DB Operations
 * 
 * @version 1.0, 2016-02-12
 * @since Rexdb-1.0
 */
public class DBOperation {
	
	private DBTemplate template;
	
	public DBOperation(DataSource dataSource) throws DBException{
		if(dataSource == null)
			throw new DBException("DB-C0008");
		
		template = new DBTemplate(dataSource);
	}
	
	protected DBTemplate getTemplate(){
		return template;
	}
	
	protected Dialect getDialect() throws DBException{
		DialectManager manager = Configuration.getCurrentConfiguration().getDialectManager();
		return manager.getDialect(template.getDataSource());
	}
	
}
