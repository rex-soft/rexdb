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
package org.rex.db.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;

import org.rex.RMap;
import org.rex.db.exception.DBException;

/**
 * Maps ResultSet.
 * 
 * @version 1.0, 2016-04-05
 * @since Rexdb-1.0
 */
public class ORUtil {

	// -----------temporary caches
	private String[] rsLabels;
	private int[] rsTypes;
	private String[] rsLabelsRenamed;
	
	//------------
	public String[] getResultSetLabels(ResultSet rs) throws DBException{
		readRsMeta(rs);
		return rsLabels;
	}
	
	public int[] getResultSetTypes(ResultSet rs) throws DBException{
		readRsMeta(rs);
		return rsTypes;
	}
	
	public String[] getResultSetLabelsRenamed(ResultSet rs) throws DBException{
		readRsMeta(rs);
		return rsLabelsRenamed;
	}
	
	// -----------ResultSet -> map
	public RMap<String, ?> rs2Map(ResultSet rs) throws DBException {
		readRsMeta(rs);
		RMap<String, Object> results = new RMap<String, Object>();
		for (int i = 0; i < rsLabels.length; i++) {
			try {
				results.put(rsLabelsRenamed[i], getValue(rs, rsLabels[i], rsTypes[i]));
			} catch (SQLException e) {
				throw new DBException("UOR06", e, rsLabels[i], e.getMessage());
			}
		}
		return results;
	}

	// -----------ResultSet -> object
	public <T> T rs2Object(ResultSet rs, T bean) throws DBException {
		readRsMeta(rs);
		Class<?> beanClass = bean.getClass();
		Map<String, Method> writers = ReflectUtil.getWriteableMethods(beanClass);
		Map<String, Class<?>> types = ReflectUtil.getParameterTypes(beanClass);
		for (int i = 0; i < rsLabels.length; i++) {
			Method writer = writers.get(rsLabelsRenamed[i]);
			if(writer == null) continue;
			Class<?> type = types.get(rsLabelsRenamed[i]);
			if(type == null){
				type = writer.getParameterTypes()[0];
				if(type == null) continue;
				else{
					types.put(rsLabelsRenamed[i], type);
				}
			}
			
			Object value = null;
			try {
				value = getValue(rs, rsLabels[i], rsTypes[i], type);
			} catch (SQLException e) {
				throw new DBException("UOR06", e, rsLabels[i], e.getMessage());
			}
			ReflectUtil.invokeMethod(bean, writer, value);
		}
		return bean;
	}

	// -----------column value
	/**
	 * Retrieves the value of the designated column in the current row of this ResultSet object as the given SQL type.
	 */
	public Object getValue(ResultSet rs, String label, int type) throws SQLException, DBException {
		Object value = null;
		switch (type) {
			case Types.CHAR:
			case Types.VARCHAR:
				value = rs.getString(label);
				break;
			case Types.INTEGER:
				value = rs.getInt(label);
				break;
			case Types.DOUBLE:
				value = rs.getDouble(label);
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
			case Types.TIME:
				Timestamp timestamp = rs.getTimestamp(label);
				if (timestamp != null) 
					value = new java.util.Date(timestamp.getTime());
				break;
			case Types.BLOB:
				value = readBlob(rs, label);
				break;
			case Types.CLOB:
				value = readClob(rs, label);
				break;
			default:
				value = rs.getObject(label);
		}

		return value;
	}
	
