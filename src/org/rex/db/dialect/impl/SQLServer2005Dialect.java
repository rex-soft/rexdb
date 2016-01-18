package org.rex.db.dialect.impl;

import org.rex.db.Ps;

public class SQLServer2005Dialect extends SQLServerDialect {
	
	//------------------------------------------------------------分页SQL
	public String getLimitString(String querySqlString, int offset, int limit) {
		if(offset==0){
			offset=1;
			limit=limit+1;
		}
		StringBuffer pagingBuilder = new StringBuffer();
		String orderby = getOrderByPart(querySqlString);
		String distinctStr = "";

		String loweredString = querySqlString.toLowerCase().trim();
		String sqlPartString = querySqlString.trim();
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
			.append(" TOP 100 PERCENT ROW_NUMBER() OVER (").append(orderby).append(") as __hibernate_row_nr__, ")
			.append(pagingBuilder).append(") ")
			.append(" SELECT * FROM query ")
			.append(" WHERE ")
			.append(" __hibernate_row_nr__ >=").append(offset)
			.append(" AND __hibernate_row_nr__ <").append(limit)
			.append(" ORDER BY __hibernate_row_nr__");

		return result.toString();
	}
	
	public void setLimitParam(Ps ps, boolean hasOffset,  int offset, int limit){
	}

	static String getOrderByPart(String sql) {
		String loweredString = sql.toLowerCase();
		int orderByIndex = loweredString.indexOf("order by");
		if (orderByIndex != -1) {
			return sql.substring(orderByIndex);
		} else {
			return "";
		}
	}
	
	public String getDialectName(){
		return "SQLServer2005Dialect";
	}
}
