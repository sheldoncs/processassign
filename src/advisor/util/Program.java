package advisor.util;

import java.util.ArrayList;

public class Program {

	String programCode;
	String programDesc;
	String majCode;
	ArrayList<Student> studentList = new ArrayList<Student>();
	Department deptartment;
	ArrayList concentrationList = new ArrayList();
	boolean hasConcentrations;
    ArrayList advisorList = new ArrayList();
    String term;
    
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public ArrayList getAdvisorList() {
		return advisorList;
	}
	public void setAdvisorList(Advisor advisor) {
		
		advisorList.add(advisor);
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public String getProgramDesc() {
		return programDesc;
	}
	public void setProgramDesc(String programDesc) {
		this.programDesc = programDesc;
	}
	public ArrayList<Student> getStudentList() {
		return studentList;
	}
	public void setStudentList(Student s) {
		
		studentList.add(s);
	}
	public Department getDeptartment() {
		return deptartment;
	}
	public void setDeptartment(Department deptartment) {
		this.deptartment = deptartment;
	}
	public ArrayList getConcentrationList() {
		return concentrationList;
	}
	public void setConcentrationList(Concentration concentration) {
		
		concentrationList.add(concentration);
	}
	public boolean isHasConcentrations() {
		return hasConcentrations;
	}
	public void setHasConcentrations(boolean hasConcentrations) {
		this.hasConcentrations = hasConcentrations;
	}
	
	
	
}
