package org.rex.db.transaction;

import org.rex.db.datasource.ConnectionHolder;

/**
 * 每次执行线程前，事物状态
 */
public class TransactionStatus {

	/**
	 * 连接对象
	 */
	private ConnectionHolder connectionHolder;
	
	protected TransactionStatus(ConnectionHolder connectionHolder) {
		this.connectionHolder = connectionHolder;
	}

	protected void setConnectionHolder(ConnectionHolder connectionHolder) {
		this.connectionHolder = connectionHolder;
	}

	public ConnectionHolder getConnectionHolder() {
		return connectionHolder;
	}
}
