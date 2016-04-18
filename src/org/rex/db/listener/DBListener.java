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
 * 基于观察者模式实现的监听接口，由具体监听程序实现，将会拦截基于框架的所有数据库操作
 */
public interface DBListener extends EventListener{
	
	/**
	 * 执行任意SQL前调用该方法
	 */
	public void onExecute(SqlContext context);
	
	/**
	 * 执行SQL后调用该方法
	 */
	public void afterExecute(SqlContext context, Object results);
	
	/**
	 * 开始事物前调用该方法
	 */
	public void onTransaction(TransactionContext context);
	
	/**
	 * 结束事物后调用该方法
	 */
	public void afterTransaction(TransactionContext context);
}
