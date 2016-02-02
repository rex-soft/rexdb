package org.rex;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

/**
 * LinkedHashMap with conversions
 * @author z
 */
public class WMap<K,V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = -6140270080244028827L;

	private static final Logger LOGGER = LoggerFactory.getLogger(WMap.class);
	
	// -------------constructor
	public WMap() {
		super();
	}

	public WMap(int capacity) {
		super(capacity);
	}

	public WMap(int capacity, float factor) {
		super(capacity, factor);
	}

	public WMap(Map<K,V> map) {
		super(map);
	}

	// -------------------------------------------------

	//------get value as string
	/**
	 * Get value as String
	 * @param key key with which the specified value is to be associated
	 * @param emptyAsNull returns null if the value equals ""
	 * 
	 * @return the value associated with key
	 */
	public String getString(String key, boolean emptyAsNull) {
		if (!containsKey(key))
			return null;
		String value = getStringValue(get(key));
		return emptyAsNull && "".equals(value) ? null : value;
	}

	/**
	 * Get value as String
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return the value associated with key
	 */
	public String getString(String key) {
		return getString(key, false);
	}

	/**
	 * Convert an object to String, contact values if the object is a string array
	 * @param object object to convert
	 * 
	 * @return string value of the object
	 */
	protected String getStringValue(Object value) {
		if (value == null)
			return null;
		else if (value instanceof String)
			return (String) value;
		else if (value instanceof String[]){
			String[] ss = (String[])value;
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < ss.length; i++) {
				builder.append(ss[i]);
				if(i != ss.length - 1)
					builder.append(',');
			}
			return builder.toString();
		}else
			return String.valueOf(value);
	}

	//------get value as boolean
	/**
	 * Get value as boolean
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return the value associated with key
	 */
	public boolean getBoolean(String key) {
		if (!containsKey(key))
			return false;
		Object value = get(key);
		if (value == null)
			return false;
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.valueOf(getStringValue(value));
		}
	}

	//------get value as int
	/**
	 * Get value as int, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return the value associated with key
	 */
	public int getInt(String key) {
		if (!containsKey(key))
			return 0;
		Object value = get(key);
		return getInt(value);
	}

	/**
	 * try to Convert an object to int, throw exception if convert failed
	 * @param object object to convert
	 * 
	 * @return int value of the object
	 */
	protected int getInt(Object value) {
		if (value == null)
			return 0;
		else if (value instanceof Integer)
			return (Integer) value;
		else if (value instanceof Number)
			return ((Number) value).intValue();
		else {
			String s = getStringValue(value);
			try{
				return Integer.parseInt(s);
			}catch(NumberFormatException e){
				throw new NumberFormatException("Couldn't convert " + s + "(" + value.getClass().getName() + ") to int, " + e.getMessage());
			}
		}
	}

	//------get value as java.util.Date
	/**
	 * 获取java.util.Date类型的值
	 */
	public Date getDate(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getDate(value);
	}
	
	//thanks for 
	private static volatile Map<String, SimpleDateFormat> dateFormats = new LinkedHashMap<String, SimpleDateFormat>(){
		{
			put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));//2014-03-12 12:05:34, 2014/3/12 12:5:34, 2014年3月12日 13时5分34秒
			put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", new SimpleDateFormat("yyyy-MM-dd-HH-mm"));//2014-03-12 12:05
			put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", new SimpleDateFormat("yyyy-MM-dd-HH"));//2014-03-12 12
			put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D$", new SimpleDateFormat("yyyy-MM-dd"));//2014-03-12
			put("^\\d{4}\\D+\\d{1,2}\\D$", new SimpleDateFormat("yyyy-MM"));//2014-03
			put("^\\d{4}\\D$", new SimpleDateFormat("yyyy"));//2014
			put("^\\d{14}$", new SimpleDateFormat("yyyyMMddHHmmss"));//20140312120534
			put("^\\d{12}$", new SimpleDateFormat("yyyyMMddHHmm"));//201403121205
			put("^\\d{10}$", new SimpleDateFormat("yyyyMMddHH"));//2014031212
			put("^\\d{8}$", new SimpleDateFormat("yyyyMMdd"));//20140312
			put("^\\d{6}$", new SimpleDateFormat("yyyyMM"));//201403
			put("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));//13:05:34
			put("^\\d{2}\\s*:\\s*\\d{2}$", new SimpleDateFormat("yyyy-MM-dd-HH-mm"));//13:05
			put("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", new SimpleDateFormat("yy-MM-dd"));//14.10.18
			put("^\\d{1,2}\\D+\\d{1,2}$", new SimpleDateFormat("yyyy-dd-MM"));//30.12
			put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", new SimpleDateFormat("dd-MM-yyyy"));//12.21.2013
		}
	};
	
	/**
	 * 获取java.util.Date类型的值
	 */
	protected Date getDate(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Date)
			return (Date) value;
		else{
			
			
		}
	}
	
	/**
	 * try to parse a string to Date in order of date formats
	 * @param dateStr date string to format
	 * @return Date if successed
	 */
	private Date parseDate(String dateStr){
		
	}

	/**
	 * 获取java.sql.Date类型的值
	 */
	public java.sql.Date getDateSql(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getDateSql(value);
	}

	/**
	 * 获取java.sql.Date类型的值
	 */
	protected java.sql.Date getDateSql(Object value) {
		if (value == null)
			return null;
		else if (value instanceof java.sql.Date)
			return (java.sql.Date) value;
		else if (value instanceof java.util.Date) {
			log.warn("taxlite LiteMap：你已经将java.util.Date类型的值" + value + "转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.util.Date) value).getTime());
		} else if (value instanceof java.sql.Time) {
			log.warn("taxlite LiteMap：你已经将java.sql.Time类型的值" + value + "转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.sql.Time) value).getTime());
		} else if (value instanceof java.sql.Timestamp) {
			log.warn("taxlite LiteMap：你已经将java.sql.Timestamp类型的值" + value + "转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.sql.Timestamp) value).getTime());
		} else if (value instanceof String) {// 如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s = (String) value;
			if ("".equals(value))
				return null;
			try {
				SimpleDateFormat sdf = null;
				if (s.length() == 8) {
					sdf = new SimpleDateFormat("yyyyMMdd");
				} else if (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				} else if (s.length() == 19 && s.charAt(4) == '-' && s.charAt(7) == '-' && s.charAt(10) == ' ' && s.charAt(13) == ':'
						&& s.charAt(16) == ':') {
					sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				}
				if (sdf != null)
					return new java.sql.Date(sdf.parse(s).getTime());
			} catch (ParseException pe) {
			}
		} else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
			try {
				Method method = value.getClass().getMethod("dateValue", null);
				return (java.sql.Date) method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以java.sql.Date的类型获取");
	}

	/**
	 * 获取java.sql.Time类型的值
	 */
	public Time getTime(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getTime(value);
	}

	/**
	 * 获取java.sql.Time类型的值
	 */
	protected Time getTime(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Time)
			return (Time) value;
		else if (value instanceof java.sql.Date) {
			return new Time(((java.sql.Date) value).getTime());
		} else if (value instanceof java.util.Date) {
			return new Time(((java.util.Date) value).getTime());
		} else if (value instanceof java.sql.Timestamp) {
			log.warn("taxlite LiteMap：你已经将java.sql.Timestamp类型的值" + value + "转换为java.sql.Time型，taxlite可以转换，但可能丢失时间数据");
			return new Time(((java.sql.Timestamp) value).getTime());
		} else if (value instanceof String) {// 如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s = (String) value;
			if ("".equals(value))
				return null;
			try {
				SimpleDateFormat sdf = null;
				if (s.length() == 19 && s.charAt(4) == '-' && s.charAt(7) == '-' && s.charAt(10) == ' ' && s.charAt(13) == ':'
						&& s.charAt(16) == ':') {
					sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				} else if (s.length() == 8) {
					sdf = new SimpleDateFormat("yyyyMMdd");
				} else if (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				}
				if (sdf != null)
					return new java.sql.Time(sdf.parse(s).getTime());
			} catch (ParseException pe) {
			}
		} else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
			try {
				Method method = value.getClass().getMethod("timeValue", null);
				return (java.sql.Time) method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以java.sql.Time的类型获取");
	}

	/**
	 * 获取java.sql.Timestamp类型的值
	 */
	public Timestamp getTimestamp(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getTimestamp(value);
	}

	/**
	 * 获取java.sql.Timestamp类型的值
	 */
	protected Timestamp getTimestamp(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Timestamp)
			return (Timestamp) value;
		else if (value instanceof java.sql.Date) {
			return new Timestamp(((java.sql.Date) value).getTime());
		} else if (value instanceof java.util.Date) {
			return new Timestamp(((java.util.Date) value).getTime());
		} else if (value instanceof java.sql.Time) {
			return new Timestamp(((java.sql.Time) value).getTime());
		} else if (value instanceof String) {// 如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s = (String) value;
			if ("".equals(value))
				return null;
			try {
				SimpleDateFormat sdf = null;
				if (s.length() == 19 && s.charAt(4) == '-' && s.charAt(7) == '-' && s.charAt(10) == ' ' && s.charAt(13) == ':'
						&& s.charAt(16) == ':') {
					sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				} else if (s.length() == 8) {
					sdf = new SimpleDateFormat("yyyyMMdd");
				} else if (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				}
				if (sdf != null)
					return new java.sql.Timestamp(sdf.parse(s).getTime());
			} catch (ParseException pe) {
			}
		} else if ("oracle.sql.TIMESTAMP".equals(value.getClass().getName())) {
			try {
				Method method = value.getClass().getMethod("timestampValue", null);
				return (java.sql.Timestamp) method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以java.sql.Timestamp的类型获取");
	}

	/**
	 * 获取Date型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Date getDateSql(String key, String format) {
		Date date = getDate(key, format);
		if (date == null)
			return null;
		else
			return new java.sql.Date(date.getTime());
	}

	/**
	 * 获取Time型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Time getTime(String key, String format) {
		Date date = getDate(key, format);
		if (date == null)
			return null;
		else
			return new java.sql.Time(date.getTime());
	}

	/**
	 * 获取Timestamp型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Timestamp getTimeStamp(String key, String format) {
		Date date = getDate(key, format);
		if (date == null)
			return null;
		else
			return new java.sql.Timestamp(date.getTime());
	}

	/**
	 * 获取Date型对象，如果是字符串类型，将按照指定的格式format
	 */
	public Date getDate(String key, String format) {
		if (!containsKey(key))
			return null;
		Object value = get(key);

		if (value == null || "".equals(value))
			return null;
		else if (value instanceof String) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.parse((String) value);
			} catch (Exception e) {
				throw new RuntimeException("调用getDate方法时产生异常，无法转换此字符，key:" + key + ",value:" + value, e);
			}
		} else
			return getDate(key);
	}

	/**
	 * 获取long类型数据
	 */
	public long getLong(String key) {
		if (!containsKey(key))
			return 0;
		Object value = get(key);
		return getLong(value);
	}

	/**
	 * 获取long类型数据
	 */
	protected long getLong(Object value) {
		if (value == null)
			return 0;
		else if (value instanceof Long)
			return ((Long) value).longValue();
		else if (value instanceof Integer)
			return ((Integer) value).longValue();
		else if (value instanceof BigDecimal)
			return ((BigDecimal) value).longValue();
		else if (value instanceof String) {
			try {
				return Long.valueOf((String) value).longValue();
			} catch (Exception e) {
				throw new RuntimeException("调用getLong方法时产生异常，无法转换此字符，value:" + value, e);
			}
		} else
			throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以long的类型获取");
	}

	/**
	 * 获取Float类型数据
	 */
	public float getFloat(String key) {
		if (!containsKey(key))
			return 0;
		Object value = get(key);
		return getFloat(value);
	}

	/**
	 * 获取Float类型数据
	 */
	protected float getFloat(Object value) {
		if (value == null)
			return 0;
		else if (value instanceof Float)
			return ((Float) value).floatValue();
		else if (value instanceof Integer)
			return ((Integer) value).floatValue();
		else if (value instanceof BigDecimal)
			return ((BigDecimal) value).floatValue();
		else if (value instanceof Long)
			return ((Long) value).floatValue();
		else if (value instanceof Double)
			return ((Double) value).floatValue();
		else if (value instanceof String) {
			try {
				return Float.valueOf((String) value).floatValue();
			} catch (Exception e) {
				throw new RuntimeException("调用getFloat方法时产生异常，无法转换此字符，value:" + value, e);
			}
		} else
			throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以float的类型获取");
	}

	/**
	 * 获取Double类型数据
	 */
	public double getDouble(String key) {
		if (!containsKey(key))
			return 0;
		Object value = get(key);
		return getDouble(value);
	}

	/**
	 * 获取Double类型数据
	 */
	protected double getDouble(Object value) {

		if (value == null)
			return 0;
		else if (value instanceof Double)
			return ((Double) value).doubleValue();
		else if (value instanceof Integer)
			return ((Integer) value).doubleValue();
		else if (value instanceof BigDecimal)
			return ((BigDecimal) value).doubleValue();
		else if (value instanceof Long)
			return ((Long) value).doubleValue();
		else if (value instanceof Float)
			return ((Float) value).doubleValue();
		else if (value instanceof String) {
			try {
				return Double.valueOf((String) value).doubleValue();
			} catch (Exception e) {
				throw new RuntimeException("调用getDouble方法时产生异常，无法转换此字符，value:" + value, e);
			}
		} else
			throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以double的类型获取");

	}

	/**
	 * 获取BigDecimal类型数据
	 */
	public BigDecimal getDecimal(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getDecimal(value);
	}

	/**
	 * 获取BigDecimal类型数据
	 */
	protected BigDecimal getDecimal(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Double)
			return new BigDecimal(((Double) value).doubleValue());
		else if (value instanceof Integer)
			return new BigDecimal(((Integer) value).doubleValue());
		else if (value instanceof BigDecimal)
			return (BigDecimal) value;
		else if (value instanceof Long)
			return new BigDecimal(((Long) value).doubleValue());
		else if (value instanceof Float)
			return new BigDecimal(((Float) value).doubleValue());
		else if (value instanceof String) {
			try {
				return new BigDecimal((String) value);
			} catch (Exception e) {
				throw new RuntimeException("调用getBigDecimal方法时产生异常，无法转换此字符，value:" + value, e);
			}
		} else
			throw new RuntimeException("你试图将value:" + value + ",class:" + value.getClass().getName() + "以BigDecimal的类型获取");

	}

	/**
	 * 获取字符串数组，该方法用于获取前台表单的值
	 */
	public String[] getStringArray(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);

		if (!value.getClass().isArray()) {
			String s = getString(key);
			return new String[] { s };
		} else if (value instanceof String[]) {
			return (String[]) value;
		} else {
			Object[] os = (Object[]) value;
			String[] ss = new String[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = getStringValue(os[i]);
			}
			return ss;
		}
	}

	/**
	 * 获取int型数组，该方法用于获取前台表单的值
	 */
	public int[] getIntArray(String key) {
		String[] s = getStringArray(key);
		if (s == null)
			return null;
		int[] is = new int[s.length];
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null)
				is[i] = 0;
			else
				try {
					is[i] = Integer.parseInt(s[i]);
				} catch (Exception e) {
					log.error("调用getIntArray方法时产生异常，无法转换此字符value:" + s[i] + ";返回了默认值0");
					is[i] = 0;
				}
		}
		return is;
	}

	/**
	 * 获取long型数组，该方法用于获取前台表单的值
	 */
	public long[] getLongArray(String key) {
		String[] s = getStringArray(key);
		if (s == null)
			return null;
		long[] is = new long[s.length];
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null)
				is[i] = 0;
			else
				try {
					is[i] = Long.parseLong(s[i]);
				} catch (Exception e) {
					log.error("调用getLongArray方法时产生异常，无法转换此字符value:" + s[i] + ";返回了默认值0");
					is[i] = 0;
				}
		}
		return is;
	}

	/**
	 * 获取float型数组，该方法用于获取前台表单的值
	 */
	public float[] getFloatArray(String key) {
		String[] s = getStringArray(key);
		if (s == null)
			return null;
		float[] is = new float[s.length];
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null)
				is[i] = 0;
			else
				try {
					is[i] = Float.parseFloat(s[i]);
				} catch (Exception e) {
					log.error("调用getFloatArray方法时产生异常，无法转换此字符value:" + s[i] + ";返回了默认值0");
					is[i] = 0;
				}
		}
		return is;
	}

	/**
	 * 获取double型数组，该方法用于获取前台表单的值
	 */
	public double[] getDoubleArray(String key) {
		String[] s = getStringArray(key);
		if (s == null)
			return null;
		double[] is = new double[s.length];
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null)
				is[i] = 0;
			else
				try {
					is[i] = Double.parseDouble(s[i]);
				} catch (Exception e) {
					log.error("调用getDoubleArray方法时产生异常，无法转换此字符value:" + s[i] + ";返回了默认值0");
					is[i] = 0;
				}
		}
		return is;
	}

	/**
	 * 获取java.util.Date型数组，该方法用于获取前台表单的值
	 */
	public Date[] getDateArray(String key, String format) {
		String[] s = getStringArray(key);
		if (s == null)
			return null;
		Date[] is = new Date[s.length];
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		for (int i = 0; i < s.length; i++) {
			if (s[i] == null || "".equals(s[i]))
				is[i] = null;
			else
				try {
					is[i] = sdf.parse(s[i]);
				} catch (Exception e) {
					throw new RuntimeException("调用getDateArray方法时产生异常，无法转换此字符value:" + s[i]);
				}
		}
		return is;
	}

	/**
	 * 获取java.sql.Date型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Date[] getDateSqlArray(String key, String format) {
		Date[] ds = getDateArray(key, format);
		if (ds == null)
			return null;
		java.sql.Date[] is = new java.sql.Date[ds.length];
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == null)
				is[i] = null;
			else
				is[i] = new java.sql.Date(ds[i].getTime());
		}
		return is;
	}

	/**
	 * 获取java.sql.Time型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Time[] getTimeArray(String key, String format) {
		Date[] ds = getDateArray(key, format);
		if (ds == null)
			return null;
		java.sql.Time[] is = new java.sql.Time[ds.length];
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == null)
				is[i] = null;
			else
				is[i] = new java.sql.Time(ds[i].getTime());
		}
		return is;
	}

	/**
	 * 获取java.sql.Timestamp型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Timestamp[] getTimestampArray(String key, String format) {
		Date[] ds = getDateArray(key, format);
		if (ds == null)
			return null;
		java.sql.Timestamp[] is = new java.sql.Timestamp[ds.length];
		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == null)
				is[i] = null;
			else
				is[i] = new java.sql.Timestamp(ds[i].getTime());
		}
		return is;
	}

	// -------------------------------------------方便使用的set方法
	/**
	 * 相当于put方法
	 */
	public Object set(Object key, Object value) {
		return put(key, value);
	}

	public Integer set(Object key, int value) {
		Integer i = new Integer(value);
		put(key, i);
		return i;
	}

	public Float set(Object key, float value) {
		Float f = new Float(value);
		put(key, f);
		return f;
	}

	public Double set(Object key, double value) {
		Double d = new Double(value);
		put(key, d);
		return d;
	}

	public Long set(Object key, long value) {
		Long l = new Long(value);
		put(key, l);
		return l;
	}

	public Boolean set(Object key, boolean value) {
		Boolean b = new Boolean(value);
		put(key, b);
		return b;
	}

	/**
	 * 相当于putAll方法
	 */
	public void setAll(Map m) {
		if (m != null) {
			putAll(m);
		}
	}

	public void putAll(Map m) {
		if (m != null) {
			super.putAll(m);
		}
	}
}
