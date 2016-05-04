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
package org.rex.db.listener;

import java.util.Arrays;

import javax.sql.DataSource;

import org.rex.db.dialect.LimitHandler;
import org.rex.db.util.ConstantUtil;

/**
 * Wraps SQL context.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class SqlContext extends BaseContext{
	
	/** 
	 * Constant utilities.
	 */
	private static final ConstantUtil constants = new ConstantUtil(SqlContext.class);
	
	/**
	 * Event: query.
	 */
	public static final int SQL_QUERY = 1;
	
	/**
	 * Event: update.
	 */
	public static final int SQL_UPDATE = 2;
	
	/**
	 * Event: batch update.
	 */
	public static final int SQL_BATCH_UPDATE = 3;
	
	/**
	 * Event: call.
	 */
	public static final int SQL_CALL = 4;
	
	private int sqlType;
	
	private boolean betweenTransaction;
	
	private DataSource dataSource;
	
	private String[] sql;
	
	private Object parameters;
	
	private LimitHandler limitHandler;
	
	public SqlContext(int sqlType, boolean betweenTransaction, DataSource dataSource, String[] sql, Object parameters, LimitHandler limitHandler){
		super();
		this.sqlType = sqlType;
		this.betweenTransaction = betweenTransaction;
		this.dataSource = dataSource;
		this.sql = sql;
		this.parameters = parameters;
		this.limitHandler = limitHandler;
	}

	public int getSqlType() {
		return sqlType;
	}

	public boolean isBetweenTransaction() {
		return betweenTransaction;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public String[] getSql() {
		return sql;
	}

	public Object getParameters() {
		return parameters;
	}
	
	public LimitHandler getLimitHandler() {
		return limitHandler;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("sqlType=")
			.append(constants.toCode(new Integer(sqlType), "SQL"))
			.append(", betweenTransaction=")
			.append(betweenTransaction)
			.append(", dataSource=")
			.append(dataSource)
			.append(", sql=")
			.append(Arrays.toString(sql))
			.append(", parameters=")
			.append(parameters);
		
		return sb.toString();
	}
	
}
