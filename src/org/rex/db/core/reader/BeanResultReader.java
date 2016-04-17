/**
 * Copyright 2016 the original author or authors.
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
 * 读取单条结果集，进行OR映射
 */
public class BeanResultReader<T> implements ResultReader<T> {

	private ORUtil orUtil = new ORUtil();

	private Ps ps;

	private T resultBean;
	private List<T> results;

	private int rowNum = 0;
	private Method cloneMethod = null;

	/**
	 * 创建结果集读取类，适用于普通查询
	 * 
	 * @param ps 查询参数
	 * @param originalKey 是否按照结果集原始键处理
	 * @param resultPojo
	 */
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
	
	private Method getCloneMethod() throws DBException{
		if(cloneMethod == null)
			cloneMethod = ReflectUtil.getCloneMethod(resultBean);
		return cloneMethod;
	}

	public List<T> getResults() {
		return results;
	}

	/**
	 * OR映射
	 */
	protected T row2Bean(ResultSet rs, int rowNum, Ps ps, T bean) throws DBException {
		return orUtil.rs2Object(rs, bean);
	}
	

}
