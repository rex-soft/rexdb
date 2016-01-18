package org.rex.db.datasource;

import java.util.Properties;
import javax.sql.DataSource;

public interface DataSourceFactory {

	void setProperties(Properties props) throws Exception;

	DataSource getDataSource() throws Exception;
}
