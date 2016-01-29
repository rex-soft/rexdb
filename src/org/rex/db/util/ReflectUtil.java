package org.rex.db.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class ReflectUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);

	/**
	 * 查询类中是否有setter方法(未优化)
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static boolean hasSetter(Class<?> clazz, String key) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBRuntimeException("xx");
		}
		
		PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
		if (proDescrtptors != null && proDescrtptors.length > 0) {
			for (PropertyDescriptor propDesc : proDescrtptors) {
				if (propDesc.getName().equals(key) 
						&& "java.lang.String".equals(propDesc.getPropertyType().getName())
						&& propDesc.getWriteMethod() != null) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 获取该类的所有可写属性
	 */
	public static Map<String, Method> getWriteableParams(Class<?> clazz) throws DBException {
		Map<String, Method> params = new HashMap<String, Method>();
		BeanInfo bean = null;
		try {
			bean = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new DBException("DB-Q10008", e);
		}
		
		PropertyDescriptor[] props = bean.getPropertyDescriptors();
		for (int i = 0; i < props.length; i++) {
			String key = props[i].getName();
			if (props[i].getWriteMethod() != null)
				params.put(key, props[i].getWriteMethod());
		}
		return params;
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
	private static void setValue(Object bean, Method writer, String key, String value, boolean ignoreMismatches) throws DBException{
		if(value == null)
			invokeMethod(bean, writer, null);
		else {
			Class<?> paramType = writer.getParameterTypes()[0];
			
			//类型匹配，赋值
			if(value instanceof String){
				invokeMethod(bean, writer, value);
			}else{//值是String类型，而写入方法参数不是，进行类型转换
				String paramTypeName = paramType.getName();
				if("int".equals(paramTypeName) || "java.lang.Integer".equals(paramTypeName)){//Integer
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
	}
	
	/**
	 * 向对象赋值
	 * @throws DBException 
	 */
	private static void invokeMethod(Object object, Method method, Object value) throws DBException{
		try {
			method.invoke(object, new Object[]{value});
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
