package org.rex.db.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rex.WMap;
import org.rex.db.Ps;
import org.rex.db.configuration.Configuration;
import org.rex.db.core.executor.DefaultQueryExecutor;
import org.rex.db.core.executor.QueryExecutor;
import org.rex.db.core.reader.ClassResultReader;
import org.rex.db.core.reader.DefaultResultSetIterator;
import org.rex.db.core.reader.MapResultReader;
import org.rex.db.core.reader.ResultReader;
import org.rex.db.core.reader.ResultSetIterator;
import org.rex.db.core.statement.DefaultStatementCreatorFactory;
import org.rex.db.core.statement.StatementCreatorFactory;
import org.rex.db.core.statement.batch.BatchPreparedStatementCreator;
import org.rex.db.core.statement.batch.BatchStatementCreator;
import org.rex.db.core.statement.callable.CallableStatementCreator;
import org.rex.db.core.statement.prepared.PreparedStatementCreator;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.SqlContext;
import org.rex.db.util.DataSourceUtil;

public class DBTemplate {

	protected final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * 是否忽略SQL Warnings，设置为否时抛出异常
	 */
	private boolean ignoreWarnings;
	
	private DataSource dataSource;
	private QueryExecutor queryExecutor;
	private ResultSetIterator resultSetIterator;
	private StatementCreatorFactory statementCreatorFactory;

	public DBTemplate() {
		ignoreWarnings = true;
		queryExecutor = new DefaultQueryExecutor();
		resultSetIterator = new DefaultResultSetIterator();
		statementCreatorFactory = new DefaultStatementCreatorFactory();
	}

