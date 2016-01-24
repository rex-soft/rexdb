package org.rex.db.logger.log4j;

import org.apache.commons.logging.Log;
import org.rex.db.logger.Logger;
import org.rex.db.util.LoggerUtil;

public class Log4jLogger implements Logger {
    
    private Log log;
    
    public Log4jLogger(Log log) {
        this.log = log;
    }

    public void error(String msg, String... args) {
        log.error(LoggerUtil.format(msg, args));
    }

    public void error(String msg, Object... args) {
        log.error(LoggerUtil.format(msg, args));
    }

    public void error(String msg, Throwable ex, String... args) {
        log.error(LoggerUtil.format(msg, args), ex);
    }

    public void info(String msg, String... args) {
        log.info(LoggerUtil.format(msg, args));
    }

    public void info(String msg, Throwable ex, String... args) {
        log.info(LoggerUtil.format(msg, args), ex);
    }

    

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void warn(String msg, String... args) {
        log.warn(LoggerUtil.format(msg, args));
    }

    public void warn(String msg, Object... args) {
        log.warn(LoggerUtil.format(msg, args));
    }

    public void warn(String msg, Throwable ex, String... args) {
        log.warn(LoggerUtil.format(msg, args), ex);
    }
    
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }
    
    public void debug(String msg, String... args) {
        log.debug(LoggerUtil.format(msg, args));
    }

    public void debug(String msg, Object... args) {
        log.debug(LoggerUtil.format(msg, args));
    }

    public void debug(String msg, Throwable ex, String... args) {
        log.debug(LoggerUtil.format(msg, args), ex);
    }
    
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }
    
    public void trace(String msg, String... args) {
        log.trace(LoggerUtil.format(msg, args));
    }

    public void trace(String msg, Object... args) {
        log.trace(LoggerUtil.format(msg, args));
    }

    public void trace(String msg, Throwable ex, String... args) {
        log.trace(LoggerUtil.format(msg, args), ex);
    }


    public void fatal(String msg, String... args) {
        log.fatal(LoggerUtil.format(msg, args));
    }

    public void fatal(String msg, Throwable ex, String... args) {
        log.fatal(LoggerUtil.format(msg, args), ex);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

}
