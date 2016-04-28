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
package org.rex.db;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.SqlUtil;

/**
 * Encapsulates prepared parameters, including IN, OUT, INOUT parameters.
 * 
 * @author z
 * @version 1.0, 2016-04-17
 * @since Rexdb-1.0
 */
public class Ps {

	public static final String CALL_OUT_DEFAULT_PREFIX = "out_";
	public static final String CALL_RETURN_DEFAULT_PREFIX = "return_";

	private List<SqlParameter> parameters;

	private List<Class<?>> returnResultTypes;

	public Ps() {
		parameters = new ArrayList<SqlParameter>();
	}

	public Ps(Object... parameters) {
		this();
		for (int i = 0; i < parameters.length; i++) {
			add(parameters[i]);
		}
	}

	// --------setting
	/**
	 * Returns the value of 'dateAdjust' setting, if the setting is true, all prepared parameters of java.util.Date 
	 * type will automatically convert to java.sql.Timstamp type.
	 * 
	 * @return boolean
	 */
	public static boolean isDateAdjust() {
		return Configuration.getCurrentConfiguration().isDateAdjust();
	}

	// -------------------------------------------inner class
	/**
	 * IN parameter
	 */
	public class SqlParameter {
		private int sqlType;
		private Object value;

		public SqlParameter(int sqlType, Object value) {
			this.sqlType = sqlType;
			this.value = value;

			if (isDateAdjust()) {
				if (value != null && !(value instanceof Timestamp) && (value instanceof Date)) {
					this.value = new java.sql.Timestamp(((Date) value).getTime());
					this.sqlType = Types.TIMESTAMP;
				}
			}
		}

		public int getSqlType() {
			return sqlType;
		}

		public Object getValue() {
			return value;
		}

		public String toString() {
			return "[type=in, sqlType=" + SqlUtil.getNameByType(sqlType) + ", value=" + value + "]";
		}
	}

	/**
	 * OUT parameter
	 */
	public class SqlOutParameter<T> extends SqlParameter {
		private String paramName;
		private Class<T> outEntitryClass;
		private boolean resultSet;

		public SqlOutParameter(int sqlType) {
			super(sqlType, null);
		}

		public SqlOutParameter(int sqlType, String paramName) {
			super(sqlType, null);
			this.paramName = paramName;
		}

		protected SqlOutParameter(int sqlType, Object value) {
			super(sqlType, value);
		}

		protected SqlOutParameter(int sqlType, String paramName, Object value) {
			super(sqlType, value);
			this.paramName = paramName;
		}

		public SqlOutParameter(int sqlType, String paramName, Class<T> outEntitryClass, boolean resultSet) {
			super(sqlType, null);
			this.paramName = paramName;
			this.outEntitryClass = outEntitryClass;
			this.resultSet = resultSet;
		}

		public String getParamName() {
			return paramName;
		}

		public Class<T> getOutEntitryClass() {
			return outEntitryClass;
		}

		public boolean isResultSet() {
			return resultSet;
		}

		public String toString() {
			return "[type=out, sqlType=" + SqlUtil.getNameByType(super.sqlType) + ", value=" + super.value + ", paramName=" + paramName
					+ ", outEntitryClass=" + outEntitryClass + ", resultSet=" + resultSet + "]";
		}
	}

	/**
	 * INOUT parameter
	 */
	public class SqlInOutParameter<T> extends SqlOutParameter<T> {

		public SqlInOutParameter(int sqlType, Object value) {
			super(sqlType, value);
		}

		public SqlInOutParameter(int sqlType, String paramName, Object value) {
			super(sqlType, paramName, value);
		}

		public String toString() {
			return "[type=in out, sqlType=" + SqlUtil.getNameByType(getSqlType()) + ", value=" + getValue() + ", paramName=" + getParamName()
					+ ", outEntitryClass=" + getOutEntitryClass() + ", resultSet=" + isResultSet() + "]";
		}
	}

	// -------------------------------------------set parameters
	/**
	 * Replaces the element at the specified position in the list with the specified parameter.
	 * 
	 * @param list list to alter.
	 * @param index index of parameter to replace.
	 * @param value parameter to be stored at the specified position.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	private void set(List list, int index, Object value) {
		--index;
		if (index == list.size()) {
			list.add(value);
		} else if (index < list.size()) {
			list.set(index, value);
		} else
			throw new DBRuntimeException("DB-00001", value, index + 1, list.size());
	}

	/**
	 * Sets IN parameter at the specified position, and declares it as the specified SQL type.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * 
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	protected Ps setParameter(int index, Object value, int sqlType) {
		set(parameters, index, new SqlParameter(sqlType, value));
		return this;
	}

	/**
	 * Sets IN parameter at the specified position.
	 * 
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * 
	 * @return reference to this object.
	 */
	protected Ps addParameter(Object value, int sqlType) {
		parameters.add(new SqlParameter(sqlType, value));
		return this;
	}

