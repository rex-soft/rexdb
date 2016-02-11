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
