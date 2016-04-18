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
package org.rex.db.listener.impl;

import org.rex.db.listener.SqlContext;
import org.rex.db.listener.TransactionContext;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 框架内置的监听，用于监控所有数据库操作，并直接使用System.out.println打印
 */
public class SqlConsolePrinterListener extends SqlDebugListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlConsolePrinterListener.class);
	
	public void setLevel(String level) {
		LOGGER.warn("SqlConsolePrinterListener dose not support property {0}.", level);
	}

	public void onExecute(SqlContext context) {
		if(!simple)
			System.out.println("On execute " + serializeSqlContext(context));
	}

	public void afterExecute(SqlContext context, Object results) {
		System.out.println("After execute " + serializeSqlContext(context, results));
	}

	public void onTransaction(TransactionContext context) {
		if(!simple)
			System.out.println("On transaction " + serializeTransactionContext(context));
	}

	public void afterTransaction(TransactionContext context) {
		System.out.println("After transaction " + serializeTransactionContext(context));
	}
}