	// -------------------------------------------get parameters
	/**
	 * Returns the number of declared parameters.
	 * 
	 * @return the number of parameters.
	 */
	public int getParameterSize() {
		return parameters.size();
	}

	/**
	 * Returns a List of all declared parameters.
	 * 
	 * @return a list of all parameters.
	 */
	public List<SqlParameter> getParameters() {
		return parameters;
	}

	/**
	 * Returns a List of all declared parameters' java types.
	 * 
	 * @return a list of java types.
	 */
	public List<Class<?>> getReturnResultTypes() {
		return returnResultTypes;
	}

	// -----------------------------------------declares prepared parameters
	// ----------------------------declares parameter at the specified position

	/**
	 * Sets IN parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Object value) {
		return setParameter(index, value, SqlUtil.getSqlType(value));
	}

	/**
	 * Sets IN parameter at the specified position, and declares it as the specified SQL type.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Object value, int sqlType) {
		return setParameter(index, value, sqlType);
	}

	/**
	 * Sets IN parameter as NULL type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setNull(int index) {
		return setParameter(index, null, Types.NULL);
	}

	/**
	 * Sets String parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, String value) {
		return setParameter(index, value, Types.VARCHAR);
	}

	/**
	 * Sets boolean parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, boolean value) {
		return setParameter(index, value, Types.BOOLEAN);
	}

	/**
	 * Sets BigDecimal parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, BigDecimal value) {
		return setParameter(index, value, Types.NUMERIC);
	}

	/**
	 * Sets integer parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, int value) {
		return setParameter(index, value, Types.INTEGER);
	}

	/**
	 * Sets long parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, long value) {
		return setParameter(index, value, Types.BIGINT);
	}

	/**
	 * Sets double parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, double value) {
		return setParameter(index, value, Types.DOUBLE);
	}

	/**
	 * Sets float parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, float value) {
		return setParameter(index, value, Types.FLOAT);
	}

	/**
	 * Sets short parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, short value) {
		return setParameter(index, value, Types.SMALLINT);
	}

	/**
	 * Sets byte parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, byte value) {
		return setParameter(index, value, Types.TINYINT);
	}

	/**
	 * Sets byte array parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, byte[] value) {
		return setParameter(index, value, Types.VARBINARY);
	}

	/**
	 * Sets Blob parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Blob value) {
		return setParameter(index, value, Types.BLOB);
	}

	/**
	 * Sets Clob parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Clob value) {
		return setParameter(index, value, Types.CLOB);
	}

	/**
	 * Sets Date parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Date value) {
		return setParameter(index, value, Types.DATE);
	}

	/**
	 * Sets java.sql.Date parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, java.sql.Date value) {
		return setParameter(index, value, Types.DATE);
	}

	/**
	 * Sets Time parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Time value) {
		return setParameter(index, value, Types.TIME);
	}

	/**
	 * Sets Timestamp parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps set(int index, Timestamp value) {
		return setParameter(index, value, Types.TIMESTAMP);
	}

	// ----------------------------appends parameter to the end of the declared parameters list.
	/**
	 * Appends IN parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Object value) {
		return addParameter(value, SqlUtil.getSqlType(value));
	}

	/**
	 * Appends IN parameter as the specified SQL type to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 */
	public Ps add(Object value, int sqlType) {
		return addParameter(value, sqlType);
	}

