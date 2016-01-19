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
	
	protected void doBegin(TransactionDefinition definition) throws DBException {
		try {
//			applyTimeout(timeout);
			doGetTransaction().begin();
		}catch (NotSupportedException ex) {
			throw new DBException("JTA implementation does not support nested transactions", ex);
		}catch (UnsupportedOperationException ex) {
			throw new DBException("JTA implementation does not support nested transactions", ex);
		}catch (SystemException ex) {
			throw new DBException("JTA failure on begin", ex);
		}
	}

	protected void applyTimeout(int timeout) throws DBException, SystemException {
		if (timeout > TransactionDefinition.TIMEOUT_DEFAULT) {
			doGetTransaction().setTransactionTimeout(timeout);
		}
	}


	protected void doCommit() throws DBException {
		try {
			int jtaStatus = doGetTransaction().getStatus();
			if (jtaStatus == Status.STATUS_NO_TRANSACTION) {
				throw new DBException("JTA transaction already completed - probably rolled back");
			}
			
			if (jtaStatus == Status.STATUS_ROLLEDBACK) {
				try {
					doGetTransaction().rollback();
				}catch (IllegalStateException ex) {
				}
				throw new DBException("JTA transaction already rolled back (probably due to a timeout)");
			}
			doGetTransaction().commit();
		}catch (RollbackException ex) {
			throw new DBException("JTA transaction unexpectedly rolled back (maybe due to a timeout)", ex);
		}catch (IllegalStateException ex) {
			throw new DBException("Unexpected internal transaction state", ex);
		}catch (SystemException ex) {
			throw new DBException("JTA failure on commit", ex);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	protected void doRollback() throws DBException {
		try {
			int jtaStatus = doGetTransaction().getStatus();
			if (jtaStatus != Status.STATUS_NO_TRANSACTION) {
				try {
					doGetTransaction().rollback();
				}catch (IllegalStateException ex) {
					if (jtaStatus != Status.STATUS_ROLLEDBACK) 
						throw new DBException("Unexpected internal transaction state", ex);
				}
			}
		}catch (SystemException ex) {
			throw new DBException("JTA failure on rollback", ex);
		}
	}

	//-----------------------
	protected UserTransaction doGetTransaction() throws DBException {
		if(userTransaction != null)
			return userTransaction;
		
		initUserTransaction();
		if (userTransaction == null)
			throw new DBException("No JTA UserTransaction available -  programmatic PlatformTransactionManager.getTransaction usage not supported");
		else
			return userTransaction;
	}
	
	protected void initUserTransaction() throws DBException{
		if (userTransaction != null) return;
		
		String transactionName = userTransactionName != null ? userTransactionName : DEFAULT_USER_TRANSACTION_NAME, UserTransaction;
		userTransaction = lookupTransaction(transactionName, UserTransaction.class);
	}
	
	
	//从jndi中查找UserTransaction对象
	protected <T> T lookupTransaction(String transactionName, Class<T> clazz) throws DBException {
		InitialContext initCtx = null;
		try {
			if (environment == null) {
				initCtx = new InitialContext();
			} else {
				initCtx = new InitialContext(environment);
			}

			Object obj = initCtx.lookup(transactionName);
			if(!clazz.isInstance(obj)) throw new DBException("JNDI对象类型不匹配"+clazz.getName());
			
			return (T)obj;
		} catch (NamingException e) {
			throw new DBException("There was an error configuring JndiDataSourceTransactionPool. Cause: "+ e, e);
		}finally {
			try {
				if (initCtx != null)
					initCtx.close();
			}catch (NamingException ex) {
			}
		}
	}

	@Override
	protected void afterCompletion() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Connection doGetTransactionConnection() {
		// TODO Auto-generated method stub
		return null;
	}
}
