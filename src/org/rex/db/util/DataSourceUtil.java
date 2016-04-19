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
package org.rex.db.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBException;
import org.rex.db.transaction.ThreadConnectionHolder;

/**
 * DataSource utilities.
 * 
 * @version 1.0, 2016-02-02
 * @since Rexdb-1.0
 */
public class DataSourceUtil {

	/**
	 * Copies the given properties and hides password.
	 */
	public static Properties hiddenPassword(Properties properties) {
		Properties clone = null;
		if (properties != null) {
			clone = (Properties) properties.clone();
			if (clone.containsKey("password"))
				clone.put("password", "******(hidden)");
		}
		return clone;
	}

	/**
	 * Gets connection from the given DataSource.
	 */
	public static Connection getConnection(DataSource ds) throws DBException {
		if (ds == null)
			throw new DBException("DB-UDS01");

		ConnectionHolder holder = (ConnectionHolder) ThreadConnectionHolder.get(ds);
		if (holder != null) {
			return holder.getConnection();
		} else {
			try {
				return ds.getConnection();
			} catch (SQLException e) {
				throw new DBException("DB-UDS02", e, ds.hashCode(), e.getMessage());
			}
		}
	}

	/**
	 * Closes the given connection if it is not in transaction.
	 */
	public static void closeConnectionIfNotTransaction(Connection con, DataSource ds) throws DBException {
		if (con == null || ThreadConnectionHolder.has(ds)) {
			return;
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			throw new DBException("DB-UDS03", e, e.getMessage(), ds.hashCode(), con.hashCode());
		}
	}

}
