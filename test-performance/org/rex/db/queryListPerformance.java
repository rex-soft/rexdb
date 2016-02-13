package org.rex.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.rex.DB;
import org.rex.db.exception.DBException;

import db.Student;

/**
 * test update api using Map as parameters
 */
public class queryListPerformance {
	
	static final String INSERT = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) VALUES (?,?,?,?,?,?,?,?,?)";
	
	static final String QUERY = "SELECT * FROM R_STUDENT";
	
	final static String CLEAN ="DELETE FROM R_STUDENT";
	
	//----main
	/**
	 * Run the test
	 */
	public static void main(String[] args) throws DBException, SQLException {
		run(20000, 50);
	}
	
	/**
	 * Run performance test
	 * @param rowNumber row number that REXDB and JDBC insert each.
	 * @param loops loop count
	 * @throws DBException 
	 * @throws SQLException 
	 */
	public static void run(int rowNumber, int loops) throws DBException, SQLException{
		long rexCount = 0, jdbcCount = 0;
		
		cleanAll();
		warmUp(rowNumber);
		
		System.out.println("starting running test, query rowNumber: " + rowNumber + ", loops: " + loops + ", unit: milliseconds");
		for (int i = 0; i < loops; i++) {
			long rexCosts, jdbcCosts;
			
			//1.rexdb
			long start = System.currentTimeMillis();
			rexdbQuery();
			rexCosts = System.currentTimeMillis() - start;
			
			//2.jdbc
			start = System.currentTimeMillis();
			jdbcQuery();
			jdbcCosts = System.currentTimeMillis() - start;
			
			//3.count
			rexCount += rexCosts;
			jdbcCount += jdbcCosts;
			
			System.out.println("[loop " + (i + 1) + "] rexdb: " + rexCosts + ", jdbc: " + jdbcCosts);
		}

		System.out.println("=========[average] rexdb: " + (rexCount/loops) + ", jdbc: " + (jdbcCount/loops) + "=========");
		
		cleanAll();
	}
	
	static void warmUp(int number) throws DBException, SQLException{
		System.out.println("preparing test...insert "+number+" rows for query.");
		DB.beginTransaction();
		for (int i = 0; i < number; i++) {
			DB.update(INSERT, new Ps("Jim", 1, new Date(), new Date(), new Date(), 10, null, null, 1));
		}
		DB.commit();
		
		rexdbQuery();
		jdbcQuery();
	}
	
	static void cleanAll() throws DBException{
		System.out.println("cleaning up... delete all rows.");
		int r = DB.update(CLEAN);
		System.out.println(r +" rows deleted.");		
	}
	
	//------------------------------------rexdb
	/**
	 * insert students by java beans
	 * @param stu
	 * @throws DBException
	 */
	public static void rexdbQuery() throws DBException{
		List<Student> list = DB.getList(QUERY, Student.class);
//		System.out.println(list.get(0));
	}
	
	//-------------------------------------jdbc
	static BasicDataSource bds = null;
	static{
		bds = new BasicDataSource();
		bds.setDriverClassName("org.apache.commons.dbcp.BasicDataSource");
		bds.setUrl("jdbc:mysql://localhost:3306/rexdb");
		bds.setUsername("root");
		bds.setPassword("12345678");
	}
	
	public static void jdbcQuery() throws SQLException {
		List<Student> list = new ArrayList<Student>();
		Connection conn = null;
		try{
			conn = bds.getConnection();
			PreparedStatement ps = conn.prepareStatement(QUERY);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Student student = new Student();
				student.setStudentId(rs.getInt("STUDENT_ID"));
				student.setName(rs.getString("NAME"));
				student.setSex(rs.getInt("SEX"));
				student.setBirthday(rs.getDate("BIRTHDAY"));
				student.setBirthTime(rs.getTime("BIRTH_TIME"));
				student.setEnrollmentTime(rs.getTimestamp("ENROLLMENT_TIME"));
				student.setMajor(rs.getInt("MAJOR"));
				student.setPhoto(rs.getBytes("PHOTO"));
				student.setRemark(rs.getString("REMARK"));
				student.setReadonly(rs.getInt("READONLY"));
				list.add(student);
			}
		}finally{
			conn.close();
		}
	}
}
