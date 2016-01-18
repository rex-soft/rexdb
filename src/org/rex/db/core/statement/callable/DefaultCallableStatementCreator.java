package org.rex.db.core.statement.callable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.core.statement.prepared.PreparedStatementSetter;
import org.rex.db.exception.DBException;

/**
 * 用于创建调用对象并设置参数
 */
public class DefaultCallableStatementCreator extends PreparedStatementSetter implements CallableStatementCreator {

	public DefaultCallableStatementCreator(String sql) {
		super(sql);
	}

	/**
	 * 创建预编译对象
	 */
	public CallableStatement createCallableStatement(Connection conn) throws DBException, SQLException {
		return createCallableStatement(conn, null);
	}
	
	/**
	 * 创建预编译对象
	 */
	public CallableStatement createCallableStatement(Connection conn, Ps ps) throws DBException, SQLException {
		CallableStatement cs = conn.prepareCall(sql);
//		ParameterMetaData meta = cs.getParameterMetaData();
//		if(meta.getParameterCount() != ps.getParameterSize())
//			throw new W11DBException("");
		
		if(ps == null) return cs;
		
		List<Ps.SqlParameter> parameters = ps.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
//			int paramMode = meta.getParameterMode(i + 1);
			
			//输出参数
			if(parameter instanceof Ps.SqlOutParameter) {
				String paramName = ((Ps.SqlOutParameter) parameter).getParamName();
				if (paramName != null) {
					cs.registerOutParameter(i + 1, parameter.getSqlType(), paramName);
				}else {
					cs.registerOutParameter(i + 1, parameter.getSqlType());
				}
			}
			
			//输入参数，或输入输出参数
			if(!(parameter instanceof Ps.SqlOutParameter) || parameter instanceof Ps.SqlInOutParameter){
				setParam(cs, parameter.getSqlType(), parameter.getValue(), i + 1);
			}
		}
		return cs;
	}

	
//	public String toString() {
//		StringBuffer sbuf = new StringBuffer()
//			.append("type = CallableStatement")
//			.append(", sql = ").append(sql);
//		return sbuf.toString();
//	}
}