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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles database pagination queries
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public abstract class LimitHandler{

	private int offset = 0;
	
	private int rows = 0;
	
	public LimitHandler(int rows) {
		this.rows = rows;
	}

	public LimitHandler(int offset, int rows) {
		this.offset = offset;
		this.rows = rows;
	}
	
	public int getOffset() {
		return offset;
	}

	public int getRows() {
		return rows;
	}
	
	public boolean hasOffset(){
		return offset > 0;
	}

	//---------abstracts
	/**
	 * Returns a SQL with paging parameters
	 * @param sql query SQL
	 * @return wrapped SQL
	 */
	public abstract String wrapSql(String sql);
	
	public abstract void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException;

	//---------toString
	public String toString() {
		return "offset=" + offset + ", rows=" + rows;
	}
}