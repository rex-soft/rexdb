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
import org.rex.db.core.statement.DefaultStatementCreatorFactory;
import org.rex.db.core.statement.StatementCreator;
import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.SqlContext;
import org.rex.db.sql.SqlParser;
import org.rex.db.transaction.ThreadConnectionHolder;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.JdbcUtil;

/**
 * 数据库模板，封装了框架支持的数据库操作
 */
public class DBTemplate {
	
	private final DataSource dataSource;
	private final StatementCreator statementCreator;
	private final QueryExecutor queryExecutor;
	private final ResultSetIterator resultSetIterator;

	public DBTemplate(DataSource dataSource) throws DBException {
		this.dataSource = dataSource;
		this.statementCreator = new DefaultStatementCreatorFactory().buildStatementCreator();
		this.queryExecutor = new DefaultQueryExecutor();
		this.resultSetIterator = new DefaultResultSetIterator();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	//-------------------------------------------
	/**
	 * 执行不带预编译的SQL并处理结果集
	 */
	public void query(String sql, ResultReader<?> resultReader) throws DBException {
		validateSql(sql);
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = statementCreator.createStatement(con);
			applyTimeout(stmt, this.dataSource);

			rs = queryExecutor.executeQuery(stmt, sql);
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
	 * 执行预编译SQL
	 */
	public void query(String sql, Ps ps, ResultReader<?> resultReader) throws DBException {
		validateSql(sql, ps);
		SqlContext context = fireOnEvent(SqlContext.SQL_QUERY, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = statementCreator.createPreparedStatement(con, sql, ps);
			applyTimeout(preparedStatement, this.dataSource);

			rs = queryExecutor.executeQuery(preparedStatement);
			resultSetIterator.read(resultReader, rs);

			checkWarnings(con, preparedStatement, rs);
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, ps, e.getMessage());
		}finally {
			close(con, preparedStatement, rs);
			fireAfterEvent(context, resultReader.getResults());
		}
	}
	
	/**
	 * 通过PreparedStatementCreator执行多条SQL（非批处理方式）
	 */
	public int update(String sql) throws DBException {
		validateSql(sql);
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		int retval = 0;
		try {
			statement = statementCreator.createStatement(con);
			applyTimeout(statement, this.dataSource);
			
			retval = queryExecutor.executeUpdate(statement, sql);
			
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
	 * 通过PreparedStatementCreator执行多条SQL（非批处理方式）
	 */
	public int update(String sql, Ps ps) throws DBException {
		validateSql(sql, ps);
		SqlContext context = fireOnEvent(SqlContext.SQL_UPDATE, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		
		int retval = 0;
		try {
			preparedStatement = statementCreator.createPreparedStatement(con, sql, ps);
			applyTimeout(preparedStatement, this.dataSource);
			
			retval = queryExecutor.executeUpdate(preparedStatement);
			
			checkWarnings(con, preparedStatement, null);
			return retval;
		}catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, ps, e.getMessage());
		}finally {
			close(con, preparedStatement, null);
			fireAfterEvent(context, retval);
		}
	}
	
	/**
	 * 执行批处理SQL
	 */
	public int[] batchUpdate(String sql, Ps[] ps) throws DBException {
		validateSql(sql, ps);
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), new String[]{sql}, ps);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		PreparedStatement preparedStatement = null;
		
		int[] retvals = null;
		try {
			preparedStatement = statementCreator.createBatchPreparedStatement(con, sql, ps);
			applyTimeout(preparedStatement, this.dataSource);
			
			retvals = queryExecutor.executeBatch(preparedStatement);
			
			checkWarnings(con, preparedStatement, null);
			return retvals;
		}catch (SQLException e) {
			ArrayList<Ps> psList = new ArrayList<Ps>(Arrays.asList(ps));
			throw new DBException("DB-C0005", e, sql, psList, e.getMessage());
		}finally {
			close(con, preparedStatement, null);
			fireAfterEvent(context, retvals);
		}
	}
	
