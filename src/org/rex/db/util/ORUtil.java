package org.rex.db.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
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

public class ORUtil {

	// -----------temporary caches
	private String[] rsLabels;
	private int[] rsTypes;
	private String[] rsLabelsRenamed;

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
		Map<String, Method> beanParams = ReflectUtil.getWriteableMethods(bean.getClass());
		for (int i = 0; i < rsLabels.length; i++) {
			// 获取POJO写入方法
			Method writer = null;

			// 要求类属性必须和数据库列名一致，并且全为
			writer = beanParams.get(rsLabelsRenamed[i]);
			if (writer == null) continue;

			Class<?> param0 = writer.getParameterTypes()[0];
			String paramClassName = param0.isArray() ? param0.getComponentType().getName() + "[]" : param0.getName();// set方法只有一个参数

			// 从结果集中取值并转换类型
			Object value = null;
			try {
				getValue(rs, rsLabels[i], rsTypes[i]);
//				value = getValue(rs, rsLabels[i], rsTypes[i], paramClassName);
			} catch (SQLException e) {
				throw new DBException("UOR06", e, rsLabels[i], e.getMessage());
			}
			// 赋值
//			ReflectUtil.invokeMethod(bean, writer, value);
		}

		return bean;
	}

	// -----------get column value
	/**
	 * 从结果集中读取一个字段
	 */
	private Object getValue(ResultSet rs, String label, int type) throws SQLException, DBException {
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
	private Object getValue(ResultSet rs, String label, int sqlType, String paramClassName) throws DBException, SQLException {
		if ("java.lang.Object".equals(paramClassName))
			return rs.getObject(label);

		Object value = null;
		switch (sqlType) {
		case Types.BOOLEAN:
			if ("java.lang.Boolean".equals(paramClassName) || "boolean".equals(paramClassName))
				value = new Boolean(rs.getBoolean(label));
			else if ("java.lang.String".equals(paramClassName))
				value = rs.getString(label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BOOLEAN", paramClassName);
			break;

		case Types.CHAR:
		case Types.VARCHAR:

			if ("java.lang.String".equals(paramClassName)) {
				value = rs.getString(label);
			} else
				throw new DBException("DB-UOR04", label, "sqlType.CHAR|VARCHAR", paramClassName);
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

			if ("java.lang.Integer".equals(paramClassName) || "int".equals(paramClassName))
				value = new Integer(rs.getInt(label));
			else if ("java.lang.Float".equals(paramClassName) || "float".equals(paramClassName))
				value = new Float(rs.getFloat(label));
			else if ("java.lang.Double".equals(paramClassName) || "double".equals(paramClassName))
				value = new Double(rs.getDouble(label));
			else if ("java.lang.Long".equals(paramClassName) || "long".equals(paramClassName))
				value = new Long(rs.getLong(label));
			else if ("java.math.BigDecimal".equals(paramClassName))
				value = rs.getBigDecimal(label);
			else if ("java.lang.String".equals(paramClassName))
				value = rs.getString(label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BIT|TINYINT|SMALLINT|INTEGER|BIGINT|REAL|FLOAT|DOUBLE|DECIMAL|NUMERIC",
						paramClassName);
			break;

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			if ("byte[]".equals(paramClassName))
				value = readBlob(rs, label);
			else
				throw new DBException("DB-UOR04", label, "sqlType.BINARY|VARBINARY|LONGVARBINARY|BLOB", paramClassName);
			break;
		case Types.CLOB:
			StringBuffer clob = readClob(rs, label);
			if (clob == null)
				break;
			if ("byte[]".equals(paramClassName))
				value = clob.toString().getBytes();
			else if ("java.lang.String".equals(paramClassName)) {
				value = clob.toString();
			} else if ("java.lang.StringBuffer".equals(paramClassName)) {
				value = clob;
			} else
				throw new DBException("DB-UOR04", label, "sqlType.CLOB", paramClassName);
			break;
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			if ("java.sql.Date".equals(paramClassName))
				value = rs.getDate(label);
			else if ("java.sql.Time".equals(paramClassName))
				value = rs.getTime(label);
			else if ("java.sql.Timestamp".equals(paramClassName))
				value = rs.getTimestamp(label);
			else if ("java.util.Date".equals(paramClassName)) {
				java.sql.Timestamp dateValue = rs.getTimestamp(label);
				if (dateValue != null) {
					value = new java.util.Date(dateValue.getTime());
				}
			} else
				throw new DBException("DB-UOR04", label, "sqlType.DATE|TIMESTAMP|TIME", paramClassName);
			break;
		default:
			throw new DBException("DB-UOR05", label, sqlType);
		}

		return value;
	}

	/**
	 * 读取clob数据，内容过大时可能造成内存溢出
	 */
	private StringBuffer readClob(ResultSet rs, String label) throws SQLException, DBException {
		Clob c = rs.getClob(label);
		if (c != null) {
			StringBuffer content = new StringBuffer();
			String line = null;
			Reader reader = null;
			BufferedReader br = null;
			try {
				reader = c.getCharacterStream();
				br = new BufferedReader(reader);

				while ((line = br.readLine()) != null) {
					content.append(line);
					content.append("\r\n");
				}
			} catch (IOException e) {
				throw new DBException("DB-UOR03", e, label, e.getMessage());
			} finally {
				try {
					if (br != null)
						br.close();
					if (reader != null)
						reader.close();
				} catch (IOException e) {
				}
			}

			return content;
		}
		return null;
	}

	/**
	 * 读取blob数据，内容过大时可能造成内存溢出
	 */
	private byte[] readBlob(ResultSet rs, String label) throws SQLException, DBException {
		Blob blob = rs.getBlob(label);
		if (blob != null) {
			InputStream bis = blob.getBinaryStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[1024 * 1024];// 相当于我们的缓存
				int byteRead = 0;
				while ((byteRead = bis.read(buffer)) != -1) {
					baos.write(buffer, 0, byteRead);
				}
				baos.flush();
				return baos.toByteArray();
			} catch (IOException e) {
				throw new DBException("DB-UOR02", e, label, e.getMessage());
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (baos != null)
						baos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
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
