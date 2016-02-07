package org.rex.db.sql.dynamic;

import org.rex.db.Ps;

public interface Bean2Ps {

	/**
	 * 将实体类转换为Ps对象
	 * @param bean
	 * @return
	 */
	public Ps toPs(Object bean, String[] requiredParam);
}
