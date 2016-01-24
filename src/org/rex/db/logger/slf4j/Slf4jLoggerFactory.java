package org.rex.db.logger.slf4j;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class Slf4jLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(cls));
    }

    @Override
    protected Logger getLoggerImpl(String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }

}
