package advisor.util;

import java.util.ArrayList;

public class Level {

	String clasCode;
	String clasDesc;
	ArrayList studentList = new ArrayList();
	
	public String getClasCode() {
		return clasCode;
	}
	public void setClasCode(String clasCode) {
		this.clasCode = clasCode;
	}
	public String getClasDesc() {
		return clasDesc;
	}
	public void setClasDesc(String clasDesc) {
		this.clasDesc = clasDesc;
	}
	public void setStudent(Student s) {
		
		studentList.add(s);
	}
}
