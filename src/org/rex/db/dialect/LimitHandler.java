package org.rex.db.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Used to handle database pagination queries
 */
public abstract class LimitHandler{

	private int offset = 0;
	
	private int rows = 0;
	
	public LimitHandler(int rows) {
		this.rows = rows;
	}

	public LimitHandler(int offset, int rows) {
		this.offset = offset;
		this.rows = rows;
	}
	
	public int getOffset() {
		return offset;
	}

	public int getRows() {
		return rows;
	}
	
	public boolean hasOffset(){
		return offset > 0;
	}

	//---------abstracts
	/**
	 * return a SQL with paging parameters
	 * @param sql query SQL
	 * @return wrapped SQL
	 */
	public abstract String wrapSql(String sql);
	
	public abstract void afterSetParameters(PreparedStatement statement, int parameterCount) throws SQLException;

	//---------toString
	public String toString() {
		return "offset=" + offset + ", rows=" + rows;
	}
}