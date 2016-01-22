package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;

public interface IConnectionProxy extends Connection
{
    void unclose();

    void __close() throws SQLException;

    void unregisterStatement(Object statement);

    SQLException checkException(SQLException sqle);

    boolean isBrokenConnection();

    long getCreationTime();

    long getLastAccess();

    void markLastAccess();

    void setParentPool(SimpleConnectionPool parentPool);

    Connection getDelegate();

    void captureStack(long leakThreshold, Timer houseKeepingTimer);
}
