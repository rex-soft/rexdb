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
package org.rex.db.listener.impl;

import java.util.Arrays;
import java.util.Calendar;

import org.rex.db.listener.DBListener;
import org.rex.db.listener.SqlContext;
import org.rex.db.listener.TransactionContext;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * SQL Listener for Logging.
 * 
 * @version 1.0, 2016-02-01
 * @since Rexdb-1.0
 */
public class SqlDebugListener implements DBListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlDebugListener.class);
	
	/**
	 * Logging level
	 */
	private String level = "debug";
	
	/**
	 * Print simple log.
	 */
	protected boolean simple = false;

	public void setLevel(String level) {
		if("debug".equals(level) || "info".equals(level)){
			this.level = level;
			LOGGER.debug("SqlDebugListener has switched to {0} mode.", this.level);
		}else{
			LOGGER.warn("SqlDebugListener does not support debug level {0} , choose one from debug or info.", level);
		}
	}

	public void setSimple(boolean simple) {
		this.simple = simple;
		LOGGER.debug("SqlDebugListener has switched to {0} mode.", this.simple ? "simple" : "complete");
	}

	public void onExecute(SqlContext context) {
		if(!simple)
			print("On execute " + serializeSqlContext(context));
	}

	public void afterExecute(SqlContext context, Object results) {
		print("Executed " + serializeSqlContext(context, results));
	}

	public void onTransaction(TransactionContext context) {
		print("On transaction " + serializeTransactionContext(context));
	}

	public void afterTransaction(TransactionContext context) {
		print("After transaction " + serializeTransactionContext(context));
	}
	
	//--protected
	protected void print(String s){
		if("debug".equals(level) && LOGGER.isDebugEnabled()){
			LOGGER.debug(s);
		}else{
			LOGGER.info(s);
		}
	}
	
	protected String serializeSqlContext(SqlContext context){
		return serializeSqlContext(context, null);
	}
	
	protected String serializeSqlContext(SqlContext context, Object results){
		if(context == null)
			return null;
		
		String[] sql = context.getSql();
		Object parameters = context.getParameters();
		
		StringBuffer sb = new StringBuffer();
		if(results != null){
			sb.append("reults=")
			.append(results)
			.append(", costs=")
			.append(Calendar.getInstance().getTimeInMillis() - context.getCreateTime().getTime())
			.append("ms");
		}else{
			sb.append(", sql=")
			.append(sql.length == 1 ? sql[0] : Arrays.toString(sql))
			.append(", parameters=")
			.append(parameters)
			.append(", limitHandler=")
			.append(context.getLimitHandler());
		}
		
		sb.append(", context-id=")
		.append(context.getContextId());
		
		return sb.toString();
	}
	
	protected String serializeTransactionContext(TransactionContext context){
		if(context == null)
			return null;
		
		StringBuffer sb = new StringBuffer();
		sb.append("context=")
		.append(context)
		.append(", context-id=")
		.append(context.getContextId());
		
		return sb.toString();
	}
}
