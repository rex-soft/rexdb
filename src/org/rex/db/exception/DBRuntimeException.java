package org.rex.db.exception;


public class DBRuntimeException extends RuntimeException {
	
	public DBRuntimeException(Throwable e){
		super(e);
	}
	
	public DBRuntimeException(String code) {
		super(ExceptionResourceFactory.getInstance().translate(code));
	}
	
	public DBRuntimeException(String code, Object... params) {
		super(ExceptionResourceFactory.getInstance().translate(code, params));
	}

	public DBRuntimeException(String code, Throwable e, Object... params) {
		super(ExceptionResourceFactory.getInstance().translate(code, params), e);
	}

}