package org.rex.db.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.rex.db.exception.DBRuntimeException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * 获取类中的常量，即public static final声明的常量
 */
public class ConstantUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConstantUtil.class);

	private final Map<String, Object> constants = new HashMap<String, Object>();

	private final Class<?> clazz;

	public ConstantUtil(Class<?> clazz) {
		this.clazz = clazz;
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
				String name = f.getName();
				try {
					Object value = f.get(null);
					constants.put(name, value);
				} catch (IllegalAccessException ex) {
					LOGGER.warn("Failed to read constant property {0} of {1}, {2}.", name, clazz.getName(), ex.getMessage());
				}
			}
		}
	}

	public int getSize() {
		return constants.size();
	}

	public Object asObject(String code) {
		code = code.toUpperCase();
		if (!constants.containsKey(code))
			throw new DBRuntimeException("DB-UCS01", clazz.getName(), code);
		
		return constants.get(code);
	}

	public Number asNumber(String code) {
		Object obj = asObject(code);
		if (!(obj instanceof Number))
			throw new DBRuntimeException("DB-UCS02", clazz.getName(), code, obj.getClass().getName());
		return (Number) obj;
	}

	public String asString(String code) {
		Object obj = asObject(code);
		return obj == null ? null : obj.toString();
	}

	public Set getValues(String namePrefix) {
		namePrefix = namePrefix.toUpperCase();
		Set values = new HashSet();
		for (Iterator it = constants.keySet().iterator(); it.hasNext();) {
			String code = (String) it.next();
			if (code.startsWith(namePrefix)) {
				values.add(constants.get(code));
			}
		}
		return values;
	}

	public String toCode(Object value, String namePrefix) {
		namePrefix = namePrefix.toUpperCase();
		for (Iterator it = constants.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			if (key.startsWith(namePrefix) && entry.getValue().equals(value))
				return key;
		}
		throw new DBRuntimeException("DB-UCS03", clazz.getName(), namePrefix, value);
	}

}
