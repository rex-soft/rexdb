package org.rex.db.core.reader;

import java.sql.ResultSet;
import java.util.List;

import org.rex.db.exception.DBException;

/**
 * 读取结果集接口
 */
public interface ResultReader<T> {
	 
	/**
	 * 处理单条结果集
	 */
	void processRow(ResultSet rs) throws DBException;
	
	/**
	 * 获取结果对象
	 */
	List<T> getResults();
}

