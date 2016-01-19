package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.configuration.Configuration;
import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.TransactionContext;

public abstract class AbstractTransactionManager implements TransactionManager {

	//----------------------------------
	/**
	 * 执行SQL前监听
	 * @throws DBException 
	 */
	private TransactionContext fireOnEvent(int event) throws DBException{
		return fireOnEvent(event, null);
	}
	
	private TransactionContext fireOnEvent(int event, TransactionDefinition definition) throws DBException{
		TransactionContext context = null;
		ListenerManager listenerManager = getListenerManager();
		if(listenerManager.hasListener()){
			context = getContext(event, definition);
			listenerManager.fireOnTransaction(context);
		}
		return context;
	}
	
	/**
	 * 执行SQL后监听
	 * @throws DBException 
	 */
	private void fireAfterEvent(TransactionContext context) throws DBException{
		if(context != null){
			getListenerManager().fireAfterTransaction(context);
		}
	}
	
	private TransactionContext getContext(int event, TransactionDefinition definition){
		return new TransactionContext(event, definition);
	}
	
	private ListenerManager getListenerManager() throws DBException{
		return Configuration.getCurrentConfiguration().getListenerManager();
	}
	
	//----------------------------------implements
	/**
	 * 获取事物对象
	 */
	public void begin(TransactionDefinition definition) throws DBException {
		
		if (definition == null) {
			definition = new DefaultTransactionDefinition();
		}

		//使用当前的事务，如果当前没有事务，就抛出异常。
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {//XXX:取消这种无用选项
//			ConnectionHolder connectionHolder = doGetTransaction();
//			if(connectionHolder == null)
//				throw new DBException("DB-C10017", "PROPAGATION_MANDATORY");
		}

		//如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。
		if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED) {
			try {
				TransactionContext context = fireOnEvent(TransactionContext.TRANSACTION_BEGIN, definition);
				doBegin(definition);//开始事物
				fireAfterEvent(context);
			} catch (Exception e) {
				throw new DBException("DB-C10022", e, e.getMessage());
			}

		//如果当前没有事务，就以非事务方式执行。
		}else {
			//do nothing
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

	//----------------------------------abstract
	/**
	 * 返回当前事物对象
	 */
//	protected abstract ConnectionHolder doGetTransaction();

	/**
	 * 开始事物
	 */
	protected abstract void doBegin(TransactionDefinition definition) throws Exception;

	/**
	 * 提交事务
	 */
	protected abstract void doCommit() throws Exception;

	/**
	 * 回滚
	 */
	protected abstract void doRollback() throws Exception;

	/**
	 * 事务结束后执行
	 */
	protected abstract void afterCompletion();
	
	/**
	 * 获取事务所属连接
	 */
	protected abstract Connection doGetTransactionConnection();

}