	public DBTemplate(DataSource dataSource) throws DBException {
		setDataSource(dataSource);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setIgnoreWarnings(boolean ignoreWarnings) {
		this.ignoreWarnings = ignoreWarnings;
	}

	public boolean isIgnoreWarnings() {
		return ignoreWarnings;
	}

	/**
	 * 如果设定允许，产生警告时，抛出异常
	 */
	private void throwExceptionOnWarning(Statement statement) throws DBException {
		if(!this.ignoreWarnings){
			SQLWarning warning = null;
			try {
				warning = statement.getWarnings();
			} catch (SQLException e) {
			}
			
			if (warning != null) 
				throw new DBException("DB-C10009", warning);
		}
	}
	
	/**
	 * 关闭连接、声明、结果集
	 * @param con 连接
	 * @param stmt 声明
	 * @param rs 结果集
	 * @throws DBException
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
		
		DataSourceUtil.closeConnection(con, this.dataSource);
	}
	
	/**
	 * 执行SQL前监听
	 * @throws DBException 
	 */
	private SqlContext fireOnEvent(int sqlType, boolean betweenTransaction, DataSource dataSource, String[] sql, Ps[] ps) throws DBException{
		SqlContext context = null;
		ListenerManager listenerManager = getListenerManager();
		if(listenerManager.hasListener()){
			context = getContext(sqlType, betweenTransaction, dataSource, sql, ps);
			listenerManager.fireOnExecute(context);
		}
		return context;
	}
	
	/**
	 * 执行SQL后监听
	 * @throws DBException 
	 */
	private void fireAfterEvent(SqlContext context, Object result) throws DBException{
		if(context != null){
			getListenerManager().fireAfterExecute(context, result);
		}
	}
	
	private SqlContext getContext(int sqlType, boolean betweenTransaction, DataSource dataSource, String[] sql, Ps[] ps){
		return new SqlContext(sqlType, betweenTransaction, dataSource, sql, ps);
	}
	
	private ListenerManager getListenerManager() throws DBException{
		return Configuration.getCurrentConfiguration().getListenerManager();
	}
	
	//-------------------------------------------
	/**
	 * 执行不带预编译的SQL并处理结果集
	 */
	public void query(String sql, ResultReader resultReader) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			DataSourceUtil.applyTransactionTimeout(stmt, this.dataSource);

			rs = queryExecutor.executeQuery(stmt, sql);
			resultSetIterator.read(resultReader, rs);
			
			throwExceptionOnWarning(stmt);
		}catch (SQLException e) {
			throw new DBException("DB-Q10013", e, sql, e.getMessage());
		}finally {
			close(con, stmt, rs);
			fireAfterEvent(context, resultReader.getResults());
		}
	}

	/**
	 * 执行预编译SQL
	 */
	public void query(String sql, Ps ps, ResultReader resultReader) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		PreparedStatementCreator psc = statementCreatorFactory.newPreparedStatementCreator(sql);
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = psc.createPreparedStatement(con, ps);
			DataSourceUtil.applyTransactionTimeout(preparedStatement, this.dataSource);

			rs = queryExecutor.executeQuery(preparedStatement);
			resultSetIterator.read(resultReader, rs);

			throwExceptionOnWarning(preparedStatement);
		}catch (SQLException e) {
			throw new DBException("DB-Q10014", e, psc, ps, e.getMessage());
		}finally {
			close(con, preparedStatement, rs);
			fireAfterEvent(context, resultReader.getResults());
		}
	}
	
	/**
	 * 通过PreparedStatementCreator执行多条SQL（非批处理方式）
	 */
	public int update(String sql) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		
		int retval = 0;
		try {
			statement = con.createStatement();
			DataSourceUtil.applyTransactionTimeout(statement, this.dataSource);
			
			retval = queryExecutor.executeUpdate(statement, sql);
			
			throwExceptionOnWarning(statement);
			return retval;
		}catch (SQLException ex) {
			throw new DBException("DB-U10002", ex, sql, ex.getMessage());
		}finally {
			close(con, statement, null);
			fireAfterEvent(context, retval);
		}
	}
	
	/**
	 * 通过PreparedStatementCreator执行多条SQL（非批处理方式）
	 */
	public int update(String sql, Ps ps) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		PreparedStatementCreator psc = statementCreatorFactory.newPreparedStatementCreator(sql);
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		
		int retval = 0;
		try {
			preparedStatement = psc.createPreparedStatement(con, ps);
			DataSourceUtil.applyTransactionTimeout(preparedStatement, this.dataSource);
			
			retval = queryExecutor.executeUpdate(preparedStatement);
			
			throwExceptionOnWarning(preparedStatement);
			return retval;
		}catch (SQLException ex) {
			throw new DBException("DB-U10004", ex, psc, ps, ex.getMessage());
		}finally {
			close(con, preparedStatement, null);
			fireAfterEvent(context, retval);
		}
	}
	
	/**
	 * 执行批处理SQL
	 */
	public int[] batchUpdate(String sql, Ps[] ps) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), new String[]{sql}, ps);
		
		BatchPreparedStatementCreator bpsc = statementCreatorFactory.newBatchPreparedStatementCreator(sql);
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		
		int[] retvals = null;
		try {
			preparedStatement = bpsc.createBatchPreparedStatement(con, ps);
			DataSourceUtil.applyTransactionTimeout(preparedStatement, this.dataSource);
			
			retvals = queryExecutor.executeBatch(preparedStatement);
			
			throwExceptionOnWarning(preparedStatement);
			return retvals;
		}catch (SQLException ex) {
			ArrayList<Ps> psList = new ArrayList<Ps>(Arrays.asList(ps));
			throw new DBException("DB-U10003", ex, bpsc, psList, ex.getMessage());
		}finally {
			close(con, preparedStatement, null);
			fireAfterEvent(context, retvals);
		}
	}
	
	/**
	 * 批量执行多条SQL
	 */
	public int[] batchUpdate(String sql[]) throws DBException{
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), sql, null);
		
		BatchStatementCreator bsc = statementCreatorFactory.newBatchStatementCreator();
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		
		int[] retvals = null;
		try {
			statement = bsc.createBatchStatement(con, sql);
			DataSourceUtil.applyTransactionTimeout(statement, this.dataSource);
			
			retvals = queryExecutor.executeBatch(statement);
			
			throwExceptionOnWarning(statement);
			return retvals;
		} catch (SQLException ex) {
			ArrayList<String> sqlList = new ArrayList<String>(Arrays.asList(sql));
			throw new DBException("DB-U10005", ex, sqlList, ex.getMessage());
		} finally {
			close(con, statement, null);
			fireAfterEvent(context, retvals);
		}
	}

	/**
	 * 通过CallableStatement执行查询，通常用于调用存储过程
	 */
	public WMap call(String sql, Ps ps, boolean originalKey) throws DBException {
		SqlContext context = fireOnEvent(SqlContext.SQL_CALL, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		CallableStatementCreator csc = statementCreatorFactory.newCallableStatementCreator(sql);
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		CallableStatement cs = null;
		WMap outs = null;
		try {
			cs = csc.createCallableStatement(con, ps);
			DataSourceUtil.applyTransactionTimeout(cs, this.dataSource);
			
			boolean retval = queryExecutor.execute(cs);
			
			throwExceptionOnWarning(cs);
			outs = extractOutputParameters(cs, ps, originalKey);
			WMap returns = extractReturnedResultSets(cs, ps, originalKey);
			if(returns.size() > 0)
				outs.putAll(returns);
			
			return outs;
		}
		catch (SQLException ex) {
			throw new DBException("DB-P10001", ex, csc, ps, ex.getMessage());
		}finally {
			close(con, cs, null);
			fireAfterEvent(context, outs);
		}
	}

	/**
	 * 调用存储过程后，解析输出参数
	 */
	private WMap extractOutputParameters(CallableStatement cs, Ps ps, boolean originalKey) throws DBException {
		WMap outParams = new WMap();
		if(ps == null) return outParams;
		
		List<Ps.SqlParameter> parameters = ps.getParameters();
		
		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
			if (parameter instanceof Ps.SqlOutParameter) {//只处理输出参数
				String paramterName = ((Ps.SqlOutParameter) parameter).getParamName();
				Class entitryClass = ((Ps.SqlOutParameter) parameter).getOutEntitryClass();
				Object out = null;
				try {
					out = cs.getObject(i + 1);//jdbc查询出的结果
				} catch (SQLException e) {
					throw new DBException("DB-P10002", e, cs, i + 1);
				}
				
				//输出参数是结果集时，需要进行OR转换
				if (out instanceof ResultSet) {
					ResultReader reader = newResultReader(originalKey, entitryClass);//初始化读取结果集的对象
					try {
						resultSetIterator.read(reader, (ResultSet)out);
						List list = reader.getResults();
						outParams.put(i + 1, list);
						if(paramterName != null)
							outParams.put(paramterName, list);
					}finally {
						try {
							((ResultSet) out).close();
						}catch (SQLException ignore) {}
					}
				}
				
				//输出参数不是结果集，直接取值
				else {
					outParams.put(i + 1, out);
					if(paramterName != null)
						outParams.put(paramterName, out);
				}
			}
		}
		return outParams;
	}
	
	/**
	 * 调用存储过程后，解析返回结果
	 * XXX:暂时不能解析为bean
	 */
	private WMap extractReturnedResultSets(CallableStatement cs, Ps ps, boolean originalKey) throws DBException, SQLException {
		WMap returns = new WMap();
		int rsIndx = 0;
		ResultReader reader = null;
		do {
			ResultSet rs = null;
			try {
				rs = cs.getResultSet();
				if(rs != null){
					if(reader == null)
						reader = newResultReader(originalKey, null);
					
					resultSetIterator.read(reader, rs);
					List list = reader.getResults();
					returns.put("return" + rsIndx, list);
				}
			}catch (SQLException e) {
				throw new DBException("DB-P10008", e);
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


	/**
	 * 创建一个结果集读取对象
	 * @param originalKey
	 * @param entitryClass
	 * @return
	 */
	private ResultReader newResultReader(boolean originalKey, Class entitryClass){
		ResultReader reader = null;//初始化读取结果集的对象
		if(entitryClass == null){
			reader = new MapResultReader(originalKey);
		}else{
			reader = new ClassResultReader(originalKey, entitryClass);
		}
		return reader;
	}
}
