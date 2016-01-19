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

import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.JdbcUtil;
import org.rex.db.util.SqlUtil;

/**
 * 预编译参数
 * @author z
 */
public class Ps {
	
	/**
	 * 所有参数（按照顺序对应sql中的预编译参数）
	 */
	private List<SqlParameter> parameters;
	
	public Ps(){
		parameters = new ArrayList<SqlParameter>();
	}
	
	public Ps(Object...parameters){
		this();
		for (int i = 0; i < parameters.length; i++) {
			add(parameters[i]);
		}
	}
	
	//-------------------------------------------inner class
	/**
	 * 输入参数
	 */
	public class SqlParameter{
		private int sqlType;
		private Object value;
		
		public SqlParameter(int sqlType, Object value) {
			this.sqlType = sqlType;
			
			if(value != null && value.getClass().getName().equals("java.util.Date")){
				this.value = new java.sql.Date(((Date)value).getTime());
			}else
				this.value = value;
		}

		public int getSqlType() {
			return sqlType;
		}

		public Object getValue() {
			return value;
		}

		public String toString() {
			return "[type=in, sqlType=" + JdbcUtil.getNameByType(sqlType) + ", value=" + value + "]";
		}
	}
	
	/**
	 * 输出参数
	 */
	public class SqlOutParameter<T> extends SqlParameter{
		private String paramName;
		private Class<T> outEntitryClass;
		private boolean resultSet;
		
		public SqlOutParameter(int sqlType){
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
			return "[type=out, sqlType=" + JdbcUtil.getNameByType(super.sqlType) + ", value=" + super.value+", paramName=" + paramName
					+ ", outEntitryClass=" + outEntitryClass + ", resultSet=" + resultSet + "]";
		}
	}
	
	/**
	 * 输入输出参数
	 */
	public class SqlInOutParameter<T> extends SqlOutParameter<T>{

		public SqlInOutParameter(int sqlType, Object value) {
			super(sqlType, value);
		}
		
		public SqlInOutParameter(int sqlType, String paramName, Object value) {
			super(sqlType, paramName, value);
		}
		
		public String toString() {
			return "[type=in out, sqlType=" + JdbcUtil.getNameByType(getSqlType()) + ", value=" + getValue()+", paramName=" + getParamName()
					+ ", outEntitryClass=" + getOutEntitryClass() + ", resultSet=" + isResultSet() + "]";
		}
	}
	
	//-------------------------------------------设置值
	/**
	 * 设置ArrayList的值
	 * XXX: set方式只能按顺序设置参数，不够灵活，需要改进
	 */
	private void set(List list, int index, Object value){
		 --index;
		if(index == list.size()){
			list.add(value);
		}else if(index < list.size()){
			list.set(index, value);
		}else
			throw new DBRuntimeException("DB-C10041", value, index + 1, list.size()); 
	}
	
	/**
	 * 向ArrayList的指定位置插入值
	 */
	private List insert(List list, int index, Object value){
		--index;
		if(list.size() < index)
			throw new DBRuntimeException("DB-C10040", value, index + 1, list.size());
		
		if(list.size() == index){
			list.add(value);
			return list;
		}
		
		List newList = new ArrayList();
		for(int i = 0; i < index; i++){
			newList.add(list.get(i));
		}
		
		newList.add(value);
		
		for(int i = index; i < list.size(); i++){
			newList.add(list.get(i));
		}
		
		return newList;
	}
	
	//-------同时声明参数值、类型、是否输入参数
	protected Ps setParameter(int index, Object value, int type){
		set(parameters, index, new SqlParameter(type, value));
		return this;
	}
	
	protected Ps addParameter(Object value, int type){
		parameters.add(new SqlParameter(type, value));
		return this;
	}
	
	
	//-------------------------------------------取值
	public int getParameterSize(){
		return parameters.size();
	}
	
	/**
	 * 获取所有参数值
	 */
	public List<SqlParameter> getParameters(){
		return parameters;
	}
	
	//-----------------------------------------------------------------------------设置预编译参数
	
	//----------------------------按照指定下标设置参数
	/**
	 * 设置预编译参数，根据对象的java类型设置预编译参数类型
	 */
	public Ps set(int index, Object value){
		return setParameter(index, value, SqlUtil.getSqlType(value));
	}
	
