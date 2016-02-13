package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * PostgreSQL
 */
public class SQLServerDialect implements Dialect {

	private static final Logger LOGGER = LoggerFactory.getLogger(SQLServerDialect.class);
	
	// ------------------------------------------------------------分页SQL
	protected class SQLServerLimitHandler extends LimitHandler {

		public SQLServerLimitHandler(int rows) {
			super(rows);
		}

		public SQLServerLimitHandler(int offset, int rows) {
			super(offset, rows);
			throw new DBRuntimeException("DB-A0003", getName());
		}

		public String wrapSql(String sql) {
			if (getOffset() > 0) {
				throw new DBRuntimeException("DB-A0003", getName());
			}
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 12).append(sql).insert(getAfterSelectInsertPoint(sql), " top " + getRows());
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
		}

		// --private
		private int getAfterSelectInsertPoint(String sql) {
			int selectIndex = sql.toLowerCase().indexOf("select");
			final int selectDistinctIndex = sql.toLowerCase().indexOf("select distinct");
			return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
		}
	}

	public LimitHandler getLimitHandler(int rows) {
		return new SQLServerLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		throw new DBRuntimeException("DB-A0003", getName());
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
		return "SQLSERVER";
	}
}
