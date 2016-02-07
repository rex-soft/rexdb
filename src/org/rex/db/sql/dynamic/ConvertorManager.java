package org.rex.db.sql.dynamic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.rex.db.exception.DBException;
import org.rex.db.exception.DBRuntimeException;
import org.rex.db.util.ReflectUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ConvertorManager {
	
	/**
	 * 动态类转换
	 */
	private static final Map<Class<?>, Bean2Ps> convertors = new HashMap<Class<?>, Bean2Ps>();
	
	/**
	 * 获取一个类转换器
	 * @param beanClass
	 * @return
	 * @throws DBException 
	 */
	public static Bean2Ps getConvertor(Class<?> beanClass){
		if(!convertors.containsKey(beanClass)){
			try {
				convertors.put(beanClass, build(beanClass));
			} catch (InstantiationException e) {
				throw new DBRuntimeException("DB-S0002", e, beanClass.getName(), e.getMessage());
			} catch (IllegalAccessException e) {
				throw new DBRuntimeException("DB-S0002", e, beanClass.getName(), e.getMessage());
			} catch (NotFoundException e) {
				throw new DBRuntimeException("DB-S0002", e, beanClass.getName(), e.getMessage());
			} catch (CannotCompileException e) {
				throw new DBRuntimeException("DB-S0002", e, beanClass.getName(), e.getMessage());
			}catch (DBException e) {
				throw new DBRuntimeException(e);
			}
		}
		
		return convertors.get(beanClass);
	}

	/**
	 * 生成一个动态类
	 * @param clazz
	 * @return
	 * @throws NotFoundException
	 * @throws DBException
	 * @throws CannotCompileException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static Bean2Ps build(Class<?> clazz) throws NotFoundException, DBException, CannotCompileException, InstantiationException, IllegalAccessException {

		// ClassPool：CtClass对象的容器
		ClassPool pool = ClassPool.getDefault();

		// 通过ClassPool生成一个public新类Emp.java
		CtClass ctClass = pool.makeClass("org.rex.db.sql.dynamic." + genClassName(clazz));
		ctClass.addInterface(pool.get("org.rex.db.sql.dynamic.Bean2Ps"));

		// 添加自定义方法
        StringBuffer sb = new StringBuffer();  
        sb.append("public org.rex.db.Ps toPs(Object object, String[] requiredParam){\n");
        sb.append(clazz.getName()).append(" bean = (").append(clazz.getName()).append(")object;\n");
        sb.append("org.rex.db.Ps ps = new org.rex.db.Ps();\n");
        sb.append("for (int i = 0; i < requiredParam.length; i++) {\n");
        
        
		Map<String,Method> getters = ReflectUtil.getReadableMethods(clazz);
		for (Iterator<String> iterator = getters.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Method getter = getters.get(key);
			
			sb.append("if(\"").append(key).append("\".equals(requiredParam[i])){\n");
			sb.append("ps.add(bean.").append(getter.getName()).append("());\n");
			sb.append("continue;\n");
			sb.append("}\n");
		}

        sb.append("}\n");  
        sb.append("return ps;\n");
        sb.append("}"); 
//        System.out.println(sb);
        CtMethod ctMethod = CtMethod.make(sb.toString(), ctClass);
        ctClass.addMethod(ctMethod);  
        
        //为了验证效果，下面使用反射执行方法printInfo  
        Class<?> cl = ctClass.toClass();  
        return (Bean2Ps)cl.newInstance(); 
	}
	
	/**
	 * 生成一个不会重名的类名
	 * @param clazz
	 */
	private static String genClassName(Class<?> clazz){
		String className = clazz.getName();
		return className.replaceAll("\\.", "");
	}
	
}
