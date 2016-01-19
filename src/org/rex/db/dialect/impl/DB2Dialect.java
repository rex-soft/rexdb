package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * DB2
 */
public class DB2Dialect implements Dialect {

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
		int startOfSelect = sql.toLowerCase().indexOf("select");

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100)
				.append(sql.substring(0, startOfSelect))
				.append("select * from ( select ").append(getRowNumber(sql));

		if (hasDistinct(sql)) {
			pagingSelect.append(" row_.* from ( ")
					.append(sql.substring(startOfSelect)).append(" ) as row_");
		} else {
			pagingSelect.append(sql.substring(startOfSelect + 6));
		}

		pagingSelect.append(" ) as temp_ where rownumber_ ");

		if (hasOffset) {
			pagingSelect.append(">=? and rownumber_ < ? ");
		} else {
			pagingSelect.append("<= ?");
		}

		return pagingSelect.toString();
	}

	private String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
				.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase().indexOf("order by");

		if (orderByIndex > 0 && !hasDistinct(sql)) {
			rownumber.append(sql.substring(orderByIndex));
		}

		rownumber.append(") as rownumber_,");

		return rownumber.toString();
	}

	private boolean hasDistinct(String sql) {
		return sql.toLowerCase().indexOf("select distinct") >= 0;
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "DB2";
	}
}
