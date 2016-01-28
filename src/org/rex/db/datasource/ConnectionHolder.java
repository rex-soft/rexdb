package org.rex.db.datasource;

import java.sql.Connection;
import java.util.Date;

/**
 * 用于包装数据库连接，额外增加了用于给事物计时的超时时间等属性
 * 为方便调用，包装后的数据库连接通常被放置在ThreadLocal中
 */
public class ConnectionHolder{

	private final Connection connection;

	/**
	 * 超时时间
	 */
	private Date deadline;

	public ConnectionHolder(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * 设置超时时间
	 */
	public void setTimeoutInSeconds(int seconds) {
		setTimeoutInMillis(seconds * 1000);
	}

	public void setTimeoutInMillis(long millis) {
		this.deadline = new Date(System.currentTimeMillis() + millis);
	}

	/**
	 * 获取超时时间
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * 获取剩余可执行时间
	 */
	public int getTimeToLiveInSeconds() {
		double diff = ((double) getTimeToLiveInMillis()) / 1000;
		return (int) Math.ceil(diff);
	}

	public long getTimeToLiveInMillis() {
		return deadline.getTime() - System.currentTimeMillis();
	}

}
