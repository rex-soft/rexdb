package db.mysql;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.db.Ps;

import db.Student;

public class TestInsert{
	
	public static void main(String[] args) throws Exception{
		TestInsert insert = new TestInsert();
		insert.executeSql();
		insert.executePreparedSqlWithPs();
		insert.executePreparedSqlWithObjects();
		insert.executeELSqlWithMap();
		insert.executeELSqlWithBean();
	}
	
	/**
	 * SQL with no prepared parameters
	 * 执行不带预编译参数的SQL
	 * @return Number of affected records 受影响的记录数
	 */
	public int executeSql() throws Exception{
		String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
				+ "VALUES ('Jim', 1, '1990-01-01', '01:01:01', '2000-01-01 01:01:01', 10000, null, null, 0)";

		if (sql == null){
			throw new Exception("database not support.");
		}

		return DB.update(sql);
	}

	/**
	 * org.rex.db.Ps encapsulates prepared parameters
	 * 使用org.rex.db.Ps封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executePreparedSqlWithPs() throws Exception{
		String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		
		Ps ps = new Ps("Jim", 1, new Date(), new Date(), new Date(), 10000, null, null, 0);
		return DB.update(sql, ps);
	}
	
	/**
	 * Object[] encapsulates prepared parameters
	 * 使用Object[]封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executePreparedSqlWithObjects() throws Exception{
		String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
				+ " VALUES (?,?,?,?,?,?,?,?,?)";
		
		Object[] ps = new Object[]{"Jim", 1, new Date(), 
				new Date(), new Date(), 10000, null, null, 0};
		return DB.update(sql, ps);
	}
	
	/**
	 * Java Bean encapsulates prepared parameters
	 * 使用Java Bean封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executeELSqlWithBean() throws Exception{
		String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
				+ "VALUES (#{name}, #{sex}, #{birthday}, #{birthTime}, #{enrollmentTime}, #{major}, #{photo}, #{remark}, #{readonly})";

		Student student = new Student();
		student.setName("Jim");
		student.setSex(1);
		student.setBirthday(new Date());
		student.setBirthTime(new Date());
		student.setEnrollmentTime(new Date());
		student.setMajor(10000);
		student.setReadonly(0);
		return DB.update(sql, student);
	}
	
	/**
	 * Java Bean encapsulates prepared parameters
	 * 使用Java Bean封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executeELSqlWithMap() throws Exception{
		String sql = "INSERT INTO R_STUDENT(NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
				+ "VALUES (#{name}, #{sex}, #{birthday}, #{birthTime}, #{enrollmentTime}, #{major}, #{photo}, #{remark}, #{readonly})";

		Map<String, Object> student = new HashMap<String, Object>();
		student.put("name", "Jim");
		student.put("sex", 1);
		student.put("birthday", new Date());
		student.put("birthTime", new Date());
		student.put("enrollmentTime", new Date());
		student.put("major", 10000);
		student.put("readonly", 0);
		return DB.update(sql, student);
	}
	
}
