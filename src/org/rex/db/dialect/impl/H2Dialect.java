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
 * H2
 */
public class H2Dialect implements Dialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(H2Dialect.class);

	// ------------------------------------------------------------分页SQL
	protected class H2LimitHandler extends LimitHandler {

		public H2LimitHandler(int rows) {
			super(rows);
		}

		public H2LimitHandler(int offset, int rows) {
			super(offset, rows);
		}

		public String wrapSql(String sql) {
			StringBuffer pagingSelect =  new StringBuffer(sql.length() + 20).append(sql).append(hasOffset() ? " limit ? offset ?" : " limit ?");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("setting paged prepared parameters {0}.", hasOffset() ? getRows()+", "+getOffset() : getRows());
			
			if (hasOffset()) {
				statement.setInt(parameterCount + 1, getRows());
				statement.setInt(parameterCount + 2, getOffset());
			} else
				statement.setInt(parameterCount + 1, getRows());
		}
	}

	public LimitHandler getLimitHandler(int rows) {
		return new H2LimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new H2LimitHandler(offset, rows);
	}

	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql() {
		return "SELECT 1";
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "H2";
	}

}
