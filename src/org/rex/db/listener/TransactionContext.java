/**
 * Copyright 2016 the original author or authors.
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
package org.rex.db.listener;

import org.rex.db.transaction.Definition;
import org.rex.db.util.ConstantUtil;

/**
 * 包装事物配置等信息，方便监听程序调用
 */
public class TransactionContext extends BaseContext {
	
	/** 
	 * 用于读取常量 
	 */
	static final ConstantUtil CONSTANTS = new ConstantUtil(TransactionContext.class);

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
	private Definition definition;

	/**
	 * 执行事件：TRANSACTION_BEGIN、TRANSACTION_COMMIT、TRANSACTION_ROLLBACK
	 */
	private int event;
	
	public TransactionContext(int event, Definition definition){
		this.event = event;
		this.definition = definition;
	}

	public Definition getDefinition() {
		return definition;
	}

	public int getEvent() {
		return event;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("event=")
		.append(CONSTANTS.toCode(new Integer(event), "TRANSACTION"))
		.append(", definition=[")
		.append(definition)
		.append("]");
		
		
		return sb.toString();
	}
	
}
