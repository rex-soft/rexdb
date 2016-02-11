package org.rex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.dbcp.BasicDataSource;

public class TestJDBCPerformance {

	static String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) VALUES (?,?,?,?,?,?,?,?,?)";

	static BasicDataSource bds = null;

	static{
		// <dataSource class="org.apache.commons.dbcp.BasicDataSource">
		// <property name="driverClassName" value="com.mysql.jdbc.Driver" />
		// <property name="url" value="jdbc:mysql://localhost:3306/rexdb" />
		// <property name="username" value="root" />
		// <property name="password" value="12345678" />
		// </dataSource>
		bds = new BasicDataSource();
		bds.setDriverClassName("org.apache.commons.dbcp.BasicDataSource");
		bds.setUrl("jdbc:mysql://localhost:3306/rexdb");
		bds.setUsername("root");
		bds.setPassword("12345678");
	}
	
	public static void main(String[] args) throws SQLException {
		testPer(100);
		
		long count=0, n=0;
		for (int i = 0; i <20; i++) {
			long time = testPer(100);
			System.out.println(time);
			n=i+1;
			count +=time;
		}
		System.out.println("avg: "+(count/n));
	}

	
	public static long testPer(int n) throws SQLException {
		
		//conn.setAutoCommit(false);
		long s = System.currentTimeMillis();
		
		for (int i = 0; i < n; i++) {
			Connection conn = bds.getConnection();
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, "jim");
				ps.setObject(2, 1);
				ps.setObject(3, new Timestamp(System.currentTimeMillis()));
				ps.setObject(4, new Timestamp(System.currentTimeMillis()));
				ps.setObject(5, new Timestamp(System.currentTimeMillis()));
				ps.setObject(6, 1);
				ps.setObject(7, null);
				ps.setObject(8, null);
				ps.setObject(9, 1);
				ps.executeUpdate();
				ps.close();
				conn.close();
			} catch (SQLException e) {

				System.out.println("SQLException;" + e.getMessage());
			}
		}
		
		
		
		//conn.commit();
		
		
		return (System.currentTimeMillis() - s);
	}
}
