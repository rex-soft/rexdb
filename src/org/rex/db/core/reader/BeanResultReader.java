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

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.util.ORUtil;
import org.rex.db.util.ReflectUtil;

/**
 * Bean ResultSet reader.
 * 
 * @version 1.0, 2016-02-12
 * @since Rexdb-1.0
 * @deprecated No longer supports object.
 */
public class BeanResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Ps ps;

	private T resultBean;
	private List<T> results;

	private int rowNum = 0;
	private Method cloneMethod = null;

	public BeanResultReader(Ps ps, T resultBean) {
		this.results = new LinkedList<T>();
		this.ps = ps;
		this.resultBean = resultBean;
	}

	public void setPs(Ps ps) {
		this.ps = ps;
	}

	// --------implements
	public void processRow(ResultSet rs) throws DBException {
		if (resultBean == null)
			throw new DBException("DB-C0003");
		
		T clone = resultBean;
		if (rowNum > 0) {
			Method cloneMethod = getCloneMethod();
			if(cloneMethod == null)
				throw new DBException("DB-C0004", resultBean.getClass().getName());
			
			clone = (T)ReflectUtil.invokeMethod(resultBean, cloneMethod);
		}
		results.add(row2Bean(rs, rowNum++, ps, clone));
	}
	
	public List<T> getResults() {
		return results;
	}
	
	// --------private methods
	private Method getCloneMethod() throws DBException{
		if(cloneMethod == null)
			cloneMethod = ReflectUtil.getCloneMethod(resultBean);
		return cloneMethod;
	}

	protected T row2Bean(ResultSet rs, int rowNum, Ps ps, T bean) throws DBException {
		return orUtil.rs2Object(rs, bean);
	}

}
