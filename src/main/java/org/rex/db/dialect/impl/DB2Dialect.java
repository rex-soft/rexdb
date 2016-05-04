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
 * DB2
 * 
 * @version 1.0, 2016-03-28
 * @since Rexdb-1.0
 */
public class DB2Dialect implements Dialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DB2Dialect.class);

	// ------------------------------------------------------------
	protected class DB2LimitHandler extends LimitHandler{

		public DB2LimitHandler(int rows) {
			super(rows);
		}

		public DB2LimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {

			StringBuffer pagingSelect = new StringBuffer();
			
			if (hasOffset()) {
				pagingSelect.append("select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( ")
						.append(sql)
						.append(" fetch first ")
						.append(getOffset() + getRows())
						.append(" rows only ) as inner2_ ) as inner1_ where rownumber_ > ")
						.append(getOffset())
						.append(" order by rownumber_");
			}else
				pagingSelect.append(sql)
					.append(" fetch first ")
					.append(getRows())
					.append(" rows only");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
		}
		
		//private
		private String getRowNumber(String sql) {
			StringBuffer rownumber = new StringBuffer(50)
					.append("rownumber() over(");

			int orderByIndex = sql.toLowerCase().indexOf("order by");

			if (orderByIndex > 0 && !hasDistinct(sql)) {
				rownumber.append(sql.substring(orderByIndex));
			}

			rownumber.append(") as rownumber_,");

			return rownumber.toString();
		}
		
		private boolean hasDistinct(String sql) {
			return sql.toLowerCase().indexOf("select distinct") >= 0;
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new DB2LimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new DB2LimitHandler(offset, rows);
	}
	
	// ------------------------------------------------------------
	public String getTestSql(){
		return "SELECT COUNT(*) FROM SYSIBM.SYSTABLES";
	}
	
	// ------------------------------------------------------------
	public String getName() {
		return "DB2";
	}
}
