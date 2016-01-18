package org.rex.db.listener.impl;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.rex.db.Ps;
import org.rex.db.listener.DBListener;
import org.rex.db.listener.SqlContext;
import org.rex.db.listener.TransactionContext;

public class DBPrintSqlListener implements DBListener{
	
	static Logger logger = Logger.getLogger(DBPrintSqlListener.class); 

	public void onExecute(SqlContext context) {
		String[] sql = context.getSql();
		Ps[] ps = context.getPs();
		
		StringBuffer sb = new StringBuffer();
		sb.append("On execute id=")
		.append(context.getContextId())
		.append(", sql=")
		.append(sql.length == 1 ? sql[0] : Arrays.toString(sql))
		.append(", ps=")
		.append(ps==null ? null : Arrays.toString(ps));
		
		logger.info(sb.toString());
//		System.out.println(context.getSql()[0]);
	}

	public void afterExecute(SqlContext context, Object results) {
		String[] sql = context.getSql();
		Ps[] ps = context.getPs();
		
		StringBuffer sb = new StringBuffer();
		sb.append("After execute id=")
		.append(context.getContextId())
		.append(", sql=")
		.append(sql.length == 1 ? sql[0] : Arrays.toString(sql))
		.append(", ps=")
		.append(ps==null ? null : Arrays.toString(ps))
		.append(", reults=")
		.append(results);
		
		logger.info(sb.toString());
	}

	public void onTransaction(TransactionContext context) {
		StringBuffer sb = new StringBuffer();
		sb.append("On transaction id=")
		.append(context.getContextId())
		.append(", ")
		.append(context);
		logger.info(sb.toString());
	}

	public void afterTransaction(TransactionContext context) {
		StringBuffer sb = new StringBuffer();
		sb.append("After transaction id=")
		.append(context.getContextId())
		.append(", ")
		.append(context);
		logger.info(sb.toString());
		
	}

}
