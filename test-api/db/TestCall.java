package db;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class TestCall extends Base {

	public static void main(String[] args) throws Exception {
		TestCall call = new TestCall();
		call.callIn();
		call.callOut();
		call.callInOut();
		call.callInout();
		call.callInOut();
		call.callReturn();
		call.callReturnRs();
	}

	/**
	 * 输入参数
	 */
	public void callIn() throws DBException {

		// CREATE PROCEDURE proc_in(IN id INT)
		// BEGIN
		// select * from r_student where student_id = id;
		// END

		String sql = "{call proc_in(?)}";

		RMap result = DB.call(sql, new Ps(1));
		System.out.println(result);
	}

	/**
	 * 输出参数
	 */
	public void callOut() throws DBException {

		// CREATE PROCEDURE proc_out(OUT s INT)
		// BEGIN
		// SELECT COUNT(*) INTO s FROM r_student ;
		// END

		String sql = "{call proc_out(?)}";
		Ps ps = new Ps();
		ps.addOutInt();

		// ps.setOutResultSet(0, oracle.jdbc.OracleTypes.CURSOR);
		// ps.setOutResultSet(0, oracle.jdbc.OracleTypes.CURSOR, Student.class);

		RMap result = DB.call(sql, ps);
		System.out.println(result);
	}

	/**
	 * 输入、输出参数同时存在
	 */
	public void callInOut() throws DBException {

		// CREATE PROCEDURE proc_in_out(IN i INT, OUT s INT)
		// BEGIN
		// SELECT COUNT(*) INTO s FROM r_student ;
		// END

		String sql = "{call proc_in_out(?,?)}";

		// 输出参数按照序号命名
		Ps ps1 = new Ps();
		ps1.add(1);
		ps1.addOutInt();
		
		RMap result1 = DB.call(sql, ps1);
		System.out.println(result1);

		// 为输出参数重命名
		Ps ps2 = new Ps();
		ps2.add(1);
		ps2.addOutInt("param-1");

		RMap result2 = DB.call(sql, ps2);
		System.out.println(result2);
	}

	/**
	 * 即是输入参数，也是输出
	 */
	public void callInout() throws DBException {

		// CREATE PROCEDURE proc_inout(INOUT s INT)
		// BEGIN
		// SELECT COUNT(*) INTO s FROM r_student;
		// END

		String sql = "{call proc_inout(?)}";

		// 输出参数按照序号命名
		Ps ps1 = new Ps();
		ps1.addInOut(1);

		RMap result1 = DB.call(sql, ps1);
		System.out.println(result1);

		// 为输出参数重命名
		Ps ps2 = new Ps();
		ps2.addInOut("param-1", 1);

		RMap result2 = DB.call(sql, ps2);
		System.out.println(result2);
	}

	/**
	 * 带有返回值
	 */
	public void callReturn() throws DBException {

		// CREATE PROCEDURE proc_return()
		// BEGIN
		// SELECT count(*) as c FROM r_student ;
		// END

		String sql = "{call proc_return()}";

		RMap result = DB.call(sql);
		System.out.println(result);
	}

	/**
	 * 返回值是ResultSet
	 */
	public void callReturnRs() throws DBException {

		// CREATE PROCEDURE proc_return_rs()
		// BEGIN
		// SELECT * FROM r_student limit 1,10;
		// SELECT * FROM r_student limit 10,10;
		// END

		String sql = "{call proc_return_rs()}";

		RMap result = DB.call(sql);
		System.out.println(result);
	}
}