	public Ps set(int index, Object value, int type){
		return setParameter(index, value, type);
	}
	
	public Ps setNull(int index){
		return setParameter(index, null, Types.NULL);
	}
	
	public Ps set(int index, String value){
		return setParameter(index, value, Types.VARCHAR);
	}
	
	public Ps set(int index, boolean value){
		return setParameter(index, new Boolean(value), Types.BOOLEAN);
	}
	
	public Ps set(int index, BigDecimal value){
		return setParameter(index, value, Types.NUMERIC);
	}
	
	public Ps set(int index, int value){
		return setParameter(index, new Integer(value), Types.INTEGER);
	}
	
	public Ps set(int index, long value){
		return setParameter(index, new Long(value), Types.BIGINT);
	}
	
	public Ps set(int index, double value){
		return setParameter(index, new Double(value), Types.DOUBLE);
	}
	
	public Ps set(int index, float value){
		return setParameter(index, new Float(value), Types.FLOAT);
	}
	
	public Ps set(int index, Blob value){
		return setParameter(index, value, Types.BLOB);
	}
	
	public Ps set(int index, Clob value){
		return setParameter(index, value, Types.CLOB);
	}
	
	public Ps set(int index, Date date){
		return setParameter(index, date, Types.DATE);
	}
	
	public Ps set(int index, java.sql.Date date){
		return setParameter(index, date, Types.DATE);
	}
	
	public Ps set(int index, Time time){
		return setParameter(index, time, Types.TIME);
	}
	
	public Ps set(int index, Timestamp time){
		return setParameter(index, time, Types.TIMESTAMP);
	}
	
	//----------------------------按顺序增加参数
	/**
	 * 增加预编译参数，根据对象的java类型设置预编译参数类型
	 */
	public Ps add(Object value){
		return addParameter(value, SqlUtil.getSqlType(value));
	}
	
	public Ps add(Object value, int type){
		return addParameter(value, type);
	}
	
	public Ps addNull(){
		return addParameter(null, Types.NULL);
	}
	
	public Ps add(String value){
		return addParameter(value, Types.VARCHAR);
	}
	
	public Ps add(boolean value){
		return addParameter(value, Types.BOOLEAN);
	}
	
	public Ps add(BigDecimal value){
		return addParameter(value, Types.NUMERIC);
	}
	
	public Ps add(int value){
		return addParameter(value, Types.INTEGER);
	}
	
	public Ps add(long value){
		return addParameter(value, Types.BIGINT);
	}
	
	public Ps add(float value){
		return addParameter(value, Types.FLOAT);
	}
	
	public Ps add(double value){
		return addParameter(value, Types.DOUBLE);
	}
	
	public Ps add(Blob value){
		return addParameter(value, Types.BLOB);
	}

	public Ps add(Clob value){
		return addParameter(value, Types.CLOB);
	}
	
	public Ps add(Date date){
		return addParameter(date, Types.DATE);
	}
	
	public Ps add(java.sql.Date date){
		return addParameter(date, Types.DATE);
	}
	
	public Ps add(Time time){
		return addParameter(time, Types.TIME);
	}
	
	public Ps add(Timestamp time){
		return addParameter(time, Types.TIMESTAMP);
	}
	
	//----------------------------插入参数到指定位置
	/**
	 * 插入预编译参数，根据对象的java类型设置预编译参数类型
	 */
	public Ps insert(int index, Object value){
		parameters = insert(parameters, index, new SqlParameter(SqlUtil.getSqlType(value), value));
		return this;
	}

	//----------------------------------------------------------------------设置输出参数（用于存储过程、函数调用）
	protected Ps setOutParameter(int index, int type){
		return setOutParameter(index, null, type);
	}
	
	protected Ps setOutParameter(int index, String paramName, int type){
		set(parameters, index, new SqlOutParameter(type, paramName));
		return this;
	}
	
	protected Ps addOutParameter(int type){
		return addOutParameter(null, type);
	}
	
	protected Ps addOutParameter(String paramName, int type){
		parameters.add(new SqlOutParameter(type, paramName));
		return this;
	}
	
