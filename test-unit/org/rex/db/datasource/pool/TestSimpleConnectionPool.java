package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;
import org.rex.db.datasource.ConnectionProperties;
import org.rex.db.exception.DBException;

import junit.framework.Assert;

public class TestSimpleConnectionPool {
	

	public SimpleConnectionPool initConnectionPool(Properties args) throws DBException {
		
		return new SimpleConnectionPool(ConnectionProperties.getSimpleProperties(args));
	}

	
	@Test
	public void testPool() throws SQLException, DBException{
		
		Properties properties = new Properties(){
			{
				put("initSize", 1);
				put("minSize", 3);
				put("maxSize", 5);
				put("increment", 2);
				put("retries", 1);
				put("retryInterval", 500);
				put("getConnectionTimeout", 1000);
				put("inactiveTimeout", 600000);
				put("maxLifetime", 1200000);
				put("testConnection", true);
				put("testSql", "");
				put("testTimeout", 500);
			}
		};
		
		SimpleConnectionPool pool = initConnectionPool(properties);
		
		Assert.assertEquals(pool.getTotalConnectionsCount(), 1);
		
		Connection conn1 = pool.getConnection();
		Connection conn2 = pool.getConnection();
		
		Assert.assertEquals(pool.getTotalConnectionsCount(), 3);
		Assert.assertEquals(pool.getInactiveConnections(), 1);
		
		Connection conn3 = pool.getConnection();
		Connection conn4 = pool.getConnection();
		Connection conn5 = pool.getConnection();
		
		Assert.assertEquals(pool.getTotalConnectionsCount(), 5);
		Assert.assertEquals(pool.getInactiveConnections(), 0);
		
		conn1.close();
		
		Assert.assertEquals(pool.getTotalConnectionsCount(), 5);
		Assert.assertEquals(pool.getInactiveConnections(), 1);
		
		conn2.close();
		conn3.close();
		conn4.close();
		conn5.close();
		
		Assert.assertEquals(pool.getTotalConnectionsCount(), 5);
		Assert.assertEquals(pool.getInactiveConnections(), 5);
	}
	
	@Test(expected=java.sql.SQLException.class)
	public void testOverflow() throws DBException, SQLException{
		SimpleConnectionPool pool = initConnectionPool( new Properties(){
			{
				put("maxSize", 5);
			}
		});
		
		pool.getConnection();
		pool.getConnection();
		pool.getConnection();
		pool.getConnection();
		pool.getConnection();
		pool.getConnection();
	}
}
