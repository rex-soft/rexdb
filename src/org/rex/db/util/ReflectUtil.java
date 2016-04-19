/**
 * Copyright 2016 the Rex-Soft Group.
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
package org.rex.db.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * Reflect utilities.
 * 
 * @version 1.0, 2016-04-19
 * @since Rexdb-1.0
 */
public class ReflectUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

	// writer methods
	private static final Map<Class<?>, Map<String, Method>> getters = new HashMap<Class<?>, Map<String, Method>>();
	// reader methods
	private static final Map<Class<?>, Map<String, Method>> setters = new HashMap<Class<?>, Map<String, Method>>();
	// parameter types
	private static final Map<Class<?>, Map<String, Class<?>>> types = new HashMap<Class<?>, Map<String, Class<?>>>();

	private static volatile boolean cacheEnabled = true;

	/**
	 * Cache BeanInfo?
	 */
	public static void setCacheEnabled(boolean isCacheEnabled) {
		if (cacheEnabled != isCacheEnabled) {
			LOGGER.info("reflect cache is {0}.", cacheEnabled ? "enabled" : "disabled");
			cacheEnabled = isCacheEnabled;
			if (!cacheEnabled)
				clearCache();
		}
	}

	/**
	 * Clear caches.
	 */
	public static void clearCache() {
		getters.clear();
		setters.clear();
	}

	/**
	 * Is cache enabled?
	 */
	public static boolean isCacheEnabled() {
		return cacheEnabled;
	}

	/**
	 * Gets class fields types.
	 */
	public static Map<String, Class<?>> getParameterTypes(Class<?> clazz) throws DBException {
		if (cacheEnabled) {
			if (!types.containsKey(clazz)) {
				Map<String, Class<?>> p = getTypes(clazz);
				types.put(clazz, p);
				return p;
			} else
				return types.get(clazz);
		} else
			return getTypes(clazz);
	}

	/**
	 * Gets all readable methods from class.
	 */
	public static Map<String, Method> getReadableMethods(Class<?> clazz) throws DBException {
		if (cacheEnabled) {
			if (!getters.containsKey(clazz)) {
				Map<String, Method> g = getGetters(clazz);
				getters.put(clazz, g);
				return g;
			} else
				return getters.get(clazz);
		} else
			return getGetters(clazz);
	}

	/**
	 * Gets all writeable methods from class.
	 */
	public static Map<String, Method> getWriteableMethods(Class<?> clazz) throws DBException {
		if (cacheEnabled) {
			if (!setters.containsKey(clazz)) {
				Map<String, Method> s = getSetters(clazz);
				setters.put(clazz, s);
				return s;
			} else
				return setters.get(clazz);
		} else
			return getSetters(clazz);
	}

	private static Map<String, Class<?>> getTypes(Class<?> clazz) throws DBException {
		Map<String, Class<?>> params = new HashMap<String, Class<?>>();
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (!"class".equals(key) && props[i].getReadMethod() != null)
				params.put(key, props[i].getPropertyType());
		}
		return params;
	}

	private static Map<String, Method> getGetters(Class<?> clazz) throws DBException {
		Map<String, Method> params = new HashMap<String, Method>();
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (!"class".equals(key) && props[i].getReadMethod() != null)
				params.put(key, props[i].getReadMethod());
		}
		return params;
	}

	private static Map<String, Method> getSetters(Class<?> clazz) throws DBException {
		Map<String, Method> params = new HashMap<String, Method>();
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (props[i].getWriteMethod() != null)
				params.put(key, props[i].getWriteMethod());
		}
		return params;
	}

	private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws DBException {
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getPropertyDescriptors();
	}

	private static MethodDescriptor[] getMethodDescriptor(Class<?> clazz) throws DBException {
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getMethodDescriptors();
	}

	/**
	 * Gets BeanInfo from class.
	 */
	public static BeanInfo getBeanInfo(Class<?> clazz) throws DBException {
		try {
			return Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBException("DB-URF01", e, clazz.getName(), e.getMessage());
		}
	}

	/**
	 * Gets cloneable method from object.
	 */
	public static Method getCloneMethod(Object bean) throws DBException {
		if (!(bean instanceof Cloneable))
			return null;

		MethodDescriptor[] methods = getMethodDescriptor(bean.getClass());
		for (MethodDescriptor md : methods) {
			Method method = md.getMethod();
			if ("clone".equals(method.getName()) && Modifier.isPublic(method.getModifiers()) && method.getReturnType() == bean.getClass()) {
				return method;
			}
		}
		return null;
	}

	/**
	 * Invokes setter methods with properties.
	 */
	public static void setProperties(Object bean, Properties properties) throws DBException {
		setProperties(bean, properties, false, false);
	}

	/**
	 * Invokes setter methods with properties.
	 * 
	 * @param bean the object.
	 * @param properties properties to set.
	 * @param ignoreMismatches throws exception if ignoreMismatches is false.
	 * @param ignoreEmpty skip invoking setter method if the properties value is null or ''.
	 * @throws DBException
	 */
	public static void setProperties(Object bean, Properties properties, boolean ignoreMismatches, boolean ignoreEmpty) throws DBException {
		if (bean == null || properties == null || properties.size() == 0)
			return;

		Map<String, Method> writers = getWriteableMethods(bean.getClass());
		for (Iterator<?> iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = properties.getProperty(key);

			if (ignoreEmpty && StringUtil.isEmptyString(value))
				continue;

			if (!writers.containsKey(key)) {
				if (ignoreMismatches) {
					LOGGER.warn("{0}[{1}] dose not support property [{2}: {3}], no writer method found, the property has been ignored.",
							bean.getClass().getName(), bean.hashCode(), key, value);
					continue;
				} else
					throw new DBException("URF02", bean.getClass().getName(), key, value);
			}

			Method writer = writers.get(key);
			setValue(bean, writer, key, value, ignoreMismatches);
		}
	}

	/**
	 * Invokes the given setter method.
	 */
	public static void setValue(Object bean, Method writer, String key, String value, boolean ignoreMismatches) throws DBException {
		if (value == null)
			invokeMethod(bean, writer, null);
		else {
			Class<?> paramType = writer.getParameterTypes()[0];

			if (paramType == String.class) {
				invokeMethod(bean, writer, value);
			} else if (paramType == int.class || paramType == Integer.class) {// Integer
				invokeMethod(bean, writer, Integer.parseInt(value));
			} else if (paramType == boolean.class || paramType == Boolean.class) {// Boolean
				invokeMethod(bean, writer, Boolean.parseBoolean(value));
			} else if (paramType == double.class || paramType == Double.class) {// Double
				invokeMethod(bean, writer, Double.parseDouble(value));
			} else if (paramType == float.class || paramType == Float.class) {// Float
				invokeMethod(bean, writer, Float.parseFloat(value));
			} else if (paramType == long.class || paramType == Long.class) {// Long
				invokeMethod(bean, writer, Long.parseLong(value));
			} else if (paramType == short.class || paramType == Short.class) {// Short
				invokeMethod(bean, writer, Short.parseShort(value));
			} else if (paramType == byte.class || paramType == Byte.class) {// Byte
				invokeMethod(bean, writer, Byte.parseByte(value));
			} else {
				if (ignoreMismatches)
					LOGGER.warn("property [{0}: {1}] for {2}[{3}] is String, couldn't convert to {4}, which has been ignored.", key, value,
							bean.getClass().getName(), bean.hashCode(), paramType.getName());
				else
					throw new DBException("DB-URF03", key.getClass().getName(), paramType.getName(), key, value);
			}
		}
	}

	public static Object invokeMethod(Object object, String methodName, Class<?>[] paramTypes, Object[] params) throws DBException {
		if (object == null || methodName == null)
			return null;
		return invokeMethod(object, object.getClass(), methodName, paramTypes, params);
	}

	public static Object invokeMethod(Object object, Class<?> objectClass, String methodName, Class<?>[] paramTypes, Object[] params)
			throws DBException {
		if (object == null || objectClass == null || methodName == null)
			return null;
		try {
			Method target = objectClass.getMethod(methodName, paramTypes);
			return invoke(object, target, params);
		} catch (SecurityException e) {
			throw new DBException("DB-URF07", e, object.getClass().getName(), methodName, e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new DBException("DB-URF07", e, object.getClass().getName(), methodName, e.getMessage());
		}
	}

	public static Object invokeMethod(Object object, Method method) throws DBException {
		return invoke(object, method, null);
	}

	public static Object invokeMethod(Object object, Method method, Object... value) throws DBException {
		return invoke(object, method, value);
	}

	private static Object invoke(Object object, Method method, Object[] value) throws DBException {
		try {
			return value == null ? method.invoke(object) : method.invoke(object, value);
		} catch (IllegalArgumentException e) {
			throw new DBException("DB-URF04", e, object.getClass().getName(), method.getName(), value, e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DBException("DB-URF04", e, object.getClass().getName(), method.getName(), value, e.getMessage());
		} catch (InvocationTargetException e) {
			throw new DBException("DB-URF04", e, object.getClass().getName(), method.getName(), value, e.getMessage());
		}
	}

	/**
	 * Creates new instance.
	 */
	public static <T> T instance(Class<T> targetClass) throws DBException {
		try {
			return targetClass.newInstance();
		} catch (InstantiationException e) {
			throw new DBException("DB-URF05", e, targetClass.getName(), e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DBException("DB-URF05", e, targetClass.getName(), e.getMessage());
		}
	}

	public static <T> T instance(String classPath, Class<T> targetClass) throws DBException {
		if (StringUtil.isEmptyString(classPath))
			return null;

		Class<?> clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new DBException("DB-URF05", e, classPath, e.getMessage());
		}

		try {
			return (T) instance(clazz);
		} catch (ClassCastException e) {
			throw new DBException("DB-URF06", classPath, targetClass.getName());
		}
	}
}
