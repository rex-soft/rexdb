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
package org.rex.db.core.statement;

import org.rex.db.Ps;

public class StatementCreatorManager {
	
	private StatementCreator psStatementCreator, beanStatementCreator, arrayStatementCreator;
	
	public StatementCreatorManager(){
		psStatementCreator = new PsStatementCreator();
		beanStatementCreator = new BeanStatementCreator();
		arrayStatementCreator = new ArrayStatementCreator();
	}

	/**
	 * get a default StatementCreator for no prepared statement
	 * @return
	 */
	public StatementCreator get(){
		return beanStatementCreator;
	}
	
	/**
	 * get matched StatementCreator
	 * @param parameter
	 * @return
	 */
	public StatementCreator get(Object[] parameters){
		if(parameters == null)
			return get();
		for (int i = 0; i < parameters.length; i++) {
			if(parameters[i] != null)
				return get(parameters[i]);
		}
		return get();
	}
	
	public StatementCreator get(Object parameters){
		if(parameters == null)
			return get();
		else if(parameters instanceof Ps)
			return psStatementCreator;
		else if(parameters.getClass().isArray())
			return arrayStatementCreator;
		else
			return beanStatementCreator;
	}
}
