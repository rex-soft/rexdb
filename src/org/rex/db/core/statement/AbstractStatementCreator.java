package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.SqlUtil;

/**
 * Base statement creator
 */
public abstract class AbstractStatementCreator implements StatementCreator{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatementCreator.class);

	//----------Statement
	public Statement createStatement(Connection conn) throws DBException, SQLException{
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("creating Statement of Connection[{0}].", conn.hashCode());
		
		return conn.createStatement();
	}
	
	//----------Callable Statement
	public CallableStatement createCallableStatement(Connection conn, String sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing CallableStatement for sql {0} of Connection[{1}].", sql, conn.hashCode());
		
		return conn.prepareCall(sql);
	}
	
	//----------Batch Statement
	public Statement createBatchStatement(Connection conn, String[] sql) throws SQLException {
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("creating batch Statement for sqls {0} of Connection[{1}].", Arrays.toString(sql), conn.hashCode());
		
		Statement stmt = conn.createStatement();
		for (int i = 0; i < sql.length; i++) {
			stmt.addBatch(sql[i]);
		}
		return stmt;
	}

	
	//------------------Sql validate
	private static boolean isValidateSql() throws DBException{
		return Configuration.getCurrentConfiguration().isValidateSql();
	}

	/**
	 * 在执行SQL前进行基本的校验
	 * @param sql
	 * @param ps
	 * @throws DBException 
	 */
	private static void validateSql(String sql, int expectedParameterSize) throws DBException{
		SqlUtil.validate(sql, expectedParameterSize);
	}
	
	//validate sql with array parameters
	protected static void validateSql(String sql, Object[] parameterArray) throws DBException{
		if(isValidateSql())
			validateSql(sql, parameterArray == null ? 0 : parameterArray.length);
	}
	
	protected static void validateSql(String sql, Object[][] parameterArrays) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < parameterArrays.length; i++) 
				validateSql(sql, parameterArrays[i] == null ? 0 : parameterArrays[i].length);
		}
	}

	//validate sql with ps parameters
	protected static void validateSql(String sql, Ps ps) throws DBException{
		if(isValidateSql())
			validateSql(sql, ps == null ? 0 : ps.getParameterSize());
	}
	
	protected static void validateSql(String sql, Ps[] ps) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < ps.length; i++) 
				validateSql(sql, ps[i] == null ? 0 : ps[i].getParameterSize());
		}
	}
	
	//validate sqls
	protected static void validateSql(String sql) throws DBException{
		if(isValidateSql()) validateSql(sql, 0);
	}
	
	protected static void validateSql(String[] sql) throws DBException{
		if(isValidateSql()){
			for (int i = 0; i < sql.length; i++) 
				validateSql(sql[i], 0);
		}
	}
}
