package advisor.util;

public class ProgramOld {

	String majCode;
	int seqid;
	String term;
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
	 * @param advisorId the advisorId to set
	 */
	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}

	/**
	 * @param laststop the laststop to set
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

}
