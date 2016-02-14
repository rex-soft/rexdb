package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * MySQL
 */
public class MySQLDialect implements Dialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDialect.class);

	// ------------------------------------------------------------分页SQL
	protected class MySQLLimitHandler extends LimitHandler{

		public MySQLLimitHandler(int rows) {
			super(rows);
		}

		public MySQLLimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
			StringBuffer pagingSelect = new StringBuffer(sql.length() + 30).append(sql)
					.append(hasOffset() ? " limit ?, ?" : " limit ?");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("setting paged prepared parameters {0}.", hasOffset() ? getOffset()+", "+getRows() : getRows());
			
			if(hasOffset()){
				statement.setInt(parameterCount + 1, getOffset());
				statement.setInt(parameterCount + 2, getRows());
			}else
				statement.setInt(parameterCount + 1, getRows());
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new MySQLLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new MySQLLimitHandler(offset, rows);
	}
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql(){
		return "SELECT 1";
	}
	
	// ------------------------------------------------------------版本信息
	public String getName() {
		return "MYSQL";
	}
	
}
