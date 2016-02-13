package org.rex.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.dbcp.BasicDataSource;
import org.rex.DB;
import org.rex.db.exception.DBException;

/**
 * test update api using org.rex.db.Ps as parameters
 */
public class UpdateByPsPerformance {

	static final String INSERT = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) VALUES (?,?,?,?,?,?,?,?,?)";

	final static String CLEAN ="DELETE FROM R_STUDENT";
	
	//----main
	/**
	 * Run the test
	 */
	public static void main(String[] args) throws DBException, SQLException {
		run(100, 50);
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
		Ps[] stu = makeStudents(rowNumber);
		
		warmUp();
		
		
		System.out.println("starting running test, each rowNumber: " + rowNumber + ", loops: " + loops + ", unit: milliseconds");
		for (int i = 0; i < loops; i++) {
			long rexCosts, jdbcCosts;
			
			//1.rexdb
			long start = System.currentTimeMillis();
			rexdbUpdateByBean(stu);
			rexCosts = System.currentTimeMillis() - start;
			
			//2.jdbc
			start = System.currentTimeMillis();
			jdbcUpdate(rowNumber);
			jdbcCosts = System.currentTimeMillis() - start;
			
			//3.count
			rexCount += rexCosts;
			jdbcCount += jdbcCosts;
			
			System.out.println("[loop " + (i + 1) + "] rexdb: " + rexCosts + ", jdbc: " + jdbcCosts);
		}

		System.out.println("=========[average] rexdb: " + (rexCount/loops) + ", jdbc: " + (jdbcCount/loops) + "=========");
		
		cleanAll();
	}
	
	static void warmUp() throws DBException, SQLException{
		System.out.println("preparing test...do some operation.");
		rexdbUpdateByBean(makeStudents(100));
		jdbcUpdate(100);
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
	public static void rexdbUpdateByBean(Ps[] stu) throws DBException{
		for (int i = 0; i < stu.length; i++) {
			DB.update(INSERT, stu[i]);
		}
	}
	
	private static Ps[] makeStudents(int i){
		Ps[] pss = new Ps[i];
		for (int j = 0; j < i; j++) {
			Ps ps = new Ps("Jim", 1, new Date(), new Date(), new Date(), 10, null, null, 1);
			pss[j] = ps;
		}
		return pss;
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
	
	public static void jdbcUpdate(int n) throws SQLException {
		for (int i = 0; i < n; i++) {
			Connection conn = bds.getConnection();
			try {
				PreparedStatement ps = conn.prepareStatement(INSERT);
				ps.setString(1, "jim");
				ps.setObject(2, 1);
				ps.setObject(3, new Timestamp(System.currentTimeMillis()));
				ps.setObject(4, new Timestamp(System.currentTimeMillis()));
				ps.setObject(5, new Timestamp(System.currentTimeMillis()));
				ps.setObject(6, 10);
				ps.setObject(7, null);
				ps.setObject(8, null);
				ps.setObject(9, 1);
				ps.executeUpdate();
				ps.close();
				
			}finally{
				conn.close();
			}
		}
	}
}
