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

public class JtaTransactionManager extends AbstractTransactionManager{

	public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

	private transient UserTransaction userTransaction;

	private String userTransactionName;

	private Properties environment;
	
	//----------------------------------implements
	/**
	 * 开始事物
	 */
	protected void doBegin(TransactionDefinition definition) throws DBException {
		try {
			applyTimeout(definition.getTimeout());
			doGetTransaction().begin();
		} catch (NotSupportedException e) {
			throw new DBException("DB-C10055", e, e.getMessage());
		} catch (SystemException e) {
			throw new DBException("DB-C10055", e, e.getMessage());
		}
	}

	/**
	 * 设置超时时间
	 * @param timeout
	 * @throws DBException
	 * @throws SystemException
	 */
	protected void applyTimeout(int timeout) throws DBException, SystemException {
		if (timeout > TransactionDefinition.TIMEOUT_DEFAULT) {
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
				throw new DBException("DB-C10056");
			}
			
			if (jtaStatus == Status.STATUS_ROLLEDBACK) {
				try {
					doGetTransaction().rollback();
				}catch (IllegalStateException ex) {
				}
				throw new DBException("DB-C10057");
			}
			doGetTransaction().commit();
		} catch (SecurityException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
		} catch (IllegalStateException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
		} catch (RollbackException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
		} catch (HeuristicMixedException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
		} catch (HeuristicRollbackException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
		} catch (SystemException e) {
			throw new DBException("DB-C10058", e, e.getMessage());
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
			throw new DBException("DB-C10059", e, e.getMessage());
		}
	}

	//-----------------------
	/**
	 * 从jndi中获取数据源
	 * @return UserTransaction对象
	 * @throws DBException
	 */
	protected UserTransaction doGetTransaction() throws DBException {
		if(userTransaction != null)
			return userTransaction;
		
		initUserTransaction();
		if (userTransaction == null)
			throw new DBException("DB-C10062");
		else
			return userTransaction;
	}
	
	/**
	 * 初始化事物对象
	 * @throws DBException
	 */
	protected void initUserTransaction() throws DBException{
		if (userTransaction != null) return;
		
		String transactionName = userTransactionName != null ? userTransactionName : DEFAULT_USER_TRANSACTION_NAME;
		userTransaction = lookupTransaction(transactionName, UserTransaction.class);
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
				throw new DBException("DB-C10060", transactionName, clazz.getName());
			
			return (T)obj;
		} catch (NamingException e) {
			throw new DBException("DB-C10061", e, e.getMessage());
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
