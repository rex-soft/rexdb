package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * H2
 */
public class H2Dialect implements Dialect {

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
		return ps.add(limit).add(offset);
	}

	protected String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer(sql.length() + 20).append(sql)
				.append(hasOffset ? " limit ? offset ?" : " limit ?").toString();
	}

	// ------------------------------------------------------------版本信息
	public String getDialectName() {
		return "H2Dialect";
	}
	
}
