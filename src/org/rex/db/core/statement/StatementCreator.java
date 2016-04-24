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
package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.rex.db.dialect.LimitHandler;
import org.rex.db.exception.DBException;

/**
 * Statement creator interface.
 * 
 * @version 1.0, 2016-02-12
 * @since Rexdb-1.0
 */
public interface StatementCreator {

	//----------Statement
	public Statement createStatement(Connection connection) throws DBException, SQLException;
	
	//----------PreparedStatement
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException;
	
	public PreparedStatement createPreparedStatement(Connection connection, String sql, Object parameters, LimitHandler limitHandler) throws DBException, SQLException;
	
	//----------CallableStatement
	public CallableStatement createCallableStatement(Connection connection, String sql) throws DBException, SQLException;
	
	public CallableStatement createCallableStatement(Connection connection, String sql, Object parameters) throws DBException, SQLException;
	
	//----------BatchStatement
	public Statement createBatchStatement(Connection connection, String[] sql) throws DBException, SQLException;
	
	//----------BatchPreparedStatement
	public PreparedStatement createBatchPreparedStatement(Connection connection, String sql, Object[] parametersArray) throws DBException, SQLException;
}
