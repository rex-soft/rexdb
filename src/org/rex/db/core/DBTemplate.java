/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.core.executor.DefaultQueryExecutor;
import org.rex.db.core.executor.QueryExecutor;
import org.rex.db.core.reader.ClassResultReader;
import org.rex.db.core.reader.DefaultResultSetIterator;
import org.rex.db.core.reader.MapResultReader;
import org.rex.db.core.reader.ResultReader;
import org.rex.db.core.reader.ResultSetIterator;
import org.rex.db.core.statement.StatementCreatorManager;
import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.SqlContext;
import org.rex.db.transaction.ThreadConnectionHolder;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.JdbcUtil;

/**
 * Framework kernel.
 * 
 * @version 1.0, 2016-03-28
 * @since Rexdb-1.0
 */
public class DBTemplate {
	
	private DataSource dataSource;
	
	private static StatementCreatorManager statementCreatorManager;
	private static QueryExecutor executor;
	private static ResultSetIterator resultSetIterator;

	static{
		statementCreatorManager = new StatementCreatorManager();
		executor = new DefaultQueryExecutor();
		resultSetIterator = new DefaultResultSetIterator();
	}
	
	public DBTemplate(DataSource dataSource) throws DBException {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	//--------------------query
	/**
	 * Query.
	 */
	public void query(String sql, ResultReader<?> resultReader) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, null, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = statementCreatorManager.get().createStatement(con);
			applyTimeout(stmt, dataSource);

			rs = executor.executeQuery(stmt, sql);
			resultSetIterator.read(resultReader, rs);
			
			checkWarnings(con, stmt, rs);
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, null, e.getMessage());
		}finally {
			close(con, stmt, rs);
			fireAfterEvent(context, resultReader.getResults());
		}
	}

	/**
	 * Query with prepared parameters.
	 */
	public void query(String sql, Object parameters, ResultReader<?> resultReader) throws DBException {
		query(sql, parameters, null, resultReader);
	}
	
	/**
	 * Query with prepared parameters.
	 */
	public void query(String sql, Object parameters, LimitHandler limitHandler, ResultReader<?> resultReader) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, parameters, limitHandler);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = statementCreatorManager.get(parameters).createPreparedStatement(con, sql, parameters, limitHandler);
			applyTimeout(preparedStatement, this.dataSource);
			
			rs = executor.executeQuery(preparedStatement);
			resultSetIterator.read(resultReader, rs);
			
			checkWarnings(con, preparedStatement, rs);
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, parameters, e.getMessage());
		}finally {
			close(con, preparedStatement, rs);
			fireAfterEvent(context, resultReader.getResults());
		}
	}
	//--------------------update
	/**
	 * Update.
	 */
	public int update(String sql) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, null, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		int retval = 0;
		try {
			statement = statementCreatorManager.get().createStatement(con);
			applyTimeout(statement, this.dataSource);
			
			retval = executor.executeUpdate(statement, sql);
			checkWarnings(con, statement, null);
			return retval;
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, null, e.getMessage());
		}finally {
			close(con, statement, null);
			fireAfterEvent(context, retval);
		}
	}
	
	/**
	 * Update with prepared parameters.
	 */
	public int update(String sql, Object parameters) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, parameters, null);
		
		Connection connection = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement statement = null;
		
		int retval = 0;
		try {
			statement = statementCreatorManager.get(parameters).createPreparedStatement(connection, sql, parameters);
			applyTimeout(statement, dataSource);
			
			retval = executor.executeUpdate(statement);
			checkWarnings(connection, statement, null);
			return retval;
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, parameters, e.getMessage());
		}finally {
			close(connection, statement, null);
			fireAfterEvent(context, retval);
		}
	}
	
	/**
	 * Batch update.
	 */
	public int[] batchUpdate(String sql[]) throws DBException{
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), sql, null, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		
		int[] retvals = null;
		try {
			statement = statementCreatorManager.get().createBatchStatement(con, sql);
			applyTimeout(statement, this.dataSource);
			
			retvals = executor.executeBatch(statement);
			checkWarnings(con, statement, null);
			return retvals;
		} catch (SQLException e) {
			ArrayList<String> sqlList = new ArrayList<String>(Arrays.asList(sql));
			throw new DBException("DB-C0005", e, sqlList, null, e.getMessage());
		} finally {
			close(con, statement, null);
			fireAfterEvent(context, retvals);
		}
	}
	
	/**
	 * Batch update with prepared parameters.
	 */
	public int[] batchUpdate(String sql, Object[] parametersArray) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), new String[]{sql}, parametersArray, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		
		int[] retvals = null;
		try {
			preparedStatement = statementCreatorManager.get(parametersArray).createBatchPreparedStatement(con, sql, parametersArray);
			applyTimeout(preparedStatement, this.dataSource);
			
			retvals = executor.executeBatch(preparedStatement);
			
			checkWarnings(con, preparedStatement, null);
			return retvals;
		}catch (SQLException e) {
			List<?> psList = new ArrayList(Arrays.asList(parametersArray));
			throw new DBException("DB-C0005", e, sql, psList, e.getMessage());
		}finally {
			close(con, preparedStatement, null);
			fireAfterEvent(context, retvals);
		}
	}

	//--------------------call
	/**
	 * Call.
	 */
	public RMap<String, ?> call(String sql, Object parameters) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_CALL, false, getDataSource(), new String[]{sql}, parameters, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		CallableStatement cs = null;
		RMap<String, Object> outs = null;
		try {
			cs = statementCreatorManager.get(parameters).createCallableStatement(con, sql, parameters);
			applyTimeout(cs, this.dataSource);
			
			boolean retval = executor.execute(cs);
			
			checkWarnings(con, cs, null);
			
			//out parameters
			Ps ps = null;
			if(parameters instanceof Ps){
				ps = (Ps)parameters;
				outs = extractOutputParameters(cs, ps);
			}
			
			//returns
			RMap<String, Object> returns = extractReturnedResultSets(cs, ps);
			if(returns != null && returns.size() > 0){
				if(outs != null)
					outs.putAll(returns);
				else
					outs = returns;
			}
			
			return outs;
		}
		catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, parameters, e.getMessage());
		}finally {
			close(con, cs, null);
			fireAfterEvent(context, outs);
		}
	}

	/**
	 * Call with prepared parameters.
	 */
	private RMap<String, Object> extractOutputParameters(CallableStatement cs, Ps ps) throws DBException {
		RMap<String, Object> outParams = new RMap<String, Object>();
		if(ps == null) return outParams;
		
		List<Ps.SqlParameter> parameters = ps.getParameters();
		
		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
			if (parameter instanceof Ps.SqlOutParameter) {//OUT
				String paramterName = ((Ps.SqlOutParameter<?>) parameter).getParamName();
				Class<?> entitryClass = ((Ps.SqlOutParameter<?>) parameter).getOutEntitryClass();
				Object out = null;
				try {
					out = cs.getObject(i + 1);
				} catch (SQLException e) {
					throw new DBException("DB-C0006", e, i + 1, e.getMessage(), ps);
				}
				
				//resultSet
				if (out instanceof ResultSet) {
					ResultReader reader = newResultReader(entitryClass);
					try {
						resultSetIterator.read(reader, (ResultSet)out);
						List list = reader.getResults();
						outParams.put(Ps.CALL_OUT_DEFAULT_PREFIX + (i + 1), list);
						if(paramterName != null)
							outParams.put(paramterName, list);
					}finally {
						try {
							((ResultSet) out).close();
						}catch (SQLException ignore) {}
					}
				}
				
				else {
					outParams.put(Ps.CALL_OUT_DEFAULT_PREFIX + (i + 1), out);
					if(paramterName != null)
						outParams.put(paramterName, out);
				}
			}
		}
		return outParams;
	}
	
	private RMap<String, Object> extractReturnedResultSets(CallableStatement cs, Ps ps) throws DBException, SQLException {
		List<Class<?>> returnResultTypes = ps == null ? null : ps.getReturnResultTypes();
		RMap<String, Object> returns = new RMap<String, Object>();
		int rsIndx = 0;
		do {
			ResultSet rs = null;
			try {
				rs = cs.getResultSet();
				if(rs != null){
					ResultReader reader = null;
					if(returnResultTypes == null || returnResultTypes.size() < rsIndx + 1 || returnResultTypes.get(rsIndx) == null)
						reader = newResultReader(null);
					else
						reader = newResultReader(returnResultTypes.get(rsIndx));
					
					resultSetIterator.read(reader, rs);
					returns.put(Ps.CALL_RETURN_DEFAULT_PREFIX + (rsIndx + 1), reader.getResults());
				}
			}catch (SQLException e) {
				throw new DBException("DB-C0007", e, e.getMessage(), ps);
			}finally {
				try {
					if(rs != null) rs.close();
				}catch (SQLException ignore) {
				}
			}
			rsIndx++;
		}
		while (cs.getMoreResults());
		
		return returns;
	}
	
	private ResultReader newResultReader(Class entitryClass){
		ResultReader reader = null;//初始化读取结果集的对象
		if(entitryClass == null){
			reader = new MapResultReader();
		}else{
			reader = new ClassResultReader(entitryClass);
		}
		return reader;
	}
	
	/**
	 * Closes the Connection, the Statement and the ResultSet.
	 */
	private void close(Connection con, Statement stmt, ResultSet rs) throws DBException{
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException ignore) {
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException ignore) {
			}
		}
		
		DataSourceUtil.closeConnectionIfNotTransaction(con, this.dataSource);
	}
	
	//-----------------timeout
	/**
	 * Applies timeout.
	 */
	public void applyTimeout(Statement stmt, DataSource ds) throws DBException {
		int live = Configuration.getCurrentConfiguration().getQueryTimeout();
		ConnectionHolder holder = (ConnectionHolder) ThreadConnectionHolder.get(ds);
		if (holder != null && holder.getDeadline() != null) {//以短的时间为准
			int tranLive = holder.getTimeToLiveInSeconds();
			if(live < 0  || (live > 0 && live > tranLive))
				live = tranLive;
		}
		
		if(live > 0){
			try {
				stmt.setQueryTimeout(live);
			} catch (SQLException e) {
				throw new DBException("DB-C10014", e);
			}
		}
	}
	
	//-----------------listeners
	private SqlContext fireOnEvent(int sqlType, boolean betweenTransaction, DataSource dataSource, String[] sql, Object parameters, LimitHandler limitHandler) throws DBException{
		SqlContext context = null;
		ListenerManager listenerManager = getListenerManager();
		if(listenerManager.hasListener()){
			context = getContext(sqlType, betweenTransaction, dataSource, sql, parameters, limitHandler);
			listenerManager.fireOnExecute(context);
		}
		return context;
	}
	
	private void fireAfterEvent(SqlContext context, Object result) throws DBException{
		if(context != null){
			getListenerManager().fireAfterExecute(context, result);
		}
	}
	
	private SqlContext getContext(int sqlType, boolean onTransaction, DataSource dataSource, String[] sql, Object parameters, LimitHandler limitHandler){
		return new SqlContext(sqlType, onTransaction, dataSource, sql, parameters, limitHandler);
	}
	
	private ListenerManager getListenerManager() throws DBException{
		return Configuration.getCurrentConfiguration().getListenerManager();
	}
	
	//------------------Check warnings
	private void checkWarnings(Connection connection, Statement statement, ResultSet resultSet) throws DBException{
		boolean isCheck = Configuration.getCurrentConfiguration().isCheckWarnings();
		if(isCheck)
			JdbcUtil.checkWarnings(connection, statement, resultSet);
	}

}
