package db.mysql.entitry;

import java.util.Arrays;
import java.util.Date;

public class Student {
	
	private int studentId;
	private String name;
	private int sex;
	private Date birthday;
	private Date birthTime;
	private int major;
	private byte[] photo;
	private String remark;
	
	public Student(){
	}
	
	public Student(int studentId, String name, int sex, Date birthday, Date birthTime, int major, byte[] photo, String remark) {
		super();
		this.studentId = studentId;
		this.name = name;
		this.sex = sex;
		this.birthday = birthday;
		this.birthTime = birthTime;
		this.major = major;
		this.photo = photo;
		this.remark = remark;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getBirthTime() {
		return birthTime;
	}

	public void setBirthTime(Date birthTime) {
		this.birthTime = birthTime;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String toString() {
		return "Student [studentId=" + studentId + ", name=" + name + ", sex=" + sex + ", birthday=" + birthday + ", birthTime=" + birthTime
				+ ", major=" + major + ", photo=" + Arrays.toString(photo) + ", remark=" + remark + "]";
	}
	
}
