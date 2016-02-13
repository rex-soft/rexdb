package org.rex.db.dynamic.javassist;

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

public class BeanConvertorManager {
	
	private static final String PACKAGE = "org.rex.db.dynamic";
	
	private static final String CLASS_PREFIX = "JSetterFor";
	
	/**
	 * 动态类转换
	 */
	private static final Map<Class<?>, BeanConvertor> convertors = new HashMap<Class<?>, BeanConvertor>();
	
	/**
	 * 获取一个类转换器
	 * @param beanClass
	 * @return
	 * @throws DBException 
	 */
	public static BeanConvertor getConvertor(Class<?> beanClass){
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
	private static BeanConvertor build(Class<?> clazz) throws NotFoundException, DBException, CannotCompileException, InstantiationException, IllegalAccessException {

		ClassPool pool = ClassPool.getDefault();
		pool.importPackage("java.sql.PreparedStatement");
		pool.importPackage("java.sql.SQLException");
		pool.importPackage("java.sql.ResultSet");
		pool.importPackage("org.rex.db.util.ORUtil");
		pool.importPackage("org.rex.db.util.SqlUtil");
		pool.importPackage("org.rex.db.exception.DBException");

		CtClass ctClass = pool.makeClass(PACKAGE + "." + CLASS_PREFIX + genClassName(clazz));
		ctClass.setSuperclass(pool.get("org.rex.db.dynamic.javassist.BeanConvertor"));

		//method setParameters
        CtMethod setParametersMethod = CtMethod.make(buildSetParametersMethodString(clazz), ctClass);
        ctClass.addMethod(setParametersMethod);
        
        //method getColumnCodes
        CtMethod getColumnCodesMethod = CtMethod.make(buildGetColumnCodesMethodString(clazz), ctClass);
        ctClass.addMethod(getColumnCodesMethod);
        
        //method readResultSet
        CtMethod readResultSetMethod = CtMethod.make(buildReadResultSetMethodString(clazz), ctClass);
        ctClass.addMethod(readResultSetMethod);
        
        //generate instance
        Class<?> cl = ctClass.toClass();  
        return (BeanConvertor)cl.newInstance();
	}
	
	/**
	 * build a readResultSet method string
	 * @param clazz java bean class
	 * @return method string
	 * @throws DBException
	 */
	private static String buildReadResultSetMethodString(Class<?> clazz) throws DBException{
		StringBuffer sb = new StringBuffer();
		sb.append("public Object readResultSet(ResultSet rs, ORUtil orUtil, int[] requiredColumnCodes) throws SQLException, DBException{\n");
		sb.append("String[] rsLabels = orUtil.getResultSetLabels(rs);\n");
		sb.append("int[] rsTypes = orUtil.getResultSetTypes(rs);\n");
		
		sb.append(clazz.getName()).append(" bean = new ").append(clazz.getName()).append("();\n");
		sb.append("for (int i = 0; i < rsTypes.length; i++) {\n");
		sb.append("switch (requiredColumnCodes[i]) {\n");
		
		Map<String,Method> writers = ReflectUtil.getWriteableMethods(clazz);
		Map<String, Class<?>> types = ReflectUtil.getParameterTypes(clazz);
		Iterator<Map.Entry<String, Method>> iter = writers.entrySet().iterator();
		
		int i = 0;
		while(iter.hasNext()){
			Map.Entry<String, Method> entry = (Map.Entry<String, Method>)iter.next();
			String key = entry.getKey();
			Method setter = entry.getValue();
			Class<?> type = types.get(key);
			String[] typeClassNameAndSuffix = getClassName(type == null ? setter.getParameterTypes()[0] : type);
			
			sb.append("case ").append(i++).append(":\n");
			sb.append("bean.").append(setter.getName()).append("(((").append(typeClassNameAndSuffix[0])
				.append(")orUtil.getValue(rs, rsLabels[i], rsTypes[i], ")
				.append(formatClassName(typeClassNameAndSuffix[0])).append("))").append(typeClassNameAndSuffix[1]).append(");\n");
			sb.append("break;\n");
		}
		
		sb.append("}\n");
		sb.append("}\n");
		sb.append("return bean;\n");
		sb.append("}\n");
		
//		System.out.println(sb);
		return sb.toString();
	}
	
	
	/**
	 * build a getColumnCodes method string
	 * @param clazz java bean class
	 * @return method string
	 * @throws DBException
	 */
	private static String buildGetColumnCodesMethodString(Class<?> clazz) throws DBException{
		StringBuffer sb = new StringBuffer();
		sb.append("public int[] getColumnCodes(String[] rsLabelsRenamed){\n");
		sb.append("int[] cols = new int[rsLabelsRenamed.length];\n");
		sb.append("for (int i = 0; i < rsLabelsRenamed.length; i++) {\n");
		
		Map<String,Method> writers = ReflectUtil.getWriteableMethods(clazz);
		Iterator<Map.Entry<String, Method>> iter = writers.entrySet().iterator();
		int i = 0;
		while(iter.hasNext()){
			Map.Entry<String, Method> entry = (Map.Entry<String, Method>)iter.next();
			String key = entry.getKey();
			
			sb.append("if(\"").append(key).append("\".equals(rsLabelsRenamed[i])){\n");
			sb.append("cols[i] = ").append(i++).append(";\n");
			sb.append("continue;\n");
			sb.append("}\n");
		}
		
		sb.append("}\n");
		sb.append("return cols;\n");
		sb.append("}\n");
		
//		System.out.println(sb);
		return sb.toString();
	}
	
	/**
	 * build a setParameters method string
	 * @param clazz java bean class
	 * @return method string
	 * @throws DBException
	 */
	private static String buildSetParametersMethodString(Class<?> clazz) throws DBException{
        StringBuffer sb = new StringBuffer();  
        sb.append("public void setParameters(PreparedStatement preparedStatement, Object object, String[] requiredParam) throws SQLException {\n");
        sb.append("if(preparedStatement == null || object == null || requiredParam == null) return;\n");
        sb.append(clazz.getName()).append(" bean = (").append(clazz.getName()).append(")object;\n");
        sb.append("for (int i = 0; i < requiredParam.length; i++) {\n");
        
		Map<String,Method> getters = ReflectUtil.getReadableMethods(clazz);
		Iterator<Map.Entry<String, Method>> iter = getters.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Method> entry = (Map.Entry<String, Method>)iter.next();
			String key = entry.getKey();
			Method getter = entry.getValue();
			
			sb.append("if(\"").append(key).append("\".equals(requiredParam[i])){\n");
			sb.append("SqlUtil.setParameter(preparedStatement, i + 1, convertValue(bean.").append(getter.getName()).append("()));\n");
			sb.append("continue;\n");
			sb.append("}\n");
		}

        sb.append("}\n");  
        sb.append("}\n");
        
//        System.out.println(sb);
        return sb.toString();
	}
	
