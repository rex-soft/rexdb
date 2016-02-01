package org.rex.db.core.statement;

/**
 * 预编译创建器工厂
 */
public interface StatementCreatorFactory {

	public StatementCreator buildStatementCreator();
}