	/**
	 * Appends IN parameter as NULL type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addNull() {
		return addParameter(null, Types.NULL);
	}

	/**
	 * Appends String parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(String value) {
		return addParameter(value, Types.VARCHAR);
	}

	/**
	 * Appends boolean parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(boolean value) {
		return addParameter(value, Types.BOOLEAN);
	}

	/**
	 * Appends BigDecimal parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(BigDecimal value) {
		return addParameter(value, Types.NUMERIC);
	}

	/**
	 * Appends integer parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(int value) {
		return addParameter(value, Types.INTEGER);
	}

	/**
	 * Appends long parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(long value) {
		return addParameter(value, Types.BIGINT);
	}

	/**
	 * Appends float parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(float value) {
		return addParameter(value, Types.FLOAT);
	}

	/**
	 * Appends double parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(double value) {
		return addParameter(value, Types.DOUBLE);
	}

	/**
	 * Appends short parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(short value) {
		return addParameter(value, Types.SMALLINT);
	}

	/**
	 * Appends byte parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(byte value) {
		return addParameter(value, Types.TINYINT);
	}

	/**
	 * Appends byte array parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(byte[] value) {
		return addParameter(value, Types.VARBINARY);
	}

	/**
	 * Appends Blob parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Blob value) {
		return addParameter(value, Types.BLOB);
	}

	/**
	 * Appends Clob parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Clob value) {
		return addParameter(value, Types.CLOB);
	}

	/**
	 * Appends Date parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Date value) {
		return addParameter(value, Types.DATE);
	}

	/**
	 * Appends java.sql.Date parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(java.sql.Date value) {
		return addParameter(value, Types.DATE);
	}

	/**
	 * Appends Time parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Time value) {
		return addParameter(value, Types.TIME);
	}

	/**
	 * Appends Timestamp parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps add(Timestamp value) {
		return addParameter(value, Types.TIMESTAMP);
	}

	// ----------------------------inserts parameter at the specified position.
	/**
	 * Inserts parameter at the specified position in the declared parameters list.
	 * 
	 * @param index the index at which the parameter is to be inserted.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws IndexOutOfBoundsException if the index is out of range.
	 */
	public Ps insert(int index, Object value) {
		--index;
		if (index < 0 || index > parameters.size())
			throw new DBRuntimeException("DB-00001", value, index + 1, parameters.size());

		parameters.add(index, new SqlParameter(SqlUtil.getSqlType(value), value));
		return this;
	}

	// ----------------------------------------------------------------------declares OUT parameters at the specified position
	// ---------protected
	/**
	 * Declares OUT parameter as the specified SQL type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	protected Ps setOutParameter(int index, int sqlType) {
		return setOutParameter(index, null, sqlType);
	}

	/**
	 * Declares OUT parameter as the specified SQL type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	protected Ps setOutParameter(int index, String paramName, int sqlType) {
		set(parameters, index, new SqlOutParameter(sqlType, paramName));
		return this;
	}

	/**
	 * Appends OUT parameter as the specified SQL type to the end of the declared parameters list.
	 * 
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 */
	protected Ps addOutParameter(int sqlType) {
		return addOutParameter(null, sqlType);
	}

	/**
	 * Appends OUT parameter as the specified SQL type to the end of the declared parameters list, and declares an alias for this
	 * parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 */
	protected Ps addOutParameter(String paramName, int sqlType) {
		parameters.add(new SqlOutParameter(sqlType, paramName));
		return this;
	}

	// ---------public
	/**
	 * Declares OUT ResultSet as the specified ResultSet SQL type (depends on the JDBC driver) at the specified
	 * position, also declares a java type to map to and an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param sqlType the JDBC driver's ResultSet SQL type.
	 * @param resultClass the java type to map to.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutResultSet(int index, String paramName, int sqlType, Class<?> resultClass) {
		set(parameters, index, new SqlOutParameter(sqlType, paramName, resultClass, true));
		return this;
	}

	/**
	 * Appends OUT ResultSet as the specified ResultSet SQL type (depends on the JDBC driver) to the end of the
	 * declared parameters list, also declares a java type to map to and an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param sqlType the JDBC driver's ResultSet SQL type.
	 * @param resultClass the java type to map to.
	 * @return reference to this object.
	 */
	public Ps addOutResultSet(String paramName, int sqlType, Class<?> outEntitryClass) {
		parameters.add(new SqlOutParameter(sqlType, paramName, outEntitryClass, true));
		return this;
	}

