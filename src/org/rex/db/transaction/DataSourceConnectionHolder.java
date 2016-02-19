package org.rex.db.transaction;

import java.sql.Connection;

import org.rex.db.datasource.ConnectionHolder;

/**
 * 数据源事物对象
 */
public class DataSourceConnectionHolder extends ConnectionHolder{
	
	/**
	 * 事物配置
	 */
	private Definition definition;

	public DataSourceConnectionHolder(Connection connection) {
		super(connection);
	}
	
	public DataSourceConnectionHolder(Connection connection, Definition definition) {
		super(connection);
		this.definition = definition;
	}
	
	public Definition getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	/**
	 * 本次事物开启前，需要保存的事物状态
	 */
	private Integer previousIsolationLevel;

	protected void setPreviousIsolationLevel(Integer previousIsolationLevel) {
		this.previousIsolationLevel = previousIsolationLevel;
	}

	public Integer getPreviousIsolationLevel() {
		return previousIsolationLevel;
	}
}
