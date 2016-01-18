package org.rex.db.util;

import java.sql.Types;

public class SqlUtil {

	/**
	 * 根据java对象类型，获取sql type
	 * 
	 * 1. String - Types.VARCHAR
	 * 2. int|Integer - Types.INTEGER
	 * 3. BigDecimal - Types.NUMERIC
	 * 4. long|Long - Types.BIGINT
	 * 5. float|Float - Types.FLOAT
	 * 6. double|Double - Types.DOUBLE
	 * 7. Date - Types.DATE
	 * 8. Time - Types.TIME

	 */
	public static int getSqlType(Object param){
		int type;
		if(param==null) {
			type = Types.NULL;
		}
		else{
			String paramClassName=param.getClass().getName();//参数类名称
			if("java.lang.String".equals(paramClassName)) 
				type = Types.VARCHAR;
			else if("int".equals(paramClassName) || "java.lang.Integer".equals(paramClassName)) 
				type = Types.INTEGER;
			else if("java.math.BigDecimal".equals(paramClassName)) 
				type = Types.NUMERIC;
			else if("long".equals(paramClassName) || "java.lang.Long".equals(paramClassName)) 
				type = Types.BIGINT;
			else if("float".equals(paramClassName) || "java.lang.Float".equals(paramClassName)) 
				type = Types.FLOAT;
			else if("double".equals(paramClassName) || "java.lang.Double".equals(paramClassName)) 
				type = Types.DOUBLE;
			else if("java.util.Date".equals(paramClassName) || "java.sql.Date".equals(paramClassName)) 
				type = Types.DATE;
			else if("java.sql.Time".equals(paramClassName)) 
				type = Types.TIME;
			else if("java.sql.Timestamp".equals(paramClassName)) 
				type = Types.TIMESTAMP;
			else if("java.sql.Blob".equals(paramClassName)) //不确定BLOB/CLOB是否可用
				type = Types.BLOB;
			else if("java.sql.Clob".equals(paramClassName)) 
				type = Types.CLOB;
			else
				type = Types.VARCHAR;//其余一概作为varchar处理
		}

		return type;
	}
	
	/**
	 * 根据对象数组声明参数<br/>
	 * 1. String - Types.VARCHAR
	 * 2. int|Integer - Types.INTEGER
	 * 3. BigDecimal - Types.NUMERIC
	 * 4. long|Long - Types.BIGINT
	 * 5. float|Float - Types.FLOAT
	 * 6. double|Double - Types.DOUBLE
	 * 7. Date - Types.DATE
	 * 8. Time - Types.TIME
	 * @param params 参数数组，与SQL语句中的‘?’一一对应
	 */
	public static int[] getSqlTypes(Object[] params){
		if(params==null) return null;
		
		int[] paramTypes = new int[params.length];
		for(int i=0;i<params.length;i++){
			paramTypes[i] = getSqlType(params[i]);
		}
		return paramTypes;
	}
}
