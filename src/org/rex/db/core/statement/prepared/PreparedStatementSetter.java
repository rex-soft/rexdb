package org.rex.db.core.statement.prepared;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PreparedStatementSetter {
	
	protected String sql;
	
	public PreparedStatementSetter(String sql) {
		this.sql = sql;
	}

	/**
	 * 为PreparedStatement赋值
	 */
	protected void setParam(PreparedStatement ps, int sqlType, Object value, int index) throws SQLException{
		if (value == null) {
			ps.setNull(index, sqlType);
		}
		else {
			switch (sqlType) {
				case Types.VARCHAR : 
					ps.setString(index, (String) value);
					break;

				default : 
					ps.setObject(index, value, sqlType);
					break;
			}
		}
	}

	public String toString() {
		return sql;
	}
}
