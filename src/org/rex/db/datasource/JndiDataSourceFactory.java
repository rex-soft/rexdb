package org.rex.db.datasource;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.rex.db.exception.DBException;
import org.rex.db.util.DataSourceUtil;
import org.rex.db.util.StringUtil;

/**
 * 根据JNDI加载数据源，通常用于Java EE服务器环境中
 */
public class JndiDataSourceFactory extends DataSourceFactory {

	public static final String INITIAL_CONTEXT = "context";
	public static final String JNDI_NAME = "jndi";

	public JndiDataSourceFactory(Properties properties) throws DBException {
		super(properties);
	}
	
	public DataSource createDataSource() throws DBException {
		Properties properties = getProperties();
				
		String jndiName = properties.getProperty(JNDI_NAME), initialContext = properties.getProperty(INITIAL_CONTEXT);
		if(StringUtil.isEmptyString(jndiName))
			throw new DBException("DB-D0004", JNDI_NAME, DataSourceUtil.hiddenPassword(properties));
		
		InitialContext initCtx = null;
		try {
			DataSource dataSource;
			initCtx = new InitialContext(properties);
			if (!StringUtil.isEmptyString(initialContext)) {
				Context ctx = (Context) initCtx.lookup(initialContext);
				dataSource = (DataSource) ctx.lookup(jndiName);
			} else {
				dataSource = (DataSource) initCtx.lookup(jndiName);
			}
			
			return dataSource;
		} catch (NamingException e) {
			throw new DBException("DB-D0005", e, e.getMessage(), properties);
		}finally {
			try {
				if (initCtx != null)
					initCtx.close();
			}catch (NamingException ex) {
			}
		}
	}
	
}
