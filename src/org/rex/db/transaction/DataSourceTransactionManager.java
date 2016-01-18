package org.rex.db.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.DataSourceUtil;

/**
 * 单JDBC数据源事物
 */
public class DataSourceTransactionManager extends AbstractTransactionManager {

	private DataSource dataSource;

	public DataSourceTransactionManager() {
	}

	public DataSourceTransactionManager(DataSource dataSource) {
		if (dataSource == null) {
			throw new DBRuntimeException("DB-C10021");
		}
		this.dataSource = dataSource;
	}

	/**
	 * 设置数据源
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 获取数据源
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	//-------------------------------实现父类

	/**
	 * 获取事物对象
	 */
	protected ConnectionHolder doGetTransaction() {
		if (ThreadConnectionHolder.has(dataSource)) {
			return ThreadConnectionHolder.get(dataSource);
		}else
			return null;
	}

	/**
	 * 开始事务
	 */
	protected void doBegin(TransactionDefinition definition) throws SQLException, DBException {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		
		if (connectionHolder == null) {
			Connection con = DataSourceUtil.getConnection(dataSource);
			connectionHolder = new DataSourceConnectionHolder(con, definition);
		}

		Connection con = connectionHolder.getConnection();
		
		// 设置隔离级别
		if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
			connectionHolder.setPreviousIsolationLevel(con.getTransactionIsolation());
			con.setTransactionIsolation(definition.getIsolationLevel());
		}

		// 是否只读
		if (definition.isReadOnly()) {
			con.setReadOnly(true);
		}

		// 切换至手动提交事物
		con.setAutoCommit(false);

		// 注册事物超时时间
		if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
			connectionHolder.setTimeoutInSeconds(definition.getTimeout());
		}

		// 将连接绑定至线程
		ThreadConnectionHolder.bind(dataSource, connectionHolder);
	}


	/**
	 * 提交事务
	 */
	protected void doCommit() throws Exception {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null){
			throw new DBException("DB-C10038", dataSource);
		}
		
		try{
			connectionHolder.getConnection().commit();
		}catch(Exception e){
			if (connectionHolder.getDefinition().isRollbackOnCommitFailure()) 
				doRollbackOnCommitException(e);
			throw e;
		}
	}
	
	/**
	 * 提交事务出现异常时，自动回滚
	 */
	private void doRollbackOnCommitException(Throwable ex) throws DBException {
		try {
			doRollback();
		}catch (Exception e) {
			throw new DBException("DB-C10024", e, e.getMessage());
		}
	}

	/**
	 * 回滚事务
	 */
	protected void doRollback() throws SQLException, DBException {
		ConnectionHolder connectionHolder = doGetTransaction();
		if(connectionHolder == null){
			throw new DBException("DB-C10038", dataSource);
		}
		connectionHolder.getConnection().rollback();
	}


	/**
	 * 恢复连接至初始状态
	 */
	protected void afterCompletion() {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null) return;
		
		Connection con = connectionHolder.getConnection();
		ThreadConnectionHolder.unbind(this.dataSource);

		try {
			con.setAutoCommit(true);
			if (connectionHolder.getPreviousIsolationLevel() == null) {
				con.setTransactionIsolation(connectionHolder.getPreviousIsolationLevel());
			}

			if (con.isReadOnly()) {
				con.setReadOnly(false);
			}
		}catch (Exception ex) {
		}

		try {
			DataSourceUtil.closeConnection(con, this.dataSource);
		}catch (DBException ex) {
		}
	}

	/**
	 * 获取事物所属连接
	 */
	protected Connection doGetTransactionConnection(){
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null) return null;
		return connectionHolder.getConnection();
	}
}
