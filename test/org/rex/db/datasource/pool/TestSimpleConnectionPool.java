package org.rex.db.datasource.pool;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class TestSimpleConnectionPool {

	@Test
	public void testSimpleConnectionPool() throws SQLException {
		
		Properties properties = new Properties();
		properties.put("driverClassName", "com.mysql.jdbc.Driver");
		properties.put("url", "jdbc:mysql://localhost:3306/rexdb1");
		properties.put("username", "root");
		properties.put("password", "12345678");
		
		SimpleConnectionPool pool = new SimpleConnectionPool(properties);
	}

}
