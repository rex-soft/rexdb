package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Oracle8i
 * 
 * 不支持left join on语法
 * 联合USER_TABLES的原因是表回收站开启情形下表删除后在USER_CONSTRAINTS还存在（BIN$开头），其它方法联合也是这个原因
 */
public class Oracle8iDialect implements Dialect {

	private static final Logger LOGGER = LoggerFactory.getLogger(Oracle8iDialect.class);
	
	// ------------------------------------------------------------分页SQL
	protected class OracleLimitHandler extends LimitHandler{

		public OracleLimitHandler(int rows) {
			super(rows);
		}

		public OracleLimitHandler(int offset, int rows) {
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

			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("setting paged prepared parameters {0}.", hasOffset() ? getRows()+", "+ (getRows() + getOffset()) : getRows());
			
			if(hasOffset()){
				statement.setInt(parameterCount + 1, getRows() + getOffset());
				statement.setInt(parameterCount + 2, getOffset());
			}else
				statement.setInt(parameterCount + 1, getRows());
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new OracleLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new OracleLimitHandler(offset, rows);
	}
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql(){
		return "SELECT 1 FROM DUAL";
	}
	
	// ------------------------------------------------------------当前方言版本信息
	public String getName() {
		return "ORACLE";
	}

}
