package org.rex.db.core;

import javax.sql.DataSource;

import org.rex.db.configuration.Configuration;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;

/**
 * 数据库操作基类
 */
public class DBOperation {
	
	private String sql;
	private DBTemplate template;
	private Dialect dialect;
	
	protected DBTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(DBTemplate template) {
		this.template = template;
		this.dialect = null;
	}
	
	public void setDataSource(DataSource dataSource) throws DBException {
		if(dataSource == null)
			throw new DBException("DB-C0008");
		
		this.template = new DBTemplate(dataSource);
		this.dialect = null;
	}
	
	/**
	 * 设置SQL
	 */
	public void setSql(String sql) throws DBException {
		if (sql == null)
			throw new DBException("DB-C0009");
		
		this.sql = sql;
	}

	/**
	 * 获取SQL
	 */
	public String getSql() {
		return sql;
	}
	
	/**
	 * 获取方言
	 */
	protected Dialect getDialect() throws DBException{
		if(dialect == null){
			dialect = getDialectManager().getDialect(template.getDataSource());
		}
		
		return dialect;
	}
	
	/**
	 * 获取方言管理器
	 * @throws DBException 
	 */
	private DialectManager getDialectManager() throws DBException{
		return Configuration.getCurrentConfiguration().getDialectManager();
	}
}