	/**
	 * 批量执行多条SQL
	 */
	public int[] batchUpdate(String sql[]) throws DBException{
		validateSql(sql);
		SqlContext context = fireOnEvent(SqlContext.SQL_BATCH_UPDATE, false, getDataSource(), sql, null);
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		Statement statement = null;
		
		int[] retvals = null;
		try {
			statement = statementCreator.createBatchStatement(con, sql);
			applyTimeout(statement, this.dataSource);
			
			retvals = queryExecutor.executeBatch(statement);
			
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
	 * 通过CallableStatement执行查询，通常用于调用存储过程
	 */
	public RMap call(String sql, Ps ps, boolean originalKey) throws DBException {
		validateSql(sql, ps);
		SqlContext context = fireOnEvent(SqlContext.SQL_CALL, false, getDataSource(), new String[]{sql}, new Ps[]{ps});
		
		Connection con = DataSourceUtil.getConnection(this.dataSource);
		CallableStatement cs = null;
		RMap outs = null;
		try {
			cs = statementCreator.createCallableStatement(con, sql, ps);
			applyTimeout(cs, this.dataSource);
			
			boolean retval = queryExecutor.execute(cs);
			
			checkWarnings(con, cs, null);
			outs = extractOutputParameters(cs, ps, originalKey);
			RMap returns = extractReturnedResultSets(cs, ps, originalKey);
			if(returns.size() > 0)
				outs.putAll(returns);
			
			return outs;
		}
		catch (SQLException e) {
			throw new DBException("DB-C0005", e, sql, ps, e.getMessage());
		}finally {
			close(con, cs, null);
			fireAfterEvent(context, outs);
		}
	}

	/**
	 * 调用存储过程后，解析输出参数
	 */
	private RMap extractOutputParameters(CallableStatement cs, Ps ps, boolean originalKey) throws DBException {
		RMap outParams = new RMap();
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
					throw new DBException("DB-C0006", e, i + 1, e.getMessage(), ps);
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
	private RMap extractReturnedResultSets(CallableStatement cs, Ps ps, boolean originalKey) throws DBException, SQLException {
		RMap returns = new RMap();
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
		
		DataSourceUtil.closeConnectionIfNotTransaction(con, this.dataSource);
	}
	
	//-----------------timeout
	/**
	 * 设置超时时间
	 */
	public void applyTimeout(Statement stmt, DataSource ds) throws DBException {
		int live = Configuration.getCurrentConfiguration().getQueryTimeout();
		ConnectionHolder holder = (ConnectionHolder) ThreadConnectionHolder.get(ds);
		if (holder != null && holder.getDeadline() != null) {//已短的时间为准
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
	//------------------Check warnings
	private void checkWarnings(Connection connection, Statement statement, ResultSet resultSet) throws DBException{
		boolean isCheck = Configuration.getCurrentConfiguration().isCheckWarnings();
		if(isCheck)
			JdbcUtil.checkWarnings(connection, statement, resultSet);
	}
	
	//------------------Sql validate
	private boolean isValidateSql() throws DBException{
		return Configuration.getCurrentConfiguration().isValidateSql();
	}

	/**
	 * 在执行SQL前进行基本的校验
	 * @param sql
	 * @param ps
	 * @throws DBException 
	 */
	private void validateSql(String sql, Ps ps) throws DBException{
		if(isValidateSql())
			SqlParser.validate(sql, ps);
	}
	
	private void validateSql(String sql, Ps[] ps) throws DBException{
		for (int i = 0; i < ps.length; i++) {
			validateSql(sql, ps[i]);
		}
	}
	
	private void validateSql(String sql) throws DBException{
		if(isValidateSql())
			SqlParser.validate(sql, null);
	}
	
	private void validateSql(String[] sql) throws DBException{
		for (int i = 0; i < sql.length; i++) {
			if(isValidateSql())
				SqlParser.validate(sql[i], null);
		}
	}
}
