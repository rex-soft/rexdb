package org.rex.db.logger.log4j;

import org.apache.commons.logging.LogFactory;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class Log4jLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Log4jLogger(LogFactory.getLog(cls));
    }
    
    @Override
    protected Logger getLoggerImpl(String name) {
        return new Log4jLogger(LogFactory.getLog(name));
    }

}