	public Ps setOutResultSet(int index, String paramName, int sqlType, Class pojoClass){
		set(parameters, index, new SqlOutParameter(sqlType, paramName, pojoClass, true));
		return this;
	}
	
	public Ps addOutResultSet(String paramName, int sqlType, Class outEntitryClass){
		parameters.add(new SqlOutParameter(sqlType, paramName, outEntitryClass, true));
		return this;
	}
	
	//----------------------------按照指定下标设置输出参数
	public Ps setOutResultSet(int index, int sqlType){
		return setOutResultSet(index, null, sqlType, null);
	}
	
	public Ps setOutResultSet(int index, int sqlType, Class pojoClass){
		return setOutResultSet(index, null, sqlType, pojoClass);
	}
	
	public Ps setOutString(int index){
		return setOutParameter(index, Types.VARCHAR);
	}
	
	public Ps setOutBoolean(int index){
		return setOutParameter(index, Types.BOOLEAN);
	}
	
	public Ps setOutBigDecimal(int index){
		return setOutParameter(index, Types.NUMERIC);
	}
	
	public Ps setOutInt(int index){
		return setOutParameter(index, Types.INTEGER);
	}
	
	public Ps setOutLong(int index){
		return setOutParameter(index, Types.BIGINT);
	}
	
	public Ps setOutFloat(int index){
		return setOutParameter(index, Types.FLOAT);
	}
	
	public Ps setOutDouble(int index){
		return setOutParameter(index, Types.DOUBLE);
	}
	
	public Ps setOutBlob(int index){
		return setOutParameter(index, Types.BLOB);
	}
	
	public Ps setOutClob(int index){
		return setOutParameter(index, Types.CLOB);
	}
	
	public Ps setOutDate(int index){
		return setOutParameter(index, Types.DATE);
	}
	
	public Ps setOutTime(int index){
		return setOutParameter(index, Types.TIME);
	}
	
	public Ps setOutTimestamp(int index){
		return setOutParameter(index, Types.TIMESTAMP);
	}

	//-------为输出参数命名
	public Ps setOutResultSet(int index, String paramName, int sqlType){
		return setOutResultSet(index, paramName, sqlType, null);
	}
	
	public Ps setOutString(int index, String paramName){
		return setOutParameter(index, paramName, Types.VARCHAR);
	}
	
	public Ps setOutBoolean(int index, String paramName){
		return setOutParameter(index, paramName, Types.BOOLEAN);
	}
	
	public Ps setOutBigDecimal(int index, String paramName){
		return setOutParameter(index, paramName, Types.NUMERIC);
	}
	
	public Ps setOutInt(int index, String paramName){
		return setOutParameter(index, paramName, Types.INTEGER);
	}
	
	public Ps setOutLong(int index, String paramName){
		return setOutParameter(index, paramName, Types.BIGINT);
	}
	
	public Ps setOutFloat(int index, String paramName){
		return setOutParameter(index, paramName, Types.FLOAT);
	}
	
	public Ps setOutDouble(int index, String paramName){
		return setOutParameter(index, paramName, Types.DOUBLE);
	}
	
	public Ps setOutBlob(int index, String paramName){
		return setOutParameter(index, paramName, Types.BLOB);
	}
	
	public Ps setOutClob(int index, String paramName){
		return setOutParameter(index, paramName, Types.CLOB);
	}
	
	public Ps setOutDate(int index, String paramName){
		return setOutParameter(index, paramName, Types.DATE);
	}
	
	public Ps setOutTime(int index, String paramName){
		return setOutParameter(index, paramName, Types.TIME);
	}
	
	public Ps setOutTimestamp(int index, String paramName){
		return setOutParameter(index, paramName, Types.TIMESTAMP);
	}
	
	//----------------------------按顺序增加输出参数
	public Ps addOutResultSet(int sqlType){
		return addOutResultSet(null, sqlType, null);
	}
	
	public Ps addOutResultSet(int sqlType, Class pojoClass){
		return addOutResultSet(null, sqlType, pojoClass);
	}
	
	public Ps addOutString(){
		return addOutParameter(Types.VARCHAR);
	}
	
