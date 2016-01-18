package org.rex.db.exception;

import java.util.Properties;

/**
 * 异常资源
 */
public class ExceptionResource {

	private Properties errors;
	
	public ExceptionResource(Properties errors){
		this.errors = errors;
	}
	
	public String getMessage(String code){
		if(errors == null) return null;
			
		return errors.getProperty(code);
	}

	public String toString() {
		return "ExceptionResource [errors=" + errors + "]";
	}
	
}
