package org.rex.db.dialect.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class TestAllDialects {
	
	final static Logger LOGGER = LoggerFactory.getLogger(TestAllDialects.class);
	
	static String sql = null;
	static List<Dialect> dialects = new ArrayList<Dialect>();
	
	@Before
	public void init(){
		sql = "select * from test where id like ?";
		dialects.add(new DB2Dialect());
		dialects.add(new DerbyDialect());
		dialects.add(new DMDialect());
		dialects.add(new H2Dialect());
		dialects.add(new HSQLDialect());
		dialects.add(new Oracle8iDialect());
		dialects.add(new Oracle9iDialect());
		dialects.add(new PostgreSQLDialect());
		dialects.add(new SQLServer2005Dialect());
		dialects.add(new SQLServerDialect());
	}
	
	@Test
	public void testAll(){
		for (Dialect dialect : dialects) {
			testGetName(dialect);
			testGetLimitSqlStringInt(dialect);
			testGetLimitSqlStringIntInt(dialect);
			testGetLimitPsPsInt(dialect);
			testGetLimitPsPsIntInt(dialect);
			testGetTestSql(dialect);
		}
	}

	public void testGetLimitSqlStringInt(Dialect dialect) {
		String limitSql = dialect.getLimitSql(sql, 10);
		
		LOGGER.debug(limitSql);
	}

	public void testGetLimitSqlStringIntInt(Dialect dialect) {
		String limitSql = dialect.getLimitSql(sql, 10, 10);
		
		LOGGER.debug(limitSql);
	}

	public void testGetLimitPsPsInt(Dialect dialect) {
		Ps ps = new Ps("%10");
		ps = dialect.getLimitPs(ps, 10);
		
		LOGGER.debug(ps.toString());
	}

	public void testGetLimitPsPsIntInt(Dialect dialect) {
		Ps ps = new Ps("%10");
		ps = dialect.getLimitPs(ps, 10, 10);
		
		LOGGER.debug(ps.toString());
	}

	public void testGetTestSql(Dialect dialect) {
		String testSql = dialect.getTestSql();
		
		LOGGER.debug(testSql);
	}

	public void testGetName(Dialect dialect) {
		String name = dialect.getName();
		
		LOGGER.debug(name);
	}

}