	/**
	 * Declares OUT ResultSet as the specified ResultSet SQL type (depends on the JDBC driver) at the specified
	 * position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param sqlType the JDBC driver's ResultSet SQL type.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutResultSet(int index, int sqlType) {
		return setOutResultSet(index, null, sqlType, null);
	}

	/**
	 * Declares OUT ResultSet as the specified ResultSet SQL type (depends on the JDBC driver) at the specified
	 * position, also declares a java type to map to for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param sqlType the JDBC driver's ResultSet SQL type.
	 * @param resultClass the java type to map to.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutResultSet(int index, int sqlType, Class<?> resultClass) {
		return setOutResultSet(index, null, sqlType, resultClass);
	}

	/**
	 * Declares OUT parameter as String type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutString(int index) {
		return setOutParameter(index, Types.VARCHAR);
	}

	/**
	 * Declares OUT parameter as boolean type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBoolean(int index) {
		return setOutParameter(index, Types.BOOLEAN);
	}

	/**
	 * Declares OUT parameter as BigDecimal type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBigDecimal(int index) {
		return setOutParameter(index, Types.NUMERIC);
	}

	/**
	 * Declares OUT parameter as integer type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutInt(int index) {
		return setOutParameter(index, Types.INTEGER);
	}

	/**
	 * Declares OUT parameter as long type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutLong(int index) {
		return setOutParameter(index, Types.BIGINT);
	}

	/**
	 * Declares OUT parameter as float type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutFloat(int index) {
		return setOutParameter(index, Types.FLOAT);
	}

	/**
	 * Declares OUT parameter as double type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutDouble(int index) {
		return setOutParameter(index, Types.DOUBLE);
	}

	/**
	 * Declares OUT parameter as short type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutShort(int index) {
		return setOutParameter(index, Types.SMALLINT);
	}

	/**
	 * Declares OUT parameter as byte type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutByte(int index) {
		return setOutParameter(index, Types.TINYINT);
	}

	/**
	 * Declares OUT parameter as byte array type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBytes(int index) {
		return setOutParameter(index, Types.VARBINARY);
	}

	/**
	 * Declares OUT parameter as Blob type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBlob(int index) {
		return setOutParameter(index, Types.BLOB);
	}

	/**
	 * Declares OUT parameter as Clob type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutClob(int index) {
		return setOutParameter(index, Types.CLOB);
	}

	/**
	 * Declares OUT parameter as Date type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutDate(int index) {
		return setOutParameter(index, Types.DATE);
	}

	/**
	 * Declares OUT parameter as Time type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutTime(int index) {
		return setOutParameter(index, Types.TIME);
	}

	/**
	 * Declares OUT parameter as Timestamp type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutTimestamp(int index) {
		return setOutParameter(index, Types.TIMESTAMP);
	}

	// -------Declares OUT parameter
	/**
	 * Declares OUT parameter as the specified type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutResultSet(int index, String paramName, int sqlType) {
		return setOutResultSet(index, paramName, sqlType, null);
	}

	/**
	 * Declares OUT parameter as String type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutString(int index, String paramName) {
		return setOutParameter(index, paramName, Types.VARCHAR);
	}

	/**
	 * Declares OUT parameter as boolean type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBoolean(int index, String paramName) {
		return setOutParameter(index, paramName, Types.BOOLEAN);
	}

	/**
	 * Declares OUT parameter as BigDecimal type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBigDecimal(int index, String paramName) {
		return setOutParameter(index, paramName, Types.NUMERIC);
	}

	/**
	 * Declares OUT parameter as integer type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutInt(int index, String paramName) {
		return setOutParameter(index, paramName, Types.INTEGER);
	}

	/**
	 * Declares OUT parameter as long type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutLong(int index, String paramName) {
		return setOutParameter(index, paramName, Types.BIGINT);
	}

	/**
	 * Declares OUT parameter as float type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutFloat(int index, String paramName) {
		return setOutParameter(index, paramName, Types.FLOAT);
	}

	/**
	 * Declares OUT parameter as double type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutDouble(int index, String paramName) {
		return setOutParameter(index, paramName, Types.DOUBLE);
	}

	/**
	 * Declares OUT parameter as short type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutShort(int index, String paramName) {
		return setOutParameter(index, paramName, Types.SMALLINT);
	}

	/**
	 * Declares OUT parameter as byte type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutByte(int index, String paramName) {
		return setOutParameter(index, paramName, Types.TINYINT);
	}

	/**
	 * Declares OUT parameter as byte array type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBytes(int index, String paramName) {
		return setOutParameter(index, paramName, Types.VARBINARY);
	}

	/**
	 * Declares OUT parameter as Blob type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutBlob(int index, String paramName) {
		return setOutParameter(index, paramName, Types.BLOB);
	}

	/**
	 * Declares OUT parameter as Clob type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutClob(int index, String paramName) {
		return setOutParameter(index, paramName, Types.CLOB);
	}

	/**
	 * Declares OUT parameter as Date type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutDate(int index, String paramName) {
		return setOutParameter(index, paramName, Types.DATE);
	}

	/**
	 * Declares OUT parameter as Time type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutTime(int index, String paramName) {
		return setOutParameter(index, paramName, Types.TIME);
	}

	/**
	 * Declares OUT parameter as Timestamp type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setOutTimestamp(int index, String paramName) {
		return setOutParameter(index, paramName, Types.TIMESTAMP);
	}

	// ----------------------------Appends OUT parameter
	/**
	 * Appends OUT parameter as ResultSet to the end of the declared parameters list.
	 * 
	 * @param sqlType JDBC driver's ResultSet SQL type.
	 * @return reference to this object.
	 */
	public Ps addOutResultSet(int sqlType) {
		return addOutResultSet(null, sqlType, null);
	}

	/**
	 * Appends OUT parameter as ResultSet to the end of the declared parameters list.
	 * 
	 * @param sqlType JDBC driver's ResultSet SQL type.
	 * @param resultClass java type to map to.
	 * @return reference to this object.
	 */
	public Ps addOutResultSet(int sqlType, Class<?> resultClass) {
		return addOutResultSet(null, sqlType, resultClass);
	}

