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
package org.rex.db.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.rex.db.dialect.impl.DB2Dialect;
import org.rex.db.dialect.impl.DMDialect;
import org.rex.db.dialect.impl.DerbyDialect;
import org.rex.db.dialect.impl.H2Dialect;
import org.rex.db.dialect.impl.HSQLDBDialect;
import org.rex.db.dialect.impl.KingbaseDialect;
import org.rex.db.dialect.impl.MySQLDialect;
import org.rex.db.dialect.impl.Oracle8iDialect;
import org.rex.db.dialect.impl.Oracle9iDialect;
import org.rex.db.dialect.impl.OscarDialect;
import org.rex.db.dialect.impl.PostgreSQLDialect;
import org.rex.db.dialect.impl.SQLServer2005Dialect;
import org.rex.db.dialect.impl.SQLServerDialect;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.DataSourceUtil;

/**
 * Dialect Factory
 * 
 * @version 1.0, 2016-04-05
 * @since Rexdb-1.0
 */
public class DialectFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DialectFactory.class);

	public static Dialect resolveDialect(DataSource dataSource) throws DBException {
		Dialect dialect = null;
		Connection con = null;
		try {
			con = DataSourceUtil.getConnection(dataSource);
			dialect = resolveDialect(con);
		} finally {
			if (con != null) {
				DataSourceUtil.closeConnectionIfNotTransaction(con, dataSource);
			}
		}
		return dialect;
	}
	
	
	/**
	 * Returns dialect by connection
	 * @param connection the database connection
	 * @return dialect for database
	 * @throws DBException could not read resultSet meta
	 */
	public static Dialect resolveDialect(Connection connection) throws DBException {
		if(connection == null) return null;
		try {
			DatabaseMetaData dbmd = connection.getMetaData();
			Dialect dialect = resolveDialectInternal(dbmd);
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("dialect resolved as {0} for connection[{1}]", dialect.getName(), connection.hashCode());
			
			return dialect;
		} catch (SQLException e) {
			throw new DBException("DB-A0001", e, e.getMessage());
		}
	}

	/**
	 * 根据数据库类型创建方言实例
	 * 
	 * @param metaData 数据库元描述
	 * @return 方言实例
	 * @throws DBException 当找不到数据库对应的方言时，抛出异常
	 * @throws SQLException 获取数据库元数据描述失败时，抛出异常
	 */
	private static Dialect resolveDialectInternal(DatabaseMetaData metaData) throws DBException {
		String databaseName;
		int databaseMajorVersion;
		try {
			databaseName = metaData.getDatabaseProductName();
			databaseMajorVersion = metaData.getDatabaseMajorVersion();
		} catch (SQLException e) {
			throw new DBException("DB-A0001", e, e.getMessage());
		}

		if ("Oracle".equals(databaseName)) {
			switch (databaseMajorVersion) {
			case 8:
				return new Oracle8iDialect();
			default:
				return new Oracle9iDialect();
			}
		}

		if (databaseName.startsWith("Microsoft SQL Server")) {
			switch (databaseMajorVersion) {
			case 8:
				return new SQLServerDialect();
			default:
				return new SQLServer2005Dialect();
			}
		}

		if (databaseName.startsWith("DB2"))
			return new DB2Dialect();
		if ("MySQL".equals(databaseName))
			return new MySQLDialect();
		if ("PostgreSQL".equals(databaseName))
			return new PostgreSQLDialect();
		if ("HSQL Database Engine".equals(databaseName))
			return new HSQLDBDialect();
		if ("H2".equals(databaseName))
			return new H2Dialect();
		if ("Apache Derby".equals(databaseName))
			return new DerbyDialect();
		if ("DM DBMS".equals(databaseName))
			return new DMDialect();
		if ("KingbaseES".equals(databaseName))
			return new KingbaseDialect();
		if ("OSCAR".equals(databaseName))
			return new OscarDialect();
			
		throw new DBException("DB-A0002", databaseName);
	}
}
