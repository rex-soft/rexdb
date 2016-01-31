package org.rex.db.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class BaseContext {

	private String contextId;

	private Date createTime;

	public BaseContext() {
		contextId = UUID.randomUUID().toString();
		this.createTime = Calendar.getInstance().getTime();
	}

	public String getContextId() {
		return contextId;
	}

	public Date getCreateTime() {
		return createTime;
	}

}
