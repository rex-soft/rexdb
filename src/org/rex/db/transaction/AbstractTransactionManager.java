package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.TransactionContext;

public abstract class AbstractTransactionManager implements TransactionManager {

	//----------------------------------implements
	/**
	 * 开始事物
	 */
	public void begin(TransactionDefinition definition) throws DBException {
		if (definition == null)
			definition = new DefaultTransactionDefinition();

		try {
			TransactionContext context = fireOnEvent(TransactionContext.TRANSACTION_BEGIN, definition);
			doBegin(definition);
			fireAfterEvent(context);
		} catch (DBException e) {
			throw e;
		} catch (Exception e) {
			throw new DBException("DB-C10022", e, e.getMessage());
		}
	}

	/**
	 * 提交事务
	 */
	public void commit() throws DBException {
		try {
			TransactionContext context = fireOnEvent(TransactionContext.TRANSACTION_COMMIT);
			doCommit();
			fireAfterEvent(context);
		}catch (Exception e) {
			throw new DBException("DB-C10023", e, e.getMessage());
		}finally {
			afterCompletion();
		}
	}
	

	/**
	 * 回滚事务
	 */
	public void rollback() throws DBException {
		try {
			TransactionContext context = fireOnEvent(TransactionContext.TRANSACTION_ROLLBACK);
			doRollback();
			fireAfterEvent(context);
		}catch (Exception e) {
			throw new DBException("DB-C10024", e, e.getMessage());
		}finally {
			afterCompletion();
		}
	}
	
	/**
	 * 获取事务所属连接
	 */
	public Connection getTransactionConnection() throws DBException {
		return doGetTransactionConnection();
	}

	//----------------------------------Listener
	/**
	 * 开始事物前调用监听接口
	 */
	protected TransactionContext fireOnEvent(int event) throws DBException{
		return fireOnEvent(event, null);
	}
	
	protected TransactionContext fireOnEvent(int event, TransactionDefinition definition) throws DBException{
		TransactionContext context = null;
		ListenerManager listenerManager = getListenerManager();
		if(listenerManager.hasListener()){
			context = getContext(event, definition);
			listenerManager.fireOnTransaction(context);
		}
		return context;
	}
	
	/**
	 * 结束事物后调用监听接口
	 */
	protected void fireAfterEvent(TransactionContext context) throws DBException{
		if(context != null){
			getListenerManager().fireAfterTransaction(context);
		}
	}
	
	protected TransactionContext getContext(int event, TransactionDefinition definition){
		return new TransactionContext(event, definition);
	}
	
	private ListenerManager getListenerManager() throws DBException{
		return Configuration.getCurrentConfiguration().getListenerManager();
	}
	
	//----------------------------------abstract

	/**
	 * 开始事物
	 */
	protected abstract void doBegin(TransactionDefinition definition) throws DBException;

	/**
	 * 提交事务
	 */
	protected abstract void doCommit() throws DBException;

	/**
	 * 回滚
	 */
	protected abstract void doRollback() throws DBException;

	/**
	 * 事务结束后执行
	 */
	protected abstract void afterCompletion();
	
	/**
	 * 获取事务所属连接
	 */
	protected abstract Connection doGetTransactionConnection();

}
