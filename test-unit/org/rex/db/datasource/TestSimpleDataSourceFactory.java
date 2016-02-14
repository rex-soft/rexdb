package org.rex.db.datasource;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Test;
import org.rex.db.exception.DBException;

import junit.framework.Assert;

public class TestSimpleDataSourceFactory {
	
	private DataSource getDataSource(Properties properties) throws DBException{
		SimpleDataSourceFactory sdf = new SimpleDataSourceFactory(properties);
		DataSource ds = sdf.getDataSource();
		return ds;
	}

	@Test
	public void testFactory() throws DBException, SQLException {
		DataSource ds = getDataSource(ConnectionProperties.getSimpleProperties());
		
		Assert.assertNotNull(ds);
	}

	@Test(expected=org.rex.db.exception.DBException.class)
	public void testFaultPrperties() throws DBException, SQLException {
		DataSource ds = getDataSource(null);
	}
	
	@Test(expected=org.rex.db.exception.DBRuntimeException.class)
	public void testUnsupportedMethod() throws DBException, SQLException {
		DataSource ds = getDataSource(ConnectionProperties.getSimpleProperties());
		ds.getConnection("", "");
	}
}
