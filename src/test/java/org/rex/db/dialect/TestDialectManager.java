package org.rex.db.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Test;
import org.rex.db.datasource.ConnectionProperties;
import org.rex.db.datasource.SimpleDataSourceFactory;
import org.rex.db.exception.DBException;

import junit.framework.Assert;

public class TestDialectManager {
	
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
		Assert.assertEquals("MYSQL", dialect.getName());
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
