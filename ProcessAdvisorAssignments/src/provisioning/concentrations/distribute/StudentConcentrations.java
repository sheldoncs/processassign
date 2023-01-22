package provisioning.concentrations.distribute;

import java.util.Iterator;

import advisor.util.Advisor;




public class StudentConcentrations extends AdvisorConcentrations {

	private int seqId;
	private String term;
	private String majCode;
	private int assignID;
	private String programme;
	
	public StudentConcentrations(){
		
	}
	public int getAssignmentID() {
		// TODO Auto-generated method stub
		return assignID;
	}

	public String getMajCode() {
		// TODO Auto-generated method stub
		return majCode;
	}

	
	public void pullStudentConcentrations() {
		// TODO Auto-generated method stub
		
		pullAdvisorConcentrations();
        
        Iterator acIterator = advisorList.iterator();
        while (acIterator.hasNext()){
            AdvisorConcentrations ac =  (AdvisorConcentrations) acIterator.next();
            String programme = db.findProgramme(ac.getSequenceID());
        	studentList = db.pullStudentConcentrations(ac.getMajCode(),banDb.getCurrentTerm().subSequence(0, 4)+"10" );
            Iterator scIterator = studentList.iterator();
            while (scIterator.hasNext()){
            	StudentConcentrations sc =  (StudentConcentrations) scIterator.next();
            	if (programme.equals(sc.getProgramme())){
            	 sc.setSequenceId(ac.getSequenceID());
            	 db.updateStudentConcentration(sc);
            	 Advisor advisors = new Advisor();
            	 advisors.setAdvPidm(banDb.getPidm( db.getAdvisorID(sc.getSequenceId())));
            	 advisors.setStuPidm(banDb.getPidm(db.getStudentID(sc.getAssignmentID())));
            	 banDb.insertAdvisor(advisors);
            	}
            }
        }
       
        
	}

	public void setAdvisorID(String advID) {
		// TODO Auto-generated method stub

	}

	public void setAssignmentID(int id) {
		// TODO Auto-generated method stub
		this.assignID = id;

	}

	public void setMajCode(String code) {
		// TODO Auto-generated method stub
		this.majCode = code;

	}

	
	public void setTerm(String t){
		this.term = t;
	}
	public String getTerm(){
		
		return term;
	}

	public int getSequenceId() {
		// TODO Auto-generated method stub
		return seqId;
	}

	public void setSequenceId(int id) {
		
		// TODO Auto-generated method stub
		this.seqId=id;
	}

	public String getProgramme() {
		return programme;
	}

	public void setProgramme(String programme) {
		this.programme = programme;
	}

	
	

}
