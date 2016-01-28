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
 * JNDI连接池工厂
 */
public class JndiDataSourceFactory implements DataSourceFactory {

	public static final String INITIAL_CONTEXT = "context";
	public static final String JNDI_NAME = "jndi";

	private volatile DataSource dataSource;
	
	public JndiDataSourceFactory(){
	}
	
	public JndiDataSourceFactory(Properties properties) throws DBException{
		setProperties(properties);
	}

	public synchronized void setProperties(Properties properties) throws DBException {
		
		
		String jndiName = properties.getProperty(JNDI_NAME);
		String initialContext = properties.getProperty(INITIAL_CONTEXT);
		if(StringUtil.isEmptyString(jndiName))
			throw new DBException("DB-D0004", JNDI_NAME, DataSourceUtil.hiddenPassword(properties));
		
		InitialContext initCtx = null;
		try {
			initCtx = new InitialContext(properties);
			if (!StringUtil.isEmptyString(initialContext)) {
				Context ctx = (Context) initCtx.lookup(initialContext);
				dataSource = (DataSource) ctx.lookup(jndiName);
			} else {
				dataSource = (DataSource) initCtx.lookup(jndiName);
			}

		} catch (NamingException e) {
			throw new DBException("There was an error configuring JndiDataSourceTransactionPool. Cause: "+ e, e);
		}finally {
			try {
				if (initCtx != null)
					initCtx.close();
			}catch (NamingException ex) {
			}
		}
	}

	public synchronized DataSource getDataSource() {
		return dataSource;
	}
}