	/**
	 * Appends OUT parameter as String type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutString() {
		return addOutParameter(Types.VARCHAR);
	}

	/**
	 * Appends OUT parameter as boolean type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutBoolean() {
		return addOutParameter(Types.BOOLEAN);
	}

	/**
	 * Appends OUT parameter as BigDecimal type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutBigDecimal() {
		return addOutParameter(Types.NUMERIC);
	}

	/**
	 * Appends OUT parameter as integer type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutInt() {
		return addOutParameter(Types.INTEGER);
	}

	/**
	 * Appends OUT parameter as long type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutLong() {
		return addOutParameter(Types.BIGINT);
	}

	/**
	 * Appends OUT parameter as float type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutFloat() {
		return addOutParameter(Types.FLOAT);
	}

	/**
	 * Appends OUT parameter as double type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutDouble() {
		return addOutParameter(Types.DOUBLE);
	}

	/**
	 * Appends OUT parameter as short type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutShort() {
		return addOutParameter(Types.SMALLINT);
	}

	/**
	 * Appends OUT parameter as byte type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutByte() {
		return addOutParameter(Types.TINYINT);
	}

	/**
	 * Appends OUT parameter as byte array type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutBytes() {
		return addOutParameter(Types.VARBINARY);
	}

	/**
	 * Appends OUT parameter as Blob type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutBlob() {
		return addOutParameter(Types.BLOB);
	}

	/**
	 * Appends OUT parameter as Clob type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutClob() {
		return addOutParameter(Types.CLOB);
	}

	/**
	 * Appends OUT parameter as Date type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutDate() {
		return addOutParameter(Types.DATE);
	}

	/**
	 * Appends OUT parameter as Time type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutTime() {
		return addOutParameter(Types.TIME);
	}

	/**
	 * Appends OUT parameter as Timestamp type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addOutTimestamp() {
		return addOutParameter(Types.TIMESTAMP);
	}

	// -------Appends OUT parameter and declares alias

	/**
	 * Appends OUT parameter as ResultSet type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param sqlType JDBC driver's ResultSet SQL type.
	 * @return reference to this object.
	 */
	public Ps addOutResultSet(String paramName, int sqlType) {
		return addOutResultSet(paramName, sqlType, null);
	}

	/**
	 * Appends OUT parameter as String type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutString(String paramName) {
		return addOutParameter(paramName, Types.VARCHAR);
	}

	/**
	 * Appends OUT parameter as boolean type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutBoolean(String paramName) {
		return addOutParameter(paramName, Types.BOOLEAN);
	}

	/**
	 * Appends OUT parameter as BigDecimal type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutBigDecimal(String paramName) {
		return addOutParameter(paramName, Types.NUMERIC);
	}

	/**
	 * Appends OUT parameter as integer type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutInt(String paramName) {
		return addOutParameter(paramName, Types.INTEGER);
	}

	/**
	 * Appends OUT parameter as long type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutLong(String paramName) {
		return addOutParameter(paramName, Types.BIGINT);
	}

	/**
	 * Appends OUT parameter as float type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutFloat(String paramName) {
		return addOutParameter(paramName, Types.FLOAT);
	}

	/**
	 * Appends OUT parameter as double type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutDouble(String paramName) {
		return addOutParameter(paramName, Types.DOUBLE);
	}

	/**
	 * Appends OUT parameter as short type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutShort(String paramName) {
		return addOutParameter(paramName, Types.SMALLINT);
	}

	/**
	 * Appends OUT parameter as byte type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutByte(String paramName) {
		return addOutParameter(paramName, Types.TINYINT);
	}

	/**
	 * Appends OUT parameter as byte array type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutBytes(String paramName) {
		return addOutParameter(paramName, Types.VARBINARY);
	}

	/**
	 * Appends OUT parameter as Blob type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutBlob(String paramName) {
		return addOutParameter(paramName, Types.BLOB);
	}

	/**
	 * Appends OUT parameter as Clob type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutClob(String paramName) {
		return addOutParameter(paramName, Types.CLOB);
	}

	/**
	 * Appends OUT parameter as Date type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutDate(String paramName) {
		return addOutParameter(paramName, Types.DATE);
	}

	/**
	 * Appends OUT parameter as Time type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutTime(String paramName) {
		return addOutParameter(paramName, Types.TIME);
	}

	/**
	 * Appends OUT parameter as Timestamp type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addOutTimestamp(String paramName) {
		return addOutParameter(paramName, Types.TIMESTAMP);
	}

	// ----------------------------------------------------------------------Declares INOUT parameter

	/**
	 * Declares INOUT parameter as the specified SQL type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	protected Ps setInOutParameter(int index, String paramName, Object value, int sqlType) {
		set(parameters, index, new SqlInOutParameter(sqlType, value));
		return this;
	}

	/**
	 * Declares INOUT parameter as the specified SQL type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @param sqlType the SQL type to be sent to the database.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	protected Ps setInOutParameter(int index, Object value, int sqlType) {
		set(parameters, index, new SqlInOutParameter(sqlType, value));
		return this;
	}

	/**
	 * Appends INOUT parameter as the specified SQL type to the end of the declared parameters list, also declares an alias for this
	 * parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @param sqlType the INOUT parameter's SQL type.
	 * @return reference to this object.
	 */
	protected Ps addInOutParameter(String paramName, Object value, int sqlType) {
		parameters.add(new SqlInOutParameter(sqlType, paramName, value));
		return this;
	}

