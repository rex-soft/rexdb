package db.mysql;

import java.util.List;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.exception.DBException;

public class TestSelect {

	public static void main(String[] args) throws DBException {
		TestSelect testSelect = new TestSelect();
		testSelect.getMapList();
		testSelect.getMap();
	}
	
	public List<RMap> getMapList() throws DBException{
		String sql = "select * from r_student limit 10";
		
		// 查询结果封装为List<WMap>，列下标转换为java风格
		List<RMap> result = DB.getMapList(sql);
		System.out.println(result);
		return result;
	}
	
	public RMap getMap() throws DBException{
		String sql = "select * from r_student limit 1";

		// 查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result = DB.getMap(sql);
		System.out.println(result);
		return result;
	}
	
}
