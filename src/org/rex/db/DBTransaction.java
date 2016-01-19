package org.rex.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.transaction.DataSourceTransactionManager;
import org.rex.db.transaction.DefaultTransactionDefinition;
import org.rex.db.transaction.TransactionManager;

/**
 * 事务对象
 * 
 * @author z
 */
public class DBTransaction extends DefaultTransactionDefinition {

	protected static Map<DataSource, TransactionManager> managers = new HashMap<DataSource, TransactionManager>();

	protected static TransactionManager getTransactionManager(DataSource dataSource) {
		if (!managers.containsKey(dataSource))
			managers.put(dataSource, new DataSourceTransactionManager(dataSource));

		return managers.get(dataSource);
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
	public static void begin(DataSource dataSource, DefaultTransactionDefinition definition) throws DBException {
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

}
