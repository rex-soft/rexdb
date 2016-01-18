package org.rex;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 封装了类型转换方法的Map
 * @author zhw
 */
public class WMap extends LinkedHashMap{

	private static Log log = LogFactory.getLog(WMap.class);
	
	//-------------------------------------------------构造函数
	public WMap() {
		super();
	}

	public WMap(int capacity) {
		super(capacity);
	}

	public WMap(int capacity, float factor) {
		super(capacity,factor);
	}

	public WMap(Map map) {
		super(map);
	}
	
	//-------------------------------------------------基本方法
	
	/**
	 * 判断某个变量是不是空值，当为null或者为""都返回true
	 */
	public boolean isEmpty(String key){
		if(!containsKey(key)) return true;
		else if(get(key)==null) return true;
		else if("".equals((String)get(key))) return true;
		else return false;
	}
	
	/**
	 * 获取字符串类型值
	 */
	public String getString(String key,boolean returnNull){
		if(!containsKey(key)) return null;
		String value=getStringValue(get(key));
		return returnNull&&"".equals(value)?null:value;
	}
	
	/**
	 * 获取字符串类型值
	 */
	public String getString(String key){
		return getString(key, false);
	}
	
	/**
	 * 获取字符串类型值
	 */
	protected String getStringValue(Object value){
		if(value==null) return null;
		else if(value instanceof String) return (String)value;
		else if(value instanceof String[]) return ((String[])value)[0];
		else return String.valueOf(value);
	}
	
	/**
	 * 获取布尔型值
	 */
	public boolean getBoolean(String key){
		if(!containsKey(key)) return false;
		Object value=get(key);
		if(value==null) return false;
		if(value instanceof Boolean){
			if(((Boolean)value).booleanValue()) return true;
			else return false;
		}else{
			String stringValue=getStringValue(value);
			if("true".equalsIgnoreCase(stringValue)) return true;
			else return false;
		}
	}
	
	/**
	 * 获取int类型的值
	 */
	public int getInt(String key){
		if(!containsKey(key)) return 0;
		Object value=get(key);
		return getInt(value);
	}
	
	/**
	 * 获取int类型的值
	 */
	protected int getInt(Object value){
		if(value==null) return 0;
		else if(value instanceof Integer) return ((Integer)value).intValue();
		else if(value instanceof Short) return ((Short)value).intValue();
		else if(value instanceof Double) return ((Double)value).intValue();
		else if(value instanceof Long) return ((Long)value).intValue();
		else if(value instanceof String) {
			try{
				return Integer.parseInt((String)value);
			}catch(Exception e){
				log.error("调用getInt方法时产生异常，无法转换此字符value:"+value+";返回了默认值0");
				return 0;
			}
		}
		else if(value instanceof BigDecimal) return ((BigDecimal)value).intValue();
		else if(value instanceof int[]) return ((int[])value)[0];
		else if(value instanceof String[]) return Integer.parseInt(((String[])value)[0]);
		else throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以int的类型获取");
	}
	
	/**
	 * 获取java.util.Date类型的值
	 */
	public Date getDate(String key){
		if(!containsKey(key)) return null;
		Object value=get(key);
		return getDate(value);
	}
	
