package org.rex.db.exception;


public class DBException extends Exception {
	
	public DBException(String code) {
		super(ExceptionResourceFactory.getInstance().translate(code));
	}
	
	public DBException(String code, Object... params) {
		super(ExceptionResourceFactory.getInstance().translate(code, params));
	}

	public DBException(String code, Throwable e, Object... params) {
		super(ExceptionResourceFactory.getInstance().translate(code, params), e);
	}
}