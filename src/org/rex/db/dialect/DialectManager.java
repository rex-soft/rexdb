package org.rex.db.dialect;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 管理数据库方言
 */
public class DialectManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DialectManager.class);

	private final Map<String, Dialect> dialectInstances = Collections.synchronizedMap(new HashMap<String, Dialect>());
	
	/**
	 * 为数据源指定一个方言
	 * @param dataSource 数据源
	 * @param dialect 用户指定的方言
	 */
	public void setDialect(DataSource dataSource, Dialect dialect){
		dialectInstances.put(String.valueOf(dataSource.hashCode()), dialect);
		
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("dialect[{0}] for datasource[{1}] registed.", dialect.getName(), dataSource.hashCode());
	}
	
	/**
	 * 获取数据源对应的方言
	 * @param dataSource 数据源
	 * @return 方言实例
	 * @throws SQLException 获取数据库元数据描述失败时，抛出异常
	 * @throws DBException 
	 */
	public Dialect getDialect(DataSource dataSource) throws DBException {
		String hashCode = String.valueOf(dataSource.hashCode());
		
		synchronized(dialectInstances){
			if (!dialectInstances.containsKey(hashCode)) {
				Dialect dialect = DialectFactory.resolveDialect(dataSource);
				dialectInstances.put(hashCode, dialect);
			}
			return dialectInstances.get(hashCode);
		}
	}
	

}
