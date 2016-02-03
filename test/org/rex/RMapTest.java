package org.rex;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class RMapTest {
	
	RMap rmap = new RMap();
	
	@Before
	public void init(){
		rmap.set("string-null", null);
		rmap.set("string-empty", "");
		
		rmap.set("string-n1", "0");
		rmap.set("string-n2", "1");
		rmap.set("string-n3", "-1");
		rmap.set("string-n4", "1L");
		rmap.set("string-n5", "0.234935");
		rmap.set("string-n6", "-0.234935");
		
		rmap.set("int", 1);
		rmap.set("long", 1L);
		rmap.set("double", 1d);
		rmap.set("float", 1f);
		rmap.set("short", (short)1);
		rmap.set("char", '1');
		rmap.set("byte", (byte)1);
		rmap.set("boolean", true);
		rmap.set("decimal", new BigDecimal(1));
		rmap.set("object", "1");
		
        rmap.set("array-string", new String[]{"1", "2", "3"});
        rmap.set("array-int", new int[]{1, 2, 3});
        rmap.set("array-long", new long[]{1L, 2L, 3L});
        rmap.set("array-float", new float[]{1f, 2f, 3f});
        rmap.set("array-double", new double[]{1, 2, 3});
        rmap.set("array-short", new short[]{1, 2, 3});
        rmap.set("array-boolean", new boolean[]{true, false, true});
        rmap.set("array-char", new char[]{'1', '2', '3'});
        rmap.set("array-byte", new byte[]{1, 2, 3});
        rmap.set("array-object", new Object[]{"1", "2", "3"});
		
		rmap.set("string-d1", "2014-03-12 12:05:34");
		rmap.set("string-d2", "2014-03-12 12:05");
		rmap.set("string-d3", "2014-03-12 12");
		rmap.set("string-d4", "2014-03-12");
        rmap.set("string-d5", "2014-03");
        rmap.set("string-d6", "2014");
        rmap.set("string-d7", "20140312120534");
        rmap.set("string-d8", "2014/03/12 12:05:34");
        rmap.set("string-d9", "2014/3/12 12:5:34");
        rmap.set("string-d10", "2014年3月12日 13时5分34秒");
        rmap.set("string-d11", "201403121205");
        rmap.set("string-d12", "1234567890");
        rmap.set("string-d13", "20140312");
        rmap.set("string-d14", "201403");
        rmap.set("string-d15", "2000 13 33 13 13 13");
        rmap.set("string-d16", "30.12.2013");
        rmap.set("string-d17", "12.21.2013");
        rmap.set("string-d18", "21.1");
        rmap.set("string-d19", "13:05:34");
        rmap.set("string-d20", "12:05");
        rmap.set("string-d21", "14.1.8");
        rmap.set("string-d22", "14.10.18");
        rmap.set("string-d23", "2015年6月12日");
        rmap.set("string-d24", "2015年6月");
        rmap.set("string-d25", "2015年");
	}

	@Test
	public void testRMap() {
		RMap m = new RMap();
		Assert.assertEquals(0, m.size());
	}

	@Test
	public void testRMapInt() {
		RMap m = new RMap(5);
		Assert.assertEquals(0, m.size());
	}

	@Test
	public void testRMapIntFloat() {
		RMap m = new RMap(5, 0.75f);
		Assert.assertEquals(0, m.size());
	}

	@Test
	public void testRMapMapOfKV() {
		RMap m = new RMap(new HashMap());
		Assert.assertEquals(0, m.size());
	}

	@Test
	public void testGetStringStringBoolean() {
		Assert.assertEquals(null, rmap.getString("string-empty", true));
		Assert.assertEquals("", rmap.getString("string-empty", false));
	}

	@Test
	public void testGetStringString() {
		Assert.assertEquals(null, rmap.getString("xxxxx"));
		Assert.assertEquals(null, rmap.getString("string-null"));
		Assert.assertEquals("", rmap.getString("string-empty"));
		
		Assert.assertEquals("1", rmap.getString("int"));
		Assert.assertEquals("1", rmap.getString("long"));
		Assert.assertEquals("1", rmap.getString("short"));
		Assert.assertEquals("1.0", rmap.getString("double"));
		Assert.assertEquals("1.0", rmap.getString("float"));
		Assert.assertEquals("1", rmap.getString("char"));
		Assert.assertEquals("1", rmap.getString("byte"));
		Assert.assertEquals("true", rmap.getString("boolean"));
		Assert.assertEquals("1", rmap.getString("decimal"));
		Assert.assertEquals("1", rmap.getString("object"));
		
		
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-string"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-int"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-short"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-long"));
		Assert.assertEquals("[1.0, 2.0, 3.0]", rmap.getString("array-float"));
		Assert.assertEquals("[1.0, 2.0, 3.0]", rmap.getString("array-double"));
		Assert.assertEquals("[true, false, true]", rmap.getString("array-boolean"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-char"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-byte"));
		Assert.assertEquals("[1, 2, 3]", rmap.getString("array-object"));
	}

	@Test
	public void testGetStringValue() {
		Assert.assertEquals(null, rmap.getStringValue(null));
		Assert.assertEquals("", rmap.getStringValue(""));
		Assert.assertEquals(1, rmap.getStringValue(1));
		Assert.assertEquals("[1, 2, 3]", rmap.getStringValue(new int[]{1,2,3}));
	}

	@Test
	public void testGetBoolean() {
		Assert.assertEquals(false, rmap.getBoolean(""));
		Assert.assertEquals(false, rmap.getBoolean("string-null"));
		Assert.assertEquals(false, rmap.getBoolean("string-empty"));
		Assert.assertEquals(true, rmap.getBoolean("boolean"));
		Assert.assertEquals(false, rmap.getBoolean("decimal"));
	}
	
	@Test
	public void testGetInt() {
		Assert.assertEquals(0, rmap.getInt("string-n1"));
		Assert.assertEquals(1, rmap.getInt("string-n2"));
		Assert.assertEquals(-1, rmap.getInt("string-n3"));
		try{
			rmap.getInt("string-n4"); //1L
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("Couldn't convert"));
		}
		
		try{
			rmap.getInt("string-n5");//0.234935
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		try{
			rmap.getInt("string-n6");//0.234935
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		
		Assert.assertEquals(1, rmap.getInt("int"));
		Assert.assertEquals(1, rmap.getInt("long"));
		Assert.assertEquals(1, rmap.getInt("double"));
		Assert.assertEquals(1, rmap.getInt("float"));
		Assert.assertEquals(1, rmap.getInt("short"));
		Assert.assertEquals(1, rmap.getInt("char"));
		Assert.assertEquals(1, rmap.getInt("byte"));
		
		try{
			rmap.getInt("boolean");//true
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		Assert.assertEquals(1, rmap.getInt("decimal"));
		Assert.assertEquals(1, rmap.getInt("object"));
	}

	@Test
	public void testParseInt() {
		Assert.assertEquals(1, rmap.parseInt("1"));
		Assert.assertEquals(-1, rmap.parseInt("-1"));
		Assert.assertEquals(0, rmap.parseInt("0"));
		
		try{
			rmap.parseInt("1.2");
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
	}

	@Test
	public void testGetLong() {
		Assert.assertEquals(0, rmap.getLong("string-n1"));
		Assert.assertEquals(1, rmap.getLong("string-n2"));
		Assert.assertEquals(-1, rmap.getLong("string-n3"));
		try{
			rmap.getLong("string-n4"); //1L
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("Couldn't convert"));
		}
		
		try{
			rmap.getLong("string-n5");//0.234935
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		try{
			rmap.getLong("string-n6");//0.234935
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		Assert.assertEquals(1, rmap.getLong("int"));
		Assert.assertEquals(1, rmap.getLong("long"));
		Assert.assertEquals(1, rmap.getLong("double"));
		Assert.assertEquals(1, rmap.getLong("float"));
		Assert.assertEquals(1, rmap.getLong("short"));
		Assert.assertEquals(1, rmap.getLong("char"));
		Assert.assertEquals(1, rmap.getLong("byte"));
		
		try{
			rmap.getLong("boolean");//true
		}catch(NumberFormatException e){
			Assert.assertEquals(true, e.getMessage().contains("For input"));
		}
		
		Assert.assertEquals(1, rmap.getLong("decimal"));
		Assert.assertEquals(1, rmap.getLong("object"));
	}

	@Test
	public void testParseLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBigDecimal() {
		fail("Not yet implemented");
	}

	@Test
	public void testParseBigDecimal() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDateString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDateForSql() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTimestamp() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDateObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddDateFormat() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLongArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFloatArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDateArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAll() {
		fail("Not yet implemented");
	}

}
