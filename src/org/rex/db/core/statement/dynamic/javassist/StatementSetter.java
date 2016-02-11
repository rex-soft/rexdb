package org.rex.db.core.statement.dynamic.javassist;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class StatementSetter {

	/**
	 * 将实体类转换为Ps对象
	 * @param bean
	 * @return
	 */
	public abstract void setParameters(PreparedStatement preparedStatement, Object bean, String[] requiredParam) throws SQLException;
	
	
	protected static Object convertValue(Object o){
		return o;
	}
	
	protected static Integer convertValue(int v){
		return new Integer(v);
	}
	
	protected static Boolean convertValue(boolean v){
		return new Boolean(v);
	}
	
	protected static Byte convertValue(byte v){
		return new Byte(v);
	}
	
	protected static String convertValue(char v){
		return String.valueOf(v);
	}
	
	protected static Double convertValue(double v){
		return new Double(v);
	}
	
	protected static Float convertValue(float v){
		return new Float(v);
	}
	
	protected static Long convertValue(long v){
		return new Long(v);
	}
	
	protected static Short convertValue(short v){
		return new Short(v);
	}
	
	//---array
	protected static String convertValue(int[] v){
		return Arrays.toString((int[])v);
	}
	
	protected static String convertValue(boolean[] v){
		return Arrays.toString((boolean[])v);
	}
	
	protected static String convertValue(byte[] v){
		return Arrays.toString((byte[])v);
	}
	
	protected static String convertValue(char[] v){
		return Arrays.toString((char[])v);
	}
	
	protected static String convertValue(double[] v){
		return Arrays.toString((double[])v);
	}
	
	protected static String convertValue(float[] v){
		return Arrays.toString((float[])v);
	}
	
	protected static String convertValue(long[] v){
		return Arrays.toString((long[])v);
	}
	
	protected static String convertValue(short[] v){
		return Arrays.toString((short[])v);
	}
}
