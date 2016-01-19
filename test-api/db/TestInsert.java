package db;

import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class TestInsert extends Base{
	
	//--------------
	public static void main(String[] args) throws Exception{
		TestInsert insert = new TestInsert();
		insert.executeSql();
		insert.executePreparedSqlWithPs();
		insert.executePreparedSqlWithObjects();
		insert.executeELSqlWithBean();
		insert.executeELSqlWithMap();
	}
	
	/**
	 * Generating the primary key of the student's information needs to be realized by each database.
	 * 生成学生信息主键，需要各数据库自行实现
	 * @return Student id 学生序号
	 */
	private Integer generateStudentId() throws DBException{
		String dbName = DB.getDialect().getName();
		if("MYSQL".equals(dbName)){
			return null;
		}else if("ORACLE".equals(dbName)){
			String sql = "SELECT SQ_STUDENT_ID.NEXTVAL AS ID FROM DUAL";
			return DB.getMap(sql).getInt("id");
		}
		return null;
	}
	
	/**
	 * SQL with no prepared parameters
	 * 执行不带预编译参数的SQL
	 * @return Number of affected records 受影响的记录数
	 */
	public int executeSql() throws Exception{
		String sql =  null;
		String dbName = DB.getDialect().getName();
		
		//MYSQL
		if("MYSQL".equals(dbName)){
			sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
					+ "VALUES ("+(generateStudentId())+", 'Jim', 1, '1990-01-01', '01:01:01', '2000-01-01 01:01:01', 10000, null, null, 0)";
			
		//ORACLE
		}else if("ORACLE".equals(dbName)){
			sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
					+ "VALUES ("+(generateStudentId())+", 'Jim', 1, to_date('1990-01-01','YYYY-MM-DD'), to_date('01:01:01','HH24:MI:SS'), "
							+ "to_date('2000-01-01 01:01:01','YYYY-MM-DD HH24:MI:SS'), 10000, null, null, 0)";
		}
		
		if(sql == null)
			throw new Exception("database not support.");
		
		return DB.update(sql);
	}

	/**
	 * org.rex.db.Ps encapsulates prepared parameters
	 * 使用org.rex.db.Ps封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executePreparedSqlWithPs() throws Exception{
		String sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		Ps ps = new Ps(generateStudentId(), "Jim", 1, getBirthday(), getBirthTime(), getEnrollmentTime(), 10000, null, null, 0);
		
	//	Or define Ps like this 
	//	或者这样定义Ps
	/*	Ps ps = new Ps();
		ps.add(generateStudentId());
		ps.add("Jim");
		ps.add(1);
		ps.add(getBirthday());
		ps.add(getBirthTime());
		ps.add(getEnrollmentTime());
		ps.add(10000);
		ps.addNull();
		ps.addNull();
		ps.add(0);
	*/
		
	//	Or like this 
	//	这样也可以
	/*	Ps ps = new Ps();
		ps.set(1, generateStudentId());
		ps.set(2, "Jim");
		ps.set(3, 1);
		ps.set(4, getBirthday());
		ps.set(5, getBirthTime());
		ps.set(6, getEnrollmentTime());
		ps.set(7, 10000);
		ps.setNull(8);
		ps.setNull(9);
		ps.set(10);
	*/	
		
		return DB.update(sql, ps);
	}
	
	/**
	 * Object[] encapsulates prepared parameters
	 * 使用Object[]封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executePreparedSqlWithObjects() throws Exception{
		String sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		Object[] ps = new Object[]{generateStudentId(), "Jim", 1, getBirthday(), 
				getBirthTime(), getEnrollmentTime(), 10000, null, null, 0};
		return DB.update(sql, ps);
	}
	
	/**
	 * Java Bean encapsulates prepared parameters
	 * 使用Java Bean封装预编译参数
	 * @return Number of affected records 受影响的记录数
	 */
	public int executeELSqlWithBean() throws Exception{
		String sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
				+ "VALUES (#{studentId}, #{name}, #{sex}, #{birthday}, #{birthTime}, #{enrollmentTime}, #{major}, #{photo}, #{remark}, #{readonly})";

		Student student = new Student();
		student.setStudentId(generateStudentId());
		student.setName("Jim");
		student.setSex(1);
		student.setBirthday(getBirthday());
		student.setBirthTime(getBirthTime());
		student.setEnrollmentTime(getEnrollmentTime());
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
		String sql = "INSERT INTO R_STUDENT(STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY) "
				+ "VALUES (#{studentId}, #{name}, #{sex}, #{birthday}, #{birthTime}, #{enrollmentTime}, #{major}, #{photo}, #{remark}, #{readonly})";

		Map<String, Object> student = new HashMap<String, Object>();
		student.put("studentId", generateStudentId());
		student.put("name", "Jim");
		student.put("sex", 1);
		student.put("birthday", getBirthday());
		student.put("birthTime", getBirthTime());
		student.put("enrollmentTime", getEnrollmentTime());
		student.put("major", 10000);
		student.put("readonly", 0);
		return DB.update(sql, student);
	}
	
}
