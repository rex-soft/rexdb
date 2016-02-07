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

public class ReflectUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

	private static final Map<Class<?>, Map<String, Method>> getters = new HashMap<Class<?>, Map<String, Method>>();
	
	private static final Map<Class<?>, Map<String, Method>> setters = new HashMap<Class<?>, Map<String, Method>>();
	
	private static volatile boolean cacheEnabled = true;
	
	/**
	 * 设置是否启用了BeanInfo缓存
	 */
	public static void setCacheEnabled(boolean isCacheEnabled){
		if(cacheEnabled != isCacheEnabled){
			LOGGER.info("Reflect cache is {0}.", cacheEnabled ? "enabled" : "disabled");
			cacheEnabled = isCacheEnabled;
			if(!cacheEnabled) clearCache();
		}
	}
	
	/**
	 * 清空缓存
	 */
	public static void clearCache(){
		getters.clear();
		setters.clear();
	}
	
	/**
	 * 是否启用了BeanInfo缓存
	 * @return
	 */
	public static boolean isCacheEnabled(){
		return cacheEnabled;
	}
	
	/**
	 * 获取类的所有可读方法
	 * 
	 * @param clazz
	 * @return
	 * @throws DBException
	 */
	public static Map<String, Method> getReadableMethods(Class<?> clazz) throws DBException {
		if(cacheEnabled){
			if(!getters.containsKey(clazz)){
				Map <String, Method> g = getGetters(clazz);
				getters.put(clazz, g);
				return g;
			}else
				return getters.get(clazz);
		}else
			return getGetters(clazz);
	}
	
	/**
	 * 获取该类的所有可写方法
	 */
	public static Map<String, Method> getWriteableMethods(Class<?> clazz) throws DBException {
		if(cacheEnabled){
			if(!setters.containsKey(clazz)){
				Map <String, Method> s = getSetters(clazz);
				setters.put(clazz, s);
				return s;
			}else
				return setters.get(clazz);
		}else
			return getSetters(clazz);
	}
	
	private static Map<String, Method> getGetters(Class<?> clazz) throws DBException {
		Map<String, Method> params = new HashMap<String, Method>();
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (props[i].getReadMethod() != null)
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

	/**
	 * 获取类Bean属性
	 * 
	 * @param clazz
	 * @return
	 * @throws DBException
	 */
	private static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws DBException {
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getPropertyDescriptors();
	}

	/**
	 * 获取类Bean方法
	 * 
	 * @param clazz
	 * @return
	 * @throws DBException
	 */
	private static MethodDescriptor[] getMethodDescriptor(Class<?> clazz) throws DBException {
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getMethodDescriptors();
	}

	/**
	 * 获取类Bean信息
	 * 
	 * @param clazz
	 * @return
	 * @throws DBException
	 */
	public static BeanInfo getBeanInfo(Class<?> clazz) throws DBException {
		try {
			return Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBException("DB-URF01", e, clazz.getName(), e.getMessage());
		}
	}

	/**
	 * 获取对象的clone方法（对象必须实现Cloneable接口，并且具备一个可以调用的clone方法，且该方法的返回值类型与对象相同）
	 * 
	 * @param clazz
	 * @return
	 * @throws DBException
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
	 * 反射赋值
	 * 
	 * @param bean
	 * @param properties
	 * @throws DBException
	 */
	public static void setProperties(Object bean, Properties properties) throws DBException {
		setProperties(bean, properties, false, false);
	}

	/**
	 * 反射赋值
	 * 
	 * @param bean
	 * @param properties
	 * @param ignoreMismatches
	 * @param ignoreEmpty
	 * @throws DBException
	 */
	public static void setProperties(Object bean, Properties properties, boolean ignoreMismatches, boolean ignoreEmpty) throws DBException {
		if (bean == null || properties == null || properties.size() == 0)
			return;

		Map<String, Method> writers = getWriteableMethods(bean.getClass());
		for (Iterator<?> iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = properties.getProperty(key);
			
			if(ignoreEmpty && StringUtil.isEmptyString(value))
				continue;

			if (!writers.containsKey(key)) {
				if (ignoreMismatches) {
					LOGGER.warn("Property [{0}: {1}] not supported for {2}[{3}], no writer method found, ignore.", key, value,
							bean.getClass().getName(), bean.hashCode());
					continue;
				} else
					throw new DBException("URF02", bean.getClass().getName(), key, value);
			}

			Method writer = writers.get(key);
			setValue(bean, writer, key, value, ignoreMismatches);
		}
	}

	/**
	 * 向写入方法赋值
	 */
	public static void setValue(Object bean, Method writer, String key, String value, boolean ignoreMismatches) throws DBException {
		if (value == null)
			invokeMethod(bean, writer, null);
		else {
			Class<?> paramType = writer.getParameterTypes()[0];
			String paramTypeName = paramType.getName();

			// 类型匹配，赋值
			if ("java.lang.String".equals(paramTypeName)) {
				invokeMethod(bean, writer, value);
			} else if ("int".equals(paramTypeName) || "java.lang.Integer".equals(paramTypeName)) {// Integer
				invokeMethod(bean, writer, Integer.parseInt(value));
			} else if ("boolean".equals(paramTypeName) || "java.lang.Boolean".equals(paramTypeName)) {// Boolean
				invokeMethod(bean, writer, Boolean.parseBoolean(value));
			} else if ("double".equals(paramTypeName) || "java.lang.Double".equals(paramTypeName)) {// Double
				invokeMethod(bean, writer, Double.parseDouble(value));
			} else if ("float".equals(paramTypeName) || "java.lang.Float".equals(paramTypeName)) {// Float
				invokeMethod(bean, writer, Float.parseFloat(value));
			} else if ("long".equals(paramTypeName) || "java.lang.Long".equals(paramTypeName)) {// Long
				invokeMethod(bean, writer, Long.parseLong(value));
			} else {
				if (ignoreMismatches)
					LOGGER.warn("Value of property [{0}: {1}] for {2}[{3}] is String, couldn't convert to {4}, ignore.", key, value,
							bean.getClass().getName(), bean.hashCode(), paramTypeName);
				else
					throw new DBException("DB-URF03", key.getClass().getName(), paramType.getName(), key, value);
			}
		}
	}

	/**
	 * 反射调用指定名称的方法
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] paramTypes, Object[] params) throws DBException {
		if(object == null || methodName == null) return null;
		return invokeMethod(object, object.getClass(), methodName, paramTypes, params);
	}
	
	/**
	 * 反射调用指定名称的方法
	 */
	public static Object invokeMethod(Object object, Class<?> objectClass, String methodName, Class<?>[] paramTypes, Object[] params) throws DBException {
		if(object == null || objectClass == null || methodName == null) return null;
		try {
			Method target = objectClass.getMethod(methodName, paramTypes);
			return invoke(object, target, params);
		} catch (SecurityException e) {
			throw new DBException("DB-URF07", e, object.getClass().getName(), methodName, e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new DBException("DB-URF07", e, object.getClass().getName(), methodName, e.getMessage());
		}
	}
	
	/**
	 * 调用对象无参数的方法
	 * 
	 * @throws DBException
	 */
	public static Object invokeMethod(Object object, Method method) throws DBException {
		return invoke(object, method, null);
	}

	/**
	 * 向对象赋值，至少要有1个参数
	 * 
	 * @throws DBException
	 */
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
	 * 创建对象实例
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

	/**
	 * 创建对象实例
	 */
	public static <T> T instance(String classPath, Class<T> targetClass) throws DBException {
		if (StringUtil.isEmptyString(classPath))
			return null;

		Class<?> clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new DBException("DB-URF05", e, classPath, e.getMessage());
		}

		if (targetClass != null && !targetClass.isAssignableFrom(clazz))
			throw new DBException("DB-URF06", classPath, targetClass.getName());

		return (T) instance(clazz);
	}
}
