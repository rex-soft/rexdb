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
 * Database transaction.
 * 
 * @author z
 * @version 1.0, 2016-04-17
 * @since Rexdb-1.0
 */
public class DBTransaction extends DefaultDefinition {

	protected static Map<DataSource, TransactionManager> managers = new HashMap<DataSource, TransactionManager>();

	protected static TransactionManager jtaTransactionManager = null;

	protected static TransactionManager getTransactionManager(DataSource dataSource) {
		if (!managers.containsKey(dataSource))
			managers.put(dataSource, new DataSourceTransactionManager(dataSource));

		return managers.get(dataSource);
	}

	protected static TransactionManager getJtaTransactionManager() {
		if (jtaTransactionManager == null)
			jtaTransactionManager = new JtaTransactionManager();

		return jtaTransactionManager;
	}

	// ------transaction
	public DBTransaction() throws DBException {
		super();
	}

	/**
	 * Begins a new transaction for the dataSource. The framework will get a new connection from the specified dataSource, set
	 * auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same thread are using this
	 * connection until committing or rollback.
	 * 
	 * @param dataSource the DataSource that transaction is beginning for.
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public void begin(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).begin(this);
	}

	/**
	 * Begins a new transaction with definition for the dataSource. The framework will get a new connection from the specified
	 * dataSource, set auto-commit mode to false and put it into ThreadLocal. Operations for this dataSource in the same thread
	 * are using this connection until committing or rollback.
	 * 
	 * @param dataSource the DataSource that transaction is beginning for.
	 * @param definition transaction definition.
	 * @throws DBException if configuration wasn't loaded, could not access database, transaction is already begin etc.
	 */
	public static void begin(DataSource dataSource, DefaultDefinition definition) throws DBException {
		getTransactionManager(dataSource).begin(definition);
	}

	/**
	 * Commits transaction for the dataSource.
	 * 
	 * @param dataSource the DataSource that transaction is committing for.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void commit(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).commit();
	}

	/**
	 * Rollback transaction for the dataSource.
	 * 
	 * @param dataSource the DataSource that transaction is rollback for.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static void rollback(DataSource dataSource) throws DBException {
		getTransactionManager(dataSource).rollback();
	}

	/**
	 * Gets connection from ThreadLocal for the dataSource.
	 * 
	 * @param dataSource the DataSource that transaction is on.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not find connection from ThreadLocal
	 *             for the given dataSource etc.
	 */
	public static Connection getTransactionConnection(DataSource dataSource) throws DBException {
		return getTransactionManager(dataSource).getTransactionConnection();
	}

	// -------jta
	/**
	 * Begins a JTA transaction with definition.
	 * 
	 * @param definition transaction definition.
	 * @throws DBException if configuration wasn't loaded, could not access database, could not begin transaction etc.
	 */
	public static void beginJta(DefaultDefinition definition) throws DBException {
		getJtaTransactionManager().begin(definition);
	}

	/**
	 * Begins a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not begin transaction etc.
	 */
	public static void beginJta() throws DBException {
		getJtaTransactionManager().begin(null);
	}

	/**
	 * Commits a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not commit transaction etc.
	 */
	public static void commitJta() throws DBException {
		getJtaTransactionManager().commit();
	}

	/**
	 * Rollback a JTA transaction.
	 * 
	 * @throws DBException if configuration wasn't loaded, could not access database, could not commit transaction etc.
	 */
	public static void rollbackJta() throws DBException {
		getJtaTransactionManager().rollback();
	}
}
