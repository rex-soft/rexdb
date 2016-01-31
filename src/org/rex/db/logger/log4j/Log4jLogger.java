package org.rex.db.logger.log4j;

import org.apache.commons.logging.Log;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;
import org.rex.db.util.StringUtil;

public class Log4jLogger implements Logger {

	private Log log;

	public Log4jLogger(Log log) {
		this.log = log;
	}

	public void error(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.error(StringUtil.format(msg, args));
	}

	public void error(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.error(StringUtil.format(msg, args));
	}

	public void error(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.error(StringUtil.format(msg, args), ex);
	}

	public void info(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.info(StringUtil.format(msg, args));
	}

	public void info(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.info(StringUtil.format(msg, args));
	}

	public void info(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.info(StringUtil.format(msg, args), ex);
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public void warn(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.warn(StringUtil.format(msg, args));
	}

	public void warn(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.warn(StringUtil.format(msg, args));
	}

	public void warn(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.warn(StringUtil.format(msg, args), ex);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public void debug(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.debug(StringUtil.format(msg, args));
	}

	public void debug(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.debug(StringUtil.format(msg, args));
	}

	public void debug(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.debug(StringUtil.format(msg, args), ex);
	}

	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	public void trace(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.trace(StringUtil.format(msg, args));
	}

	public void trace(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.trace(StringUtil.format(msg, args));
	}

	public void trace(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.trace(StringUtil.format(msg, args), ex);
	}

	public void fatal(String msg, String... args) {
		if (!LoggerFactory.isNolog())
			log.fatal(StringUtil.format(msg, args));
	}

	public void fatal(String msg, Object... args) {
		if (!LoggerFactory.isNolog())
			log.fatal(StringUtil.format(msg, args));
	}

	public void fatal(String msg, Throwable ex, String... args) {
		if (!LoggerFactory.isNolog())
			log.fatal(StringUtil.format(msg, args), ex);
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
