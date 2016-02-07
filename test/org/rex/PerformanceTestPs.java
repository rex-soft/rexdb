package org.rex;

import java.util.Date;

import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class PerformanceTestPs {
	
	static String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
			+ " VALUES (?,?,?,?,?,?,?,?,?)";

	
	public static void main(String[] args) throws DBException {
		
		Ps[] stu = makePs(1000);
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
	
	public static long testPer(Ps[] stu) throws DBException{
		long s = System.currentTimeMillis();
		DB.beginTransaction();
		for (int i = 0; i < stu.length; i++) {
			DB.update(sql, stu[i]);
		}
		DB.commit();
		return System.currentTimeMillis() - s;
		
	}
	
	private static Ps[] makePs(int n){
		Ps pss[] = new Ps[n];
		for (int j = 0; j < n; j++) {
			Ps ps = new Ps("Jim", 1, new Date(), new Date(), new Date(), 10000, null, null, 0);
			pss[j] = ps;
		}
		
		return pss;
	}
}
