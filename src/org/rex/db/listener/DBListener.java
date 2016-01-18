package org.rex.db.listener;

import java.util.EventListener;

import org.rex.db.transaction.TransactionDefinition;

public interface DBListener extends EventListener{
	
	/**
	 * 执行任意SQL前调用该方法
	 */
	public void onExecute(SqlContext context);
	
	/**
	 * 执行SQL后调用该方法
	 */
	public void afterExecute(SqlContext context, Object results);
	
	/**
	 * 开始事物前调用该方法
	 */
	public void onTransaction(TransactionContext context);
	
	/**
	 * 结束事物后调用该方法
	 */
	public void afterTransaction(TransactionContext context);
}
