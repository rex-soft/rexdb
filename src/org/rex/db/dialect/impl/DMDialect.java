package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;

/**
 * 达梦数据库
 */
public class DMDialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	protected class DMLimitHandler extends LimitHandler {

		public DMLimitHandler(int rows) {
			super(rows);
		}

		public DMLimitHandler(int offset, int rows) {
			super(offset, rows);
		}

		public String wrapSql(String sql) {
			sql = sql.trim();
			boolean isForUpdate = false;
			if (sql.toLowerCase().endsWith(" for update")) {
				sql = sql.substring(0, sql.length() - 11);
				isForUpdate = true;
			}

			StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
			if (hasOffset()) {
				pagingSelect
						.append("select * from ( select row_.*, rownum rownum_ from ( ");
			} else {
				pagingSelect.append("select * from ( ");
			}
			pagingSelect.append(sql);
			if (hasOffset()) {
				pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
			} else {
				pagingSelect.append(" ) where rownum <= ?");
			}

			if (isForUpdate) {
				pagingSelect.append(" for update");
			}

			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			if (hasOffset()) {
				statement.setInt(parameterCount + 1, getRows());
				statement.setInt(parameterCount + 2, getOffset());
			} else
				statement.setInt(parameterCount + 1, getRows());
		}
	}

	public LimitHandler getLimitHandler(int rows) {
		return new DMLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new DMLimitHandler(offset, rows);
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
