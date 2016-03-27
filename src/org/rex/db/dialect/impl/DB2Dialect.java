package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * DB2
 */
public class DB2Dialect implements Dialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DB2Dialect.class);

	// ------------------------------------------------------------分页SQL
	protected class DB2LimitHandler extends LimitHandler{

		public DB2LimitHandler(int rows) {
			super(rows);
		}

		public DB2LimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {

			StringBuffer pagingSelect = new StringBuffer();
			
			if (hasOffset()) {
				pagingSelect.append("select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( ")
						.append(sql)
						.append(" fetch first ")
						.append(getOffset() + getRows())
						.append(" rows only ) as inner2_ ) as inner1_ where rownumber_ > ")
						.append(getOffset())
						.append(" order by rownumber_");
			}else
				pagingSelect.append(sql)
					.append(" fetch first ")
					.append(getRows())
					.append(" rows only");
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
		}
		
		//private
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
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new DB2LimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new DB2LimitHandler(offset, rows);
	}
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql(){
		return "SELECT COUNT(*) FROM SYSIBM.SYSTABLES";
	}
	
	// ------------------------------------------------------------版本信息
	public String getName() {
		return "DB2";
	}
}
