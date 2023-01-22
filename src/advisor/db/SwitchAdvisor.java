package advisor.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import advisor.util.Advisor;

public class SwitchAdvisor {
	
	String[] semesters = {"201910","202010","202110"}; 
	
	public void makeAssignments() {
		BannerDb db = new BannerDb(0);
		db.openConn();
		for (int i = 0; i < semesters.length; i++) {
			ArrayList<Advisor> list = db.gatherFirstAssignments(semesters[i]);
			Iterator<Advisor> ii = list.iterator();
			while (ii.hasNext()) {
				Advisor advisor = ii.next();
			   db.updateAdvisor(advisor, semesters);
			}
		}
		db.closeConn();
	}
	public static void main(String[] args) {
	    SwitchAdvisor sw = new SwitchAdvisor();
	    sw.makeAssignments();
	}
}
