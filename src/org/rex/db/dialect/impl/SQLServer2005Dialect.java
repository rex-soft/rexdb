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

import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class SQLServer2005Dialect extends SQLServerDialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SQLServer2005Dialect.class);
	
	//------------------------------------------------------------分页SQL
	protected class SQLServer2005LimitHandler extends LimitHandler{

		public SQLServer2005LimitHandler(int rows) {
			super(rows);
		}

		public SQLServer2005LimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
			int offset = getOffset(), rows = getRows();
			
			if(offset==0){
				offset=1;
				rows=rows+1;
			}
			StringBuffer pagingBuilder = new StringBuffer();
			String orderby = getOrderByPart(sql);
			String distinctStr = "";

			String loweredString = sql.toLowerCase().trim();
			String sqlPartString = sql.trim();
			if (loweredString.trim().startsWith("select")) {
				int index = 6;
				if (loweredString.startsWith("select distinct")) {
					distinctStr = "DISTINCT ";
					index = 15;
				}
				sqlPartString = sqlPartString.substring(index);
			}
			pagingBuilder.append(sqlPartString);

			if (orderby == null || orderby.length() == 0) {
				orderby = "ORDER BY CURRENT_TIMESTAMP";
			}

			StringBuffer result = new StringBuffer();
			result.append("WITH query AS (SELECT ").append(distinctStr)
				.append(" TOP 100 PERCENT ROW_NUMBER() OVER (").append(orderby).append(") as _row_, ")
				.append(pagingBuilder).append(") ")
				.append(" SELECT * FROM query ")
				.append(" WHERE ")
				.append(" _row_ >=").append(offset)
				.append(" AND _row_ <").append(rows + offset)
				.append(" ORDER BY _row_");

			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", result);
			
			return result.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
		}
		
		//private
		private String getOrderByPart(String sql) {
			String loweredString = sql.toLowerCase();
			int orderByIndex = loweredString.indexOf("order by");
			if (orderByIndex != -1) {
				return sql.substring(orderByIndex);
			} else {
				return "";
			}
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new SQLServer2005LimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new SQLServer2005LimitHandler(offset, rows);
	}
	
}
