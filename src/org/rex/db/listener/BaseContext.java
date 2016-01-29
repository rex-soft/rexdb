package org.rex.db.listener;

import java.util.UUID;

public class BaseContext {

	private String contextId;
	
	public BaseContext(){
		contextId = UUID.randomUUID().toString(); 
	}

	public String getContextId() {
		return contextId;
	}
}
