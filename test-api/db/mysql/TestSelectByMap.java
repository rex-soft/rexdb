package db.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.exception.DBException;

import db.Student;

public class TestSelectByMap {

	public static void main(String[] args) throws DBException {
		TestSelectByMap testSelectByMap = new TestSelectByMap();
		testSelectByMap.getMapList();
		testSelectByMap.getBeanListByClass();
		testSelectByMap.getMap();
		testSelectByMap.getMapByClass();
	}

	public List<RMap> getMapList() throws DBException {
		String sql = "select * from r_student where student_id < #{studentId}";

		// ---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 10);

		// 查询结果封装为org.rex.WMap，列下标转换为java风格
		List<RMap> result = DB.getMapList(sql, paramMap);
		System.out.println(result);
		return result;
	}

	public List<Student> getBeanListByClass() throws DBException {
		String sql = "select * from r_student where student_id < #{studentId}";
		// ---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 10);
		// 查询结果封装为Bean
		List<Student> result = DB.getList(sql, paramMap, Student.class);
		System.out.println(result);
		return result;
	}
	
	public RMap getMap() throws DBException{
		String sql = "select * from r_student where student_id = #{studentId}";
		// ---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 1);
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result = DB.getMap(sql, paramMap);
		System.out.println(result);
		return result;
	}
	
	public Student getMapByClass() throws DBException{
		String sql = "select * from r_student where student_id = #{studentId}";
		// ---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 1);
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		//为已经创建好的对象赋值
		Student result = DB.get(sql, paramMap, Student.class);
		System.out.println(result);
		return result;
	}
	
}
