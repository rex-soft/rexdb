package org.rex.db.core.statement;

/**
 * 默认的预编译创建器工厂
 */
public class DefaultStatementCreatorFactory implements StatementCreatorFactory{

	public StatementCreator buildStatementCreator() {
		return new DefaultStatementCreator();
	}

//	public PreparedStatementCreator newPreparedStatementCreator(String sql) {
//		return new DefaultPreparedStatementCreator(sql);
//	}
//
//	public BatchPreparedStatementCreator newBatchPreparedStatementCreator(
//			String sql) {
//		return new DefaultBatchPreparedStatementCreator(sql);
//	}
//
//	public CallableStatementCreator newCallableStatementCreator(String sql) {
//		return new DefaultCallableStatementCreator(sql);
//	}
//
//	public BatchStatementCreator newBatchStatementCreator() {
//		return new DefaultBatchStatementCreator();
//	}
}
