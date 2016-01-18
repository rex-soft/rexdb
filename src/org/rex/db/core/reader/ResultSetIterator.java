package org.rex.db.core.reader;

import java.sql.ResultSet;

import org.rex.db.exception.DBException;

public interface ResultSetIterator {

	/**
	 * 遍历结果集
	 */
	public void read(ResultReader resultReader, ResultSet rs) throws DBException;
}
