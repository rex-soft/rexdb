package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;

public class DefaultResultSetIterator implements ResultSetIterator {

	/**
	 * 遍历结果集
	 */
	public void read(ResultReader resultReader, ResultSet rs) throws DBException {
		try {
			while (rs.next()) {
				resultReader.processRow(rs);
			}
		} catch (SQLException e) {
			throw new DBException("DB-C0002", e, e.getMessage());
		}
	}
}