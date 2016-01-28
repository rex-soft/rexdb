package org.rex.db.dialect;

import org.rex.db.Ps;

/**
 * 针对不同数据库的sql特征，进行不同的包装
 */
public interface Dialect {

	// ------------------------------------------------------------分页SQL
	/**
	 * 获取分页语句
	 * 
	 * @param sql SQL语句
	 * @param limit 记录数
	 * @return 分业后的SQL语句
	 */
	public String getLimitSql(String sql, int rows);

	/**
	 * 获取分页语句
	 * 
	 * @param sql SQL语句
	 * @param offset 偏移
	 * @param limit 记录数
	 * @return 分业后的SQL语句
	 */
	public String getLimitSql(String sql, int offset, int rows);

	/**
	 * 当将sql包装为分页sql后，重置其预编译参数，增加分页参数。
	 * 
	 * @param ps 预编译参数
	 * @param limit 查询记录数
	 */
	public Ps getLimitPs(Ps ps, int rows);

	/**
	 * 当将sql包装为分页sql后，重置其预编译参数，增加分页参数。
	 * @param ps  预编译参数
	 * @param offset 偏移值
	 * @param limit 查询记录数
	 */
	public Ps getLimitPs(Ps ps, int offset, int rows);
	
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
