package org.rex;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rex.db.Ps;

public class PerformanceTest {

	@Test
	public void test() {
		fail("Not yet implemented");
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
		
		return DB.update(sql, ps);
	}

}
