package org.rex.db.logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.rex.db.exception.DBRuntimeException;
import org.rex.db.logger.jdk.JdkLoggerFactory;
import org.rex.db.logger.log4j.Log4jLoggerFactory;
import org.rex.db.logger.log4j2.Log4j2LoggerFactory;
import org.rex.db.logger.slf4j.Slf4jLoggerFactory;

public abstract class LoggerFactory {

	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	private static LoggerFactory factory;

	private static final List<LoggerClass> loggers = new LinkedList<LoggerClass>() {
		{
			add(new LoggerClass<Log4jLoggerFactory>("org.apache.commons.logging.LogFactory", Log4jLoggerFactory.class));
			add(new LoggerClass<Slf4jLoggerFactory>("org.slf4j.LoggerFactory", Slf4jLoggerFactory.class));
			add(new LoggerClass<Log4j2LoggerFactory>("org.apache.logging.log4j.LogManager", Log4j2LoggerFactory.class));
		}
	};

	private static void createLoggerFactory() {
		for (LoggerClass logger : loggers) {
			if (logger.isSupported()) {
				factory = logger.createInstance();
				break;
			}
		}
		if (factory == null)
			factory = new JdkLoggerFactory();
	}

	public static Logger getLogger(Class<?> cls) {
		return getLoggerFactory().getLoggerImpl(cls);
	}

	public static Logger getLogger(String name) {
		return getLoggerFactory().getLoggerImpl(name);
	}

	protected static LoggerFactory getLoggerFactory() {
		lock.readLock().lock();
		try {
			if (factory != null)
				return factory;
		} finally {
			lock.readLock().unlock();
		}
		lock.writeLock().lock();
		try {
			if (factory == null) {
				createLoggerFactory();
			}
			return factory;
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected abstract Logger getLoggerImpl(Class<?> cls);

	protected abstract Logger getLoggerImpl(String name);

	private static class LoggerClass<T extends LoggerFactory> {

		private final String loggerClazzName;
		private final Class<T> loggerImplClazz;

		public LoggerClass(String loggerClazzName, Class<T> loggerImplClazz) {
			this.loggerClazzName = loggerClazzName;
			this.loggerImplClazz = loggerImplClazz;
		}

		public boolean isSupported() {
			try {
				Class.forName(loggerClazzName);
				return true;
			} catch (ClassNotFoundException ignore) {
				return false;
			}
		}

		public LoggerFactory createInstance() {
			try {
				return loggerImplClazz.newInstance();
			} catch (Exception e) {
				throw new DBRuntimeException("", e);
			}
		}
	}

}
