package advisor.util;

import java.util.ArrayList;

public class Student {
	
	String id;
	String firstName;
	String lastName;
	Department dept;
	Program program;
	Advisor advisor;
	Major major;
	ArrayList majorList = new ArrayList();
	Level level;
	String stuType;
	Concentration concentration;
	boolean hasConcentration;
	
	public String getStuType() {
		return stuType;
	}
	public void setStuType(String stuType) {
		this.stuType = stuType;
	}
	
	
	public boolean isHasConcentration() {
		return hasConcentration;
	}
	public void setHasConcentration(boolean hasConcentration) {
		this.hasConcentration = hasConcentration;
	}
	public Concentration getConcentration() {
		return concentration;
	}
	public void setConcentration(Concentration concentration) {
		this.concentration = concentration;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public Major getMajor() {
		return major;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Department getDept() {
		return dept;
	}
	public void setDept(Department dept) {
		this.dept = dept;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public Advisor getAdvisor() {
		return advisor;
	}
	public void setAdvisor(Advisor advisor) {
		this.advisor = advisor;
	}
	public void setMajorlist(Major major){
		
		majorList.add(major);
	}
    public ArrayList getmajorList(){
    	return majorList;
    }
}
