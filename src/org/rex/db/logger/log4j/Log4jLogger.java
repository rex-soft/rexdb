/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
