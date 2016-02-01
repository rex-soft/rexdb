package org.rex.db.core.statement;

/**
 * 默认的预编译创建器工厂
 */
public class DefaultStatementCreatorFactory implements StatementCreatorFactory{

	public StatementCreator buildStatementCreator() {
		return new DefaultStatementCreator();
	}
}
