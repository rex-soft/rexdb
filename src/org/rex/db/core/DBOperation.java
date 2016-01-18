package org.rex.db.core;

import javax.sql.DataSource;

import org.rex.db.configuration.Configuration;
import org.rex.db.dialect.Dialect;
import org.rex.db.dialect.DialectManager;
import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

public class DBOperation {
	
	private String sql;
	private DBTemplate template = new DBTemplate();
	
	private Dialect dialect;//方言
	
	protected DBTemplate getTemplate() {
		return template;
	}
	
	public void setTemplate(DBTemplate template) {
		this.template = template;
		this.dialect = null;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.template.setDataSource(dataSource);
		this.dialect = null;
	}
	
	/**
	 * 设置SQL
	 */
	public void setSql(String sql) {
		if (sql == null)
			throw new DBRuntimeException("DB-Q10011");
		
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
		if(template.getDataSource() == null)
			throw new DBException("DB-D10003");
		
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
