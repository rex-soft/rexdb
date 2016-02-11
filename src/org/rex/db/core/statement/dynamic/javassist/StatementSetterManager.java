package org.rex.db.core.statement.dynamic.javassist;

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

public class StatementSetterManager {
	
	private static final String PACKAGE = "org.rex.db.core.statement.dynamic";
	
	private static final String CLASS_PREFIX = "JSetterFor";
	
	/**
	 * 动态类转换
	 */
	private static final Map<Class<?>, StatementSetter> convertors = new HashMap<Class<?>, StatementSetter>();
	
	/**
	 * 获取一个类转换器
	 * @param beanClass
	 * @return
	 * @throws DBException 
	 */
	public static StatementSetter getConvertor(Class<?> beanClass){
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
	private static StatementSetter build(Class<?> clazz) throws NotFoundException, DBException, CannotCompileException, InstantiationException, IllegalAccessException {

		ClassPool pool = ClassPool.getDefault();
		pool.importPackage("org.rex.db.util.SqlUtil");
		pool.importPackage("java.sql.PreparedStatement");
		pool.importPackage("java.sql.SQLException");

		CtClass ctClass = pool.makeClass(PACKAGE + "." + CLASS_PREFIX + genClassName(clazz));
		ctClass.setSuperclass(pool.get("org.rex.db.core.statement.dynamic.javassist.StatementSetter"));

        StringBuffer sb = new StringBuffer();  
        sb.append("public void setParameters(PreparedStatement preparedStatement, Object object, String[] requiredParam) throws SQLException {\n");
        sb.append("if(preparedStatement == null || object == null || requiredParam == null) return;\n");
        sb.append(clazz.getName()).append(" bean = (").append(clazz.getName()).append(")object;\n");
        sb.append("for (int i = 0; i < requiredParam.length; i++) {\n");
        
		Map<String,Method> getters = ReflectUtil.getReadableMethods(clazz);
		for (Iterator<String> iterator = getters.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Method getter = getters.get(key);
			
			sb.append("if(\"").append(key).append("\".equals(requiredParam[i])){\n");
			sb.append("SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.").append(getter.getName()).append("()));\n");
			sb.append("continue;\n");
			sb.append("}\n");
		}

        sb.append("}\n");  
        sb.append("}"); 
//        System.out.println(sb);
        CtMethod ctMethod = CtMethod.make(sb.toString(), ctClass);
        ctClass.addMethod(ctMethod);  
        
        Class<?> cl = ctClass.toClass();  
        return (StatementSetter)cl.newInstance();
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
