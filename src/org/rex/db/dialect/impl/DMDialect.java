package org.rex.db.dialect.impl;

import org.rex.db.Ps;
import org.rex.db.dialect.Dialect;

/**
 * 达梦数据库
 * 注意：USER_TABLES，DBA_TABLES在表数量大时速度极慢，无法读取数据
 */
public class DMDialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	// 获取分页语句
	protected String getLimitString(String sql, boolean hasOffset) {
		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase().endsWith(" for update")) {
			sql = sql.substring(0, sql.length() - 11);
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (hasOffset) {
			pagingSelect
					.append("select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
		} else {
			pagingSelect.append(" ) where rownum <= ?");
		}

		if (isForUpdate) {
			pagingSelect.append(" for update");
		}

		return pagingSelect.toString();
	}
	
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
		return ps.add(rows).add(offset);
	}
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql(){
		return "SELECT 1 FROM DUAL";
	}
	
	// ------------------------------------------------------------版本信息
	public String getName() {
		return "DM";
	}
}
