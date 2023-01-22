package advisor.util;

import java.util.HashMap;

public class AdvisorStudentMatch {

	private String spridenId;
	private String majCode;
	private String deptCode;
	private String advisorId;
	private int sequenceId;
	private String levl;
	private String firstname;
	private String lastname;
	private String term;
	private Concentration concentration;
	private boolean isConcentration;
	private String clas;
	private String dCode;

	/**
	 * @return the clas
	 */
	public String getClas() {
		return clas;
	}

	/**
	 * @return the dCode
	 */
	public String getdCode() {
		return dCode;
	}

	/**
	 * @param dCode the dCode to set
	 */
	public void setdCode(String dCode) {
		this.dCode = dCode;
	}

	/**
	 * @param clas
	 *            the clas to set
	 */
	public void setClas(String clas) {
		this.clas = clas;
	}

	/**
	 * @return the isConcentration
	 */
	public boolean isConcentration() {
		return isConcentration;
	}

	/**
	 * @param isConcentration
	 *            the isConcentration to set
	 */
	public void setConcentration(boolean isConcentration) {
		this.isConcentration = isConcentration;
	}

	/**
	 * @return the concentration
	 */
	public Concentration getConcentration() {
		return concentration;
	}

	/**
	 * @param concentration
	 *            the concentration to set
	 */
	public void setConcentration(Concentration concentration) {
		this.concentration = concentration;
	}

	public String getSpridenId() {
		return spridenId;
	}

	public void setSpridenId(String spridenId) {
		this.spridenId = spridenId;
	}

	public String getMajCode() {
		return majCode;
	}

	public void setMajCode(String majCode) {
		this.majCode = majCode;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getAdvisorId() {
		return advisorId;
	}

	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getLevl() {
		return levl;
	}

	public void setLevl(String levl) {
		this.levl = levl;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}
