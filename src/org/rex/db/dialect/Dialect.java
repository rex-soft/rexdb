package org.rex.db.dialect;

/**
 * 针对不同数据库的sql特征，进行不同的包装
 */
public interface Dialect {


	// ------------------------------------------------------------分页SQL
	public LimitHandler getLimitHandler(int rows);
	
	public LimitHandler getLimitHandler(int offset, int rows);
	
	// ------------------------------------------------------------数据库测试SQL
	/**
	 * 获取一个针对数据库的测试SQL，如果能执行，说明连接有效
	 */
	public String getTestSql();

	// ------------------------------------------------------------版本信息
	/**
	 * 获取方言版本信息
	 * @return 方言版本
	 */
	public String getName();
}
