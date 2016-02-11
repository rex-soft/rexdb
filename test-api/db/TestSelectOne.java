package db;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;
import org.rex.db.logger.Logger;
import org.rex.db.logger.LoggerFactory;

public class TestSelectOne extends Base{
	
	static Logger logger = LoggerFactory.getLogger(TestSelectOne.class);

	//--------------
	public static void main(String[] args) throws Exception{
		logger.info("starting");
		TestSelectOne selectOne = new TestSelectOne();
		selectOne.executeSql();
		selectOne.executePreparedSql();
		selectOne.executePreparedELSql();
		logger.info("end");
	}
	
	//--------------
	/**
	 * 执行普通SQL
	 */
	public void executeSql() throws DBException{
		String sql = "select * from r_student limit 1";
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result1 = DB.getMap(sql);
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, Student.class);
		System.out.println(result2);
	}
	
	/**
	 * 执行预编译SQL，接受org.rex.db.Ps、或者Object[]作为预编译参数载体
	 */
	public void executePreparedSql() throws DBException{
		String sql = "select * from r_student where student_id = ?";
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result1 = DB.getMap(sql, new Ps(1));
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, new Ps(1), Student.class);
		System.out.println(result2);
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
		RMap result1 = DB.getMap(sql, paramMap);
		System.out.println(result1);
		
		//查询结果封装为Bean
		Student result2 = DB.get(sql, paramMap, Student.class);
		System.out.println(result2);
		
		//---------使用Java对象传递参数
		Student paramBean = new Student();
		paramBean.setStudentId(1);
		
		//查询结果封装为org.rex.WMap，列下标转换为java风格
		RMap result5 = DB.getMap(sql, paramBean);
		System.out.println(result5);
		
		//查询结果封装为Bean
		Student result6 = DB.get(sql, paramBean, Student.class);
		System.out.println(result6);
	}
}
