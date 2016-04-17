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

	private static ThreadLocal<Map<DataSource, ConnectionHolder>> connections = new ThreadLocal<Map<DataSource, ConnectionHolder>>() {//线程保存的资源
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
			throw new DBRuntimeException("DB-T0006", key);
		}
		get().put(key, value);
	}

	public static void unbind(DataSource key) {
		if (!has(key)) {
			throw new DBRuntimeException("DB-T0007", key);
		}
		get().remove(key);
	}
}
