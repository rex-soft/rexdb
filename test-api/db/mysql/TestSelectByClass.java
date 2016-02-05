package db.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.Student;

public class TestSelectByClass {

	public static void main(String[] args) throws DBException {
		TestSelectByClass testSelectByClass = new TestSelectByClass();
		testSelectByClass.getBeanList();
		testSelectByClass.getBeanListByPs();
		testSelectByClass.getBeanListByMap();
		testSelectByClass.getBeanListByBean();
		testSelectByClass.getMap();
		testSelectByClass.getBeanByPs();
	}
	
	public List<Student> getBeanList() throws DBException{
		String sql = "select * from r_student limit 10";
		//查询结果封装为List<T>
		List<Student> result = DB.getList(sql, Student.class);
		System.out.println(result);
		return result;
	}
	
	public List<Student> getBeanListByPs() throws DBException{
		String sql = "select * from r_student where student_id < ?";
		//查询结果封装为List<T>
		List<Student> result = DB.getList(sql, new Ps(10), Student.class);
		System.out.println(result);
		return result;
	}
	
	public List<Student> getBeanListByMap() throws DBException{
		String sql = "select * from r_student where student_id < #{studentId}";
		//---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 10);
		//查询结果封装为Bean
		List<Student> result = DB.getList(sql, paramMap, Student.class);
		System.out.println(result);
		return result;
	}
	
	public List<Student> getBeanListByBean() throws DBException{
		String sql = "select * from r_student where student_id < #{studentId}";
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(10);
		//查询结果封装为Bean
		List<Student> result = DB.getList(sql, paramBean, Student.class);
		System.out.println(result);
		return result;
	}
	
	public RMap getMap() throws DBException{
		String sql = "select * from r_student where student_id = ?";
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result = DB.getMap(sql, new Ps(1));
		System.out.println(result);
		return result;
	}
	
	public Student getBeanByPs() throws DBException{
		String sql = "select * from r_student where student_id = ?";
		//为已经创建好的对象赋值
		Student result = DB.get(sql, new Ps(1), Student.class);
		System.out.println(result);
		return result;
	}
}