	/**
	 * 获取java.util.Date类型的值
	 */
	protected Date getDate(Object value){
		if(value==null) return null;
		else if(value instanceof Date) return (Date)value;
		else if(value instanceof java.sql.Date) return new Date(((java.sql.Date)value).getTime());
		else if(value instanceof java.sql.Time) return new Date(((java.sql.Time)value).getTime());
		else if(value instanceof java.sql.Timestamp) return new Date(((java.sql.Timestamp)value).getTime());
		else if(value instanceof String){//如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			if("".equals(value)) return null;
			String s=(String)value;
			try{
				SimpleDateFormat sdf=null;
				if(s.length()==8){
					sdf=new SimpleDateFormat("yyyyMMdd");
				}else if(s.length()==10 && s.charAt(4)=='-' && s.charAt(7)=='-'){
					sdf=new SimpleDateFormat("yyyy-MM-dd");
				}else if(s.length()==19 && s.charAt(4)=='-' && s.charAt(7)=='-' && s.charAt(10)==' ' && s.charAt(13)==':' && s.charAt(16)==':'){
					sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				}
				if(sdf!=null) return sdf.parse(s);
			}catch(ParseException pe){
			}
		}else if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
			try {
				Method method = value.getClass().getMethod("dateValue", null);
				return new Date(((java.sql.Date)method.invoke(value, null)).getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以Date的类型获取");
	}
	
	/**
	 * 获取java.sql.Date类型的值
	 */
	public java.sql.Date getDateSql(String key){
		if(!containsKey(key)) return null;
		Object value=get(key);
		return getDateSql(value);
	}
	
	/**
	 * 获取java.sql.Date类型的值
	 */
	protected java.sql.Date getDateSql(Object value){
		if(value==null) return null;
		else if(value instanceof java.sql.Date) return (java.sql.Date)value;
		else if(value instanceof java.util.Date) {
			log.warn("taxlite LiteMap：你已经将java.util.Date类型的值"+value+"转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.util.Date)value).getTime());
		}
		else if(value instanceof java.sql.Time) {
			log.warn("taxlite LiteMap：你已经将java.sql.Time类型的值"+value+"转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.sql.Time)value).getTime());
		}
		else if(value instanceof java.sql.Timestamp) {
			log.warn("taxlite LiteMap：你已经将java.sql.Timestamp类型的值"+value+"转换为java.sql.Date型，taxlite可以转换，但可能丢失时间数据");
			return new java.sql.Date(((java.sql.Timestamp)value).getTime());
		}else if(value instanceof String){//如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s=(String)value;
			if("".equals(value)) return null;
			try{
				SimpleDateFormat sdf=null;
				if(s.length()==8){
					sdf=new SimpleDateFormat("yyyyMMdd");
				}else if(s.length()==10 && s.charAt(4)=='-' && s.charAt(7)=='-'){
					sdf=new SimpleDateFormat("yyyy-MM-dd");
				}else if(s.length()==19 && s.charAt(4)=='-' && s.charAt(7)=='-' && s.charAt(10)==' ' && s.charAt(13)==':' && s.charAt(16)==':'){
					sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				}
				if(sdf!=null) return new java.sql.Date(sdf.parse(s).getTime());
			}catch(ParseException pe){
			}
		}else if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
			try {
				Method method = value.getClass().getMethod("dateValue", null);
				return (java.sql.Date)method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以java.sql.Date的类型获取");
	}
	
	/**
	 * 获取java.sql.Time类型的值
	 */
	public Time getTime(String key){
		if(!containsKey(key)) return null;
		Object value=get(key);
		return getTime(value);
	}
	
	/**
	 * 获取java.sql.Time类型的值
	 */
	protected Time getTime(Object value){
		if(value==null) return null;
		else if(value instanceof Time) return (Time)value;
		else if(value instanceof java.sql.Date) {
			return new Time(((java.sql.Date)value).getTime());
		}
		else if(value instanceof java.util.Date) {
			return new Time(((java.util.Date)value).getTime());
		}
		else if(value instanceof java.sql.Timestamp) {
			log.warn("taxlite LiteMap：你已经将java.sql.Timestamp类型的值"+value+"转换为java.sql.Time型，taxlite可以转换，但可能丢失时间数据");
			return new Time(((java.sql.Timestamp)value).getTime());
		}else if(value instanceof String){//如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s=(String)value;
			if("".equals(value)) return null;
			try{
				SimpleDateFormat sdf=null;
				if(s.length()==19 && s.charAt(4)=='-' && s.charAt(7)=='-' && s.charAt(10)==' ' && s.charAt(13)==':' && s.charAt(16)==':'){
					sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				}else if(s.length()==8){
					sdf=new SimpleDateFormat("yyyyMMdd");
				}else if(s.length()==10 && s.charAt(4)=='-' && s.charAt(7)=='-'){
					sdf=new SimpleDateFormat("yyyy-MM-dd");
				}
				if(sdf!=null) return new java.sql.Time(sdf.parse(s).getTime());
			}catch(ParseException pe){
			}
		}else if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
			try {
				Method method = value.getClass().getMethod("timeValue", null);
				return (java.sql.Time)method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以java.sql.Time的类型获取");
	}
	
	/**
	 * 获取java.sql.Timestamp类型的值
	 */
	public Timestamp getTimestamp(String key){
		if(!containsKey(key)) return null;
		Object value=get(key);
		return getTimestamp(value);
	}
	
	/**
	 * 获取java.sql.Timestamp类型的值
	 */
	protected Timestamp getTimestamp(Object value){
		if(value==null) return null;
		else if(value instanceof Timestamp) return (Timestamp)value;
		else if(value instanceof java.sql.Date) {
			return new Timestamp(((java.sql.Date)value).getTime());
		}
		else if(value instanceof java.util.Date) {
			return new Timestamp(((java.util.Date)value).getTime());
		}
		else if(value instanceof java.sql.Time) {
			return new Timestamp(((java.sql.Time)value).getTime());
		}else if(value instanceof String){//如果是string，尝试转换成yyyyMMdd、yyyy-MM-dd
			String s=(String)value;
			if("".equals(value)) return null;
			try{
				SimpleDateFormat sdf=null;
				if(s.length()==19 && s.charAt(4)=='-' && s.charAt(7)=='-' && s.charAt(10)==' ' && s.charAt(13)==':' && s.charAt(16)==':'){
					sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				}else if(s.length()==8){
					sdf=new SimpleDateFormat("yyyyMMdd");
				}else if(s.length()==10 && s.charAt(4)=='-' && s.charAt(7)=='-'){
					sdf=new SimpleDateFormat("yyyy-MM-dd");
				}
				if(sdf!=null) return new java.sql.Timestamp(sdf.parse(s).getTime());
			}catch(ParseException pe){
			}
		}else if("oracle.sql.TIMESTAMP".equals(value.getClass().getName())){
			try {
				Method method = value.getClass().getMethod("timestampValue", null);
				return (java.sql.Timestamp)method.invoke(value, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以java.sql.Timestamp的类型获取");
	}
	
	/**
	 * 获取Date型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Date getDateSql(String key,String format){
		Date date=getDate(key,format);
		if(date==null) return null;
		else return new java.sql.Date(date.getTime());
	}
	
	/**
	 * 获取Time型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Time getTime(String key,String format){
		Date date=getDate(key,format);
		if(date==null) return null;
		else return new java.sql.Time(date.getTime());
	}
	
	/**
	 * 获取Timestamp型对象，如果是字符串类型，将按照指定的格式format
	 */
	public java.sql.Timestamp getTimeStamp(String key,String format){
		Date date=getDate(key,format);
		if(date==null) return null;
		else return new java.sql.Timestamp(date.getTime());
	}
	
	/**
	 * 获取Date型对象，如果是字符串类型，将按照指定的格式format
	 */
	public Date getDate(String key,String format){
		if(!containsKey(key)) return null;
		Object value=get(key);
		
		if(value==null || "".equals(value)) return null;
		else if(value instanceof String){
			try{
				SimpleDateFormat sdf=new SimpleDateFormat(format);
				return sdf.parse((String)value);
			}catch(Exception e){
				throw new RuntimeException("调用getDate方法时产生异常，无法转换此字符，key:"+key+",value:"+value,e);	
			}
		}else return getDate(key);
	}
	
	
	/**
	 * 获取long类型数据
	 */
	public long getLong(String key){
		if(!containsKey(key)) return 0;
		Object value=get(key);
		return getLong(value);
	}
	
	/**
	 * 获取long类型数据
	 */
	protected long getLong(Object value){
		if(value==null) return 0;
		else if(value instanceof Long) return ((Long)value).longValue();
		else if(value instanceof Integer) return ((Integer)value).longValue();
		else if(value instanceof BigDecimal) return ((BigDecimal)value).longValue();
		else if(value instanceof String){
			try{
				return Long.valueOf((String)value).longValue();
			}catch(Exception e){
				throw new RuntimeException("调用getLong方法时产生异常，无法转换此字符，value:"+value,e);	
			}
		}
		else throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以long的类型获取");
	}
	
	/**
	 * 获取Float类型数据
	 */
	public float getFloat(String key){
		if(!containsKey(key)) return 0;
		Object value=get(key);
		return getFloat(value);
	}
	
	/**
	 * 获取Float类型数据
	 */
	protected float getFloat(Object value){
		if(value==null) return 0;
		else if(value instanceof Float) return ((Float)value).floatValue();
		else if(value instanceof Integer) return ((Integer)value).floatValue();
		else if(value instanceof BigDecimal) return ((BigDecimal)value).floatValue();
		else if(value instanceof Long) return ((Long)value).floatValue();
		else if(value instanceof Double) return ((Double)value).floatValue();
		else if(value instanceof String){
			try{
				return Float.valueOf((String)value).floatValue();
			}catch(Exception e){
				throw new RuntimeException("调用getFloat方法时产生异常，无法转换此字符，value:"+value,e);	
			}
		}
		else throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以float的类型获取");
	}
	
	/**
	 * 获取Double类型数据
	 */
	public double getDouble(String key){
		if(!containsKey(key)) return 0;
		Object value=get(key);
		return getDouble(value);
	}
	
	/**
	 * 获取Double类型数据
	 */
	protected double getDouble(Object value){
		
		if(value==null) return 0;
		else if(value instanceof Double) return ((Double)value).doubleValue();
		else if(value instanceof Integer) return ((Integer)value).doubleValue();
		else if(value instanceof BigDecimal) return ((BigDecimal)value).doubleValue();
		else if(value instanceof Long) return ((Long)value).doubleValue();
		else if(value instanceof Float) return ((Float)value).doubleValue();
		else if(value instanceof String){
			try{
				return Double.valueOf((String)value).doubleValue();
			}catch(Exception e){
				throw new RuntimeException("调用getDouble方法时产生异常，无法转换此字符，value:"+value,e);	
			}
		}
		else throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以double的类型获取");
		
	}
	
	/**
	 * 获取BigDecimal类型数据
	 */
	public BigDecimal getDecimal(String key){
		if(!containsKey(key)) return null;
		Object value=get(key);
		return getDecimal(value);
	}
	
	/**
	 * 获取BigDecimal类型数据
	 */
	protected BigDecimal getDecimal(Object value){
		if(value==null) return null;
		else if(value instanceof Double) return new BigDecimal(((Double)value).doubleValue());
		else if(value instanceof Integer) return new BigDecimal(((Integer)value).doubleValue());
		else if(value instanceof BigDecimal) return (BigDecimal)value;
		else if(value instanceof Long) return new BigDecimal(((Long)value).doubleValue());
		else if(value instanceof Float) return new BigDecimal(((Float)value).doubleValue());
		else if(value instanceof String){
			try{
				return new BigDecimal((String)value);
			}catch(Exception e){
				throw new RuntimeException("调用getBigDecimal方法时产生异常，无法转换此字符，value:"+value,e);	
			}
		}
		else throw new RuntimeException("你试图将value:"+value+",class:"+value.getClass().getName()+"以BigDecimal的类型获取");

	}
	
	/**
	 * 获取字符串数组，该方法用于获取前台表单的值
	 */
	public String[] getStringArray(String key) {
		if(!containsKey(key)) return null;
		Object value=get(key);
		
		if(!value.getClass().isArray()){
			String s=getString(key);
			return new String[]{s};
		}else if(value instanceof String[]){
			return (String[])value;
		}else{
			Object[] os=(Object[])value;
			String[] ss=new String[os.length];
			for(int i=0;i<os.length;i++){
				ss[i]=getStringValue(os[i]);
			}
			return ss;
		}
	}
	
	/**
	 * 获取int型数组，该方法用于获取前台表单的值
	 */
	public int[] getIntArray(String key){
		String[] s=getStringArray(key);
		if(s==null) return null;
		int[] is=new int[s.length];
		for(int i=0;i<s.length;i++){
			if(s[i]==null) is[i]=0;
			else try{
				is[i]=Integer.parseInt(s[i]);
			}catch(Exception e){
				log.error("调用getIntArray方法时产生异常，无法转换此字符value:"+s[i]+";返回了默认值0");
				is[i]=0;
			}
		}
		return is;
	}
	
	/**
	 * 获取long型数组，该方法用于获取前台表单的值
	 */
	public long[] getLongArray(String key) {
		String[] s=getStringArray(key);
		if(s==null) return null;
		long[] is=new long[s.length];
		for(int i=0;i<s.length;i++){
			if(s[i]==null) is[i]=0;
			else try{
				is[i]=Long.parseLong(s[i]);
			}catch(Exception e){
				log.error("调用getLongArray方法时产生异常，无法转换此字符value:"+s[i]+";返回了默认值0");
				is[i]=0;
			}
		}
		return is;
	}
	
	/**
	 * 获取float型数组，该方法用于获取前台表单的值
	 */
	public float[] getFloatArray(String key) {
		String[] s=getStringArray(key);
		if(s==null) return null;
		float[] is=new float[s.length];
		for(int i=0;i<s.length;i++){
			if(s[i]==null) is[i]=0;
			else try{
				is[i]=Float.parseFloat(s[i]);
			}catch(Exception e){
				log.error("调用getFloatArray方法时产生异常，无法转换此字符value:"+s[i]+";返回了默认值0");
				is[i]=0;
			}
		}
		return is;
	}
	
	/**
	 * 获取double型数组，该方法用于获取前台表单的值
	 */
	public double[] getDoubleArray(String key) {
		String[] s=getStringArray(key);
		if(s==null) return null;
		double[] is=new double[s.length];
		for(int i=0;i<s.length;i++){
			if(s[i]==null) is[i]=0;
			else try{
				is[i]=Double.parseDouble(s[i]);
			}catch(Exception e){
				log.error("调用getDoubleArray方法时产生异常，无法转换此字符value:"+s[i]+";返回了默认值0");
				is[i]=0;
			}
		}
		return is;
	}
	
	/**
	 * 获取java.util.Date型数组，该方法用于获取前台表单的值
	 */
	public Date[] getDateArray(String key,String format){
		String[] s=getStringArray(key);
		if(s==null) return null;
		Date[] is=new Date[s.length];
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		for(int i=0;i<s.length;i++){
			if(s[i]==null|| "".equals(s[i])) is[i]=null;
			else try{
				is[i]=sdf.parse(s[i]);
			}catch(Exception e){
				throw new RuntimeException("调用getDateArray方法时产生异常，无法转换此字符value:"+s[i]);
			}
		}
		return is;
	}
	
	/**
	 * 获取java.sql.Date型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Date[] getDateSqlArray(String key,String format){
		Date[] ds=getDateArray(key,format);
		if(ds==null) return null;
		java.sql.Date[] is=new java.sql.Date[ds.length];
		for(int i=0;i<ds.length;i++){
			if(ds[i]==null) is[i]=null;
			else is[i]=new java.sql.Date(ds[i].getTime());
		}
		return is;
	}
	
	/**
	 * 获取java.sql.Time型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Time[] getTimeArray(String key,String format){
		Date[] ds=getDateArray(key,format);
		if(ds==null) return null;
		java.sql.Time[] is=new java.sql.Time[ds.length];
		for(int i=0;i<ds.length;i++){
			if(ds[i]==null) is[i]=null;
			else is[i]=new java.sql.Time(ds[i].getTime());
		}
		return is;
	}
	
	/**
	 * 获取java.sql.Timestamp型数组，该方法用于获取前台表单的值
	 */
	public java.sql.Timestamp[] getTimestampArray(String key,String format){
		Date[] ds=getDateArray(key,format);
		if(ds==null) return null;
		java.sql.Timestamp[] is=new java.sql.Timestamp[ds.length];
		for(int i=0;i<ds.length;i++){
			if(ds[i]==null) is[i]=null;
			else is[i]=new java.sql.Timestamp(ds[i].getTime());
		}
		return is;
	}
	//-------------------------------------------方便使用的set方法
	/**
	 * 相当于put方法
	 */
	public Object set(Object key,Object value){
		return put(key,value);
	}
	public Integer set(Object key,int value){
		Integer i=new Integer(value);
		put(key,i);
		return i;
	}
	public Float set(Object key,float value){
		Float f=new Float(value);
		put(key,f);
		return f;
	}
	public Double set(Object key,double value){
		Double d=new Double(value);
		put(key,d);
		return d;
	}
	public Long set(Object key,long value){
		Long l=new Long(value);
		put(key,l);
		return l;
	}
	public Boolean set(Object key,boolean value){
		Boolean b=new Boolean(value);
		put(key,b);
		return b;
	}
	
	/**
	 * 相当于putAll方法
	 */
	public void setAll(Map m){
		if(m!=null){
			putAll(m);
		}
	} 
	
	public void putAll(Map m){
		if(m!=null){
			super.putAll(m);
		}
	}
}
