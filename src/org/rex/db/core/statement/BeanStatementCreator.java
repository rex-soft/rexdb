package org.rex.db.core.statement;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.rex.db.configuration.Configuration;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.dynamic.javassist.BeanConvertor;
import org.rex.db.dynamic.javassist.BeanConvertorManager;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.ReflectUtil;
import org.rex.db.util.SqlUtil;

/**
 * Statement Creator for map and java bean
 *	
 * Set SQL with prepared statement parameters, sql has '#{...}' tags to identify parameters. such as:
 * sql: INSERT INTO TBL(CL1, CL2) VALUES(#{cl1}, #{cl2})
 * parameters: 1. java bean with variables: 'cl1', 'cl2'; 2. Map with key 'cl1' and 'cl2'.
 */
public class BeanStatementCreator extends AbstractStatementCreator{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanStatementCreator.class);
	
	//----------settings
	/**
	 * user dynamic class
	 * @throws DBException 
	 */
	private static boolean isDynamic() throws DBException{
		return Configuration.getCurrentConfiguration().isDynamicClass();
	}
	
	//------------implements
	//PreparedStatement
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException {
		return createPreparedStatement(connection, sql, parameters, null);
	}

	//PreparedStatement with limit handler
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters, LimitHandler limitHandler) throws DBException, SQLException {
		String[] all = SqlUtil.parse(sql), tokens = new String[all.length - 1];
		System.arraycopy(all, 1, tokens, 0, all.length - 1);
		String parsedSql = (limitHandler == null) ? all[0] : limitHandler.wrapSql(all[0]);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing Statement for sql {0} of Connection[{1}].", parsedSql, connection.hashCode());
		
		PreparedStatement statement = connection.prepareStatement(parsedSql);
		fillStatement(statement, tokens, parameters);
		
		if(limitHandler != null)
			limitHandler.afterSetParameters(statement, tokens.length);
		
		return statement;
	}
	
	//CallableStatement
	public CallableStatement createCallableStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException {
		String[] all = SqlUtil.parse(sql), tokens = new String[all.length - 1];
		System.arraycopy(all, 1, tokens, 0, all.length - 1);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing CallableStatement for sql {0} of Connection[{1}].", all[0], connection.hashCode());
		
		CallableStatement statement = connection.prepareCall(all[0]);
		fillStatement(statement, tokens, parameters);
		return statement;
	}

	//PreparedStatement
	public PreparedStatement createBatchPreparedStatement(Connection connection, String sql, Object[] parametersArray) throws DBException, SQLException {
		String[] all = SqlUtil.parse(sql), tokens = new String[all.length - 1];
		System.arraycopy(all, 1, tokens, 0, all.length - 1);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("preparing batch PreparedStatement for sql {0} of Connection[{1}].", all[0], connection.hashCode());
		
		PreparedStatement statement = connection.prepareStatement(all[0]);
		for (int i = 0; i < parametersArray.length; i++) {
			fillStatement(statement, tokens, parametersArray[i]);
			statement.addBatch();
		}
		
		return statement;
	}

	//-------------private methods
	private void fillStatement(PreparedStatement statement, String[] tokens, Object parameters) throws SQLException, DBException{
		if(tokens == null || parameters == null) return;
		else if(parameters instanceof Map){
			setParameters(statement, tokens, (Map<?,?>)parameters);
		}else {
			setParameters(statement, tokens, parameters);
		}
	}
	
	/**
	 * set parametersthe if the parameters is Map
	 * @param statement
	 * @param tokens
	 * @param parameters
	 * @throws SQLException 
	 */
	private void setParameters(PreparedStatement statement, String[] tokens, Map<?,?> parameters) throws SQLException{
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("setting Map parameters {0} for statement[{1}].", parameters.toString(), statement.hashCode());
		
		for (int i = 0; i < tokens.length; i++) {
			SqlUtil.setParameter(statement, i + 1, parameters.get(tokens[i]));
		}
	}
	
	/**
	 * set parametersthe if the parameters is java bean
	 * @param statement
	 * @param tokens
	 * @param parameters
	 * @throws DBException 
	 * @throws SQLException 
	 */
	private void setParameters(PreparedStatement statement, String[] tokens, Object parameters) throws DBException, SQLException{
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("setting java bean parameters {0} for statement[{1}].", parameters, statement.hashCode());
		}
		
		if(isDynamic()){
			BeanConvertor setter = BeanConvertorManager.getConvertor(parameters.getClass());
			setter.setParameters(statement, parameters, tokens);
		}else{
			Map<String, Method> readers = ReflectUtil.getReadableMethods(parameters.getClass());
			for (int i = 0; i < tokens.length; i++) {
				Method reader = readers.get(tokens[i]);
				if(reader == null){
					SqlUtil.setNull(statement, i + 1);
				}else{
					Object value = ReflectUtil.invokeMethod(parameters, reader);
					SqlUtil.setParameter(statement, i + 1, value);
 				}
			}
		}
	}
	
}
