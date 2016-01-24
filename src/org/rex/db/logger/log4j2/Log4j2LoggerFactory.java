package org.rex.db.logger.log4j2;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class Log4j2LoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(cls));
    }

    @Override
    protected Logger getLoggerImpl(String name) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(name));
    }

}
