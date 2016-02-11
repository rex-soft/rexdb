package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.LimitHandler;

/**
 * DB2
 */
public class DB2Dialect implements Dialect {

	// ------------------------------------------------------------分页SQL
	protected class DB2LimitHandler extends LimitHandler{

		public DB2LimitHandler(int rows) {
			super(rows);
		}

		public DB2LimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
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

			if (hasOffset()) {
				pagingSelect.append(">=? and rownumber_ < ? ");
			} else {
				pagingSelect.append("<= ?");
			}

			return pagingSelect.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
			if(hasOffset()){
				statement.setInt(parameterCount + 1, getOffset());
				statement.setInt(parameterCount + 2, getRows());
			}else
				statement.setInt(parameterCount + 1, getRows());
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
