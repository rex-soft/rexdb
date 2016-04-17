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

import java.sql.Connection;

import org.rex.db.exception.DBException;

/**
 * 事物管理
 */
public interface TransactionManager {

	/**
	 * 返回当前事物状态
	 */
	void begin(Definition definition) throws DBException;

	/**
	 * 提交事务
	 */
	void commit() throws DBException;

	/**
	 * 回滚事务
	 */
	void rollback() throws DBException;

	/**
	 * 获取开启事物的连接
	 */
	Connection getTransactionConnection() throws DBException;
}
