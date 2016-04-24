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
package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.rex.db.configuration.Configuration;
import org.rex.db.dynamic.javassist.BeanConvertor;
import org.rex.db.dynamic.javassist.BeanConvertorManager;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.ReflectUtil;

/**
 * Bean ResultSet reader.
 * 
 * @version 1.0, 2016-03-17
 * @since Rexdb-1.0
 */
public class ClassResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Class<T> resultClass;
	private List<T> results;

	private int rowNum = 0;

	int[] columnsCodeCacheForDynamic = null;
	
	//----------settings
	/**
	 * user dynamic class
	 * @throws DBException 
	 */
	private static boolean isDynamic() throws DBException{
		return Configuration.getCurrentConfiguration().isDynamicClass();
	}
	

	//--------construct
	public ClassResultReader(Class<T> resultClass) {
		this.results = new LinkedList<T>();
		this.resultClass = resultClass;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		results.add(row2Bean(rs, rowNum++));
	}

	public List<T> getResults() {
		return results;
	}

	// --------private methods
	private T row2Bean(ResultSet rs, int rowNum) throws DBException {
		if (resultClass == null)
			throw new DBException("DB-C0003");
		if(isDynamic()){
			BeanConvertor setter = BeanConvertorManager.getConvertor(resultClass);
			String[] rsLabelsRenamed = orUtil.getResultSetLabelsRenamed(rs);
			if(columnsCodeCacheForDynamic == null)
				columnsCodeCacheForDynamic = setter.getColumnCodes(rsLabelsRenamed);

			try {
				return (T)setter.readResultSet(rs, orUtil, columnsCodeCacheForDynamic);
			} catch (SQLException e) {
				throw new DBException(e);
			}
		}else{
			T bean = ReflectUtil.instance(resultClass);
			return orUtil.rs2Object(rs, bean);
		}
		
	}
}
