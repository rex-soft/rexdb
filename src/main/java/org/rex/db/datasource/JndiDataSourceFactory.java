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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.StringUtil;

/**
 * JNDI DataSource Factory.
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public class JndiDataSourceFactory extends DataSourceFactory {

	public static final String INITIAL_CONTEXT = "context";
	public static final String JNDI_NAME = "jndi";

	public JndiDataSourceFactory(Properties properties) throws DBException {
		super(properties);
	}
	
	public DataSource createDataSource() throws DBException {
		Properties properties = getProperties();
				
		String jndiName = properties.getProperty(JNDI_NAME), initialContext = properties.getProperty(INITIAL_CONTEXT);
		if(StringUtil.isEmptyString(jndiName))
			throw new DBException("DB-D0004", JNDI_NAME, DataSourceUtil.hiddenPassword(properties));
		
		InitialContext initCtx = null;
		try {
			DataSource dataSource;
			initCtx = new InitialContext(properties);
			if (!StringUtil.isEmptyString(initialContext)) {
				Context ctx = (Context) initCtx.lookup(initialContext);
				dataSource = (DataSource) ctx.lookup(jndiName);
			} else {
				dataSource = (DataSource) initCtx.lookup(jndiName);
			}
			
			return dataSource;
		} catch (NamingException e) {
			throw new DBException("DB-D0005", e, e.getMessage(), properties);
		}finally {
			try {
				if (initCtx != null)
					initCtx.close();
			}catch (NamingException ex) {
			}
		}
	}
	
}
