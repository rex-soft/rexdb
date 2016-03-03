package org.rex.db.transaction;

import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.ConstantUtil;

/**
 * 定义事物运行参数
 */
public class DefaultDefinition implements Definition {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDefinition.class);
	
	/** 
	 * 用于读取事物定义常量 
	 */
	private static final ConstantUtil CONSTANTS = new ConstantUtil(Definition.class);
	
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
	private boolean autoRollback = false;
	
	//--------construction
	public DefaultDefinition() throws DBException{
		applyConfigrations();
	}
	
	/**
	 * 应用全局配置
	 */
	protected void applyConfigrations() throws DBException{
		Configuration config = Configuration.getCurrentConfiguration();
		autoRollback = config.isAutoRollback();
		try{
			setTimeout(config.getTransactionTimeout());
		}catch(Exception e){
			LOGGER.warn("configuration setting transaction timeout is invalid, {0}, which has been ignored.", e.getMessage());
		}
		
		try{
			if(config.getTransactionIsolation() != null)
				setIsolationLevel(config.getTransactionIsolation());
		}catch(Exception e){
//			e.printStackTrace();
			LOGGER.warn("configuration setting isolation level is invalid, {0}, which has been ignored.", e.getMessage());
		}
	}

	//--------事物隔离级别
	public void setIsolationLevel(String isolationLevelName) throws DBException {
		if (isolationLevelName == null || !isolationLevelName.startsWith(ISOLATION_CONSTANT_PREFIX)) {
			throw new DBException("DB-T0001", isolationLevelName);
		}
		setIsolationLevel(CONSTANTS.asNumber(isolationLevelName).intValue());
	}

	public void setIsolationLevel(int isolationLevel) throws DBException {
		if (!CONSTANTS.getValues(ISOLATION_CONSTANT_PREFIX).contains(new Integer(isolationLevel))) {
			throw new DBException("DB-T0001", isolationLevel);
		}
		this.isolationLevel = isolationLevel;
	}

	public int getIsolationLevel() {
		return isolationLevel;
	}

	//--------超时时间
	public void setTimeout(int timeout) throws DBException {
		if (timeout < TIMEOUT_DEFAULT) {
			throw new DBException("DB-T0002", timeout);
		}
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	//--------设置只读
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	//--------设置事物提交失败时自动回滚
	public void setAutoRollback(boolean autoRollback) {
		this.autoRollback = autoRollback;
	}

	public boolean isAutoRollback() {
		return autoRollback;
	}

	//--------to string
	public String toString() {
		StringBuffer desc = new StringBuffer();
		desc.append("ISOLATION=")
			.append(CONSTANTS.toCode(new Integer(this.isolationLevel), ISOLATION_CONSTANT_PREFIX))
			.append(", TIMEOUT=")
			.append(this.timeout)
			.append(", READ_ONLY=")
			.append(this.readOnly);
		return desc.toString();
	}

}
