package org.rex.db.listener.impl;

import java.util.Arrays;

import org.rex.db.Ps;
import org.rex.db.listener.DBListener;
import org.rex.db.listener.SqlContext;
import org.rex.db.listener.TransactionContext;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 框架内置的监听，用于监控所有数据库操作，并由LOGGER DEBUG输出
 */
public class SqlDebugListener implements DBListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlDebugListener.class);
	
	private String level = "debug";

	public void setLevel(String level) {
		if("debug".equals(level) || "info".equals(level)){
			this.level = level;
		}else
			LOGGER.warn("Debug level {0} not support for SqlDebugListener, only supports debug or info.", level);
	}

	public void onExecute(SqlContext context) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("On execute " + serializeSqlContext(context));
		}
	}

	public void afterExecute(SqlContext context, Object results) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("After execute " + serializeSqlContext(context, results));
		}
	}

	public void onTransaction(TransactionContext context) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("On transaction " + serializeTransactionContext(context));
		}
	}

	public void afterTransaction(TransactionContext context) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("After transaction " + serializeTransactionContext(context));
		}
	}
	
	//--protected
	protected String serializeSqlContext(SqlContext context){
		return serializeSqlContext(context, null);
	}
	
	protected String serializeSqlContext(SqlContext context, Object results){
		if(context == null)
			return null;
		
		String[] sql = context.getSql();
		Ps[] ps = context.getPs();
		
		StringBuffer sb = new StringBuffer();
		sb.append("sql=")
		.append(sql.length == 1 ? sql[0] : Arrays.toString(sql))
		.append(", ps=")
		.append(ps==null ? null : Arrays.toString(ps));
		if(results != null){
			sb.append(", reults=")
			.append(results);
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
