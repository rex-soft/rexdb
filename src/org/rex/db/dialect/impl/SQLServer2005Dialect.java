package org.rex.db.dialect.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.dialect.LimitHandler;

public class SQLServer2005Dialect extends SQLServerDialect {
	
	//------------------------------------------------------------分页SQL
	protected class SQLServer2005LimitHandler extends LimitHandler{

		public SQLServer2005LimitHandler(int rows) {
			super(rows);
		}

		public SQLServer2005LimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
			int offset = getOffset(), rows = getRows();
			
			if(offset==0){
				offset=1;
				rows=rows+1;
			}
			StringBuffer pagingBuilder = new StringBuffer();
			String orderby = getOrderByPart(sql);
			String distinctStr = "";

			String loweredString = sql.toLowerCase().trim();
			String sqlPartString = sql.trim();
			if (loweredString.trim().startsWith("select")) {
				int index = 6;
				if (loweredString.startsWith("select distinct")) {
					distinctStr = "DISTINCT ";
					index = 15;
				}
				sqlPartString = sqlPartString.substring(index);
			}
			pagingBuilder.append(sqlPartString);

			if (orderby == null || orderby.length() == 0) {
				orderby = "ORDER BY CURRENT_TIMESTAMP";
			}

			StringBuffer result = new StringBuffer();
			result.append("WITH query AS (SELECT ").append(distinctStr)
				.append(" TOP 100 PERCENT ROW_NUMBER() OVER (").append(orderby).append(") as __row_nr__, ")
				.append(pagingBuilder).append(") ")
				.append(" SELECT * FROM query ")
				.append(" WHERE ")
				.append(" __row_nr__ >=").append(offset)
				.append(" AND __row_nr__ <").append(rows)
				.append(" ORDER BY __row_nr__");

			return result.toString();
		}

		public void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException {
		}
		
		//private
		private String getOrderByPart(String sql) {
			String loweredString = sql.toLowerCase();
			int orderByIndex = loweredString.indexOf("order by");
			if (orderByIndex != -1) {
				return sql.substring(orderByIndex);
			} else {
				return "";
			}
		}
	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new SQLServer2005LimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new SQLServer2005LimitHandler(offset, rows);
	}
	
}
