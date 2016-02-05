package db.mysql;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.Student;

public class TestSelectPagedList{

	//--------------
	public static void main(String[] args) throws Exception{
		TestSelectPagedList selectPagedList = new TestSelectPagedList();
		selectPagedList.executeSql();
		selectPagedList.executePreparedSql();
		selectPagedList.executePreparedELSql();
	}
	
	//--------------
	/**
	 * 执行普通SQL
	 */
	public void executeSql() throws DBException{
		String sql = "select * from r_student";
		
		//查询结果封装为List<WMap>，列下标转换为java风格
		List<RMap> result1 = DB.getMapList(sql, 0, 10);
		System.out.println(result1);
		
		//查询结果封装为List<T>
		List<Student> result2 = DB.getList(sql, Student.class, 0, 10);
		System.out.println(result2);
		
	}
	
	/**
	 * 执行预编译SQL，接受org.rex.db.Ps、或者Object[]作为预编译参数载体
	 */
	public void executePreparedSql() throws DBException{
		String sql = "select * from r_student where student_id < ?";
		
		//查询结果封装为List<WMap>，列下标转换为java风格
		List<RMap> result1 = DB.getMapList(sql, new Ps(10), 0, 10);
		System.out.println(result1);
		
		//查询结果封装为List<T>
		List<Student> result2 = DB.getList(sql, new Ps(10), Student.class, 0, 10);
		System.out.println(result2);
	}
	
	/**
	 * 执行带有表达式的预编译SQL，支持java.util.Map，或者Pojo对象
	 */
	public void executePreparedELSql() throws DBException, ParseException{
		String sql = "select * from r_student where student_id < #{studentId}";
		
		//---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 10);
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		List<RMap> result1 = DB.getMapList(sql, paramMap, 0, 10);
		System.out.println(result1);
		
		//查询结果封装为Bean
		List<Student> result2 = DB.getList(sql, paramMap, Student.class, 0, 10);
		System.out.println(result2);
		
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(10);
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		List<RMap> result4 = DB.getMapList(sql, paramBean, 0, 10);
		System.out.println(result4);
		
		//查询结果封装为Bean
		List<Student> result5 = DB.getList(sql, paramBean, Student.class, 0, 10);
		System.out.println(result5);
	}
}
