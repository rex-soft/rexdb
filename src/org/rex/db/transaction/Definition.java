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

/**
 * Transaction definition.
 * 
 * @version 1.0, 2016-02-19
 * @since Rexdb-1.0
 */
public interface Definition {

	String ISOLATION_CONSTANT_PREFIX = "ISOLATION";

	// -------------------------------isolation level
	/**
	 * A constant indicating that transactions are not supported.
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * A constant indicating that dirty reads, non-repeatable reads and phantom reads can occur.
	 */
	int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

	/**
	 * A constant indicating that dirty reads are prevented; non-repeatable reads and phantom reads can occur.
	 */
	int ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;

	/**
	 * A constant indicating that dirty reads and non-repeatable reads are prevented; phantom reads can occur.
	 */
	int ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;

	/**
	 * A constant indicating that dirty reads, non-repeatable reads and phantom reads are prevented.
	 */
	int ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;

	// --------------------default
	/**
	 * Transaction timeout.
	 */
	int TIMEOUT_DEFAULT = -1;

	// --------------------getters
	/**
	 * Returns isolation level
	 */
	int getIsolationLevel();

	/**
	 * Returns timeOut
	 */
	int getTimeout();

	/**
	 * Is readOnly
	 */
	boolean isReadOnly();

	/**
	 * Is auto rollBack
	 */
	boolean isAutoRollback();
}
