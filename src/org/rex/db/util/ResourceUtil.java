package org.rex.db.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 用于加载配置文件
 */
public class ResourceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceUtil.class);

	private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

	/**
	 * 从类路径中加载输入流
	 */
	public static InputStream getResourceAsStream(String resource) throws DBException {
		return getResourceAsStream(null, resource);
	}

	public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws DBException {
		InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
		if (in == null) {
			throw new DBException("DB-URS01", resource);
		}
		return in;
	}

	/**
	 * 从类路径中加载配置
	 */
	public static Properties getResourceAsProperties(String resource) throws DBException {
		return getResourceAsProperties(null, resource, null);
	}

	public static Properties getResourceAsProperties(String resource, String encoding) throws DBException {
		return getResourceAsProperties(null, resource, encoding);
	}

	public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws DBException {
		return getResourceAsProperties(loader, resource, null);
	}

	public static Properties getResourceAsProperties(ClassLoader loader, String resource, String encoding) throws DBException {
		return getProperties(resource, getResourceAsStream(resource), encoding);
	}

	/**
	 * 从类路径中加载File
	 */
	public static File getResourceAsFile(String resource) throws DBException {
		return getResourceAsFile(null, resource);
	}

	public static File getResourceAsFile(ClassLoader loader, String resource) throws DBException {
		return new File(getResourceURL(loader, resource).getFile());
	}

	/**
	 * 从URL中加载资源
	 */
	public static URL getResourceURL(String resource) throws DBException {
		return getResourceURL(null, resource);
	}

	public static URL getResourceURL(ClassLoader loader, String resource) throws DBException {
		URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
		if (url == null) {
			throw new DBException("DB-URS01", resource);
		}
		return url;
	}

	/**
	 * 从URL中加载输入流
	 */
	public static InputStream getUrlAsStream(String urlString) throws DBException {
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			return conn.getInputStream();
		} catch (IOException e) {
			throw new DBException("DB-URS02", e, urlString, e.getMessage());
		}
	}

	public static Properties getUrlAsProperties(String urlString) throws DBException {
		return getUrlAsProperties(urlString, null);
	}

	public static Properties getUrlAsProperties(String urlString, String encoding) throws DBException {
		InputStream inputStream = getUrlAsStream(urlString);
		return getProperties(urlString, inputStream, encoding);
	}

	private static Properties getProperties(String path, InputStream inputStream, String encoding) throws DBException {
		Properties props = new Properties();
		try {
			props.load(inputStream);
		} catch (IOException e) {
			throw new DBException("DB-URS02", e, path, e.getMessage());
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOGGER.warn("could not close input stream of {0}, {1}.", path, e.getMessage());
			}
		}
		
		//transform coding if necessary, it's not smart in this way, but support jdk5
		if(encoding != null){
			for (Iterator<?> iterator = props.keySet().iterator(); iterator.hasNext();) {
				String key = (String)iterator.next();
				String value = props.getProperty(key);
				
				try {
					props.put(key, new String(value.getBytes("ISO-8859-1"), encoding));
				} catch (UnsupportedEncodingException e) {
					throw new DBException("DB-URS02", e, path, e.getMessage());
				}
			}
		}
	
		return props;
	}

	/**
	 * 使用类加载器加载文件
	 */
	static class ClassLoaderWrapper {

		ClassLoader systemClassLoader;

		ClassLoaderWrapper() {
			try {
				systemClassLoader = ClassLoader.getSystemClassLoader();
			} catch (SecurityException ignored) {
			}
		}

		public URL getResourceAsURL(String resource) {
			return getResourceAsURL(resource, getClassLoaders(null));
		}

		public URL getResourceAsURL(String resource, ClassLoader classLoader) {
			return getResourceAsURL(resource, getClassLoaders(classLoader));
		}

		public InputStream getResourceAsStream(String resource) {
			return getResourceAsStream(resource, getClassLoaders(null));
		}

		public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
			return getResourceAsStream(resource, getClassLoaders(classLoader));
		}

		InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
			for (ClassLoader cl : classLoader) {
				if (null != cl) {
					InputStream returnValue = cl.getResourceAsStream(resource);
					if (null == returnValue) {
						returnValue = cl.getResourceAsStream("/" + resource);
					}
					if (null != returnValue) {
						return returnValue;
					}
				}
			}
			return null;
		}

		URL getResourceAsURL(String resource, ClassLoader[] classLoader) {
			URL url;
			for (ClassLoader cl : classLoader) {
				if (cl != null) {
					url = cl.getResource(resource);
					if (url == null) {
						url = cl.getResource("/" + resource);
					}
					if (url != null) {
						return url;
					}
				}
			}
			return null;
		}

		ClassLoader[] getClassLoaders(ClassLoader classLoader) {
			return new ClassLoader[] { classLoader, Thread.currentThread().getContextClassLoader(), getClass().getClassLoader(), systemClassLoader };
		}

	}
}
