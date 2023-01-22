package provisioning.concentrations.distribute;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;


public class AdvisorConcentrations extends Concentrations  {

	private int seqId;
	private String majCode;
	
	public AdvisorConcentrations(){
		super();
	}
	public void pullAdvisorConcentrations() {
		// TODO Auto-generated method stub
		banDb = new BannerDb(0);
		banDb.openConn();
		db = new AdvisorDb("","admin","kentish","owl1");
		advisorList = db.pullAdvisorConcentrations(banDb);
		
	}
	
	public String getMajCode() {
		// TODO Auto-generated method stub
		return majCode;
	}

	public int getSequenceID() {
		// TODO Auto-generated method stub
		return seqId;
	}

	public void setMajCode(String m) {
		// TODO Auto-generated method stub
		this.majCode = m;
	}

	public void setSequenceID(int s) {
		// TODO Auto-generated method stub
		this.seqId = s;
	}


	public void setBannerDb(BannerDb db) {
		// TODO Auto-generated method stub
		this.banDb = db;
		
	}


}
