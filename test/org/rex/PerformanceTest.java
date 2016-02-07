package org.rex;

import java.util.Date;

import org.junit.Test;
import org.rex.db.DBUpdate;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.sql.SqlParser;

import db.Student;

public class PerformanceTest {
	
	static String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
			+ " VALUES (#{name},#{sex},#{birthday},#{birthTime},#{enrollmentTime},#{major},#{photo},#{remark},#{readonly})";

	
	public static void main(String[] args) throws DBException {
		
		Student[] stu = makeStudent(1000);
		testPer(stu);
		
		long count=0, n=0;
		for (int i = 0; i < 100; i++) {
			long time = testPer(stu);
			System.out.println(time);
			n=i+1;
			count +=time;
		}
		System.out.println("avg: "+(count/n));
		DB.update("delete from R_STUDENT");
	}
	
	public static long testPer(Student[] stu) throws DBException{
		long s = System.currentTimeMillis();
		DB.beginTransaction();
		for (int i = 0; i < stu.length; i++) {
			DB.update(sql, stu[i]);
		}
		DB.commit();
		return System.currentTimeMillis() - s;
		
//		long s = System.nanoTime();
//		DB.beginTransaction();
//		
//		DBUpdate update = new DBUpdate(DB.getDefaultDataSource(), sql);
//		
//		for (int i = 0; i < stu.length; i++) {
//			update.update(stu[i]);
//		}
//		DB.commit();
//		return (System.nanoTime() - s)/1000;
		
	}
	
	
	public void newUpdate() throws DBException{
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			new DBUpdate(DB.getDefaultDataSource(), sql);
		}
		System.out.println(System.currentTimeMillis() - s);
	}
	
	@Test
	public void parseSql() throws DBException{
		long s = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			SqlParser.parse(sql, makeStudent(1)[0]);
		}
		System.out.println("--->"+(System.currentTimeMillis() - s));
	}
	
	public void validateSql() throws DBException{
		Object[] o =SqlParser.parse(sql, makeStudent(1)[0]);
		
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			SqlParser.validate((String)o[0], (Ps)o[1]);
		}
		System.out.println(System.currentTimeMillis() - s);
	}
	
	private static Student[] makeStudent(int i){
		
		Student[] stu = new Student[i];
		for (int j = 0; j < i; j++) {
		
			Student student = new Student();
			student.setName("Jim");
			student.setSex(1);
			student.setBirthday(new Date());
			student.setBirthTime(new Date());
			student.setEnrollmentTime(new Date());
			student.setMajor(10000);
			student.setReadonly(0);
			
			stu[j] = student;
		}
		
		return stu;
	}
	
}
