package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.exception.DBException;

/**
 * 事物管理
 */
public interface TransactionManager {

	/**
	 * 返回当前事物状态
	 */
	void begin(TransactionDefinition definition) throws DBException;

	/**
	 * 提交事务
	 */
	void commit() throws DBException;

	/**
	 * 回滚事务
	 */
	void rollback() throws DBException;

	/**
	 * 获取开启事物的连接
	 */
	Connection getTransactionConnection() throws DBException;
}
