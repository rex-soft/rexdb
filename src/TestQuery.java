import java.util.List;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.configuration.Configuration;
import org.rex.db.exception.DBException;

public class TestQuery {
	public static void main1(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST";
		List<RMap> list = DB.getMapList(sql);
		System.out.println(list);
	}

	public static void main2(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST";
		List<RexTest> list = DB.getList(sql, RexTest.class);
		System.out.println(list);
	}

	public static void main3(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST";
		List<RexTest> list = DB.getList(sql, RexTest.class, 1, 1);
		System.out.println(list);
	}

	public static void main4(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST limit 1";
		RexTest rexTest = DB.get(sql, RexTest.class);
		System.out.println(rexTest);
	}

	public static void main5(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST limit 1";
		RMap rexTest = DB.getMap(sql);
		System.out.println(rexTest);
	}

	public static void main6(String[] args) throws DBException {
		String sql = "SELECT count(*) as COUNT FROM REX_TEST";
		int count = DB.getMap(sql).getInt("count");
		System.out.println(count);
	}

	public static void main7(String[] args) throws DBException {
		String sql = "SELECT * FROM REX_TEST limit ?";
		RexTest rexTest = DB.get(sql, new Object[] { 1 }, RexTest.class);
		System.out.println(rexTest);
	}

	public static void main(String[] args) throws DBException {
		String sql = "SELECT 1 FROM DUAL";
		RMap map = DB.getMap(sql);
		System.out.println(map);
	}
}
