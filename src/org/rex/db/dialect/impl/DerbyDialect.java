package org.rex.db.dialect.impl;

/**
 * DerbyDialect
 */
public class DerbyDialect extends DB2Dialect{
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql(){
		return "SELECT 1 FROM SYS.SYSTABLES";
	}
	
	// ------------------------------------------------------------版本信息
	public String getName(){
		return "DERBY";
	}
}
