package provisioning.concentrations.distribute;

import java.util.ArrayList;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;

public abstract class Concentrations {

	protected ArrayList advisorList;
	protected BannerDb banDb;
	protected ArrayList studentList;
	protected AdvisorDb db;
	
	public Concentrations(){
		
	}
	public void closeDB(){
		banDb.closeConn();
		db.closeDb();
	
	}
	
}
