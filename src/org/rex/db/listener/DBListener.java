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
package org.rex.db.listener;

import java.util.EventListener;

/**
 * Listener interface.
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public interface DBListener extends EventListener{
	
	/**
	 * Before executing SQLs.
	 */
	public void onExecute(SqlContext context);
	
	/**
	 * After executing SQLs.
	 */
	public void afterExecute(SqlContext context, Object results);
	
	/**
	 * Before executing transactions.
	 */
	public void onTransaction(TransactionContext context);
	
	/**
	 * After executing transactions.
	 */
	public void afterTransaction(TransactionContext context);
}
