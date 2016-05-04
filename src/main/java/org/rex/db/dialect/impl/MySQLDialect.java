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
package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * MySQL
 * 
 * @version 1.0, 2016-03-28
 * @since Rexdb-1.0
 */
public class MySQLDialect implements Dialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDialect.class);

	// ------------------------------------------------------------
	protected class MySQLLimitHandler extends LimitHandler{

		public MySQLLimitHandler(int rows) {
			super(rows);
		}

		public MySQLLimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 30).append(sql)
					.append(hasOffset() ? " limit ?, ?" : " limit ?");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("setting paged prepared parameters {0}.", hasOffset() ? getOffset()+", "+getRows() : getRows());
			
			if(hasOffset()){
				statement.setInt(parameterCount + 1, getOffset());
				statement.setInt(parameterCount + 2, getRows());
			}else
				statement.setInt(parameterCount + 1, getRows());
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new MySQLLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new MySQLLimitHandler(offset, rows);
	}
	
	// ------------------------------------------------------------
	public String getTestSql(){
		return "SELECT 1";
	}
	
	// ------------------------------------------------------------
	public String getName() {
		return "MYSQL";
	}
	
}