	/**
	 * Appends INOUT parameter as the specified SQL type to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @param sqlType the INOUT parameter's SQL type.
	 * @return reference to this object.
	 */
	protected Ps addInOutParameter(Object value, int sqlType) {
		parameters.add(new SqlInOutParameter(sqlType, value));
		return this;
	}

	// ----------------------------Declares INOUT parameter
	/**
	 * Declares INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Object value) {
		return setInOutParameter(index, value, SqlUtil.getSqlType(value));
	}

	/**
	 * Declares INOUT parameter as the specified SQL type at the specified position
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @param sqlType the INOUT parameter's SQL type.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Object value, int sqlType) {
		return setInOutParameter(index, value, sqlType);
	}

	/**
	 * Declares INOUT parameter as NULL type at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOutNull(int index) {
		return setInOutParameter(index, null, Types.NULL);
	}

	/**
	 * Declares String INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String value) {
		return setInOutParameter(index, value, Types.VARCHAR);
	}

	/**
	 * Declares boolean INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, boolean value) {
		return setInOutParameter(index, value, Types.BOOLEAN);
	}

	/**
	 * Declares BigDecimal INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, BigDecimal value) {
		return setInOutParameter(index, value, Types.NUMERIC);
	}

	/**
	 * Declares integer INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, int value) {
		return setInOutParameter(index, value, Types.INTEGER);
	}

	/**
	 * Declares long INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, long value) {
		return setInOutParameter(index, value, Types.BIGINT);
	}

	/**
	 * Declares double INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, double value) {
		return setInOutParameter(index, value, Types.DOUBLE);
	}

	/**
	 * Declares float INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, float value) {
		return setInOutParameter(index, value, Types.FLOAT);
	}

	/**
	 * Declares short INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, short value) {
		return setInOutParameter(index, value, Types.SMALLINT);
	}

	/**
	 * Declares byte INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, byte value) {
		return setInOutParameter(index, value, Types.TINYINT);
	}

	/**
	 * Declares byte array INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, byte[] value) {
		return setInOutParameter(index, value, Types.VARBINARY);
	}

	/**
	 * Declares Blob INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Blob value) {
		return setInOutParameter(index, value, Types.BLOB);
	}

	/**
	 * Declares Clob INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Clob value) {
		return setInOutParameter(index, value, Types.CLOB);
	}

	/**
	 * Declares Date INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Date value) {
		return setInOutParameter(index, value, Types.DATE);
	}

	/**
	 * Declares Date INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, java.sql.Date value) {
		return setInOutParameter(index, value, Types.DATE);
	}

	/**
	 * Declares Time INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Time value) {
		return setInOutParameter(index, value, Types.TIME);
	}

	/**
	 * Declares Timestamp INOUT parameter at the specified position.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, Timestamp value) {
		return setInOutParameter(index, value, Types.TIMESTAMP);
	}

	// -------------------Declares INOUT parameter and alias
	/**
	 * Declares INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Object value) {
		return setInOutParameter(index, paramName, value, SqlUtil.getSqlType(value));
	}

	/**
	 * Declares INOUT parameter as the specified SQL type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @param sqlType JDBC driver's ResultSet SQL type.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Object value, int sqlType) {
		return setInOutParameter(index, paramName, value, sqlType);
	}

	/**
	 * Declares INOUT parameter as NULL type at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOutNull(int index, String paramName) {
		return setInOutParameter(index, paramName, null, Types.NULL);
	}

	/**
	 * Declares String INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, String value) {
		return setInOutParameter(index, paramName, value, Types.VARCHAR);
	}

	/**
	 * Declares boolean INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, boolean value) {
		return setInOutParameter(index, paramName, value, Types.BOOLEAN);
	}

	/**
	 * Declares BigDecimal INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, BigDecimal value) {
		return setInOutParameter(index, paramName, value, Types.NUMERIC);
	}

	/**
	 * Declares integer INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, int value) {
		return setInOutParameter(index, paramName, value, Types.INTEGER);
	}

	/**
	 * Declares long INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, long value) {
		return setInOutParameter(index, paramName, value, Types.BIGINT);
	}

	/**
	 * Declares double INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, double value) {
		return setInOutParameter(index, paramName, value, Types.DOUBLE);
	}

	/**
	 * Declares float INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, float value) {
		return setInOutParameter(index, paramName, value, Types.FLOAT);
	}

	/**
	 * Declares short INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, short value) {
		return setInOutParameter(index, paramName, value, Types.SMALLINT);
	}

	/**
	 * Declares byte INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, byte value) {
		return setInOutParameter(index, paramName, value, Types.TINYINT);
	}

	/**
	 * Declares byte array INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, byte[] value) {
		return setInOutParameter(index, paramName, value, Types.VARBINARY);
	}

	/**
	 * Declares Blob INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Blob value) {
		return setInOutParameter(index, paramName, value, Types.BLOB);
	}

	/**
	 * Declares Clob INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Clob value) {
		return setInOutParameter(index, paramName, value, Types.CLOB);
	}

	/**
	 * Declares Date INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Date value) {
		return setInOutParameter(index, paramName, value, Types.DATE);
	}

	/**
	 * Declares Date INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, java.sql.Date value) {
		return setInOutParameter(index, paramName, value, Types.DATE);
	}

	/**
	 * Declares Time INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Time value) {
		return setInOutParameter(index, paramName, value, Types.TIME);
	}

	/**
	 * Declares Timestamp INOUT parameter at the specified position, also declares an alias for this parameter.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 * @throws DBRuntimeException if the index is out of range.
	 */
	public Ps setInOut(int index, String paramName, Timestamp value) {
		return setInOutParameter(index, paramName, value, Types.TIMESTAMP);
	}

