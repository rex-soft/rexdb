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
package org.rex.db.core.statement;

import org.rex.db.Ps;

/**
 * Statement creator manager.
 *
 * @version 1.0, 2016-02-12
 * @since Rexdb-1.0
 */
public class StatementCreatorManager {
	
	private StatementCreator psStatementCreator, beanStatementCreator, arrayStatementCreator;
	
	public StatementCreatorManager(){
		psStatementCreator = new PsStatementCreator();
		beanStatementCreator = new BeanStatementCreator();
		arrayStatementCreator = new ArrayStatementCreator();
	}

	/**
	 * Returns bean StatementCreator instance.
	 */
	public StatementCreator get(){
		return beanStatementCreator;
	}
	
	/**
	 * Returns suitable instances for the given objects.
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
	
	/**
	 * Returns suitable StatementCreator instance for the given object.
	 */
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
