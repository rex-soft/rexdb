package db.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.Student;

public class TestSelectByBean {

	public static void main(String[] args) throws DBException {
		TestSelectByBean testSelectByBean = new TestSelectByBean();
		testSelectByBean.getMapList();
		testSelectByBean.getBeanListByClass();
		testSelectByBean.getMap();
		testSelectByBean.getBeanByClass();
		testSelectByBean.getBeanByMap();
		testSelectByBean.getBeanByPs();
	}
	
	public List<RMap> getMapList() throws DBException{
		String sql = "select * from r_student where student_id < #{studentId}";
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(10);
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		List<RMap> result = DB.getMapList(sql, paramBean);
		System.out.println(result);
		return result;
	}
	
	public List<Student> getBeanListByClass() throws DBException{
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
		String sql = "select * from r_student where student_id = #{studentId}";
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(1);
		//为已经创建好的对象赋值
		RMap result = DB.getMap(sql, paramBean);
		System.out.println(result);
		return result;
	}
	
	public Student getBeanByClass() throws DBException{
		String sql = "select * from r_student where student_id = #{studentId}";
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(1);
		//查询结果封装为Bean
		Student result = DB.get(sql, paramBean, Student.class);
		System.out.println(result);
		return result;
	}
	
	public Student getBeanByMap() throws DBException{
		String sql = "select * from r_student where student_id = #{studentId}";
		//---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 10);
		Student result = DB.get(sql, paramMap, Student.class);
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
