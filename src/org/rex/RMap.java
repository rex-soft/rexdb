package org.rex;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A LinkedHashMap with type conversion methods.
 * @author z
 */
public class RMap<K,V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = -2606902499614194563L;

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

	public RMap(Map<K,V> map) {
		super(map);
	}

	// -------------------------------------------------

	//------------get value as string
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
	 * @return string value
	 */
	protected String getStringValue(Object value) {
		if (value == null)
			return null;
		else if (value instanceof String)
			return (String) value;
		else if (value.getClass().isArray()){
			if(value instanceof int[]){
				return Arrays.toString((int[])value);
			}else if(value instanceof boolean[]){
				return Arrays.toString((boolean[])value);
			}else if(value instanceof byte[]){
				return Arrays.toString((byte[])value);
			}else if(value instanceof char[]){
				return Arrays.toString((char[])value);
			}else if(value instanceof double[]){
				return Arrays.toString((double[])value);
			}else if(value instanceof float[]){
				return Arrays.toString((float[])value);
			}else if(value instanceof long[]){
				return Arrays.toString((long[])value);
			}else if(value instanceof short[]){
				return Arrays.toString((short[])value);
			}else{
				Object[] ss = (Object[])value;
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < ss.length; i++) {
					builder.append(getStringValue(ss[i]));
					if(i != ss.length - 1)
						builder.append(',').append(' ');
				}
				return builder.insert(0, '[').append(']').toString();
			}
		}else
			return String.valueOf(value);
	}

	//------------get value as boolean
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

	//------------get value as number
	/**
	 * Get value as int, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return int value
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
	
	protected int parseInt(Object value){
		String s = getStringValue(value);
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to int, " + e.getMessage());
		}
	}
	
	/**
	 * Get value as long, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return long value
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
	
	protected long parseLong(Object value){
		String s = getStringValue(value);
		try{
			return Long.parseLong(s);
		}catch(NumberFormatException e){
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to long, " + e.getMessage());
		}
	}

	/**
	 * Get value as float, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return float value
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

	protected float parseFloat(Object value){
		String s = getStringValue(value);
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException e){
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to float, " + e.getMessage());
		}
	}

	/**
	 * Get value as double, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return double value
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
	
	protected double parseDouble(Object value){
		String s = getStringValue(value);
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException e){
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to double, " + e.getMessage());
		}
	}

	/**
	 * Get value as BigDecimal, return 0 if the value is null or empty, throw exception if convert failed
	 * @param key key with which the specified value is to be associated
	 * 
	 * @return BigDecimal value
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
	
	protected BigDecimal parseBigDecimal(Object value){
		String s = getStringValue(value);
		try{
			return new BigDecimal(s);
		}catch(NumberFormatException e){
			throw new NumberFormatException("Couldn't convert " + value + "(" + value.getClass().getName() + ") to BigDecimal, " + e.getMessage());
		}
	}

	//------------get value as Date
	/**
	 * Get value as java.util.Date
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public Date getDate(String key) {
		if (!containsKey(key))
			return null;
		Object value = get(key);
		return getDate(value);
	}
	
	/**
	 * Get value as java.sql.Date
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public java.sql.Date getDateForSql(String key) {
		Date date = getDate(key);
		if(date== null) 
			return null;
		return new java.sql.Date(date.getTime());
	}
	
	/**
	 * Get value as Time
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public Time getTime(String key) {
		Date date = getDate(key);
		if(date== null) 
			return null;
		return new Time(date.getTime());
	}

	/**
	 * Get value as Timestamp
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public Timestamp getTimestamp(String key) {
		Date date = getDate(key);
		if(date== null) 
			return null;
		return new Timestamp(date.getTime());
	}
	
	/**
	 * try to parse value to Date
	 * @param value object to convert
	 * @return date value
	 */
	protected Date getDate(Object value) {
		if (value == null)
			return null;
		else if (value instanceof Date)
			return (Date) value;
		else{
			String s = getStringValue(value); 
			for (DateParser dateParser : dateParsers) {
				Date date = dateParser.parse(s);
				if(date != null)
					return date;
			}
			throw new IllegalArgumentException("Couldn't convert " + s + "(" + value.getClass().getName() + ") to date whitch toString is " + s + ", no date format parttern matches.");
		}
	}
	
	/**
	 * Inner class for automatically parsing date string
	 */
	static class DateParser{
		private Pattern pattern;
		private SimpleDateFormat sdf;
		
		public DateParser(String pattern, String format) {
			super();
			this.pattern = Pattern.compile(pattern);
			this.sdf = new SimpleDateFormat(format);
		}
		
		/**
		 * parse date string which matches the pattern
		 * @param dateStr date string
		 * @return java.util.Date or null
		 */
		public Date parse(String dateStr){
			if(pattern.matcher(dateStr).matches()){
				try {
					return sdf.parse(dateStr);
				} catch (ParseException e) {
					throw new IllegalArgumentException("Couldn't parse date string "+ dateStr +" which matches pattern " + pattern.pattern() + "," + e.getMessage());
				}
			}
			return null;
		}
	}
	
	private static volatile List<DateParser> dateParsers = new ArrayList<DateParser>(){
		private static final long serialVersionUID = -1336942317939192127L;
		{
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss"));//2016-02-02 02:02:02, 2016/2/2 2:2:2, 2016年2月2日 2时2分2秒
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH-mm"));//2016-02-02 02:02
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yyyy-MM-dd-HH"));//2016-02-02 02
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D$", "yyyy-MM-dd"));//2016-02-02
			add(new DateParser("^\\d{4}\\D+\\d{1,2}\\D$", "yyyy-MM"));//2016-02
			add(new DateParser("^\\d{4}\\D$", "yyyy"));//2016
			add(new DateParser("^\\d{14}$", "yyyyMMddHHmmss"));//20160202020202
			add(new DateParser("^\\d{12}$", "yyyyMMddHHmm"));//201602020202
			add(new DateParser("^\\d{10}$", "yyyyMMddHH"));//2016020202
			add(new DateParser("^\\d{8}$", "yyyyMMdd"));//20160202
			add(new DateParser("^\\d{6}$", "yyyyMM"));//201602
			add(new DateParser("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss"));//02:02:02
			add(new DateParser("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm"));//02:02
			add(new DateParser("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd"));//02.02.02
			add(new DateParser("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM"));//02.02
			add(new DateParser("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy"));//02.02.2016
		}
	};
	
	/**
	 * Add a pattern for date format
	 * @param pattern date regular expression
	 * @param format java date format
	 */
	public static void addDateFormat(String pattern, String format){
		dateParsers.add(new DateParser(pattern, format));
	}

	//------------get value as array

	/**
	 * Get value as string array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
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
			Object[] os = (Object[]) value;
			String[] ss = new String[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = getStringValue(os[i]);
			}
			return ss;
		}
	}
	
	/**
	 * Get value as int array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public int[] getIntArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			int i = parseInt(value);
			return new int[] { i };
		} else if (value instanceof int[]) {
			return (int[]) value;
		} else {
			Object[] os = (Object[]) value;
			int[] ss = new int[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = parseInt(os[i]);
			}
			return ss;
		}
	}

	/**
	 * Get value as long array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public long[] getLongArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			long i = parseLong(value);
			return new long[] { i };
		} else if (value instanceof long[]) {
			return (long[]) value;
		} else {
			Object[] os = (Object[]) value;
			long[] ss = new long[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = parseLong(os[i]);
			}
			return ss;
		}
	}

	/**
	 * Get value as float array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public float[] getFloatArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			float i = parseFloat(value);
			return new float[] { i };
		} else if (value instanceof float[]) {
			return (float[]) value;
		} else {
			Object[] os = (Object[]) value;
			float[] ss = new float[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = parseFloat(os[i]);
			}
			return ss;
		}
	}

	/**
	 * Get value as double array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public double[] getDoubleArray(String key) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			double i = parseDouble(value);
			return new double[] { i };
		} else if (value instanceof double[]) {
			return (double[]) value;
		} else {
			Object[] os = (Object[]) value;
			double[] ss = new double[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = parseDouble(os[i]);
			}
			return ss;
		}
	}

	/**
	 * Get value as java.util.Date array, if the value is not an array, will be the first element of the array
	 * @param key key with which the specified value is to be associated
	 * @return the value associated with key
	 */
	public Date[] getDateArray(String key, String format) {
		Object value = get(key);
		if (value == null)
			return null;

		if (!value.getClass().isArray()) {
			Date d = getDate(value);
			return new Date[] { d };
		} else if (value instanceof Date[]) {
			return (Date[]) value;
		} else {
			Object[] os = (Object[]) value;
			Date[] ss = new Date[os.length];
			for (int i = 0; i < os.length; i++) {
				ss[i] = getDate(os[i]);
			}
			return ss;
		}
	}


	// -------------------------setters
	/**
     * put value with the specified key in this map.
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
