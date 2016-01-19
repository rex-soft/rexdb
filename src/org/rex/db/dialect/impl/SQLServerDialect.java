package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * PostgreSQL
 */
public class SQLServerDialect implements Dialect {


	// ------------------------------------------------------------分页SQL
	public String getLimitSql(String sql, int rows) {
		return getLimitSql(sql, 0, rows);
	}

	public String getLimitSql(String sql, int offset, int rows) {
		if (offset > 0) {
			throw new UnsupportedOperationException("数据库不支持带有偏移的分页查询");
		}
		return new StringBuffer(sql.length() + 8).append(sql)
				.insert(getAfterSelectInsertPoint(sql), " top " + rows)
				.toString();
	}

	private int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf("select");
		final int selectDistinctIndex = sql.toLowerCase().indexOf(
				"select distinct");
		return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
	}

	public Ps getLimitPs(Ps ps, int rows) {
		return ps;
	}

	public Ps getLimitPs(Ps ps, int offset, int rows) {
		if (offset > 0) {
			throw new UnsupportedOperationException("数据库不支持带有偏移的分页查询");
		}

		return getLimitPs(ps, rows);
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "SQLSERVER";
	}
}
