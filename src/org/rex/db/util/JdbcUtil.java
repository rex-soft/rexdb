package org.rex.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class JdbcUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

	/**
	 * 检查执行过程中的各种警告
	 */
	public static void checkWarnings(Connection connection, Statement statement, ResultSet resultSet){
		if(connection != null) checkConnectionWarning(connection);
		if(statement != null) checkStatementWarning(statement);
		if(resultSet != null) checkResultSetWarning(resultSet);
	}
	
	/**
	 * 检查Connection中的警告
	 */
	public static void checkConnectionWarning(Connection connection) {
		try {
			SQLWarning warning = connection.getWarnings();
			if (warning != null) 
				LOGGER.warn("Connection Warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("Couldn't get connection warning, {0}.", e, e.getMessage());
		}
	}
	
	/**
	 * 检查Statement中的警告
	 */
	public static void checkStatementWarning(Statement statement) {
		try {
			SQLWarning warning = statement.getWarnings();
			if (warning != null) 
				LOGGER.warn("Statement Warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("Couldn't get statement warning, {0}.", e, e.getMessage());
		}
	}
	
	/**
	 * 检查ResultSet中的警告
	 */
	public static void checkResultSetWarning(ResultSet resultSet) {
		try {
			SQLWarning warning = resultSet.getWarnings();
			if (warning != null) 
				LOGGER.warn("ResultSet Warning was found, {0}.", warning, warning.getMessage());
		} catch (SQLException e) {
			LOGGER.warn("Couldn't get resultset warning, {0}.", e, e.getMessage());
		}
	}
}
