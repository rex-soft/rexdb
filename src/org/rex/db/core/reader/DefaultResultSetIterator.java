package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class DefaultResultSetIterator implements ResultSetIterator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResultSetIterator.class);

	/**
	 * 遍历结果集
	 */
	public void read(ResultReader resultReader, ResultSet rs) throws DBException {
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("reading ResultSet[{0}].", rs.hashCode());
		
		try {
			while (rs.next()) {
				resultReader.processRow(rs);
			}
		} catch (SQLException e) {
			throw new DBException("DB-C0002", e, e.getMessage());
		}
		
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("finished reading ResultSet[{0}], which has {1} rows.", rs.hashCode(), resultReader.getResults().size());
	}
}