	public Ps addOutBoolean(){
		return addOutParameter(Types.BOOLEAN);
	}
	
	public Ps addOutBigDecimal(){
		return addOutParameter(Types.NUMERIC);
	}
	
	public Ps addOutInt(){
		return addOutParameter(Types.INTEGER);
	}
	
	public Ps addOutLong(){
		return addOutParameter(Types.BIGINT);
	}
	
	public Ps addOutFloat(){
		return addOutParameter(Types.FLOAT);
	}
	
	public Ps addOutDouble(){
		return addOutParameter(Types.DOUBLE);
	}
	
	public Ps addOutBlob(){
		return addOutParameter(Types.BLOB);
	}
	
	public Ps addOutClob(){
		return addOutParameter(Types.CLOB);
	}
	
	public Ps addOutDate(){
		return addOutParameter(Types.DATE);
	}
	
	public Ps addOutTime(){
		return addOutParameter(Types.TIME);
	}
	
	public Ps addOutTimestamp(){
		return addOutParameter(Types.TIMESTAMP);
	}
	
	//-------为输出参数命名
	public Ps addOutResultSet(String paramName, int sqlType){
		return addOutResultSet(paramName, sqlType, null);
	}
	
	public Ps addOutString(String paramName){
		return addOutParameter(paramName, Types.VARCHAR);
	}
	
	public Ps addOutBoolean(String paramName){
		return addOutParameter(paramName, Types.BOOLEAN);
	}
	
	public Ps addOutBigDecimal(String paramName){
		return addOutParameter(paramName, Types.NUMERIC);
	}
	
	public Ps addOutInt(String paramName){
		return addOutParameter(paramName, Types.INTEGER);
	}
	
	public Ps addOutLong(String paramName){
		return addOutParameter(paramName, Types.BIGINT);
	}
	
	public Ps addOutFloat(String paramName){
		return addOutParameter(paramName, Types.FLOAT);
	}
	
	public Ps addOutDouble(String paramName){
		return addOutParameter(paramName, Types.DOUBLE);
	}
	
	public Ps addOutBlob(String paramName){
		return addOutParameter(paramName, Types.BLOB);
	}
	
	public Ps addOutClob(String paramName){
		return addOutParameter(paramName, Types.CLOB);
	}
	
	public Ps addOutDate(String paramName){
		return addOutParameter(paramName, Types.DATE);
	}
	
	public Ps addOutTime(String paramName){
		return addOutParameter(paramName, Types.TIME);
	}
	
	public Ps addOutTimestamp(String paramName){
		return addOutParameter(paramName, Types.TIMESTAMP);
	}
	
	//----------------------------------------------------------------------设置输入输出参数，即参数既是输入也是输出（用于存储过程、函数调用）
	protected Ps addInOutParameter(Object value, int type){
		parameters.add(new SqlInOutParameter(type, value));
		return this;
	}

	protected Ps setInOutParameter(int index, Object value, int type){
		set(parameters, index, new SqlInOutParameter(type, value));
		return this;
	}
	
	protected Ps addInOutParameter(String paramName, Object value, int type){
		parameters.add(new SqlInOutParameter(type, paramName, value));
		return this;
	}

	protected Ps setInOutParameter(int index, String paramName, Object value, int type){
		set(parameters, index, new SqlInOutParameter(type, value));
		return this;
	}
	
	//----------------------------按照指定下标设置输入输出参数
	/**
	 * 设置预编译参数，根据对象的java类型设置预编译参数类型
	 */
	public Ps setInOut(int index, Object value){
		return setInOutParameter(index, value, SqlUtil.getSqlType(value));
	}
	
	public Ps setInOut(int index, Object value, int type){
		return setInOutParameter(index, value, type);
	}
	
	public Ps setInOutNull(int index){
		return setInOutParameter(index, null, Types.NULL);
	}
	
	public Ps setInOut(int index, String value){
		return setInOutParameter(index, value, Types.VARCHAR);
	}
	
	public Ps setInOut(int index, boolean value){
		return setInOutParameter(index, new Boolean(value), Types.BOOLEAN);
	}
	
	public Ps setInOut(int index, BigDecimal value){
		return setInOutParameter(index, value, Types.NUMERIC);
	}
	