	/**
	 * Retrieves the value of the designated column in the current row of this ResultSet object as the given java type.
	 */
	public <T> T getValue(ResultSet rs, String label, int sqlType, Class<T> javaType) throws DBException, SQLException {

		if (javaType == Object.class)
			return (T)rs.getObject(label);

		Object value = null;
		switch (sqlType) {
		case Types.BOOLEAN:
			if (javaType == boolean.class || javaType == Boolean.class)
				value = new Boolean(rs.getBoolean(label));
			else if (javaType == String.class)
				value = rs.getString(label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BOOLEAN", javaType.getName());
			break;

		case Types.CHAR:
		case Types.VARCHAR:

			if (javaType == String.class) {
				value = rs.getString(label);
			} else
				throw new DBException("DB-UOR04", label, "sqlType.CHAR|VARCHAR", javaType.getName());
			break;

		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.REAL:
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.LONGVARCHAR:
		case Types.BIT:

			if (javaType == Integer.class || javaType == int.class)
				value = rs.getInt(label);
			else if (javaType == String.class)
				value = rs.getString(label);
			else if (javaType == Float.class || javaType == float.class)
				value = rs.getFloat(label);
			else if (javaType == Double.class || javaType == double.class)
				value = rs.getDouble(label);
			else if (javaType == Long.class || javaType == long.class)
				value = rs.getLong(label);
			else if (javaType == Short.class || javaType == short.class)
				value = rs.getShort(label);
			else if (javaType == BigDecimal.class)
				value = rs.getBigDecimal(label);
			else if (javaType == Byte.class || javaType == byte.class)
				value = rs.getByte(label);
			
			else
				throw new DBException("DB-UOR04", label, "sqlType.BIT|TINYINT|SMALLINT|INTEGER|BIGINT|REAL|FLOAT|DOUBLE|DECIMAL|NUMERIC", javaType.getName());
			break;

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			if (javaType.isArray() && javaType.getComponentType() == byte.class)
				value = rs.getBytes(label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BINARY|VARBINARY|LONGVARBINARY", javaType.getName());
			break;
		case Types.BLOB:
			if (javaType.isArray() && javaType.getComponentType() == byte.class)
				value = readBlob(rs, label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BLOB", javaType.getName());
			break;
		case Types.CLOB:
			String clob = readClob(rs, label);
			if (clob == null)
				break;
			if (javaType.isArray() && javaType.getComponentType() == byte.class){
				value = clob.getBytes();
			}else if (javaType == String.class) {
				value = clob;
			} else if (javaType == StringBuffer.class) {
				value = new StringBuffer(clob);
			} else
				throw new DBException("DB-UOR04", label, "sqlType.CLOB", javaType.getName());
			break;
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			if (javaType == java.sql.Date.class)
				value = rs.getDate(label);
			else if (javaType == java.sql.Time.class)
				value = rs.getTime(label);
			else if (javaType == java.sql.Timestamp.class)
				value = rs.getTimestamp(label);
			else if (javaType == java.util.Date.class) {
				java.sql.Timestamp dateValue = rs.getTimestamp(label);
				if (dateValue != null) {
					value = new java.util.Date(dateValue.getTime());
				}
			} else
				throw new DBException("DB-UOR04", label, "sqlType.DATE|TIMESTAMP|TIME", javaType.getName());
			break;
		default:
			throw new DBException("DB-UOR05", label, sqlType);
		}

		return (T)value;
	}


	/**
	 * Retrieves the value of the Clob column.
	 */
	private String readClob(ResultSet rs, String label) throws SQLException, DBException {
	    String value = null;
	    Clob clob = rs.getClob(label);
	    if (clob != null) {
	      int size = (int) clob.length();
	      value = clob.getSubString(1, size);
	    }
	    return value;
	}

	/**
	 * Retrieves the value of the Blob column.
	 */
	private byte[] readBlob(ResultSet rs, String label) throws SQLException, DBException {
	    Blob blob = rs.getBlob(label);
	    byte[] value = null;
	    if (null != blob) {
	    	value = blob.getBytes(1, (int) blob.length());
	    }
	    return value;
	}

	// -----------ResultSet Meta
	private void readRsMeta(ResultSet rs) throws DBException {
		if (rsLabels == null) {
			try {
				createMeta(rs.getMetaData());
			} catch (SQLException e) {
				throw new DBException("DB-UOR01", e, e.getMessage());
			}
		}
	}

	private void createMeta(ResultSetMetaData meta) throws SQLException {
		int c = meta.getColumnCount();
		rsLabels = new String[c];
		rsTypes = new int[c];
		rsLabelsRenamed = new String[c];

		for (int i = 0; i < c; i++) {
			rsLabels[i] = meta.getColumnLabel(i + 1);
			rsTypes[i] = meta.getColumnType(i + 1);
			rsLabelsRenamed[i] = renameLabel(rsLabels[i]);
		}
	}

	/**
	 * Converts the column name to java-style naming.
	 * sample: CJXM_DM->cjxmDm;AA_BB_CC->aaBbCc
	 */
	private String renameLabel(String label) {
		StringBuilder result = new StringBuilder(label.length());
		char[] chars = label.toCharArray();
		boolean last_ = false;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '_') {
				last_ = true;
				continue;
			}

			if (last_) {
				last_ = false;
				result.append(Character.toUpperCase(chars[i]));
			} else
				result.append(Character.toLowerCase(chars[i]));
		}

		return result.toString();
	}

}
