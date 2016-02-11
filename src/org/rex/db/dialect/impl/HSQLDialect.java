package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;

/**
 * HSQL
 */
public class HSQLDialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	protected class HSQLLimitHandler extends LimitHandler {

		public HSQLLimitHandler(int rows) {
			super(rows);
		}

		public HSQLLimitHandler(int offset, int rows) {
			super(offset, rows);
		}

		public String wrapSql(String sql) {
			return new StringBuffer(sql.length() + 10).append(sql)
					.insert(sql.toLowerCase().indexOf("select") + 6, hasOffset() ? " limit ? ?" : " top ?").toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			if (hasOffset()) {
				statement.setInt(parameterCount + 1, getOffset());
				statement.setInt(parameterCount + 2, getRows());
			} else
				statement.setInt(parameterCount + 1, getRows());
		}
	}

	public LimitHandler getLimitHandler(int rows) {
		return new HSQLLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new HSQLLimitHandler(offset, rows);
	}

	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql() {
		return "CALL SESSION_ID()";
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "HSQL";
	}
}
