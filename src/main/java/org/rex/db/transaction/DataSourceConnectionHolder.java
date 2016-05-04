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
package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.datasource.ConnectionHolder;

/**
 * DataSource Connection Holder.
 * 
 * @version 1.0, 2016-02-19
 * @since Rexdb-1.0
 */
public class DataSourceConnectionHolder extends ConnectionHolder{
	
	/**
	 * Definition for current transaction.
	 */
	private Definition definition;

	public DataSourceConnectionHolder(Connection connection) {
		super(connection);
	}
	
	public DataSourceConnectionHolder(Connection connection, Definition definition) {
		super(connection);
		this.definition = definition;
	}
	
	public Definition getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	/**
	 * Temporarily stores the previous isolation level.
	 */
	private Integer previousIsolationLevel;

	protected void setPreviousIsolationLevel(Integer previousIsolationLevel) {
		this.previousIsolationLevel = previousIsolationLevel;
	}

	public Integer getPreviousIsolationLevel() {
		return previousIsolationLevel;
	}
}
