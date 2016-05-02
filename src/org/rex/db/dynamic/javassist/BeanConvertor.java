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

/**
 * Abstract Bean Convertor.
 * 
 * @version 1.0, 2016-03-17
 * @since Rexdb-1.0
 */
public abstract class BeanConvertor {

	public abstract void setParameters(PreparedStatement preparedStatement, Object bean, String[] requiredParam) throws SQLException;
	
//	public abstract void setParameters(PreparedStatement preparedStatement, Object bean, int[] requiredColumnCodes) throws SQLException;
	
	public abstract int[] getColumnCodes(String[] rsLabelsRenamed);
	
	public abstract Object readResultSet(ResultSet rs, ORUtil orUtil, int[] requiredColumnCodes) throws SQLException, DBException;
	
	//-------------set parameter utils
	protected static Object convertValue(Object o){
		return o;
	}
	
	protected static Integer convertValue(int v){
		return Integer.valueOf(v);
	}
	
	protected static Boolean convertValue(boolean v){
		return Boolean.valueOf(v);
	}
	
	protected static Byte convertValue(byte v){
		return Byte.valueOf(v);
	}
	
	protected static String convertValue(char v){
		return String.valueOf(v);
	}
	
	protected static Double convertValue(double v){
		return Double.valueOf(v);
	}
	
	protected static Float convertValue(float v){
		return Float.valueOf(v);
	}
	
	protected static Long convertValue(long v){
		return Long.valueOf(v);
	}
	
	protected static Short convertValue(short v){
		return Short.valueOf(v);
	}
}
