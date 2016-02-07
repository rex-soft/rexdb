package org.rex.db.core.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

/**
 * 默认的预编译对象创建器
 */
public class DefaultStatementCreator implements StatementCreator{

	//----------Statement
	public Statement createStatement(Connection conn) throws DBException, SQLException{
		return conn.createStatement();
	}
	
	//----------Prepared Statement
	public PreparedStatement createPreparedStatement(Connection conn, String sql, Ps ps) throws DBException, SQLException{
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		if(ps != null)
			setParameters(preparedStatement, ps);
		return preparedStatement;
	}
	
	//----------Callable Statement
	/**
	 * 创建预编译对象
	 */
	public CallableStatement createCallableStatement(Connection conn, String sql) throws SQLException {
		return createCallableStatement(conn, sql, null);
	}
	
	/**
	 * 创建预编译对象
	 */
	public CallableStatement createCallableStatement(Connection conn, String sql, Ps ps) throws SQLException {
		CallableStatement cs = conn.prepareCall(sql);
		
		if(ps == null) return cs;
		
		List<Ps.SqlParameter> parameters = ps.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
			
			//输出参数
			if(parameter instanceof Ps.SqlOutParameter) {
				String paramName = ((Ps.SqlOutParameter<?>) parameter).getParamName();
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
	
	//----------Batch Statement
	public Statement createBatchStatement(Connection conn, String[] sql) throws SQLException {
		
		Statement stmt = conn.createStatement();
		for (int i = 0; i < sql.length; i++) {
			stmt.addBatch(sql[i]);
		}
		return stmt;
	}
	
	//----------Batch Prepared Statement
	public PreparedStatement createBatchPreparedStatement(Connection conn, String sql, Ps[] ps) throws DBException, SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		
		if(ps != null){
	        for(int i = 0; i < ps.length; i++){
	        	if(ps[i] != null)
	        		setParameters(preparedStatement, ps[i]);
	        }
		}
		
        return preparedStatement;
	}
	
	//------private methods
	/**
	 * 为预编译赋值
	 */
	protected void setParameters(PreparedStatement preparedStatement, Ps ps) throws DBException, SQLException{
		List<Ps.SqlParameter> parameters = ps.getParameters();

		for (int i = 0; i < parameters.size(); i++) {
			Ps.SqlParameter parameter = parameters.get(i);
			if(parameter instanceof Ps.SqlOutParameter)
				throw new DBException("DB-C0001", i, parameters, ps);
			
			setParam(preparedStatement, parameter.getSqlType(), parameter.getValue(), i + 1);
		}
	}
	
	/**
	 * 为PreparedStatement赋值
	 */
	protected void setParam(PreparedStatement ps, int sqlType, Object value, int index) throws SQLException{
		if (value == null) {
			ps.setNull(index, sqlType);
		}else {
			switch (sqlType) {
				case Types.VARCHAR : 
					ps.setString(index, (String) value);
					break;

				default : 
					ps.setObject(index, value, sqlType);
					break;
			}
		}
	}



}
