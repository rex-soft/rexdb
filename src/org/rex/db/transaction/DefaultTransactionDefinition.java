package org.rex.db.transaction;

import org.rex.db.exception.DBRuntimeException;

/**
 * 定义事物运行参数
 */
public class DefaultTransactionDefinition implements TransactionDefinition {

	/** 
	 * 描述字符串中的事务超时值的前缀 
	 */
	public static final String TIMEOUT_PREFIX = "timeout_";

	/**
	 * 描述字符串中只读事务的标记 
	 */
	public static final String READ_ONLY_MARKER = "readOnly";

	/** 
	 * 用于读取事物定义常量 
	 */
	private static final Constants constants = new Constants(TransactionDefinition.class);

	
	//-------------
	/**
	 * 事物传播机制
	 */
	private int propagationBehavior = PROPAGATION_REQUIRED;

	/**
	 * 隔离级别
	 */
	private int isolationLevel = ISOLATION_DEFAULT;

	/**
	 * 超时时间
	 */
	private int timeout = TIMEOUT_DEFAULT;

	/**
	 * 只读
	 */
	private boolean readOnly = false;
	
	/**
	 * 设置事物提交失败时是否回滚
	 */
	private boolean rollbackOnCommitFailure = false;

	public DefaultTransactionDefinition() {
	}

	public DefaultTransactionDefinition(int propagationBehavior) {
		this.propagationBehavior = propagationBehavior;
	}

	/**
	 * 设置事物传播机制
	 */
	public final void setPropagationBehaviorName(String constantName) {
		if (constantName == null || !constantName.startsWith(PROPAGATION_CONSTANT_PREFIX)) {
			throw new DBRuntimeException("DB-C10025", constantName);
		}
		setPropagationBehavior(constants.asNumber(constantName).intValue());
	}

	public final void setPropagationBehavior(int propagationBehavior){
		if (!constants.getValues(PROPAGATION_CONSTANT_PREFIX).contains(new Integer(propagationBehavior))) {
			throw new DBRuntimeException("DB-C10026", propagationBehavior);
		}
		this.propagationBehavior = propagationBehavior;
	}

	public final int getPropagationBehavior() {
		return propagationBehavior;
	}

	/**
	 * 设置事物隔离级别
	 */
	public final void setIsolationLevelName(String constantName) {
		if (constantName == null || !constantName.startsWith(ISOLATION_CONSTANT_PREFIX)) {
			throw new DBRuntimeException("DB-C10027", constantName);
		}
		setIsolationLevel(constants.asNumber(constantName).intValue());
	}

	public final void setIsolationLevel(int isolationLevel) {
		if (!constants.getValues(ISOLATION_CONSTANT_PREFIX).contains(new Integer(isolationLevel))) {
			throw new DBRuntimeException("DB-C10028", isolationLevel);
		}
		this.isolationLevel = isolationLevel;
	}

	public final int getIsolationLevel() {
		return isolationLevel;
	}

	/**
	 * 设置超时时间
	 */
	public final void setTimeout(int timeout) {
		if (timeout < TIMEOUT_DEFAULT) {
			throw new DBRuntimeException("DB-C10029", timeout);
		}
		this.timeout = timeout;
	}

	public final int getTimeout() {
		return timeout;
	}

	/**
	 * 设置只读选项
	 */
	public final void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public final boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * 设置事物提交失败时是否回滚
	 */
	public void setRollbackOnCommitFailure(boolean rollbackOnCommitFailure) {
		this.rollbackOnCommitFailure = rollbackOnCommitFailure;
	}

	/**
	 * 返回事物提交失败时是否回滚
	 */
	public boolean isRollbackOnCommitFailure() {
		return rollbackOnCommitFailure;
	}

	public String toString() {
		StringBuffer desc = new StringBuffer();
		desc.append(constants.toCode(new Integer(this.propagationBehavior), PROPAGATION_CONSTANT_PREFIX));
		desc.append(',');
		desc.append(constants.toCode(new Integer(this.isolationLevel), ISOLATION_CONSTANT_PREFIX));
		if (this.timeout != TIMEOUT_DEFAULT) {
			desc.append(',');
			desc.append(TIMEOUT_PREFIX + this.timeout);
		}
		if (this.readOnly) {
			desc.append(',');
			desc.append(READ_ONLY_MARKER);
		}
		return desc.toString();
	}

}