	// ----------------------------Appends INOUT parameter
	/**
	 * Appends INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Object value) {
		return addInOutParameter(value, SqlUtil.getSqlType(value));
	}

	/**
	 * Appends INOUT parameter as the declared SQL type to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @param sqlType the INOUT parameter's SQL type.
	 * @return reference to this object.
	 */
	public Ps addInOut(Object value, int sqlType) {
		return addInOutParameter(value, sqlType);
	}

	/**
	 * Appends INOUT parameter as NULL type to the end of the declared parameters list.
	 * 
	 * @return reference to this object.
	 */
	public Ps addInOutNull() {
		return addInOutParameter(null, Types.NULL);
	}

	/**
	 * Appends String INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String value) {
		return addInOutParameter(value, Types.VARCHAR);
	}

	/**
	 * Appends boolean INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(boolean value) {
		return addInOutParameter(value, Types.BOOLEAN);
	}

	/**
	 * Appends BigDecimal INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(BigDecimal value) {
		return addInOutParameter(value, Types.NUMERIC);
	}

	/**
	 * Appends integer INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(int value) {
		return addInOutParameter(value, Types.INTEGER);
	}

	/**
	 * Appends long INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(long value) {
		return addInOutParameter(value, Types.BIGINT);
	}

	/**
	 * Appends float INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(float value) {
		return addInOutParameter(value, Types.FLOAT);
	}

	/**
	 * Appends double INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(double value) {
		return addInOutParameter(value, Types.DOUBLE);
	}

	/**
	 * Appends short INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(short value) {
		return addInOutParameter(value, Types.SMALLINT);
	}

	/**
	 * Appends byte INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(byte value) {
		return addInOutParameter(value, Types.TINYINT);
	}

	/**
	 * Appends byte array INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(byte[] value) {
		return addInOutParameter(value, Types.VARBINARY);
	}

	/**
	 * Appends Blob INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Blob value) {
		return addInOutParameter(value, Types.BLOB);
	}

	/**
	 * Appends Clob INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Clob value) {
		return addInOutParameter(value, Types.CLOB);
	}

	/**
	 * Appends Date INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Date value) {
		return addInOutParameter(value, Types.DATE);
	}

	/**
	 * Appends Date INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(java.sql.Date value) {
		return addInOutParameter(value, Types.DATE);
	}

	/**
	 * Appends Time INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Time value) {
		return addInOutParameter(value, Types.TIME);
	}

	/**
	 * Appends Timestamp INOUT parameter to the end of the declared parameters list.
	 * 
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(Timestamp value) {
		return addInOutParameter(value, Types.TIMESTAMP);
	}

	// -------Appends INOUT parameter and declares alias
	/**
	 * Appends INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Object value) {
		return addInOutParameter(paramName, value, SqlUtil.getSqlType(value));
	}

	/**
	 * Appends INOUT parameter as the declared SQL type to the end of the declared parameters list, and declares an alias for this
	 * parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @param sqlType the INOUT parameter's SQL type.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Object value, int sqlType) {
		return addInOutParameter(paramName, value, sqlType);
	}

	/**
	 * Appends INOUT parameter as NULL type to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @return reference to this object.
	 */
	public Ps addInOutNull(String paramName) {
		return addInOutParameter(paramName, null, Types.NULL);
	}

