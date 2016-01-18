package org.rex.db.transaction;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.datasource.ConnectionHolder;
import org.rex.db.exception.DBRuntimeException;

/**
 * 基于线程的事物管理
 */
public class ThreadConnectionHolder {

	private static ThreadLocal<Map> connections = new ThreadLocal<Map>() {//线程保存的资源
		protected Map<DataSource, ConnectionHolder> initialValue() {
			return new HashMap<DataSource, ConnectionHolder> ();
		}
	};

	public static Map<DataSource, ConnectionHolder> get() {
		return connections.get();
	}

	public static boolean has(DataSource key) {
		return get().containsKey(key);
	}

	public static ConnectionHolder get(DataSource key) {
		return get().get(key);
	}

	public static void bind(DataSource key, ConnectionHolder value) {
		if (has(key)) {
			throw new DBRuntimeException("DB-C10030", key);
		}
		get().put(key, value);
	}

	public static void unbind(DataSource key) {
		if (!has(key)) {
			throw new DBRuntimeException("DB-C10031", key);
		}
		Object value = get().remove(key);
	}
}
