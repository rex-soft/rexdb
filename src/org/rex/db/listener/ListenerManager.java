package org.rex.db.listener;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 监听注册器
 */
public class ListenerManager {
	
//	private static ListenerManager instance;
//	
//	static {
//		instance = new ListenerManager();
//	}
//	
//	public static ListenerManager getInstance(){
//		return instance;
//	}
	
	private Vector<DBListener> listeners;
	
	public ListenerManager(){
		listeners = new Vector<DBListener>();
	}
	
	//注册监听
	public void registe(DBListener listener) {
		listeners.addElement(listener);
	}

	//解除监听
	public void remove(DBListener listener) {
		listeners.remove(listener);
	}

	//已注册的监听数量
	public int getSize(){
		return listeners.size();
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
	
	/**
	 * 查询是否有注册的监听
	 */
	public boolean hasListener(){
		return listeners.size() > 0;
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
