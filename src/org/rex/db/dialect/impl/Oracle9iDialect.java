package org.rex.db.dialect.impl;

import org.rex.db.dialect.LimitHandler;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Oracle9i
 */
public class Oracle9iDialect extends Oracle8iDialect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Oracle9iDialect.class);
	
	//------------------------------------------------------------分页SQL
	protected class Oracle9iLimitHandler extends OracleLimitHandler{

		public Oracle9iLimitHandler(int rows) {
			super(rows);
		}

		public Oracle9iLimitHandler(int offset, int rows) {
			super(offset, rows);
		}
		
		public String wrapSql(String sql) {
			sql = sql.trim();
			String forUpdateClause = null;
			boolean isForUpdate = false;
			final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update" );
			if (forUpdateIndex > -1) {
				forUpdateClause = sql.substring( forUpdateIndex );
				sql = sql.substring( 0, forUpdateIndex - 1 );
				isForUpdate = true;
			}

			final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
			if (hasOffset()) {
				pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
			}
			else {
				pagingSelect.append( "select * from ( " );
			}
			pagingSelect.append( sql );
			if (hasOffset()) {
				pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
			}
			else {
				pagingSelect.append( " ) where rownum <= ?" );
			}

			if (isForUpdate) {
				pagingSelect.append( " " );
				pagingSelect.append( forUpdateClause );
			}

			if(LOGGER.isDebugEnabled())
				LOGGER.debug("wrapped paged sql {0}.", pagingSelect);
			
			System.out.println("---"+pagingSelect.toString());
			return pagingSelect.toString();
		}

	}
	
	public LimitHandler getLimitHandler(int rows) {
		return new Oracle9iLimitHandler(rows);
	}

	public LimitHandler getLimitHandler(int offset, int rows) {
		return new Oracle9iLimitHandler(offset, rows);
	}
}
