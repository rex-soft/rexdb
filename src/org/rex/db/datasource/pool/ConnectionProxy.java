package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 用于包装数据库连接，供框架内置的简单连接池使用
 * 最主要的作用是覆盖了close方法，在程序调用该接口时并不真正关闭数据库连接，而是交还回连接池中
 */
public interface ConnectionProxy extends Connection {

	void unclose();

	void closeConnection() throws SQLException;

	boolean isForceClosed();

	long getCreationTime();

	long getLastAccess();

	void markLastAccess();

	void setConnectionPool(SimpleConnectionPool parentPool);
}
