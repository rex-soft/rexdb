package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;

/**
 * PostgreSQL
 */
public class PostgreSQLDialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	protected class PostgreSQLLimitHandler extends LimitHandler {

		public PostgreSQLLimitHandler(int rows) {
			super(rows);
		}

		public PostgreSQLLimitHandler(int offset, int rows) {
			super(offset, rows);
		}

		public String wrapSql(String sql) {
			return new StringBuilder(sql.length() + 20).append(sql).append(hasOffset() ? " limit ? offset ?" : " limit ?").toString();
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
		return new PostgreSQLLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new PostgreSQLLimitHandler(offset, rows);
	}

	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql() {
		return "SELECT 1";
	}

	// ------------------------------------------------------------版本信息
	public String getName() {
		return "POSTGRESQL";
	}
}
