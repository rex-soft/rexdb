package org.rex.db.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;

import org.rex.RMap;
import org.rex.db.exception.DBException;

/**
 * convert ResultSet to java bean or Map, use it carefully cause one instance can only handle ONE result set. 
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
	
	// -----------result set to map
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

	// -----------result set to java bean
	public <T> T rs2Object(ResultSet rs, T bean) throws DBException {
		readRsMeta(rs);
		Map<String, Method> writers = ReflectUtil.getWriteableMethods(bean.getClass());
		Map<String, Class<?>> types = ReflectUtil.getParameterTypes(bean.getClass());
		for (int i = 0; i < rsLabels.length; i++) {
			Method writer = writers.get(rsLabelsRenamed[i]);
			Class<?> type = types.get(rsLabelsRenamed[i]);
			if(writer == null || type == null) continue;
			
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

	// -----------get column value
	/**
	 * 从结果集中读取一个字段
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
	 * 将结果集数据转换为Java所需类型
	 * 
	 * @param rs 结果集
	 * @param label 列名
	 * @param sqlType 列的SQL类型
	 * @param paramClassName 待转换的java类型
	 * @return java对象
	 * 
	 * @throws DBException
	 * @throws SQLException
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

		case Types.BIT:
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

			if (javaType == int.class || javaType == Integer.class)
				value = new Integer(rs.getInt(label));
			else if (javaType == float.class || javaType == Float.class)
				value = new Float(rs.getFloat(label));
			else if (javaType == double.class || javaType == Double.class)
				value = new Double(rs.getDouble(label));
			else if (javaType == long.class || javaType == Long.class)
				value = new Long(rs.getLong(label));
			else if (javaType == BigDecimal.class)
				value = rs.getBigDecimal(label);
			else if (javaType == String.class)
				value = rs.getString(label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BIT|TINYINT|SMALLINT|INTEGER|BIGINT|REAL|FLOAT|DOUBLE|DECIMAL|NUMERIC", javaType.getName());
			break;

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			if (javaType.isArray() && javaType.getComponentType() == byte.class)
				value = readBlob(rs, label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BINARY|VARBINARY|LONGVARBINARY|BLOB", javaType.getName());
			break;
		case Types.CLOB:
			String clob = readClob(rs, label);
			if (clob == null)
				break;
			if (javaType.isArray() && javaType.getComponentType() == byte.class)
				value = clob.getBytes();
			else if (javaType == String.class) {
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
	 * 读取clob数据，内容过大时可能造成内存溢出
	 * XXX: find a better way
	 */
	private String readClob(ResultSet rs, String label) throws SQLException, DBException {
		return rs.getString(label);
//		Clob c = rs.getClob(label);
//		if (c != null) {
//			StringBuffer content = new StringBuffer();
//			String line = null;
//			Reader reader = null;
//			BufferedReader br = null;
//			try {
//				reader = c.getCharacterStream();
//				br = new BufferedReader(reader);
//
//				while ((line = br.readLine()) != null) {
//					content.append(line);
//					content.append("\r\n");
//				}
//			} catch (IOException e) {
//				throw new DBException("DB-UOR03", e, label, e.getMessage());
//			} finally {
//				try {
//					if (br != null)
//						br.close();
//					if (reader != null)
//						reader.close();
//				} catch (IOException e) {
//				}
//			}
//
//			return content;
//		}
//		return null;
	}

	/**
	 * 读取blob数据，内容过大时可能造成内存溢出
	 *  XXX: find a better way
	 */
	private byte[] readBlob(ResultSet rs, String label) throws SQLException, DBException {
		return rs.getBytes(label);
//		Blob blob = rs.getBlob(label);
//		if (blob != null && blob.length() > 0) {
//			InputStream bis = blob.getBinaryStream();
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			try {
//				byte[] buffer = new byte[1024 * 1024];
//				int byteRead = 0;
//				while ((byteRead = bis.read(buffer)) != -1) {
//					baos.write(buffer, 0, byteRead);
//				}
//				baos.flush();
//				return baos.toByteArray();
//			} catch (IOException e) {
//				throw new DBException("DB-UOR02", e, label, e.getMessage());
//			} finally {
//				try {
//					if (bis != null)
//						bis.close();
//					if (baos != null)
//						baos.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		return null;
	}

	// -----------result set meta
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
	 * 处理掉label的下划线并转化大小写, 如CJXM_DM->cjxmDm;AA_BB_CC->aaBbCc
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
