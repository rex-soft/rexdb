package org.rex.db.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rex.db.UsingH2;
import org.rex.db.datasource.ConnectionProperties;
import org.rex.db.datasource.SimpleDataSourceFactory;
import org.rex.db.exception.DBException;

import junit.framework.Assert;

public class TestDialectManager extends UsingH2{
	
    private static Server server;
	
    @BeforeClass
	public static void start() {
		try {
			System.out.println("starting h2 on port 9092...");
			server = Server.createTcpServer(new String[] { "-tcp", "-tcpAllowOthers", "-tcpPort", "9092" }).start();
			System.out.println("stated h2:  " + server.getStatus());
		} catch (SQLException e) {
			System.out.println("error: " + e.toString());

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

    @AfterClass
	public static void stop() {
		if (server != null) {
			System.out.println("closing h2...");
			server.stop();
			System.out.println("h2 closed.");
		}
	}
	
	private DataSource getDataSource() throws DBException, SQLException{
		SimpleDataSourceFactory sdf = new SimpleDataSourceFactory(ConnectionProperties.getPoolProperties());
		return sdf.getDataSource();
	}
	
	private Connection getConnection() throws DBException, SQLException{
		return getDataSource().getConnection();
	}

	private Dialect getDialect() throws DBException, SQLException{
		return DialectFactory.resolveDialect(getConnection());
	}
	
	@Test
	public void testVersion() throws DBException, SQLException {
		Dialect dialect = getDialect();
		Assert.assertEquals("H2", dialect.getName());
	}

	@Test
	public void testTestSql() throws DBException, SQLException {
		Dialect dialect = getDialect();

		Connection conn = null;
		Statement st = null;
		try{
			conn = getConnection();
			st = conn.createStatement();
			st.executeQuery(dialect.getTestSql());
		}finally{
			st.close();
			conn.close();
		}
	}

}
