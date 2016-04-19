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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.DataSourceUtil;

/**
 * DataSource Transaction Manager.
 * 
 * @version 1.0, 2016-03-28
 * @since Rexdb-1.0
 */
public class DataSourceTransactionManager extends AbstractTransactionManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceTransactionManager.class);

	private DataSource dataSource;

	public DataSourceTransactionManager(DataSource dataSource) {
		if (dataSource == null) {
			throw new DBRuntimeException("DB-T0003");
		}
		this.dataSource = dataSource;
	}

	/**
	 * Returns current dataSource.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	//-------------------------------implements
	/**
	 * Returns ConnectionHolder for.
	 */
	protected ConnectionHolder doGetTransaction() {
		if (ThreadConnectionHolder.has(dataSource)) {
			return ThreadConnectionHolder.get(dataSource);
		}else
			return null;
	}

	/**
	 * Begins transaction.
	 */
	protected void doBegin(Definition definition) throws DBException {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if (connectionHolder == null) {
			Connection con = DataSourceUtil.getConnection(dataSource);
			connectionHolder = new DataSourceConnectionHolder(con, definition);
		}
		
		try {
			beginTransaction(connectionHolder, definition);
		} catch (SQLException e) {
			throw new DBException("DB-T0004", e, e.getMessage(), connectionHolder.getConnection().hashCode(), dataSource.hashCode());
		}

		ThreadConnectionHolder.bind(dataSource, connectionHolder);
	}
	
	private void beginTransaction(DataSourceConnectionHolder connectionHolder, Definition definition) throws SQLException{
		Connection conn = connectionHolder.getConnection();
		
		// set isolation level
		if (definition.getIsolationLevel() != Definition.ISOLATION_DEFAULT) {
			connectionHolder.setPreviousIsolationLevel(conn.getTransactionIsolation());
			conn.setTransactionIsolation(definition.getIsolationLevel());
		}

		// set readonly
		if (definition.isReadOnly()) {
			conn.setReadOnly(true);
		}

		// set auto commit mode to false
		conn.setAutoCommit(false);

		// set timeout
		if (definition.getTimeout() > 0) {
			connectionHolder.setTimeoutInSeconds(definition.getTimeout());
		}
	}


	/**
	 * Commits transaction.
	 */
	protected void doCommit() throws DBException {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null){
			throw new DBException("DB-T0005", dataSource.hashCode());
		}
		
		try {
			connectionHolder.getConnection().commit();
		} catch (SQLException e) {
			if (connectionHolder.getDefinition().isAutoRollback()) 
				doRollbackOnCommitException(e);
			throw new DBException("DB-T0008", e, connectionHolder.getConnection().hashCode(), dataSource.hashCode());
		}
	}
	
	/**
	 * Auto rollback transaction on exception.
	 */
	private void doRollbackOnCommitException(Throwable ex) throws DBException {
		doRollback();
	}

	/**
	 * Rollback transaction.
	 */
	protected void doRollback() throws DBException {
		ConnectionHolder connectionHolder = doGetTransaction();
		if(connectionHolder == null){
			throw new DBException("DB-T0005", dataSource.hashCode());
		}
		
		try {
			connectionHolder.getConnection().rollback();
		} catch (SQLException e) {
			throw new DBException("DB-T0009", e, connectionHolder.getConnection().hashCode(), dataSource.hashCode());
		}
	}

	/**
	 * Resets connection.
	 */
	protected void afterCompletion() {
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null) return;
		
		Connection con = connectionHolder.getConnection();
		ThreadConnectionHolder.unbind(this.dataSource);

		try {
			con.setAutoCommit(true);
			if (connectionHolder.getPreviousIsolationLevel() != null) {
				con.setTransactionIsolation(connectionHolder.getPreviousIsolationLevel());
			}

			if (con.isReadOnly()) {
				con.setReadOnly(false);
			}
		}catch (Exception ex) {
			LOGGER.warn("reseting connection state failed after transaction, {0}.", ex, ex.getMessage());
		}

		try {
			DataSourceUtil.closeConnectionIfNotTransaction(con, this.dataSource);
		}catch (DBException ex) {
			LOGGER.warn("closing connection failed after transaction, {0}.", ex, ex.getMessage());
		}
	}

	/**
	 * Gets transaction connection.
	 */
	protected Connection doGetTransactionConnection(){
		DataSourceConnectionHolder connectionHolder = (DataSourceConnectionHolder)doGetTransaction();
		if(connectionHolder == null) return null;
		return connectionHolder.getConnection();
	}
}
