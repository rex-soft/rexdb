package db;

import java.util.Arrays;
import java.util.Date;

/**
 * 学生信息
 */
public class Student {
	
	private Integer studentId;//主键被设置成了Integer类型，当 
	private String name;
	private int sex;
	private Date birthday;
	private Date birthTime;
	private Date enrollmentTime;
	private int major;
	private byte[] photo;
	private String remark;
	private int readonly;
	
	public Student(){
	}

	public Student(int studentId, String name, int sex, Date birthday, Date birthTime, Date enrollmentTime, int major, byte[] photo, String remark,
			int readonly) {
		this.studentId = studentId;
		this.name = name;
		this.sex = sex;
		this.birthday = birthday;
		this.birthTime = birthTime;
		this.enrollmentTime = enrollmentTime;
		this.major = major;
		this.photo = photo;
		this.remark = remark;
		this.readonly = readonly;
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

	public Date getEnrollmentTime() {
		return enrollmentTime;
	}

	public void setEnrollmentTime(Date enrollmentTime) {
		this.enrollmentTime = enrollmentTime;
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

	public int getReadonly() {
		return readonly;
	}

	public void setReadonly(int readonly) {
		this.readonly = readonly;
	}

	public String toString() {
		return "Student [studentId=" + studentId + ", name=" + name + ", sex=" + sex + ", birthday=" + birthday + ", birthTime=" + birthTime
				+ ", enrollmentTime=" + enrollmentTime + ", major=" + major + ", photo=" + Arrays.toString(photo) + ", remark=" + remark
				+ ", readonly=" + readonly + "]";
	}
	
}
