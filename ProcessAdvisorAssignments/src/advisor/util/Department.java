package advisor.util;

import java.util.ArrayList;

public class Department {

	String deptCode;
	String deptDesc;
	String term;
	ArrayList studentList = new ArrayList();
	ArrayList programList = new ArrayList();
	ArrayList advisorList = new ArrayList();
	
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
	
	public ArrayList getProgramList() {
		return programList;
	}
	public void setProgramList(Program p) {
		
		programList.add(p);
		
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getDeptDesc() {
		return deptDesc;
	}
	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}
	public ArrayList getStudentList() {
		return studentList;
	}
	public void setStudentList(Student s) {
		
		studentList.add(s);
		
	}
	public ArrayList getAdvisorList() {
		return advisorList;
	}
	public void setAdvisorList(Advisor a) {
		advisorList.add(a);
	}
	
	
}
