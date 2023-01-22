package process.remote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;
import advisor.util.MessageLogger;
import advisor.util.RunAssignments;
import advisor.util.Success;

public class RemoteService implements RemoteInterface {

	
	public Success processAssignments(String deptCode) throws RemoteException {
		// TODO Auto-generated method stub
		RunAssignments ras = new RunAssignments();
		Success s = new Success();
		s.setSuccess("failure");
		
		MessageLogger.out.println("Check this dept Code, makes no sense " +deptCode);
		try {
			
			if (deptCode.equals("500")){
				ras.executLawNavigator();
			} else if (deptCode.equals("801")){
				ras.executEconNavigator();
			} else if (deptCode.equals("806")){
				ras.executManStudiesNavigator("806");
			} else if (deptCode.equals("810")){
				ras.executGSSWNavigator();
			} else if (deptCode.equals("808")){
				ras.executSocialSciencesNavigator();
			}
			s.setSuccess("success");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			s.setSuccess("failure");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			s.setSuccess("failure");
			e.printStackTrace();
		}
		return s;
	}
/*
 * else if (deptCode.equals("801")){
				ras.executEconNavigator();
			}
 * */
	public Success confirmAdvisor() throws RemoteException {
		// TODO Auto-generated method stub
		AdvisorDb db = new AdvisorDb("", "admin", "kentish", "systemsman04");
		db.changeSchema("advisors");
		BannerDb bdb = new BannerDb(0);
		bdb.openConn();
		Success success = new Success();
		db.confirmAdvisorToBanner(bdb, success);
		return success;
	}

	

}