	/**
	 * get class name, return [0]string name, <tt>class name[]</tt> if class is array [1]suffix. if class is primitive type, convert it to Object first, then get primitive value.
	 * such as : (1)String[]{"java.lang.String", ""} (2)String[]{"Integer", ".intValue()"}
	 * @param clazz
	 * @return string array
	 */
	private static String[] getClassName(Class<?> clazz){
		if(clazz == null)
			return null;
		
		String[] name = new String[2];
		if(clazz.isArray()){
			name[0] = clazz.getComponentType().getName()+"[]";
			name[1] = "";
		}else if(clazz == int.class){
			name[0] = "Integer";
			name[1] = ".intValue()";
		}else if(clazz == boolean.class){
			name[0] = "Boolean";
			name[1] = ".booleanValue()";
		}else if(clazz == byte.class){
			name[0] = "Byte";
			name[1] = ".byteValue()";
		}else if(clazz == char.class){
			name[0] = "Char";
			name[1] = ".charValue()";
		}else if(clazz == double.class){
			name[0] = "Double";
			name[1] = ".doubleValue()";
		}else if(clazz == float.class){
			name[0] = "Float";
			name[1] = ".floatValue()";
		}else if(clazz == long.class){
			name[0] = "Long";
			name[1] = ".longValue()";
		}else if(clazz == short.class){
			name[0] = "Short";
			name[1] = ".shortValue()";
		}else{
			name[0] = clazz.getName();
			name[1] = "";
		}
		
		return name;
	}
	
	private static String formatClassName(String className){
		if("int".equals(className))
			return "getIntClass()";
		
		else return className+".class";
	}
	
	/**
	 * generate a unique class names
	 * @param clazz 
	 */
	private static String genClassName(Class<?> clazz){
		String className = clazz.getName();
		return className.replaceAll("\\.", "");
	}
	
}
