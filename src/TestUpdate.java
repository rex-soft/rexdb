import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

public class TestUpdate {
	public static void main1(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
		int i = DB.update(sql, new Object[] { 1, "test", new Date() });
		System.out.println(i + " row inserted.");
	}

	public static void main2(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (?, ?, ?)";
		int i = DB.update(sql, new Ps(1, "test", new Date()));
		System.out.println(i + " row inserted.");
	}

	public static void main3(String[] args) throws DBException {
		String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";

		Map prameters = new HashMap();
		prameters.put("id", 1);
		prameters.put("name", "test");
		prameters.put("createTime", new Date());

		int i = DB.update(sql, prameters);
		System.out.println(i + " row inserted.");
	}

	public static void main(String[] args) throws DBException {
	String sql = "INSERT INTO REX_TEST(ID, NAME, CREATE_TIME) VALUES (#{id}, #{name}, #{createTime})";

	RexTest rexTest = new RexTest(1, "test", new Date());
	int i = DB.update(sql, rexTest);
		System.out.println(i + " row inserted.");
	}
	

}
