package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * MySQL
 */
public class MySQLDialect implements Dialect {


	// ------------------------------------------------------------分页SQL
	public String getLimitSql(String sql, int rows) {
		return getLimitString(sql, false);
	}

	public String getLimitSql(String sql, int offset, int rows) {
		return getLimitString(sql, true);
	}

	public Ps getLimitPs(Ps ps, int rows) {
		if(ps==null) ps=new Ps();
		return ps.add(rows);
	}

	public Ps getLimitPs(Ps ps, int offset, int rows) {
		if(ps==null) ps=new Ps();
		return ps.add(offset).add(rows);
	}

	protected String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer(sql.length() + 20).append(sql)
				.append(hasOffset ? " limit ?, ?" : " limit ?").toString();
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "MySQL";
	}
	
}
