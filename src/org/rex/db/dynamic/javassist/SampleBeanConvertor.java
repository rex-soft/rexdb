package org.rex.db.dynamic.javassist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.SqlUtil;

public class SampleBeanConvertor extends BeanConvertor {

	public void setParameters(PreparedStatement preparedStatement, Object object, String[] requiredParam) throws SQLException {
		if (preparedStatement == null || object == null || requiredParam == null)
			return;

		SampleBean bean = (SampleBean) object;
		for (int i = 0; i < requiredParam.length; i++) {

			if ("id".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getId()));
				continue;
			}

			if ("date".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getDate()));
				continue;
			}
		}
	}

	public int[] getColumnCodes(String[] rsLabelsRenamed) {
		int[] cols = new int[rsLabelsRenamed.length];
		for (int i = 0; i < rsLabelsRenamed.length; i++) {
			if ("id".equals(rsLabelsRenamed[i])) {
				cols[i] = 0;
				continue;
			}
			if ("date".equals(rsLabelsRenamed[i])) {
				cols[i] = 1;
				continue;
			}
		}
		return cols;
	}

	public Object readResultSet(ResultSet rs, ORUtil orUtil, int[] requiredColumnCodes) throws SQLException, DBException {
		String[] rsLabels = orUtil.getResultSetLabels(rs);
		int[] rsTypes = orUtil.getResultSetTypes(rs);

		SampleBean bean = new SampleBean();
		for (int i = 0; i < rsTypes.length; i++) {
			switch (requiredColumnCodes[i]) {
			case 0:
				bean.setId(((Integer) orUtil.getValue(rs, rsLabels[i], rsTypes[i], Integer.class)).intValue());
				break;
			case 1:
				bean.setDate((java.util.Date)orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.util.Date.class));
				break;
			}
		}
		return bean;
	}
}
