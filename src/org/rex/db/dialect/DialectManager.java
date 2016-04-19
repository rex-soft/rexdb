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
package org.rex.db.dialect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Dialect Manager
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class DialectManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DialectManager.class);

	private final Map<String, Dialect> dialectInstances = Collections.synchronizedMap(new HashMap<String, Dialect>());
	
	/**
	 * Sets dialect for a dataSource
	 */
	public void setDialect(DataSource dataSource, Dialect dialect){
		dialectInstances.put(String.valueOf(dataSource.hashCode()), dialect);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("dialect[{0}] for datasource[{1}] registed.", dialect.getName(), dataSource.hashCode());
	}
	
	/**
	 * Returns dialect for the given dataSource
	 */
	public Dialect getDialect(DataSource dataSource) throws DBException {
		String hashCode = String.valueOf(dataSource.hashCode());
		
		synchronized(dialectInstances){
			if (!dialectInstances.containsKey(hashCode)) {
				Dialect dialect = DialectFactory.resolveDialect(dataSource);
				dialectInstances.put(hashCode, dialect);
			}
			return dialectInstances.get(hashCode);
		}
	}
}
