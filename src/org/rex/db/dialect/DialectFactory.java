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
import org.rex.db.dialect.impl.MySQLDialect;
import org.rex.db.dialect.impl.Oracle8iDialect;
import org.rex.db.dialect.impl.Oracle9iDialect;
import org.rex.db.dialect.impl.PostgreSQLDialect;
import org.rex.db.dialect.impl.SQLServer2005Dialect;
import org.rex.db.dialect.impl.SQLServerDialect;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.DataSourceUtil;

/**
 * 根据数据库类型创建方言
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
	 * 获取连接对应的方言，注意该方法不会主动关闭连接
	 * @param connection 数据库连接
	 * @return 方言
	 * @throws DBException 获取元数据描述失败时，抛出异常
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
				return new Oracle9iDialect();//其它版本使用Oracle 9i
			}
		}

		if (databaseName.startsWith("Microsoft SQL Server")) {
			switch (databaseMajorVersion) {
			case 8:
				return new SQLServerDialect();
			default:
				return new SQLServer2005Dialect();// 其它版本使用SQL Server 2005
			}
		}

		if (databaseName.startsWith("DB2"))
			return new DB2Dialect();
		if ("HSQL Database Engine".equals(databaseName))
			return new HSQLDBDialect();
		if ("MySQL".equals(databaseName))
			return new MySQLDialect();
		if ("PostgreSQL".equals(databaseName))
			return new PostgreSQLDialect();
		if ("Apache Derby".equals(databaseName))
			return new DerbyDialect();
		if ("DM DBMS".equals(databaseName))
			return new DMDialect();
		if ("H2".equals(databaseName)) {
			return new H2Dialect();
		}

		throw new DBException("DB-A0002", databaseName);
	}
}
