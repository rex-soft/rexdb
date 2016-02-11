package db.mysql;

import java.util.List;

import org.rex.DB;
import org.rex.RMap;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.Student;

public class TestSelectByPs {

	public static void main(String[] args) throws DBException {
		TestSelectByPs testSelectByPs = new TestSelectByPs();
		testSelectByPs.getMapList();
		testSelectByPs.getBeanList();
		testSelectByPs.getMap();
		testSelectByPs.getBeanByClass();
	}

	public List<RMap> getMapList() throws DBException{
		String sql = "select * from r_student where student_id < ?";
		//查询结果封装为List<RMap>，列下标转换为java风格
		List<RMap> result = DB.getMapList(sql, new Ps(10));
		System.out.println(result);
		return result;
	}
	
	public List<Student> getBeanList() throws DBException{
		String sql = "select * from r_student where student_id < ?";
		//查询结果封装为List<T>
		List<Student> result = DB.getList(sql, new Ps(10), Student.class);
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
	
	public Student getBeanByClass() throws DBException{
		String sql = "select * from r_student where student_id = ?";
		//为已经创建好的对象赋值
		Student result = DB.get(sql, new Ps(1), Student.class);
		System.out.println(result);
		return result;
	}
}
