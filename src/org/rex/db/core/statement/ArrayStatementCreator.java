package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.SqlUtil;

/**
 * Statement Creator for parameter array, not support primitive types array.
 * 
 * Set standard SQL prepared statement parameters. such as:
 * sql: INSERT INTO TBL(CL1, CL2) VALUES(?,?)
 * parameters: new String[]{"100", "M"}, or new Object[]{100, "M"};
 */
public class ArrayStatementCreator extends AbstractStatementCreator{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArrayStatementCreator.class);
	
	//----------Prepared Statement
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException {
		return createPreparedStatement(connection, sql, parameters, null);
	}
	
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters, LimitHandler limitHandler)
			throws DBException, SQLException {
		return createPreparedStatement(connection, sql, (Object[])parameters, limitHandler);
	}
	
	private PreparedStatement createPreparedStatement(Connection conn, String sql, Object[] parameterArray, LimitHandler limitHandler) throws DBException, SQLException{
		validateSql(sql, parameterArray);
		
		if(limitHandler != null)
			sql = limitHandler.wrapSql(sql);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing Statement for sql {0} of Connection[{1}].", sql, conn.hashCode());
		
		PreparedStatement statement = conn.prepareStatement(sql);
		setParameters(statement, parameterArray);
		
		if(limitHandler != null)
			limitHandler.afterSetParameters(statement, parameterArray == null ? 0 : parameterArray.length);
		return statement;
	}
	
	//----------Callable Statement
	public CallableStatement createCallableStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException {
		return createCallableStatement(connection, sql, (Object[])parameters);
	}
	
	private CallableStatement createCallableStatement(Connection conn, String sql, Object[] parameterArray) throws SQLException, DBException {
		validateSql(sql, parameterArray);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing CallableStatement for sql {0} of Connection[{1}].", sql, conn.hashCode());
		
		CallableStatement statement = conn.prepareCall(sql);
		setParameters(statement, parameterArray);
		return statement;
	}
	
	//----------Batch Prepared Statement
	public PreparedStatement createBatchPreparedStatement(Connection connection, String sql, Object[] parametersArray)
			throws DBException, SQLException {
		return createBatchPreparedStatement(connection, sql, (Object[][])parametersArray);
	}
	
	private PreparedStatement createBatchPreparedStatement(Connection conn, String sql, Object[][] parametersArray) throws DBException, SQLException {
		validateSql(sql, parametersArray);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing batch PreparedStatement for sql {0} of Connection[{1}].", sql, conn.hashCode());
		
		PreparedStatement statement = conn.prepareStatement(sql);
		for (int i = 0; i < parametersArray.length; i++) {
			setParameters(statement, parametersArray[i]);
			statement.addBatch();
		}
		return statement;
	}
	
	//------private methods
	/**
	 * set parameters for PreparedStatement
	 */
	private void setParameters(PreparedStatement preparedStatement, Object[] parameterArray) throws DBException, SQLException{
		if(preparedStatement == null || parameterArray == null) return;
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("setting array parameters {0} for statement[{1}].", Arrays.toString(parameterArray), preparedStatement.hashCode());
		
		for (int i = 0; i < parameterArray.length; i++) {
			if(parameterArray[i] == null)
				SqlUtil.setNull(preparedStatement, i + 1);
			else
				SqlUtil.setParameter(preparedStatement, i + 1, parameterArray[i]);
		}
	}


}
