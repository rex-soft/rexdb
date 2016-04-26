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
package org.rex;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * HashMap with type conversion methods.
 * 
 * @author z
 * @version 1.0, 2016-04-17
 * @since Rexdb-1.0
 */
public class RMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	// -------------constructor
	public RMap() {
		super();
	}

	public RMap(int capacity) {
		super(capacity);
	}

	public RMap(int capacity, float factor) {
		super(capacity, factor);
	}

	public RMap(Map<K, V> map) {
		super(map);
	}

	// -------------------------------------------------

	// ------------get value as string
	/**
	 * Returns the value as String to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @param emptyAsNull returns null if the value equals empty String.
	 * @return String value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public String getString(String key, boolean emptyAsNull) {
		if (!containsKey(key))
			return null;
		String value = getStringValue(get(key));
		return emptyAsNull && "".equals(value) ? null : value;
	}

	/**
	 * Returns the value as String to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned.
	 * @return String value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public String getString(String key) {
		return getString(key, false);
	}

	/**
	 * Converts Object to String, contacts values if the Object is array.
	 * 
	 * @param value the Object to convert.
	 * @return String value.
	 */
	protected String getStringValue(Object value) {
		if (value == null)
			return null;
		else if (value instanceof String)
			return (String) value;
		else if (value.getClass().isArray()) {
			if (value instanceof int[]) {
				return Arrays.toString((int[]) value);
			} else if (value instanceof boolean[]) {
				return Arrays.toString((boolean[]) value);
			} else if (value instanceof byte[]) {
				return Arrays.toString((byte[]) value);
			} else if (value instanceof char[]) {
				return Arrays.toString((char[]) value);
			} else if (value instanceof double[]) {
				return Arrays.toString((double[]) value);
			} else if (value instanceof float[]) {
				return Arrays.toString((float[]) value);
			} else if (value instanceof long[]) {
				return Arrays.toString((long[]) value);
			} else if (value instanceof short[]) {
				return Arrays.toString((short[]) value);
			} else {
				Object[] ss = (Object[]) value;
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < ss.length; i++) {
					builder.append(getStringValue(ss[i]));
					if (i != ss.length - 1)
						builder.append(',').append(' ');
				}
				return builder.insert(0, '[').append(']').toString();
			}
		} else
			return String.valueOf(value);
	}

	// ------------get value as boolean
	/**
	 * Returns the value as boolean to which the specified key is mapped, or false if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return boolean value to which the specified key is mapped, or false if this map contains no mapping for the key.
	 */
	public boolean getBoolean(String key) {
		Object value = get(key);
		if (value == null)
			return false;
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.valueOf(getStringValue(value));
		}
	}

	// ------------get value as number
	/**
	 * Returns the value as integer to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return integer value to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 */
	public int getInt(String key) {
		Object value = get(key);
		if (value == null)
			return 0;
		else if (value instanceof Integer)
			return (Integer) value;
		else if (value instanceof Number)
			return ((Number) value).intValue();
		else {
			return parseInt(value);
		}
	}

	protected int parseInt(Object value) {
		String s = getStringValue(value);
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to int, " + e.getMessage());
		}
	}

	/**
	 * Returns the value as long to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return long value to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 */
	public long getLong(String key) {
		Object value = get(key);
		if (value == null)
			return 0;
		else if (value instanceof Long)
			return (Long) value;
		else if (value instanceof Number)
			return ((Number) value).longValue();
		else {
			return parseLong(value);
		}
	}

	protected long parseLong(Object value) {
		String s = getStringValue(value);
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to long, " + e.getMessage());
		}
	}

	/**
	 * Returns the value as float to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return float value to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 */
	public float getFloat(String key) {
		Object value = get(key);
		if (value == null)
			return 0;
		else if (value instanceof Float)
			return (Float) value;
		else if (value instanceof Number)
			return ((Number) value).floatValue();
		else {
			return parseFloat(value);
		}
	}

	protected float parseFloat(Object value) {
		String s = getStringValue(value);
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to float, " + e.getMessage());
		}
	}

	/**
	 * Returns the value as double to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return double value to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 */
	public double getDouble(String key) {
		Object value = get(key);
		if (value == null)
			return 0;
		else if (value instanceof Double)
			return (Double) value;
		else if (value instanceof Number)
			return ((Number) value).doubleValue();
		else {
			return parseDouble(value);
		}
	}

	protected double parseDouble(Object value) {
		String s = getStringValue(value);
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to double, " + e.getMessage());
		}
	}

	/**
	 * Returns the value as BigDecimal to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return BigDecimal value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public BigDecimal getBigDecimal(String key) {
		Object value = get(key);
		if (value == null)
			return null;
		else if (value instanceof BigDecimal)
			return (BigDecimal) value;
		else if (value instanceof Double)
			return new BigDecimal((Double) value);
		else if (value instanceof Integer)
			return new BigDecimal((Integer) value);
		else if (value instanceof Long)
			return new BigDecimal((Long) value);
		else if (value instanceof Float)
			return new BigDecimal((Float) value);
		else {
			return parseBigDecimal(value);
		}
	}

	protected BigDecimal parseBigDecimal(Object value) {
		String s = getStringValue(value);
		try {
			return new BigDecimal(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to BigDecimal, " + e.getMessage());
		}
	}

	// ------------get value as Date
	/**
	 * Returns the value as Date to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return Date value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public Date getDate(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getDateByValue(value);
	}

	/**
	 * Returns the value as java.sql.Date to which the specified key is mapped, or null if this map contains no mapping for the
	 * key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * 
	 * @return java.sql.Date value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public java.sql.Date getDateForSql(String key) {
		Date date = getDate(key);
		if (date == null)
			return null;
		return new java.sql.Date(date.getTime());
	}

	/**
	 * Returns the value as Time to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return Time value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public Time getTime(String key) {
		Date date = getDate(key);
		if (date == null)
			return null;
		return new Time(date.getTime());
	}

	/**
	 * Returns the value as Timestamp to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return Timestamp value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public Timestamp getTimestamp(String key) {
		Date date = getDate(key);
		if (date == null)
			return null;
		return new Timestamp(date.getTime());
	}

	/**
	 * Parses Object to Date.
	 * 
	 * @param value Object to convert.
	 * @return Date value.
	 */
	protected Date getDateByValue(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Date)
			return (Date) value;
		else {
			String s = getStringValue(value);
			for (DateParser dateParser : dateParsers) {
				Date date = dateParser.parse(s);
				if (date != null)
					return date;
			}

			try {
				Date d = DateFormat.getInstance().parse(s);
				if (d != null)
					return d;
			} catch (ParseException e) {
				// ignore
			}

			throw new ClassCastException("Couldn't convert " + s + "(" + value.getClass().getName() + ") to date whitch toString is " + s
					+ ", no date format parttern matches.");
		}
	}

	/**
	 * Inner class for automatically parsing date string.
	 */
	static class DateParser {
		private String regular;
		private Pattern pattern;
		private SimpleDateFormat sdf;

		public DateParser(String pattern, String format) {
			super();
			this.regular = pattern;
			this.pattern = Pattern.compile(pattern);
			this.sdf = new SimpleDateFormat(format);
		}

		/**
		 * Parses date String that matches pattern.
		 * 
		 * @param dateStr date string
		 * @return java.util.Date or null
		 */
		public Date parse(String dateStr) {
			if (pattern.matcher(dateStr).matches()) {
				String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				if (regular.equals("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$") || regular.equals("^\\d{2}\\s*:\\s*\\d{2}$")) {
					dateStr = curDate + "-" + dateStr;
				} else if (regular.equals("^\\d{1,2}\\D+\\d{1,2}$")) {
					dateStr = curDate.substring(0, 4) + "-" + dateStr;
				}
				String dateReplace = dateStr.replaceAll("\\D+", "-");
				try {
					return sdf.parse(dateReplace);
				} catch (ParseException e) {
					throw new IllegalArgumentException("Couldn't parse string '" + dateStr + "' to date which matches pattern '" + pattern.pattern()
							+ "' and format '" + sdf.toPattern() + "', " + e.getMessage());
				}
			}
			return null;
		}
	}

	private static volatile List<DateParser> dateParsers = new ArrayList<DateParser>() {
		private static final long serialVersionUID = -1336942317939192127L;

		{
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss"));// 2016-02-02
																																	// 02:02:02,
																																	// 2016/2/2
																																	// 2:2:2,
																																	// 2016年2月2日
																																	// 2时2分2秒
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH-mm"));// 2016-02-02
																												// 02:02
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH"));// 2016-02-02 02
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd"));// 2016-02-02, 2016年02月02日
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D*$", "yyyy-MM"));// 2016-02
			add(new DateParser("^\\d{4}\\D*$", "yyyy"));// 2016
			add(new DateParser("^\\d{14}$", "yyyyMMddHHmmss"));// 20160202020202
			add(new DateParser("^\\d{12}$", "yyyyMMddHHmm"));// 201602020202
			add(new DateParser("^\\d{10}$", "yyyyMMddHH"));// 2016020202
			add(new DateParser("^\\d{8}$", "yyyyMMdd"));// 20160202
			add(new DateParser("^\\d{6}$", "yyyyMM"));// 201602
			add(new DateParser("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss"));// 02:02:02
			add(new DateParser("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm"));// 02:02
			add(new DateParser("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd"));// 02.02.02
			add(new DateParser("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM"));// 02.02
			add(new DateParser("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy"));// 02.02.2016
		}
	};

	/**
	 * Addes a pattern for date format.
	 * 
	 * @param pattern date regular expression.
	 * @param format java date format.
	 */
	public static void addDateFormat(String pattern, String format) {
		dateParsers.add(new DateParser(pattern, format));
	}

	// ------------get value as array
	/**
	 * Returns the value as String array to which the specified key is mapped, or null if this map contains no mapping for the
	 * key.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return String array to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public String[] getStringArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			String s = getStringValue(value);
			return new String[] { s };
		} else if (value instanceof String[]) {
			return (String[]) value;
		} else {
			String[] ss;
			if (value instanceof int[]) {
				int[] os = (int[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof boolean[]) {
				boolean[] os = (boolean[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof byte[]) {
				byte[] os = (byte[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof char[]) {
				char[] os = (char[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof double[]) {
				double[] os = (double[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof float[]) {
				float[] os = (float[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof long[]) {
				long[] os = (long[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else if (value instanceof short[]) {
				short[] os = (short[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			} else {
				Object[] os = (Object[]) value;
				ss = new String[os.length];
				for (int i = 0; i < os.length; i++)
					ss[i] = getStringValue(os[i]);
			}
			return ss;
		}
	}

	// ------------get value as RMap/List
	/**
	 * Gets value as RMap.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return RMap to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * @throws ClassCastException if entry value is not a Map.
	 */
	public RMap getMap(String key) {
		Object value = get(key);
		if (value == null)
			return null;
		else if (value instanceof RMap)
			return (RMap) value;
		else if (value instanceof Map) {
			return new RMap((Map) value);
		}

		throw new ClassCastException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to RMap");
	}

	/**
	 * Gets value as List.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @return List to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * @throws ClassCastException if the entry value is not or could not convert to List.
	 */
	public List getList(String key) {
		Object value = get(key);
		if (value == null)
			return null;
		else if (value instanceof List) {
			return (List) value;
		} else if (value.getClass().isArray()) {
			if (value.getClass().getComponentType().isPrimitive()) {
				List list = new ArrayList();
				if (value instanceof int[]) {
					int[] os = (int[]) value;
					for (int i = 0; i < os.length; i++) {
						list.add(os[i]);
					}
				} else if (value instanceof boolean[]) {
					boolean[] os = (boolean[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof byte[]) {
					byte[] os = (byte[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof char[]) {
					char[] os = (char[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof double[]) {
					double[] os = (double[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof float[]) {
					float[] os = (float[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof long[]) {
					long[] os = (long[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				} else if (value instanceof short[]) {
					short[] os = (short[]) value;
					for (int i = 0; i < os.length; i++)
						list.add(os[i]);
				}
				return list;
			} else
				return Arrays.asList((Object[]) value);
		} else if (value instanceof Collection) {
			return new ArrayList((Collection) value);
		}

		throw new ClassCastException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to List");
	}

	// -------------------------setters
	/**
	 * Associates the specified value with the specified key in this map.
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key
	 */
	public Object set(K key, V value) {
		return put(key, value);
	}

	/**
	 * Copies all of the mappings from the specified map to this map.
	 *
	 * @param m mappings to be stored in this map
	 */
	public void setAll(Map<K, V> m) {
		if (m != null) {
			putAll(m);
		}
	}
}
