package db.mysql;

import org.rex.DB;
import org.rex.db.Ps;

import db.TestBase;

public class TestTransaction extends TestBase {

	// --------------
	public static void main(String[] args) throws Exception {
		TestTransaction transaction = new TestTransaction();
		transaction.execute();
	}

	public void execute() throws Exception {

		DB.beginTransaction();
		try {
			String sql1 = "INSERT INTO r_student(name, sex, birthday, birth_time, major, photo, remark) VALUES ('Jim', 1, '1990-01-01', '01:01:01', 10000, null, null)";
			DB.update(sql1);

			String sql2 = "INSERT INTO r_student(name, sex, birthday, birth_time, major, photo, remark) VALUES (?,?,?,?,?,?,?)";
			Ps ps = new Ps("Jim", 1, getBirthday(), getBirthTime(), 10000, null, null);
			DB.update(sql2, ps);
			
			DB.commit();
		} catch (Exception e) {
			DB.rollback();
			throw e;
		}
	}
}
