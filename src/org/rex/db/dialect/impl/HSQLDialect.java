package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * HSQL
 */
public class HSQLDialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	public String getLimitSql(String sql, int limit) {
		return getLimitString(sql, false);
	}

	public String getLimitSql(String sql, int offset, int limit) {
		return getLimitString(sql, true);
	}

	public Ps getLimitPs(Ps ps, int limit) {
		if(ps==null) ps=new Ps();
		return ps.add(limit);
	}

	public Ps getLimitPs(Ps ps, int offset, int limit) {
		if(ps==null) ps=new Ps();
		return ps.add(offset).add(limit);
	}

	protected String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer(sql.length() + 10)
				.append(sql)
				.insert(sql.toLowerCase().indexOf("select") + 6,
						hasOffset ? " limit ? ?" : " top ?").toString();
	}
	
	// ------------------------------------------------------------版本信息
	public String getDialectName() {
		return "HSQLDialect";
	}
}
