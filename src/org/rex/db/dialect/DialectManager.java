package org.rex.db.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.dialect.impl.DB2Dialect;
import org.rex.db.dialect.impl.DMDialect;
import org.rex.db.dialect.impl.DerbyDialect;
import org.rex.db.dialect.impl.H2Dialect;
import org.rex.db.dialect.impl.HSQLDialect;
import org.rex.db.dialect.impl.MySQLDialect;
import org.rex.db.dialect.impl.Oracle8iDialect;
import org.rex.db.dialect.impl.Oracle9iDialect;
import org.rex.db.dialect.impl.PostgreSQLDialect;
import org.rex.db.dialect.impl.SQLServer2005Dialect;
import org.rex.db.dialect.impl.SQLServerDialect;
import org.rex.db.exception.DBException;
import org.rex.db.util.DataSourceUtil;

/**
 * 获取数据库相应的方言
 */
public class DialectManager {

	// ---已初始化的方言实例
	private Map<String, Dialect> dialectInstances = new HashMap<String, Dialect>();
	
	/**
	 * 为数据源指定一个方言
	 * @param dataSource 数据源
	 * @param dialect 用户指定的方言
	 */
	public void setDialect(DataSource dataSource, Dialect dialect){
		dialectInstances.put(String.valueOf(dataSource.hashCode()), dialect);
	}
	
	/**
	 * 获取数据源对应的方言
	 * @param dataSource 数据源
	 * @return 方言实例
	 * @throws SQLException 获取数据库元数据描述失败时，抛出异常
	 * @throws DBException 
	 */
	public Dialect getDialect(DataSource dataSource) throws DBException {
		String hashCode = String.valueOf(dataSource.hashCode());
		Dialect dialect = null;

		if (dialectInstances.containsKey(hashCode)) {
			dialect = dialectInstances.get(hashCode);
		} else {
			Connection con = null;
			try {
				con = DataSourceUtil.getConnection(dataSource);
				DatabaseMetaData dbmd = con.getMetaData();
				dialect = resolveDialectInternal(dbmd);
			} catch (SQLException e) {
				throw new DBException("DB-D10001", e, dataSource);
			} finally {
				if (con != null) {
					DataSourceUtil.closeConnection(con, dataSource);
				}
			}

			dialectInstances.put(hashCode, dialect);
		}
		return dialect;
	}

	/**
	 * 根据数据库类型创建方言实例
	 * 
	 * @param metaData 数据库元描述
	 * @return 方言实例
	 * @throws DBException 当找不到数据库对应的方言时，抛出异常
	 * @throws SQLException 获取数据库元数据描述失败时，抛出异常
	 */
	private Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException, DBException {
		String databaseName = metaData.getDatabaseProductName();
		int databaseMajorVersion = metaData.getDatabaseMajorVersion();

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
			return new HSQLDialect();
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

		throw new DBException("DB-D10002", databaseName);
	}
}
