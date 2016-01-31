package org.rex.db.listener.impl;

import org.junit.Test;
import org.rex.DB;
import org.rex.db.exception.DBException;

public class TestSqlDebugListener {

	@Test
	public void test() throws DBException {
		
		String sql = "select 1";
		DB.getMap(sql);
		DB.getMapList(sql);
		DB.getMapList(sql, 1, 1);
	}

	
	public static void main(String[] args) throws DBException {
		String sql = "select * from r_student where student_id = ?";
		DB.getMap(sql);
		DB.getMapList(sql);
		DB.getMapList(sql, 1, 1);
		System.out.println("done");
	}
}
