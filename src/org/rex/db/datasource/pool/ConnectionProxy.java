package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProxy extends Connection {

	void unclose();

	void closeConnection() throws SQLException;

	boolean isForceClosed();

	long getCreationTime();

	long getLastAccess();

	void markLastAccess();

	void setConnectionPool(SimpleConnectionPool parentPool);
}
