package org.rex.db.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.rex.WMap;
import org.rex.db.exception.DBException;

/**
 * 公共方法 注意：本类并非线程安全
 * 
 * @author zw
 */
public class ORUtil {

	/**
	 * 用于临时存储查询结果的列信息和类型信息
	 */
	private String[] resultLabels = null;
	private int[] resultTypes = null;
	private Map<String, String> labelsRenamed = new HashMap<String, String>();

	// =====================将RS结果集转换为JAVA对象实例
	// ---------将结果集转为Map对象
	public WMap rs2Map(ResultSet rs, boolean inOriginal) throws DBException {

		String[] labels = getResultLabels(rs);// 列名
		int[] types = getResultTypes(rs);// 列类型

		WMap results = new WMap();
		for (int i = 0; i < labels.length; i++) {
			String label = inOriginal ? labels[i].toUpperCase() : renameLabel(labels[i]);
			Object value;
			try {
				value = getValue(rs, labels[i], types[i]);
				results.put(label, value);
			} catch (SQLException e) {
				throw new DBException("读取结果集出错");
			}
		}

		return results;
	}
	
	/**
	 * 从结果集中读取一个字段
	 * @param rs
	 * @param label
	 * @param type
	 * @return
	 * @throws SQLException
	 * @throws DBException
	 */
	private Object getValue(ResultSet rs, String label, int type) throws SQLException, DBException{
		Object value = null;
		switch (type) {

			case Types.BLOB:
				value = readBlob(rs, label);
				break;
			case Types.CLOB:
				value = readClob(rs, label);
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
			case Types.TIME:
				Timestamp timestamp = rs.getTimestamp(label);
				if (timestamp != null) {
					value = new java.util.Date(timestamp.getTime());
				}
				break;
			default:
				value = rs.getObject(label);
		}
		
		return value;
	}

	// -------获取需要特殊处理的结果
	/**
	 * 读取clob数据，内容过大时可能造成内存溢出
	 */
	private String readClob(ResultSet rs, String label) throws SQLException, DBException {
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
				throw new DBException("DB-Q10004", e, label, "CLOB");
			} finally {
				try {
					if (br != null)
						br.close();
					if (reader != null)
						reader.close();
				} catch (IOException e) {
				}
			}

