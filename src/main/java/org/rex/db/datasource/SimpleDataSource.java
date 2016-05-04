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
package org.rex.db.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.datasource.pool.SimpleConnectionPool;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

/**
 * Simple DataSource
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public class SimpleDataSource implements DataSource {

	private final SimpleConnectionPool pool;
	
	public SimpleDataSource(Properties properties) throws DBException {
		pool = new SimpleConnectionPool(properties);
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	public Connection getConnection(String username, String password) throws SQLException {
		throw new DBRuntimeException("DB-D0002", "getConnection");
	}

	public int getLoginTimeout() throws SQLException {
		throw new DBRuntimeException("DB-D0002", "getLoginTimeout");
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new DBRuntimeException("DB-D0002", "setLoginTimeout");
	}

	public PrintWriter getLogWriter() {
		throw new DBRuntimeException("DB-D0002", "getLogWriter");
	}

	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new DBRuntimeException("DB-D0002", "setLogWriter");
	}

}
