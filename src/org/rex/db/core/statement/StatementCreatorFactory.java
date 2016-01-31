package org.rex.db.core.statement;

/**
 * 预编译创建器工厂
 */
public interface StatementCreatorFactory {

//	/**
//	 * 生成单条SQL预编译创建器
//	 */
//	public PreparedStatementCreator newPreparedStatementCreator(String sql);
//	
//	/**
//	 * 生成预编译批处理创建器
//	 */
//	public BatchPreparedStatementCreator newBatchPreparedStatementCreator(String sql);
//	
//	/**
//	 * 生成批处理创建器
//	 */
//	public BatchStatementCreator newBatchStatementCreator();
//	
//	/**
//	 * 生成调用创建器
//	 */
//	public CallableStatementCreator newCallableStatementCreator(String sql);
	
	public StatementCreator buildStatementCreator();
}