			return content.toString();
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
			// 小于2M
			// if(blob.length() <= 1024*1024*2){
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
				throw new DBException("DB-Q10004", e, label, "BLOB");
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (baos != null)
						baos.close();
				} catch (IOException e) {
				}
			}
			// XXX:这段需要斟酌，应当确保有写权限
			// }else{
			// String tmpdir="/opt/etl_temp/blob";
			// if(System.getProperties().getProperty("os.name").toLowerCase().startsWith("win")){
			// tmpdir="D:\\ETL_TEMP\\BLOB";
			// }
			// File dir = new File(tmpdir);
			// if(!dir.exists()) dir.mkdirs();
			//
			// FileOutputStream fos = null;
			// File file = null;
			// try {
			// file=new File(tmpdir + File.separator +
			// String.valueOf(Math.random()).substring(10));
			// if(!file.exists()) file.createNewFile();
			// fos = new FileOutputStream(file);
			// byte [] buffer=new byte[1024*1024];
			// int byteRead=0;
			// while((byteRead=bis.read(buffer))!=-1){
			// fos.write(buffer, 0, byteRead);
			// }
			// fos.flush();
			// value = file;
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// try {
			// if (bis != null) bis.close();
			// if (fos != null) fos.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// }
		}
		return null;
	}

	// -------读取结果集元数据信息
	protected String[] getResultLabels(ResultSet rs) throws DBException {
		if (resultLabels == null) {
			try {
				createMeta(rs.getMetaData());
			} catch (SQLException e) {
				throw new DBException("DB-Q10002", e);
			}
		}
		return resultLabels;
	}

	protected int[] getResultTypes(ResultSet rs) throws DBException {
		if (resultTypes == null)
			try {
				createMeta(rs.getMetaData());
			} catch (SQLException e) {
				throw new DBException("DB-Q10003", e);
			}

		return resultTypes;
	}

	public void createMeta(ResultSetMetaData meta) throws SQLException {

		int c = meta.getColumnCount();
		resultLabels = new String[c];
		resultTypes = new int[c];

		for (int i = 0; i < c; i++) {
			// resultLabels[i]=meta.getColumnName(i+1);
			// update by zhouzr 20130829 应该获取别名
			resultLabels[i] = meta.getColumnLabel(i + 1);
			resultTypes[i] = meta.getColumnType(i + 1);
		}
	}

	/**
	 * 处理掉label的下划线并转化大小写, 如CJXM_DM->cjxmDm;AA_BB_CC->aaBbCc
	 */
	protected String renameLabel(String label) {
		if (labelsRenamed.containsKey(label))
			return (String) labelsRenamed.get(label);

		StringBuffer result = new StringBuffer();
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

		String labelRenamed = result.toString();
		labelsRenamed.put(label, labelRenamed);
		return labelRenamed;
	}

	// ---------将结果集转为Object对象
	public <T> T rs2Object(ResultSet rs, T bean, boolean inOriginal) throws DBException {

		// 读取结果集元信息
		String[] labels = getResultLabels(rs);
		int[] types = getResultTypes(rs);

		// 遍历结果集
		Map beanParams = getWriteableParams(bean.getClass(), inOriginal);
		for (int i = 0; i < labels.length; i++) {

			// 获取POJO写入方法
			Method writer = null;

			// 要求类属性必须和数据库列名一致，并且全为
			if (inOriginal) {
				String labelDbStyle = labels[i].toUpperCase();
				if (beanParams.containsKey(labelDbStyle))
					writer = (Method) beanParams.get(labelDbStyle);
			} else {
				String labelJavaStyle = renameLabel(labels[i]);
				if (beanParams.containsKey(labelJavaStyle))
					writer = (Method) beanParams.get(labelJavaStyle);
			}
			if (writer == null)
				continue;

			Class param0 = writer.getParameterTypes()[0];
			String paramClassName = param0.isArray() ? param0.getComponentType().getName() + "[]" : param0.getName();// set方法只有一个参数

			// //从结果集中取值并转换类型
			Object value = null;
			try {
				value = getValue(rs, labels[i], types[i], paramClassName);
			} catch (SQLException e) {
				throw new DBException("读取结果集是出现异常");
			}
			//
			// //向POJO赋值
			invokeMethod(bean, writer, value);
		}

		return bean;
	}

	/**
	 * 获取该类的所有可写属性
	 */
	private Map getWriteableParams(Class clazz, boolean inOriginal) throws DBException {
		Map params = new HashMap();
		BeanInfo bean = null;
		try {
			bean = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBException("DB-Q10008", e);
		}

		PropertyDescriptor[] props = bean.getPropertyDescriptors();
		for (int i = 0; i < props.length; i++) {
			String key;

			if (inOriginal) {
				key = renameLabelUpper(props[i].getName());
				if (params.containsKey(key))// 不允许两个属性名转为大写后重复
					throw new DBException("DB-Q10009", clazz.getName(), props[i].getName().toUpperCase());
			} else
				key = props[i].getName();

			if (props[i].getWriteMethod() != null)
				params.put(key, props[i].getWriteMethod());
		}
		return params;
	}

	/**
	 * 处理掉下划线并转为大写,inOriginal=true时使用
	 */
	protected String renameLabelUpper(String label) {
		return label.replaceAll("_", "").toUpperCase();
	}

	/**
	 * 将结果集数据转换为Java所需类型
	 * 
	 * @param rs 结果集
	 * @param label 结果集列命
	 * @param sqlType 结果集列数据类型
	 * @param paramClassName 待转换的java类型
	 * @return java对象
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
			else if ("java.lang.String".equals(paramClassName) || "String".equals(paramClassName))
				value = rs.getString(label);
			else
				throw new DBException("DB-Q10015", label, "sqlType.BOOLEAN", paramClassName);
			break;

		case Types.CHAR:
		case Types.VARCHAR:

			if ("java.lang.String".equals(paramClassName) || "String".equals(paramClassName)) {
				value = rs.getString(label);
			} else
				throw new DBException("DB-Q10015", label, "sqlType.CHAR|VARCHAR", paramClassName);
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
			else if ("java.lang.String".equals(paramClassName) || "String".equals(paramClassName))
				value = rs.getString(label);
			else
				throw new DBException("DB-Q10015", label, "sqlType.BIT|TINYINT|SMALLINT|INTEGER|BIGINT|REAL|FLOAT|DOUBLE|DECIMAL|NUMERIC",
						paramClassName);
			break;

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
		case Types.BLOB:
			if ("byte[]".equals(paramClassName))
				value = rs.getBytes(label);
			else
				throw new DBException("DB-Q10015", label, "sqlType.BINARY|VARBINARY|LONGVARBINARY|BLOB", paramClassName);
			break;
		case Types.CLOB:
			if ("byte[]".equals(paramClassName))
				value = rs.getBytes(label);
			else if ("java.lang.String".equals(paramClassName) || "java.lang.StringBuffer".equals(paramClassName)) {
				Clob c = rs.getClob(label);
				if (c != null) {
					StringBuffer content = new StringBuffer();
					String line = null;
					Reader reader = c.getCharacterStream();
					BufferedReader br = new BufferedReader(reader);
					try {
						while ((line = br.readLine()) != null) {
							content.append(line);
							content.append("\r\n");
						}
					} catch (IOException e) {
						throw new DBException("DB-Q10017", e);
					}

					if ("java.lang.String".equals(paramClassName))
						value = content.toString();
					else
						value = content;
				} else
					value = null;

			} else
				throw new DBException("DB-Q10015", label, "sqlType.CLOB", paramClassName);
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
				throw new DBException("DB-Q10015", label, "sqlType.DATE|TIMESTAMP|TIME", paramClassName);
			break;
		default:
			throw new DBException("DB-Q10015", sqlType, label);
		}

		return value;
	}

	/**
	 * 向对象赋值
	 * 
	 * @throws DBException
	 */
	private static void invokeMethod(Object object, Method method, Object value) throws DBException {
		try {
			method.invoke(object, new Object[] { value });
		} catch (IllegalArgumentException e) {
			throw new DBException("DB-Q10018", e, object.getClass().getName(), method.getName(), value);
		} catch (IllegalAccessException e) {
			throw new DBException("DB-Q10019", e, object.getClass().getName(), method.getName(), value);
		} catch (InvocationTargetException e) {
			throw new DBException("DB-Q10020", e, object.getClass().getName(), method.getName(), value);
		}
	}
}
