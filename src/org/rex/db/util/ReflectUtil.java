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

import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;

public class ReflectUtil {

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
	public static void setProperties(Object bean, Map properties) throws DBException{
		Map<String, Method> writers = getWriteableParams(bean.getClass());
		for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String key = (String)entry.getKey();
			Object value = entry.getValue();
			if(!writers.containsKey(key))
				throw new DBException(key + "赋值失败，找不到写入方法");
			
			Method writer = writers.get(key);
			if(value == null)
				invokeMethod(bean, writer, null);
			else {
				Class<?> paramType = writer.getParameterTypes()[0];
				
				//类型匹配，赋值
				if(paramType.isInstance(value)){
					invokeMethod(bean, writer, value);
				}
				//值是String类型，而写入方法参数不是，进行类型转换
				else if(value instanceof String){
					String v = (String)value;
					String paramTypeName = paramType.getName();
					if("int".equals(paramTypeName) || "java.lang.Integer".equals(paramTypeName)){//Integer
						invokeMethod(bean, writer, Integer.parseInt(v));
					}else if("boolean".equals(paramTypeName) || "java.lang.Boolean".equals(paramTypeName)){//Boolean
						invokeMethod(bean, writer, Boolean.parseBoolean(v));
					}else if("double".equals(paramTypeName) || "java.lang.Double".equals(paramTypeName)){//Double
						invokeMethod(bean, writer, Double.parseDouble(v));
					}else if("float".equals(paramTypeName) || "java.lang.Float".equals(paramTypeName)){//Float
						invokeMethod(bean, writer, Float.parseFloat(v));
					}else if("long".equals(paramTypeName) || "java.lang.Long".equals(paramTypeName)){//Long
						invokeMethod(bean, writer, Long.parseLong(v));
					}else
						throw new DBException(key + "赋值失败，类型无法匹配，值类型："+key.getClass().getName()+"，类变量类型："+paramType.getName());
				}
				//写入方法参数是String，进行类型转换
				else if(String.class.isAssignableFrom(paramType)){
					invokeMethod(bean, writer, value.toString());
				}
				else
					throw new DBException(key + "赋值失败，类型无法匹配，值类型："+key.getClass().getName()+"，类变量类型："+paramType.getName());
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
	public static Object instance(String classPath, Class<?> targetClass) throws DBException{
		if(StringUtil.isEmptyString(classPath)) return null;
		
		Class clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new DBException("无法创建类");
		}
		
		if(targetClass != null && !targetClass.isAssignableFrom(clazz))
			throw new DBException("类"+classPath+"必须是"+targetClass.getName()+"的实现");
		
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new DBException("无法实例化");
		} catch (IllegalAccessException e) {
			throw new DBException("无权实例");
		}
	}
}
