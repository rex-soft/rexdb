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

	/**
	 * 获取类的所有可读属性
	 * @param clazz
	 * @return
	 * @throws DBException
	 */
	public static Map<String, Method> getReadableParams(Class<?> clazz) throws DBException{
		Map<String, Method> params = new HashMap<String, Method>();
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (props[i].getReadMethod() != null)
				params.put(key, props[i].getWriteMethod());
		}
		return params;
	}
	
	/**
	 * 获取该类的所有可写属性
	 */
	public static Map<String, Method> getWriteableParams(Class<?> clazz) throws DBException {
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
	 * @param clazz
	 * @return
	 * @throws DBException 
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws DBException{
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getPropertyDescriptors();
	}
	
	/**
	 * 获取类Bean方法
	 * @param clazz
	 * @return
	 * @throws DBException 
	 */
	public static MethodDescriptor[] getMethodDescriptor(Class<?> clazz) throws DBException{
		BeanInfo bean = getBeanInfo(clazz);
		return bean.getMethodDescriptors();
	}
	
	/**
	 * 获取类Bean信息
	 * @param clazz
	 * @return
	 * @throws DBException 
	 */
	public static BeanInfo getBeanInfo(Class<?> clazz) throws DBException{
		try {
			return Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBException("DB-Q10008", e);
		}
	}
	
	/**
	 * 获取对象的clone方法（对象必须实现Cloneable接口，并且具备一个可以调用的clone方法，且该方法的返回值类型与对象相同）
	 * @param clazz
	 * @return
	 * @throws DBException 
	 */
	public static Method getCloneMethod(Object bean) throws DBException{
		if(!(bean instanceof Cloneable))
			return null;
		
		MethodDescriptor[] methods = getMethodDescriptor(bean.getClass());
		for (MethodDescriptor md : methods) {
			Method method = md.getMethod();
			if("clone".equals(method.getName())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getReturnType() == bean.getClass()){
				return method;
			}
		}
		return null;
	}
	
	/**
	 * 反射赋值
	 * @param bean
	 * @param properties
	 * @throws DBException 
	 */
	public static void setProperties(Object bean, Properties properties) throws DBException{
		setProperties(bean, properties, false);
	}
	
	/**
	 * 反射赋值
	 * @param bean
	 * @param properties
	 * @param ignoreMismatches
	 * @throws DBException
	 */
	public static void setProperties(Object bean, Properties properties, boolean ignoreMismatches) throws DBException{
		if(bean == null || properties == null || properties.size() == 0) return;
		
		Map<String, Method> writers = getWriteableParams(bean.getClass());
		for (Iterator<?> iterator = properties.keySet().iterator(); iterator.hasNext();) {
			String key = (String)iterator.next();
			String value = properties.getProperty(key);
			
			if(!writers.containsKey(key)){
				if(ignoreMismatches){
					LOGGER.warn("Property [{0}: {1}] not supported for {2}[{3}], no writer method found, ignore.", key, value, bean.getClass().getName(), bean.hashCode());
					continue;
				}else
					throw new DBException("赋值失败，找不到写入方法："+key);
			}
			
			Method writer = writers.get(key);
			setValue(bean, writer, key, value, ignoreMismatches);
		}
	}
	
	/**
	 * 向写入方法赋值
	 */
	public static void setValue(Object bean, Method writer, String key, String value, boolean ignoreMismatches) throws DBException{
		if(value == null)
			invokeMethod(bean, writer, null);
		else {
			Class<?> paramType = writer.getParameterTypes()[0];
			String paramTypeName = paramType.getName();
			
			//类型匹配，赋值
			if("java.lang.String".equals(paramTypeName)){
				invokeMethod(bean, writer, value);
			}else if("int".equals(paramTypeName) || "java.lang.Integer".equals(paramTypeName)){//Integer
				invokeMethod(bean, writer, Integer.parseInt(value));
			}else if("boolean".equals(paramTypeName) || "java.lang.Boolean".equals(paramTypeName)){//Boolean
				invokeMethod(bean, writer, Boolean.parseBoolean(value));
			}else if("double".equals(paramTypeName) || "java.lang.Double".equals(paramTypeName)){//Double
				invokeMethod(bean, writer, Double.parseDouble(value));
			}else if("float".equals(paramTypeName) || "java.lang.Float".equals(paramTypeName)){//Float
				invokeMethod(bean, writer, Float.parseFloat(value));
			}else if("long".equals(paramTypeName) || "java.lang.Long".equals(paramTypeName)){//Long
				invokeMethod(bean, writer, Long.parseLong(value));
			}else{
				if(ignoreMismatches)
					LOGGER.warn("Value of property [{0}: {1}] for {2}[{3}] is String, couldn't convert to {4}, ignore.", 
							key, value, bean.getClass().getName(), bean.hashCode(), paramTypeName);
				else
					throw new DBException(key + "赋值失败，类型无法匹配，值类型："+key.getClass().getName()+"，类变量类型："+paramType.getName());
			}
		}
	}
	
	/**
	 * 向对象赋值
	 * @throws DBException 
	 */
	public static Object invokeMethod(Object object, Method method, Object value) throws DBException{
		try {
			return method.invoke(object, value == null ? null : new Object[]{value});
		} catch (IllegalArgumentException e) {
			throw new DBException("DB-Q10018", e, object.getClass().getName(), method.getName(), value);
		} catch (IllegalAccessException e) {
			throw new DBException("DB-Q10019", e, object.getClass().getName(), method.getName(), value);
		} catch (InvocationTargetException e) {
			throw new DBException("DB-Q10020", e, object.getClass().getName(), method.getName(), value);
		}
	}
	
	/**
	 * 创建对象实例
	 */
	public static <T> T instance(Class<T> targetClass) throws DBException{
		try {
			return targetClass.newInstance();
		} catch (InstantiationException e) {
			throw new DBException("DB-Q10006", e, targetClass.getName());
		} catch (IllegalAccessException e) {
			throw new DBException("DB-Q10007", e, targetClass.getName());
		}
	}
	/**
	 * 创建对象实例
	 */
	public static <T> T instance(String classPath, Class<T> targetClass) throws DBException{
		if(StringUtil.isEmptyString(classPath)) return null;
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new DBException("无法创建类");
		}
		
		if(targetClass != null && !targetClass.isAssignableFrom(clazz))
			throw new DBException("类"+classPath+"必须是"+targetClass.getName()+"的实现");
		
		try {
			return (T)clazz.newInstance();
		} catch (InstantiationException e) {
			throw new DBException("无法实例化");
		} catch (IllegalAccessException e) {
			throw new DBException("无权实例");
		}
	}
}
