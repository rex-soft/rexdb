package org.rex.db.listener;

import org.rex.db.transaction.Constants;
import org.rex.db.transaction.TransactionDefinition;

/**
 * 执行事物的上下文
 */
public class TransactionContext extends BaseContext {
	
	/** 
	 * 用于读取常量 
	 */
	private static final Constants constants = new Constants(TransactionContext.class);

	/**
	 * 事物的状态：开启
	 */
	public static final int TRANSACTION_BEGIN = 1;

	/**
	 * 事物的状态：提交
	 */
	public static final int TRANSACTION_COMMIT = 2;

	/**
	 * 事物的状态：回滚
	 */
	public static final int TRANSACTION_ROLLBACK = 3;

	/**
	 * 事物配置
	 */
	private TransactionDefinition definition;

	/**
	 * 执行事件：TRANSACTION_BEGIN、TRANSACTION_COMMIT、TRANSACTION_ROLLBACK
	 */
	private int event;
	
	public TransactionContext(int event, TransactionDefinition definition){
		this.event = event;
		this.definition = definition;
	}

	public TransactionDefinition getDefinition() {
		return definition;
	}

	public int getEvent() {
		return event;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("event=")
		.append(constants.toCode(new Integer(event), "TRANSACTION"))
		.append(", definition=[")
		.append(definition)
		.append("]");
		
		
		return sb.toString();
	}
	
}
