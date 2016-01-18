package org.rex.db.core.statement.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.core.statement.prepared.PreparedStatementSetter;
import org.rex.db.exception.DBException;

/**
 * 用于创建批处理对象并设置参数
 */
public class DefaultBatchPreparedStatementCreator extends PreparedStatementSetter implements BatchPreparedStatementCreator {

	public DefaultBatchPreparedStatementCreator(String sql) {
		super(sql);
	}

	public PreparedStatement createBatchPreparedStatement(Connection conn, Ps[] ps) throws DBException, SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		
        for(int i = 0; ps != null && i < ps.length; i++){
        	if(ps[i] != null)
        		setParameters(preparedStatement, ps[i]);
        }
        
        return preparedStatement;
	}
	
	/**
	 * 为预编译赋值
	 */
	protected void setParameters(PreparedStatement preparedStatement, Ps ps) throws DBException, SQLException{
		
		List<Ps.SqlParameter> parameters = ps.getParameters();
    	for(int i = 0; i < parameters.size(); i++){
    		Ps.SqlParameter parameter = parameters.get(i);
    		if(parameter instanceof Ps.SqlOutParameter)
				throw new DBException("DB-C10039", i, parameters);
    		
			setParam(preparedStatement, parameter.getSqlType(), parameter.getValue(), i + 1);
    	}
    		
    	preparedStatement.addBatch();
	}
	
//	public String toString() {
//		StringBuffer sbuf = new StringBuffer()
//			.append("type = BatchPreparedStatement")
//			.append(", sql = ").append(sql);
//		return sbuf.toString();
//	}
}
