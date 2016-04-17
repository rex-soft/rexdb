/**
 * Copyright 2016 the original author or authors.
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
package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class DefaultResultSetIterator implements ResultSetIterator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResultSetIterator.class);

	/**
	 * 遍历结果集
	 */
	public void read(ResultReader resultReader, ResultSet rs) throws DBException {
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("reading ResultSet[{0}].", rs.hashCode());
		
		try {
			while (rs.next()) {
				resultReader.processRow(rs);
			}
		} catch (SQLException e) {
			throw new DBException("DB-C0002", e, e.getMessage());
		}
		
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("finished reading ResultSet[{0}], which has {1} rows.", rs.hashCode(), resultReader.getResults().size());
	}
}