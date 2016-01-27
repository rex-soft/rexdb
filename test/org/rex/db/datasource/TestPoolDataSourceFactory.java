package org.rex.db.datasource;

import javax.sql.DataSource;

import org.junit.Test;
import org.rex.db.exception.DBException;

public class TestPoolDataSourceFactory {

	@Test
	public void testPoolFactory() throws DBException {
		PoolDataSourceFactory factory = new PoolDataSourceFactory();
		factory.setProperties(ConnectionProperties.getPoolProperties());
		DataSource ds = factory.getDataSource();
	}

}
