package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * PostgreSQL
 */
public class PostgreSQLDialect implements Dialect {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSQLDialect.class);
	
	// ------------------------------------------------------------分页SQL
	protected class PostgreSQLLimitHandler extends LimitHandler {

		public PostgreSQLLimitHandler(int rows) {
			super(rows);
		}

		public PostgreSQLLimitHandler(int offset, int rows) {
			super(offset, rows);
		}

		public String wrapSql(String sql) {
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 20).append(sql).append(hasOffset() ? " limit ? offset ?" : " limit ?");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("setting paged prepared parameters {0}.", hasOffset() ? getRows()+", "+getOffset() : getRows());
			
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
