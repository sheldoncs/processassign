package advisor.util;

import java.util.ArrayList;

public class Concentration {

	String majCode;
	int seqid;
	String term;
	String conc;
	String concDesc;
	String programCode;
	String clas;
	String studentType;
	String deptCode;
	
	
	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	ArrayList studentList = new ArrayList();
	Major major;
	ArrayList advisorList = new ArrayList();
	ArrayList levelList;

	
	
	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public String getClas() {
		return clas;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public String getProgramCode() {
		return programCode;
	}

	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}

	public ArrayList getAdvisorList() {
		return advisorList;
	}

	public void setAdvisorList(Advisor advisor) {

		advisorList.add(advisor);
	}

	public Major getMajor() {
		return major;
	}

	public void setMajor(Major major) {
		this.major = major;
	}

	public String getConcDesc() {
		return concDesc;
	}

	public void setConcDesc(String concDesc) {
		this.concDesc = concDesc;
	}

	boolean laststop;
	String advisorId;

	/**
	 * @return the laststop
	 */

	public boolean isLaststop() {
		return laststop;
	}

	/**
	 * @return the advisorId
	 */
	public String getAdvisorId() {
		return advisorId;
	}

	/**
	 * @param advisorId
	 *            the advisorId to set
	 */
	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}

	/**
	 * @param laststop
	 *            the laststop to set
	 */
	public void setLaststop(boolean laststop) {
		this.laststop = laststop;
	}

	/**
	 * @return the stuID
	 */
	public String getStuID() {
		return stuID;
	}

	/**
	 * @param stuID
	 *            the stuID to set
	 */
	public void setStuID(String stuID) {
		this.stuID = stuID;
	}

	String stuID;

	/**
	 * @return the conc
	 */
	public String getConc() {
		return conc;
	}

	/**
	 * @param conc
	 *            the conc to set
	 */
	public void setConc(String conc) {
		this.conc = conc;
	}

	/**
	 * @return the majCode
	 */
	public String getMajCode() {
		return majCode;
	}

	/**
	 * @param majCode
	 *            the majCode to set
	 */
	public void setMajCode(String majCode) {
		this.majCode = majCode;
	}

	/**
	 * @return the seqid
	 */
	public int getSeqid() {
		return seqid;
	}

	/**
	 * @param seqid
	 *            the seqid to set
	 */
	public void setSeqid(int seqid) {
		this.seqid = seqid;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term
	 *            the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	public ArrayList getStudentList() {
		return studentList;
	}

	public void setStudent(Student s) {

		studentList.add(s);
	}

	public ArrayList getLevelList() {
		return levelList;
	}

	public void setLevelList(ArrayList levelList) {
		this.levelList = levelList;

	}

	public void setStudentList(Student s) {
		
		studentList.add(s);
		
	}

}
