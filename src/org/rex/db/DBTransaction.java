package org.rex.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.transaction.DataSourceTransactionManager;
import org.rex.db.transaction.DefaultDefinition;
import org.rex.db.transaction.JtaTransactionManager;
import org.rex.db.transaction.TransactionManager;

/**
 * 事务
 * 
 * @author z
 */
public class DBTransaction extends DefaultDefinition {

	protected static Map<DataSource, TransactionManager> managers = new HashMap<DataSource, TransactionManager>();
	
	protected static TransactionManager jtaTransactionManager = null;

	protected static TransactionManager getTransactionManager(DataSource dataSource) {
		if (!managers.containsKey(dataSource))
			managers.put(dataSource, new DataSourceTransactionManager(dataSource));

		return managers.get(dataSource);
	}
	
	protected static TransactionManager getJtaTransactionManager(){
		if(jtaTransactionManager == null)
			jtaTransactionManager = new JtaTransactionManager();
		
		return jtaTransactionManager;
	}

	//------transaction
	public DBTransaction() throws DBException {
		super();
	}
	
	/**
	 * 开始事物
	 */
	public void begin(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).begin(this);
	}

	/**
	 * 开始事物
	 */
	public static void begin(DataSource dataSource, DefaultDefinition definition) throws DBException {
		getTransactionManager(dataSource).begin(definition);
	}

	/**
	 * 提交事物
	 */
	public static void commit(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).commit();
	}

	/**
	 * 回滚事物
	 */
	public static void rollback(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).rollback();
	}

	/**
	 * 获取连接
	 */
	public static Connection getTransactionConnection(DataSource dataSource) throws DBException {
		return getTransactionManager(dataSource).getTransactionConnection();
	}

	
	//-------jta
	/**
	 * 开始事物
	 */
	public static void beginJta(DefaultDefinition definition) throws DBException {
		getJtaTransactionManager().begin(definition);
	}

	/**
	 * 开始事物
	 */
	public static void beginJta() throws DBException {
		getJtaTransactionManager().begin(null);
	}

	/**
	 * 提交事物
	 */
	public static void commitJta() throws DBException {
		getJtaTransactionManager().commit();
	}
	
	/**
	 * 回滚事物
	 */
	public static void rollbackJta() throws DBException {
		getJtaTransactionManager().rollback();
	}
}
