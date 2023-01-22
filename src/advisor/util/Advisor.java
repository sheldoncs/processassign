package advisor.util;

import java.util.ArrayList;

public class Advisor {

	private String deptCode;
	private String programCode;
	private String id;
	private String firstName;
	
	private String lastName;
	private String semester;
	private String studentId;
	private int sequenceId;
	private String advisorId;
	private String studentType;
	private long stuPidm;
	private long advPidm;
	private int lastStop;
	private boolean toConcentration;
	private ArrayList levelList = new ArrayList();
	private String clas;
	private ArrayList studentList = new ArrayList();
	private Program program;
	private Major major;
	private ArrayList concentrationList;
	private String concCode;
	private Department department;
	
	public String getConcCode() {
		return concCode;
	}
	public void setConcCode(String concCode) {
		this.concCode = concCode;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public ArrayList getConcentrationList() {
		return concentrationList;
	}
	public void setConcentrationList(ArrayList concentrationList) {
		this.concentrationList = concentrationList;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public ArrayList getStudentList() {
		return studentList;
	}
	public void setStudentList(Student s) {
		
		studentList.add(s);
	}
	public String getClas() {
		return clas;
	}
	public void setClas(String clas) {
		this.clas = clas;
	}
	public void setLevelList(Level level){
		
		levelList.add(level);
	}
	public ArrayList getLevelList(){
		return levelList;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getSemester() {
		return semester;
	}
	public void setSemester(String semester) {
		this.semester = semester;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public int getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}
	public String getAdvisorId() {
		return advisorId;
	}
	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}
	public long getStuPidm() {
		return stuPidm;
	}
	public void setStuPidm(long stuPidm) {
		this.stuPidm = stuPidm;
	}
	public long getAdvPidm() {
		return advPidm;
	}
	public void setAdvPidm(long advPidm) {
		this.advPidm = advPidm;
	}
	public int getLastStop() {
		return lastStop;
	}
	public void setLastStop(int lastStop) {
		this.lastStop = lastStop;
	}
	public boolean isToConcentration() {
		return toConcentration;
	}
	public void setToConcentration(boolean toConcentration) {
		this.toConcentration = toConcentration;
	}
	public Major getMajor() {
		return major;
	}
	public void setMajor(Major major) {
		this.major = major;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	
	
}
