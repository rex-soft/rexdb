package org.rex.db.core.statement.dynamic.javassist;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.rex.db.util.SqlUtil;

public class SampleSetter extends StatementSetter{

	public void setParameters(PreparedStatement preparedStatement, Object object, String[] requiredParam) throws SQLException {
		if(preparedStatement == null || object == null || requiredParam == null) return;
		
		org.rex.db.core.statement.dynamic.javassist.SampleBean bean = (org.rex.db.core.statement.dynamic.javassist.SampleBean)object;
		for (int i = 0; i < requiredParam.length; i++) {
			
			if("id".equals(requiredParam[i])){
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getId()));
				continue;
			}
			
			if("date".equals(requiredParam[i])){
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getDate()));
				continue;
			}
		}
		
	}

}
