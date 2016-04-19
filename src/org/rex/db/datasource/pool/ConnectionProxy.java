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
package org.rex.db.datasource.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A connection proxy that overrides close method
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public interface ConnectionProxy extends Connection {

	void unclose();

	void closeConnection() throws SQLException;

	boolean isForceClosed();

	long getCreationTime();

	long getLastAccess();

	void markLastAccess();

	void setConnectionPool(SimpleConnectionPool parentPool);
}
