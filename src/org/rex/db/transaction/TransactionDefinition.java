package org.rex.db.transaction;

import java.sql.Connection;

/**
 * 事物定义
 */
public interface TransactionDefinition {

	String ISOLATION_CONSTANT_PREFIX = "ISOLATION";

	// -------------------------------事物隔离级别
	/**
	 * 用底层数据库的默认隔离级别
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * 在没有提交数据时能够读到已经更新的数据
	 */
	int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

	/**
	 * 在一个事务中进行查询时，允许读取提交前的数据，数据提交后，当前查询就可以读取到数据。update数据时候并不锁住表
	 */
	int ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;

	/**
	 * 在一个事务中进行查询时，不允许读取其他事务update的数据，允许读取到其他事务提交的新增数据
	 */
	int ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;

	/**
	 * 在一个事务中进行查询时，不允许任何对这个查询表的数据修改。
	 */
	int ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;

	// --------------------default
	/**
	 * 使用事物默认超时时间
	 */
	int TIMEOUT_DEFAULT = -1;

	// --------------------getters
	/**
	 * 返回事物隔离级别
	 */
	int getIsolationLevel();

	/**
	 * 返回事物超时时间
	 */
	int getTimeout();

	/**
	 * 返回是否是只读事务
	 */
	boolean isReadOnly();

	/**
	 * 提交失败时是否自动回滚
	 */
	boolean isAutoRollback();
}
