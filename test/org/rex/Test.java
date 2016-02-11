package org.rex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rex.db.exception.DBException;

import db.Student;

public class Test {

	public static void main(String[] args) throws DBException {
		Student s = DB.get("SELECT * FROM r_student limit ?", new Object[]{1}, Student.class);
		System.out.println(s);
	}
	
	static void s(Object... o){
		List<Map> m = new ArrayList<Map>();
	}
	
	static void s(Object o){
	}
}
