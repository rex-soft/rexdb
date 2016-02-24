package org.rex.db.listener.impl;

import org.rex.db.listener.SqlContext;
import org.rex.db.listener.TransactionContext;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 框架内置的监听，用于监控所有数据库操作，并直接使用System.out.println打印
 */
public class SqlConsolePrinterListener extends SqlDebugListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlConsolePrinterListener.class);
	
	public void setLevel(String level) {
		LOGGER.warn("SqlConsolePrinterListener dose not support property {0}.", level);
	}

	public void onExecute(SqlContext context) {
		if(!simple)
			System.out.println("On execute " + serializeSqlContext(context));
	}

	public void afterExecute(SqlContext context, Object results) {
		System.out.println("After execute " + serializeSqlContext(context, results));
	}

	public void onTransaction(TransactionContext context) {
		if(!simple)
			System.out.println("On transaction " + serializeTransactionContext(context));
	}

	public void afterTransaction(TransactionContext context) {
		System.out.println("After transaction " + serializeTransactionContext(context));
	}
}
