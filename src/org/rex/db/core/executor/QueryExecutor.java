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
package org.rex.db.core.executor;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQL执行接口
 */
public interface QueryExecutor {

	/**
	 * 执行查询
	 */
	ResultSet executeQuery(Statement stmt, String sql) throws SQLException;

	/**
	 * 执行预编译查询
	 */
	ResultSet executeQuery(PreparedStatement ps) throws SQLException;
	
	/**
	 * 执行调用
	 */
	boolean execute(CallableStatement statement) throws SQLException;

	/**
	 * 执行批处理
	 */
	int[] executeBatch(Statement statement) throws SQLException;
	
	/**
	 * 执行更新
	 */
	int executeUpdate(PreparedStatement ps) throws SQLException;
	
	/**
	 * 执行更新
	 */
	int executeUpdate(Statement stmt, String sql) throws SQLException;
}
