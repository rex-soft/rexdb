package org.rex.db.datasource;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.rex.db.exception.DBException;

/**
 * JNDI连接池工厂
 */
public class JndiDataSourceFactory implements DataSourceFactory {

	public static final String INITIAL_CONTEXT = "context";
	public static final String DATA_SOURCE = "jndi";

	private volatile DataSource dataSource;
	
	public JndiDataSourceFactory(){
	}
	
	public JndiDataSourceFactory(Properties properties) throws DBException{
		setProperties(properties);
	}

	public synchronized void setProperties(Properties properties) throws DBException {
		InitialContext initCtx = null;
		try {
			if (properties == null) {
				initCtx = new InitialContext();
			} else {
				initCtx = new InitialContext(properties);
			}

			if (properties.containsKey(INITIAL_CONTEXT) && properties.containsKey(DATA_SOURCE)) {
				Context ctx = (Context) initCtx.lookup(properties.getProperty(INITIAL_CONTEXT));
				dataSource = (DataSource) ctx.lookup(properties.getProperty(DATA_SOURCE));
			} else if (properties.containsKey(DATA_SOURCE)) {
				dataSource = (DataSource) initCtx.lookup(properties.getProperty(DATA_SOURCE));
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
