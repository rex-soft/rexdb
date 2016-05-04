/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rex.db.transaction;

import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.ConstantUtil;

/**
 * Default transaction definition.
 * 
 * @version 1.0, 2016-03-03
 * @since Rexdb-1.0
 */
public class DefaultDefinition implements Definition {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDefinition.class);
	
	/** 
	 * Constants utilities.
	 */
	private static final ConstantUtil CONSTANTS = new ConstantUtil(Definition.class);
	
	/**
	 * Transaction isolation level.
	 */
	private int isolationLevel = ISOLATION_DEFAULT;

	/**
	 * Transaction timeout.
	 */
	private int timeout = TIMEOUT_DEFAULT;

	/**
	 * Transaction readOnly setting.
	 */
	private boolean readOnly = false;
	
	/**
	 * Transaction autoRollBack setting.
	 */
	private boolean autoRollback = false;
	
	//--------construction
	public DefaultDefinition() throws DBException{
		applyConfigrations();
	}
	
	/**
	 * Applies current configuration.
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
			LOGGER.warn("configuration setting isolation level is invalid, {0}, which has been ignored.", e.getMessage());
		}
	}

	//--------isolation level
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

	//--------timeout
	public void setTimeout(int timeout) throws DBException {
		if (timeout < TIMEOUT_DEFAULT) {
			throw new DBException("DB-T0002", timeout);
		}
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	//--------readOnly
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	//--------autoRollback
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
