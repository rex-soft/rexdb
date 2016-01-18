package org.rex.db.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 用于加载配置文件
 */
public class ResourceUtil {

	private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

	private static Charset charset;

	public static URL getResourceURL(String resource) throws IOException {
		return getResourceURL(null, resource);
	}

	public static URL getResourceURL(ClassLoader loader, String resource) throws IOException {
		URL url = classLoaderWrapper.getResourceAsURL(resource, loader);
		if (url == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return url;
	}

	public static InputStream getResourceAsStream(String resource) throws IOException {
		return getResourceAsStream(null, resource);
	}

	public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
		InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
		if (in == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return in;
	}

	public static Properties getResourceAsProperties(String resource) throws IOException {
		Properties props = new Properties();
		InputStream in = getResourceAsStream(resource);
		props.load(in);
		in.close();
		return props;
	}

	public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
		Properties props = new Properties();
		InputStream in = getResourceAsStream(loader, resource);
		props.load(in);
		in.close();
		return props;
	}

	public static Reader getResourceAsReader(String resource) throws IOException {
		Reader reader;
		if (charset == null) {
			reader = new InputStreamReader(getResourceAsStream(resource));
		} else {
			reader = new InputStreamReader(getResourceAsStream(resource), charset);
		}
		return reader;
	}

	public static Reader getResourceAsReader(ClassLoader loader, String resource) throws IOException {
		Reader reader;
		if (charset == null) {
			reader = new InputStreamReader(getResourceAsStream(loader, resource));
		} else {
			reader = new InputStreamReader(getResourceAsStream(loader, resource), charset);
		}
		return reader;
	}

	public static File getResourceAsFile(String resource) throws IOException {
		return new File(getResourceURL(resource).getFile());
	}

	public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
		return new File(getResourceURL(loader, resource).getFile());
	}

	public static InputStream getUrlAsStream(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}

	public static Reader getUrlAsReader(String urlString) throws IOException {
		Reader reader;
		if (charset == null) {
			reader = new InputStreamReader(getUrlAsStream(urlString));
		} else {
			reader = new InputStreamReader(getUrlAsStream(urlString), charset);
		}
		return reader;
	}

	public static Properties getUrlAsProperties(String urlString) throws IOException {
		Properties props = new Properties();
		InputStream in = getUrlAsStream(urlString);
		props.load(in);
		in.close();
		return props;
	}

	public static Charset getCharset() {
		return charset;
	}

	public static void setCharset(Charset charset) {
		ResourceUtil.charset = charset;
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
			return new ClassLoader[] { classLoader, 
					Thread.currentThread().getContextClassLoader(), 
					getClass().getClassLoader(), 
					systemClassLoader 
				};
		}

	}
}
