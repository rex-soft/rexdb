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

import java.util.Enumeration;
import java.util.Vector;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Used to manage registered listeners, and fire events.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class ListenerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerManager.class);
	
	private final Vector<DBListener> listeners;
	
	public ListenerManager(){
		listeners = new Vector<DBListener>();
	}
	
	/**
	 * Registers listener.
	 */
	public void registe(DBListener listener) {
		listeners.addElement(listener);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("listener {0} registed.", listener.getClass().getName());
	}

	/**
	 * Removes listener.
	 */
	public void remove(DBListener listener) {
		listeners.remove(listener);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("listener {0} removed.", listener.getClass().getName());
	}

	/**
	 * Returns size of listeners.
	 */
	public int getSize(){
		return listeners.size();
	}
	
	/**
	 * Has listener.
	 */
	public boolean hasListener(){
		return listeners.size() > 0;
	}
	
	public DBListener[] getListeners(){
		return listeners.toArray(new DBListener[listeners.size()]);
	}
	
	/**
	 * Fires event before executing SQLs.
	 */
	public void fireOnExecute(final SqlContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.onExecute(context);
			}
		});
	}
	
	/**
	 * Fires event after executing SQLs
	 */
	public void fireAfterExecute(final SqlContext context, final Object result){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.afterExecute(context, result);
			}
		});
	}
	
	/**
	 * Fires event before transaction.
	 */
	public void fireOnTransaction(final TransactionContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.onTransaction(context);
			}
		});
	}
	
	/**
	 * Fires event after transaction.
	 */
	public void fireAfterTransaction(final TransactionContext context){
		notify(new ListenerCaller(){
			public void call(DBListener listener){
				listener.afterTransaction(context);
			}
		});
	}
	
	//---------private
	private void notify(ListenerCaller caller) {
		Enumeration<DBListener> enums = listeners.elements();
		while (enums.hasMoreElements()) {
			DBListener listener = enums.nextElement();
			caller.call(listener);
		}
	}
	
	interface ListenerCaller{
		public void call(DBListener listener);
	}
}
