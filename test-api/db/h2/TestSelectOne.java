package db.h2;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.WMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.Student;
import db.TestBase;

public class TestSelectOne extends TestBase{

	//--------------
	public static void main(String[] args) throws Exception{
		TestSelectOne selectOne = new TestSelectOne();
		selectOne.executeSql();
		selectOne.executePreparedSql();
		selectOne.executePreparedELSql();
	}
	
	//--------------
	/**
	 * 执行普通SQL
	 */
	public void executeSql() throws DBException{
		String sql = "select * from r_student limit 1";
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		WMap result1 = DB.getMap(sql);
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, Student.class);
		System.out.println(result2);
		
		//为已经创建好的对象赋值
		Student result3 = DB.get(sql, new Student());
		System.out.println(result3);
	}
	
	/**
	 * 执行预编译SQL，接受org.rex.db.Ps、或者Object[]作为预编译参数载体
	 */
	public void executePreparedSql() throws DBException{
		String sql = "select * from r_student where student_id = ?";
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		WMap result1 = DB.getMap(sql, new Ps(1));
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, new Ps(1), Student.class);
		System.out.println(result2);
		
		//为已经创建好的对象赋值
		Student result3 = DB.get(sql, new Ps(1), new Student());
		System.out.println(result3);
	}
	
	/**
	 * 执行带有表达式的预编译SQL，支持java.util.Map，或者Pojo对象
	 */
	public void executePreparedELSql() throws DBException, ParseException{
		String sql = "select * from r_student where student_id = #{studentId}";
		
		//---------使用Map对象传递参数
		Map paramMap = new HashMap();
		paramMap.put("studentId", 1);
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		WMap result1 = DB.getMap(sql, paramMap);
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, paramMap, Student.class);
		System.out.println(result2);
		
		//为已经创建好的对象赋值
		Student result3 = DB.get(sql, paramMap, new Student());
		System.out.println(result3);
		
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(1);
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		WMap result5 = DB.getMap(sql, paramBean);
		System.out.println(result5);
		
		//查询结果封装为Bean
		Student result6 = DB.get(sql, paramBean, Student.class);
		System.out.println(result6);
		
		//为已经创建好的对象赋值
		Student result7 = DB.get(sql, paramBean, new Student());
		System.out.println(result7);
		
	}
}
