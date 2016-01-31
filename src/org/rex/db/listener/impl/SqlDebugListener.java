package org.rex.db.listener.impl;

import java.util.Arrays;
import java.util.Calendar;

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
	
	//日志输出级别
	private String level = "debug";
	
	//输出精简的日志
	private boolean simple = false;

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
		Ps[] ps = context.getPs();
		
		StringBuffer sb = new StringBuffer();
		sb.append("sql=")
		.append(sql.length == 1 ? sql[0] : Arrays.toString(sql))
		.append(", ps=")
		.append(ps==null ? null : Arrays.toString(ps));
		if(results != null){
			sb.append(", reults=")
			.append(results)
			.append(", costs=")
			.append(Calendar.getInstance().getTimeInMillis() - context.getCreateTime().getTime())
			.append("ms");
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