	public Ps setInOut(int index, int value){
		return setInOutParameter(index, new Integer(value), Types.INTEGER);
	}
	
	public Ps setInOut(int index, long value){
		return setInOutParameter(index, new Long(value), Types.BIGINT);
	}
	
	public Ps setInOut(int index, double value){
		return setInOutParameter(index, new Double(value), Types.DOUBLE);
	}
	
	public Ps setInOut(int index, float value){
		return setInOutParameter(index, new Float(value), Types.FLOAT);
	}
	
	public Ps setInOut(int index, Blob value){
		return setInOutParameter(index, value, Types.BLOB);
	}
	
	public Ps setInOut(int index, Clob value){
		return setInOutParameter(index, value, Types.CLOB);
	}
	
	public Ps setInOut(int index, Date date){
		return setInOutParameter(index, date, Types.DATE);
	}
	
	public Ps setInOut(int index, java.sql.Date date){
		return setInOutParameter(index, date, Types.DATE);
	}
	
	public Ps setInOut(int index, Time time){
		return setInOutParameter(index, time, Types.TIME);
	}
	
	public Ps setInOut(int index, Timestamp time){
		return setInOutParameter(index, time, Types.TIMESTAMP);
	}
	
	//-------为输出参数命名
	public Ps setInOut(int index, String paramName, Object value){
		return setInOutParameter(index, paramName, value, SqlUtil.getSqlType(value));
	}
	
	public Ps setInOut(int index, String paramName, Object value, int type){
		return setInOutParameter(index, paramName, value, type);
	}
	
	public Ps setInOutNull(int index, String paramName){
		return setInOutParameter(index, paramName, null, Types.NULL);
	}
	
	public Ps setInOut(int index, String paramName, String value){
		return setInOutParameter(index, paramName, value, Types.VARCHAR);
	}
	
	public Ps setInOut(int index, String paramName, boolean value){
		return setInOutParameter(index, paramName, new Boolean(value), Types.BOOLEAN);
	}
	
	public Ps setInOut(int index, String paramName, BigDecimal value){
		return setInOutParameter(index, paramName, value, Types.NUMERIC);
	}
	
	public Ps setInOut(int index, String paramName, int value){
		return setInOutParameter(index, paramName, new Integer(value), Types.INTEGER);
	}
	
	public Ps setInOut(int index, String paramName, long value){
		return setInOutParameter(index, paramName, new Long(value), Types.BIGINT);
	}
	
	public Ps setInOut(int index, String paramName, double value){
		return setInOutParameter(index, paramName, new Double(value), Types.DOUBLE);
	}
	
	public Ps setInOut(int index, String paramName, float value){
		return setInOutParameter(index, paramName, new Float(value), Types.FLOAT);
	}
	
	public Ps setInOut(int index, String paramName, Blob value){
		return setInOutParameter(index, paramName, value, Types.BLOB);
	}
	
	public Ps setInOut(int index, String paramName, Clob value){
		return setInOutParameter(index, paramName, value, Types.CLOB);
	}
	
	public Ps setInOut(int index, String paramName, Date date){
		return setInOutParameter(index, paramName, date, Types.DATE);
	}
	
	public Ps setInOut(int index, String paramName, java.sql.Date date){
		return setInOutParameter(index, paramName, date, Types.DATE);
	}
	
	public Ps setInOut(int index, String paramName, Time time){
		return setInOutParameter(index, paramName, time, Types.TIME);
	}
	
	public Ps setInOut(int index, String paramName, Timestamp time){
		return setInOutParameter(index, paramName, time, Types.TIMESTAMP);
	}
	
	//----------------------------按顺序增加参数
	/**
	 * 增加预编译参数，根据对象的java类型设置预编译参数类型
	 */
	public Ps addInOut(Object value){
		return addInOutParameter(value, SqlUtil.getSqlType(value));
	}
	
	public Ps addInOut(Object value, int type){
		return addInOutParameter(value, type);
	}
	
	public Ps addInOutNull(){
		return addInOutParameter(null, Types.NULL);
	}
	
	public Ps addInOut(String value){
		return addInOutParameter(value, Types.VARCHAR);
	}
	
