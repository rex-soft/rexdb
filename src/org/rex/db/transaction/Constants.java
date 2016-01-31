package org.rex.db.transaction;

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
public class Constants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);

	private final Map<String, Object> map = new HashMap<String, Object>();

	private final Class<?> clazz;

	public Constants(Class<?> clazz) {
		this.clazz = clazz;
		Field[] fields = clazz.getFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
				String name = f.getName();
				try {
					Object value = f.get(null);
					this.map.put(name, value);
				} catch (IllegalAccessException ex) {
					LOGGER.warn("failed to read constant property {0} of {1}, {2}", name, clazz.getName(), ex.getMessage());
				}
			}
		}
	}

	public int getSize() {
		return this.map.size();
	}

	public Number asNumber(String code) {
		Object o = asObject(code);
		if (!(o instanceof Number))
			throw new DBRuntimeException("DB-C10018", clazz, code);
		return (Number) o;
	}

	public String asString(String code) {
		return asObject(code).toString();
	}

	public Object asObject(String code) {
		code = code.toUpperCase();
		Object val = this.map.get(code);
		if (val == null)
			throw new DBRuntimeException("DB-C10019", clazz, code);
		return val;
	}

	public Set getValues(String namePrefix) {
		namePrefix = namePrefix.toUpperCase();
		Set values = new HashSet();
		for (Iterator it = this.map.keySet().iterator(); it.hasNext();) {
			String code = (String) it.next();
			if (code.startsWith(namePrefix)) {
				values.add(this.map.get(code));
			}
		}
		return values;
	}

	public String toCode(Object value, String namePrefix) {
		namePrefix = namePrefix.toUpperCase();
		for (Iterator it = this.map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			if (key.startsWith(namePrefix) && entry.getValue().equals(value))
				return key;
		}
		throw new DBRuntimeException("DB-C10020", clazz, namePrefix, value);
	}

}
