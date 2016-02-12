package org.rex.db.core.statement.dynamic.javassist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.SqlUtil;

public class SampleSetter extends StatementSetter {

	// public void setParameters(PreparedStatement preparedStatement, Object
	// object, String[] requiredParam) throws SQLException {
	// if(preparedStatement == null || object == null || requiredParam == null)
	// return;
	//
	// SampleBean bean = (SampleBean)object;
	// for (int i = 0; i < requiredParam.length; i++) {
	//
	// if("id".equals(requiredParam[i])){
	// SqlUtil.setParameter(preparedStatement, i + 1,
	// convertValue(bean.getId()));
	// continue;
	// }
	//
	// if("date".equals(requiredParam[i])){
	// SqlUtil.setParameter(preparedStatement, i + 1,
	// convertValue(bean.getDate()));
	// continue;
	// }
	// }
	// }
	//
	// public int[] getColumnCodes(String[] rsLabelsRenamed){
	// int[] cols = new int[rsLabelsRenamed.length];
	// for (int i = 0; i < rsLabelsRenamed.length; i++) {
	// if("id".equals(rsLabelsRenamed[i])){
	// cols[i] = 0;
	// continue;
	// }
	// if("date".equals(rsLabelsRenamed[i])){
	// cols[i] = 1;
	// continue;
	// }
	// }
	// return cols;
	// }
	//
	// public Object readResultSet(ResultSet rs, ORUtil orUtil, int[]
	// requiredColumnCodes) throws SQLException, DBException{
	// String[] rsLabels = orUtil.getResultSetLabels(rs);
	// int[] rsTypes = orUtil.getResultSetTypes(rs);
	//
	// SampleBean bean = new SampleBean();
	// for (int i = 0; i < rsTypes.length; i++) {
	// switch (requiredColumnCodes[i]) {
	// case 0:
	// bean.setId(orUtil.getValue(rs, rsLabels[i], rsTypes[i], int.class));
	// break;
	// case 1:
	// bean.setDate(orUtil.getValue(rs, rsLabels[i], rsTypes[i],
	// java.util.Date.class));
	// break;
	// }
	// }
	// return bean;
	// }

	public void setParameters(PreparedStatement preparedStatement, Object object, String[] requiredParam) throws SQLException {
		if (preparedStatement == null || object == null || requiredParam == null)
			return;
		db.Student bean = (db.Student) object;
		for (int i = 0; i < requiredParam.length; i++) {
			if ("studentId".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getStudentId()));
				continue;
			}
			if ("sex".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getSex()));
				continue;
			}
			if ("birthTime".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getBirthTime()));
				continue;
			}
			if ("readonly".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getReadonly()));
				continue;
			}
			if ("major".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getMajor()));
				continue;
			}
			if ("photo".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getPhoto()));
				continue;
			}
			if ("enrollmentTime".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getEnrollmentTime()));
				continue;
			}
			if ("remark".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getRemark()));
				continue;
			}
			if ("name".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getName()));
				continue;
			}
			if ("birthday".equals(requiredParam[i])) {
				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getBirthday()));
				continue;
			}
		}
	}

	public int[] getColumnCodes(String[] rsLabelsRenamed) {
		int[] cols = new int[rsLabelsRenamed.length];
		for (int i = 0; i < rsLabelsRenamed.length; i++) {
			if ("studentId".equals(rsLabelsRenamed[i])) {
				cols[i] = 0;
				continue;
			}
			if ("sex".equals(rsLabelsRenamed[i])) {
				cols[i] = 1;
				continue;
			}
			if ("birthTime".equals(rsLabelsRenamed[i])) {
				cols[i] = 2;
				continue;
			}
			if ("readonly".equals(rsLabelsRenamed[i])) {
				cols[i] = 3;
				continue;
			}
			if ("major".equals(rsLabelsRenamed[i])) {
				cols[i] = 4;
				continue;
			}
			if ("photo".equals(rsLabelsRenamed[i])) {
				cols[i] = 5;
				continue;
			}
			if ("enrollmentTime".equals(rsLabelsRenamed[i])) {
				cols[i] = 6;
				continue;
			}
			if ("remark".equals(rsLabelsRenamed[i])) {
				cols[i] = 7;
				continue;
			}
			if ("name".equals(rsLabelsRenamed[i])) {
				cols[i] = 8;
				continue;
			}
			if ("birthday".equals(rsLabelsRenamed[i])) {
				cols[i] = 9;
				continue;
			}
		}
		return cols;
	}

	public Object readResultSet(ResultSet rs, ORUtil orUtil, int[] requiredColumnCodes) throws SQLException, DBException {
		String[] rsLabels = orUtil.getResultSetLabels(rs);
		int[] rsTypes = orUtil.getResultSetTypes(rs);
		db.Student bean = new db.Student();
		for (int i = 0; i < rsTypes.length; i++) {
			switch (requiredColumnCodes[i]) {
			case 0:
				bean.setStudentId(((Integer) orUtil.getValue(rs, rsLabels[i], rsTypes[i], Integer.class)).intValue());
				break;
			case 1:
				bean.setSex(((Integer) orUtil.getValue(rs, rsLabels[i], rsTypes[i], Integer.class)).intValue());
				break;
			case 2:
				bean.setBirthTime(((java.util.Date) orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.util.Date.class)));
				break;
			case 3:
				bean.setReadonly(((Integer) orUtil.getValue(rs, rsLabels[i], rsTypes[i], Integer.class)).intValue());
				break;
			case 4:
				bean.setMajor(((Integer) orUtil.getValue(rs, rsLabels[i], rsTypes[i], Integer.class)).intValue());
				break;
			case 5:
				bean.setPhoto(((byte[]) orUtil.getValue(rs, rsLabels[i], rsTypes[i], byte[].class)));
				break;
			case 6:
				bean.setEnrollmentTime(((java.util.Date) orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.util.Date.class)));
				break;
			case 7:
				bean.setRemark(((java.lang.String) orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.lang.String.class)));
				break;
			case 8:
				bean.setName(((java.lang.String) orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.lang.String.class)));
				break;
			case 9:
				bean.setBirthday(((java.util.Date) orUtil.getValue(rs, rsLabels[i], rsTypes[i], java.util.Date.class)));
				break;
			}
		}
		return bean;
	}
}
