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
package org.rex.db.dynamic.javassist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.SqlUtil;

/**
 * A sample of java bean convertor.
 * 
 * @version 1.0, 2016-03-16
 * @since Rexdb-1.0
 */
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
	
//	public void setParameters(PreparedStatement preparedStatement, Object object, int[] requiredColumnCodes) throws SQLException {
//		if (preparedStatement == null || object == null || requiredColumnCodes == null)
//			return;
//
//		SampleBean bean = (SampleBean) object;
//		for (int i = 0; i < requiredColumnCodes.length; i++) {
//
//			switch (requiredColumnCodes[i]) {
//			case 0:
//				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getId()));
//				break;
//			case 1:
//				SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.getDate()));
//				break;
//			default:
//				SqlUtil.setNull(preparedStatement, i + 1);
//			}
//			
//		}
//	}

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
			
			cols[i] = -1;
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
