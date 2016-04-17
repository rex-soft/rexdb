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

import java.util.Enumeration;
import java.util.Vector;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 用于管理已注册的监听程序，当发生数据库操作时，会逐个通知监听
 */
public class ListenerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerManager.class);
	
	private final Vector<DBListener> listeners;
	
	public ListenerManager(){
		listeners = new Vector<DBListener>();
	}
	
	/**
	 * 注册监听
	 * @param listener
	 */
	public void registe(DBListener listener) {
		listeners.addElement(listener);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("listener {0} registed.", listener.getClass().getName());
	}

	/**
	 * 解除监听
	 * @param listener
	 */
	public void remove(DBListener listener) {
		listeners.remove(listener);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("listener {0} removed.", listener.getClass().getName());
	}

	/**
	 * 已注册的监听数量
	 * @return
	 */
	public int getSize(){
		return listeners.size();
	}
	
	/**
	 * 查询是否有注册的监听
	 */
	public boolean hasListener(){
		return listeners.size() > 0;
	}
	
	public DBListener[] getListeners(){
		return listeners.toArray(new DBListener[listeners.size()]);
	}
	
	/**
	 * 触发SQL执行前事件
	 */
	public void fireOnExecute(final SqlContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.onExecute(context);
			}
		});
	}
	
	/**
	 * 触发SQL执行后事件
	 */
	public void fireAfterExecute(final SqlContext context, final Object result){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.afterExecute(context, result);
			}
		});
	}
	
	/**
	 * 事物操作前事件
	 */
	public void fireOnTransaction(final TransactionContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.onTransaction(context);
			}
		});
	}
	
	/**
	 * 事物操作后事件
	 */
	public void fireAfterTransaction(final TransactionContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.afterTransaction(context);
			}
		});
	}
	
	//---------private
	
	/**
	 * 通知监听类
	 */
	private void notify(ListenerCaller caller) {
		Enumeration<DBListener> enums = listeners.elements();
		while (enums.hasMoreElements()) {
			DBListener listener = enums.nextElement();
			caller.call(listener);
		}
	}
	
	/**
	 * 用于回调Listener的方法
	 */
	interface ListenerCaller{
		public void call(DBListener listener);
	}
}
