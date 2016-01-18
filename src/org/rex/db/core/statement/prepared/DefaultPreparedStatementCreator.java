package org.rex.db.core.statement.prepared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * 用于创建预编译对象并设置参数
 */
public class DefaultPreparedStatementCreator extends PreparedStatementSetter implements PreparedStatementCreator {
	
	public DefaultPreparedStatementCreator(String sql) {
		super(sql);
	}
	
	/**
	 * 创建预编译对象并设置参数
	 */
	public PreparedStatement createPreparedStatement(Connection conn, Ps ps) throws DBException, SQLException {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = conn.prepareStatement(sql);
		} catch (SQLException e) {
			throw new DBException("DB-C10011", e, sql);
		}
		
		if(ps != null)
			setParameters(preparedStatement, ps);
		
		return preparedStatement;
	}
	
	/**
	 * 为预编译赋值
	 */
	protected void setParameters(PreparedStatement preparedStatement, Ps ps) throws DBException, SQLException{
		List<Ps.SqlParameter> parameters = ps.getParameters();

		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
			if(parameter instanceof Ps.SqlOutParameter)
				throw new DBException("DB-C10039", i, parameters);
			
			setParam(preparedStatement, parameter.getSqlType(), parameter.getValue(), i + 1);
		}
	}

//	public String toString() {
//		StringBuffer sbuf = new StringBuffer()
//			.append("type = PreparedStatement")
//			.append(", sql = ").append(sql);
//		return sbuf.toString();
//	}
}