	/**
	 * Appends String INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, String value) {
		return addInOutParameter(paramName, value, Types.VARCHAR);
	}

	/**
	 * Appends boolean INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, boolean value) {
		return addInOutParameter(paramName, value, Types.BOOLEAN);
	}

	/**
	 * Appends BigDecimal INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, BigDecimal value) {
		return addInOutParameter(paramName, value, Types.NUMERIC);
	}

	/**
	 * Appends integer INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, int value) {
		return addInOutParameter(paramName, value, Types.INTEGER);
	}

	/**
	 * Appends long INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, long value) {
		return addInOutParameter(paramName, value, Types.BIGINT);
	}

	/**
	 * Appends float INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, float value) {
		return addInOutParameter(paramName, value, Types.FLOAT);
	}

	/**
	 * Appends double INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, double value) {
		return addInOutParameter(paramName, value, Types.DOUBLE);
	}

	/**
	 * Appends short INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, short value) {
		return addInOutParameter(paramName, value, Types.SMALLINT);
	}

	/**
	 * Appends byte INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, byte value) {
		return addInOutParameter(paramName, value, Types.TINYINT);
	}

	/**
	 * Appends byte array INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, byte[] value) {
		return addInOutParameter(paramName, value, Types.VARBINARY);
	}

	/**
	 * Appends Blob INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Blob value) {
		return addInOutParameter(paramName, value, Types.BLOB);
	}

	/**
	 * Appends Clob INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Clob value) {
		return addInOutParameter(paramName, value, Types.CLOB);
	}

	/**
	 * Appends Date INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Date value) {
		return addInOutParameter(paramName, value, Types.DATE);
	}

	/**
	 * Appends java.sql.Date INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, java.sql.Date value) {
		return addInOutParameter(paramName, value, Types.DATE);
	}

	/**
	 * Appends Time INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Time value) {
		return addInOutParameter(paramName, value, Types.TIME);
	}

	/**
	 * Appends Timestamp INOUT parameter to the end of the declared parameters list, also declares an alias for this parameter.
	 * 
	 * @param paramName the alias for the parameter.
	 * @param value the parameter value.
	 * @return reference to this object.
	 */
	public Ps addInOut(String paramName, Timestamp value) {
		return addInOutParameter(paramName, value, Types.TIMESTAMP);
	}

	// -------
	/**
	 * If the call returns a ResultSet, maps it to java objects that instanced from the given class.
	 * 
	 * @param resultClass java type that the ResultSet is going to map to.
	 * @return reference to this object.
	 */
	public Ps addReturnType(Class<?> resultClass) {
		if (returnResultTypes == null)
			returnResultTypes = new ArrayList<Class<?>>();
		returnResultTypes.add(resultClass);
		return this;
	}

	/**
	 * If the call returns a ResultSet, maps it to java objects that instanced from the given class.
	 * 
	 * @param index the first parameter is 1, the second is 2, ...
	 * @param resultClass java type that each row of ResultSet to map to.
	 * @return reference to this object.
	 */
	public Ps setReturnType(int index, Class<?> resultClass) {
		if (returnResultTypes == null)
			returnResultTypes = new ArrayList<Class<?>>();
		if (returnResultTypes.size() < index - 1) {
			for (int i = returnResultTypes.size(); i < index - 1; i++)
				returnResultTypes.add(null);
		}
		returnResultTypes.add(resultClass);
		return this;
	}

	// -------------------------------------------toString
	public String toString() {
		List values = new ArrayList();
		List<String> sqlTypes = new ArrayList<String>();
		List<String> paramTypes = new ArrayList<String>();

		for (SqlParameter parameter : parameters) {
			values.add(parameter.getValue());
			sqlTypes.add(SqlUtil.getNameByType(parameter.getSqlType()));
			if (parameter instanceof SqlInOutParameter)
				paramTypes.add("INOUT");
			else if (parameter instanceof SqlOutParameter)
				paramTypes.add("OUT");
			else
				paramTypes.add("IN");
		}

		StringBuffer sb = new StringBuffer();
		sb.append("values: ").append(values).append(", sql types: ").append(sqlTypes);
		return sb.toString();
	}

}
