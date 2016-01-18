package org.rex.db.core.statement;

import org.rex.db.core.statement.batch.BatchPreparedStatementCreator;
import org.rex.db.core.statement.batch.BatchStatementCreator;
import org.rex.db.core.statement.batch.DefaultBatchPreparedStatementCreator;
import org.rex.db.core.statement.batch.DefaultBatchStatementCreator;
import org.rex.db.core.statement.callable.CallableStatementCreator;
import org.rex.db.core.statement.callable.DefaultCallableStatementCreator;
import org.rex.db.core.statement.prepared.DefaultPreparedStatementCreator;
import org.rex.db.core.statement.prepared.PreparedStatementCreator;

/**
 * 默认的预编译创建器工厂
 */
public class DefaultStatementCreatorFactory implements StatementCreatorFactory{

	public PreparedStatementCreator newPreparedStatementCreator(String sql) {
		return new DefaultPreparedStatementCreator(sql);
	}

	public BatchPreparedStatementCreator newBatchPreparedStatementCreator(
			String sql) {
		return new DefaultBatchPreparedStatementCreator(sql);
	}

	public CallableStatementCreator newCallableStatementCreator(String sql) {
		return new DefaultCallableStatementCreator(sql);
	}

	public BatchStatementCreator newBatchStatementCreator() {
		return new DefaultBatchStatementCreator();
	}
}
