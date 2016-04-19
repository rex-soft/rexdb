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
package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.listener.ListenerManager;
import org.rex.db.listener.TransactionContext;

/**
 * Abstract Transaction Manager.
 * 
 * @version 1.0, 2016-02-19
 * @since Rexdb-1.0
 */
public abstract class AbstractTransactionManager implements TransactionManager {

	//----------------------------------implements
	/**
	 * Begins Transaction.
	 */
	public void begin(Definition definition) throws DBException {
		if (definition == null)
			definition = new DefaultDefinition();

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
	 * Commits Transaction.
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
	 * Rollback Transaction.
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
	 * Gets Transaction Connection.
	 */
	public Connection getTransactionConnection() throws DBException {
		return doGetTransactionConnection();
	}

	//----------------------------------Listener
	/**
	 * Fires Event before Transaction.
	 */
	protected TransactionContext fireOnEvent(int event) throws DBException{
		return fireOnEvent(event, null);
	}
	
	protected TransactionContext fireOnEvent(int event, Definition definition) throws DBException{
		TransactionContext context = null;
		ListenerManager listenerManager = getListenerManager();
		if(listenerManager.hasListener()){
			context = getContext(event, definition);
			listenerManager.fireOnTransaction(context);
		}
		return context;
	}
	
	/**
	 * Fires Event after Transaction.
	 */
	protected void fireAfterEvent(TransactionContext context) throws DBException{
		if(context != null){
			getListenerManager().fireAfterTransaction(context);
		}
	}
	
	protected TransactionContext getContext(int event, Definition definition){
		return new TransactionContext(event, definition);
	}
	
	private ListenerManager getListenerManager() throws DBException{
		return Configuration.getCurrentConfiguration().getListenerManager();
	}
	
	//----------------------------------abstract

	/**
	 * Do begin.
	 */
	protected abstract void doBegin(Definition definition) throws DBException;

	/**
	 * Do commit.
	 */
	protected abstract void doCommit() throws DBException;

	/**
	 * Do rollback.
	 */
	protected abstract void doRollback() throws DBException;

	/**
	 * Do after operation.
	 */
	protected abstract void afterCompletion();
	
	/**
	 * Gets transaction connection.
	 */
	protected abstract Connection doGetTransactionConnection();

}