	public Ps addInOut(boolean value){
		return addInOutParameter(value, Types.BOOLEAN);
	}
	
	public Ps addInOut(BigDecimal value){
		return addInOutParameter(value, Types.NUMERIC);
	}
	
	public Ps addInOut(int value){
		return addInOutParameter(value, Types.INTEGER);
	}
	
	public Ps addInOut(long value){
		return addInOutParameter(value, Types.BIGINT);
	}
	
	public Ps addInOut(float value){
		return addInOutParameter(value, Types.FLOAT);
	}
	
	public Ps addInOut(double value){
		return addInOutParameter(value, Types.DOUBLE);
	}
	
	public Ps addInOut(Blob value){
		return addInOutParameter(value, Types.BLOB);
	}

	public Ps addInOut(Clob value){
		return addInOutParameter(value, Types.CLOB);
	}
	
	public Ps addInOut(Date date){
		return addInOutParameter(date, Types.DATE);
	}
	
	public Ps addInOut(java.sql.Date date){
		return addInOutParameter(date, Types.DATE);
	}
	
	public Ps addInOut(Time time){
		return addInOutParameter(time, Types.TIME);
	}
	
	public Ps addInOut(Timestamp time){
		return addInOutParameter(time, Types.TIMESTAMP);
	}
	
	//-------为输出参数命名
	public Ps addInOut(String paramName, Object value){
		return addInOutParameter(paramName, value, SqlUtil.getSqlType(value));
	}
	
	public Ps addInOut(String paramName, Object value, int type){
		return addInOutParameter(paramName, value, type);
	}
	
	public Ps addInOutNull(String paramName){
		return addInOutParameter(paramName, null, Types.NULL);
	}
	
	public Ps addInOut(String paramName, String value){
		return addInOutParameter(paramName, value, Types.VARCHAR);
	}
	
	public Ps addInOut(String paramName, boolean value){
		return addInOutParameter(paramName, value, Types.BOOLEAN);
	}
	
	public Ps addInOutBig(String paramName, BigDecimal value){
		return addInOutParameter(paramName, value, Types.NUMERIC);
	}
	
	public Ps addInOut(String paramName, int value){
		return addInOutParameter(paramName, value, Types.INTEGER);
	}
	
	public Ps addInOut(String paramName, long value){
		return addInOutParameter(paramName, value, Types.BIGINT);
	}
	
	public Ps addInOut(String paramName, float value){
		return addInOutParameter(paramName, value, Types.FLOAT);
	}
	
	public Ps addInOut(String paramName, double value){
		return addInOutParameter(paramName, value, Types.DOUBLE);
	}
	
	public Ps addInOut(String paramName, Blob value){
		return addInOutParameter(paramName, value, Types.BLOB);
	}

	public Ps addInOut(String paramName, Clob value){
		return addInOutParameter(paramName, value, Types.CLOB);
	}
	
	public Ps addInOut(String paramName, Date date){
		return addInOutParameter(paramName, date, Types.DATE);
	}
	
	public Ps addInOut(String paramName, java.sql.Date date){
		return addInOutParameter(paramName, date, Types.DATE);
	}
	
	public Ps addInOut(String paramName, Time time){
		return addInOutParameter(paramName, time, Types.TIME);
	}
	
	public Ps addInOut(String paramName, Timestamp time){
		return addInOutParameter(paramName, time, Types.TIMESTAMP);
	}
	//-------------------------------------------toString
	public String toString() {
		List values = new ArrayList();
		List<String> sqlTypes = new ArrayList<String>();
		List<String> paramTypes = new ArrayList<String>();
		
		for (SqlParameter parameter : parameters) {
			values.add(parameter.getValue());
			sqlTypes.add(JdbcUtil.getNameByType(parameter.getSqlType()));
			if(parameter instanceof SqlInOutParameter)
				paramTypes.add("INOUT");
			else if(parameter instanceof SqlOutParameter)
				paramTypes.add("OUT");
			else
				paramTypes.add("IN");
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("values: ")
			.append(values)
			.append(", sql types: ")
			.append(sqlTypes)
			.append(", parameter types: ")
			.append(paramTypes);
		return sb.toString();
	}
	
}
