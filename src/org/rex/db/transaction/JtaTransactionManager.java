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
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.rex.db.exception.DBException;

/**
 * 简单的JTA事物管理
 */
public class JtaTransactionManager extends AbstractTransactionManager{

	public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

	private transient UserTransaction userTransaction;

	private String userTransactionName;

	private Properties environment;
	
	//----------------------------------implements
	/**
	 * 开始事物
	 */
	protected void doBegin(Definition definition) throws DBException {
		try {
			applyTimeout(definition.getTimeout());
			doGetTransaction().begin();
		} catch (NotSupportedException e) {
			throw new DBException("DB-T0012", e, e.getMessage(), userTransactionName, environment);
		} catch (SystemException e) {
			throw new DBException("DB-T0012", e, e.getMessage(), userTransactionName, environment);
		}
	}

	/**
	 * 设置超时时间
	 * @param timeout
	 * @throws DBException
	 * @throws SystemException
	 */
	protected void applyTimeout(int timeout) throws DBException, SystemException {
		if (timeout > Definition.TIMEOUT_DEFAULT) {
			doGetTransaction().setTransactionTimeout(timeout);
		}
	}

	/**
	 * 提交事务
	 */
	protected void doCommit() throws DBException {
		try {
			int jtaStatus = doGetTransaction().getStatus();
			if (jtaStatus == Status.STATUS_NO_TRANSACTION) {
				throw new DBException("DB-T0013", "JTA transaction not found, maybe JTA transaction already rolled back or timeout", userTransactionName, environment);
			}
			
			if (jtaStatus == Status.STATUS_ROLLEDBACK) {
				try {
					doGetTransaction().rollback();
				}catch (IllegalStateException ex) {
				}
				throw new DBException("DB-T0013", "JTA transaction have been rolled back or timeout", userTransactionName, environment);
			}
			
			doGetTransaction().commit();
		} catch (SecurityException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		} catch (IllegalStateException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		} catch (RollbackException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		} catch (HeuristicMixedException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		} catch (HeuristicRollbackException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		} catch (SystemException e) {
			throw new DBException("DB-T0013", e, e.getMessage(), userTransactionName, environment);
		}
	}
	
	protected void doRollback() throws DBException {
		try {
			int jtaStatus = doGetTransaction().getStatus();
			if (jtaStatus != Status.STATUS_NO_TRANSACTION) {
				try {
					doGetTransaction().rollback();
				}catch (IllegalStateException e) {
					if (jtaStatus != Status.STATUS_ROLLEDBACK) 
						throw new DBException("DB-C10059", e, e.getMessage());
				}
			}
		}catch (SystemException e) {
			throw new DBException("DB-T0014", e, e.getMessage(), userTransactionName, environment);
		}
	}

	//-----------------------
	/**
	 * 从jndi中获取数据源
	 * @return UserTransaction对象
	 * @throws DBException
	 */
	protected UserTransaction doGetTransaction() throws DBException {
		if(userTransaction == null){
			String transactionName = userTransactionName != null ? userTransactionName : DEFAULT_USER_TRANSACTION_NAME;
			userTransaction = lookupTransaction(transactionName, UserTransaction.class);
		}
			
		return userTransaction;
	}
	
	/**
	 * 从jndi中查找UserTransaction对象
	 * @param transactionName 执行事务对象名称
	 * @param clazz jndi查找到的对象类型
	 * @return jndi中查找到的对象
	 * @throws DBException
	 */
	protected <T> T lookupTransaction(String transactionName, Class<T> clazz) throws DBException {
		InitialContext initCtx = null;
		try {
			if (environment == null) {
				initCtx = new InitialContext();
			} else {
				initCtx = new InitialContext(environment);
			}

			Object obj = initCtx.lookup(transactionName);
			if(!clazz.isInstance(obj)) 
				throw new DBException("DB-T0011", transactionName, obj.getClass().getName(), clazz.getName());
			
			return (T)obj;
		} catch (NamingException e) {
			throw new DBException("DB-T0010", e, transactionName, e.getMessage());
		}finally {
			try {
				if (initCtx != null)
					initCtx.close();
			}catch (NamingException ex) {
			}
		}
	}

	protected void afterCompletion() {
	}

	protected Connection doGetTransactionConnection() {
		return null;
	}
}
