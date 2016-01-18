package db.mysql;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.rex.DB;
import org.rex.db.Ps;
import org.rex.db.exception.DBException;

import db.mysql.entitry.Student;

/**
 * 增删改表
 */
public class TestUpdate extends Base{
	
	//--------------
	public static void main(String[] args) throws Exception{
		TestUpdate insert = new TestUpdate();
		insert.executeSql();
		insert.executePreparedSql();
		insert.executePreparedELSql();
	}
	
	/**
	 * 执行普通SQL
	 */
	public void executeSql() throws Exception{
		String sql = "INSERT INTO r_student(name, sex, birthday, birth_time, major, photo, remark) VALUES ('Jim', 1, '1990-01-01', '01:01:01', 10000, null, null)";
		DB.update(sql);
	}

	/**
	 * 执行预编译SQL，接受org.rex.db.Ps、或者Object[]作为预编译参数载体
	 */
	public void executePreparedSql() throws Exception{
		String sql = "INSERT INTO r_student(name, sex, birthday, birth_time, major, photo, remark) VALUES (?,?,?,?,?,?,?)";
		
		//使用带参数的构造函数创建一个Ps对象
		Ps ps1 = new Ps("Jim", 1, getBirthday(), getBirthTime(), 10000, null, null);
		DB.update(sql, ps1);
		
		//使用add(?)方法按顺序为Ps赋值
		Ps ps2 = new Ps();
		ps2.add("Jim");
		ps2.add(1);
		ps2.add(getBirthday());
		ps2.add(getBirthTime());
		ps2.add(10000);
		ps2.addNull();
		ps2.addNull();
		DB.update(sql, ps2);
		
		//使用set()方法按顺序为Ps赋值，下标从1开始
		Ps ps3 = new Ps();
		ps3.set(1, "Jim");
		ps3.set(2, 1);
		ps3.set(3, getBirthday());
		ps3.set(4, getBirthTime());
		ps3.set(5, 10000);
		ps3.setNull(6);
		ps3.setNull(7);
		DB.update(sql, ps3);
		
		//使用数组
		Object[] ps4 = new Object[]{"Jim", 1, getBirthday(), getBirthTime(), 10000, null, null};
		DB.update(sql, ps4);
	}
	
	/**
	 * 执行带有表达式的预编译SQL，支持java.util.Map，或者Pojo对象
	 */
	public void executePreparedELSql() throws DBException, ParseException{
		String sql = "INSERT INTO r_student(name, sex, birthday, birth_time, major, photo, remark) VALUES (#{name}, #{sex}, #{birthday}, #{birthTime}, #{major}, #{photo}, #{remark})";
		
		//使用Map对象传递参数
		Map student1 = new HashMap();
		student1.put("name", "Jim");
		student1.put("sex", 1);
		student1.put("birthday", getBirthday());
		student1.put("birthTime", getBirthTime());
		student1.put("major", 10000);
		DB.update(sql, student1);
		
		//使用Pojo对象传递参数
		Student student2 = new Student();
		student2.setName("Jim");
		student2.setSex(1);
		student2.setBirthday(getBirthday());
		student2.setBirthTime(getBirthTime());
		student2.setMajor(10000);
		DB.update(sql, student2);
	}
	
}
