package advisor.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class RunAssignments {

	
	public void executLawNavigator() throws SQLException, IOException{
		
		AdvisorNavigator lnav = new EconNavigator();
		lnav.setupRelationships("500");
		lnav.assignmentProcess();
		@SuppressWarnings("unused")
		ArrayList<Assignments> assignList = lnav.gatherAdvisorAssignments();
		//lnav.outputToExcel(assignList, "law", false);
		
	}
	
	public void executSCITECHNavigator() throws SQLException, IOException{
		AdvisorNavigator econ = new EconNavigator();
		econ.setupRelationships("711");
		econ.concentrationAssignmentProcess();
		ArrayList<Assignments> assignList = econ.gatherAdvisorAssignments();
	}
	
    public void executEconNavigator() throws SQLException, IOException{
		
    	AdvisorNavigator econ = new EconNavigator();
		econ.setupRelationships("801");
		//econ.assignmentProcess();
		econ.concentrationAssignmentProcess();
		ArrayList<Assignments> assignList = econ.gatherAdvisorAssignments();
		//econ.outputToExcel(assignList, "econ",false);
	}
    public void executManStudiesNavigator(String deptCode) throws SQLException, IOException{
		
    	AdvisorNavigator man = new ManStudiesNavigatorUpdate();
		man.setupRelationships(deptCode);
		man.concentrationAssignmentProcess();
		//man.assignmentProcess();
		//man.peruseStudentList();
		//ArrayList assignList = man.gatherConcentrationAdvisorAssignments();
		//man.outputToExcel(assignList, "manstudies",true);
	}
    public void executGSSWNavigator() throws SQLException,  IOException{
		
    	AdvisorNavigator man = new EconNavigator();
		man.setupRelationships("810");
		man.concentrationAssignmentProcess();
		
		ArrayList assignList = man.gatherAdvisorAssignments();
		
		//man.outputToExcel(assignList, "GSSW",true);
	}
public void executSocialSciencesNavigator() throws SQLException,  IOException{
		
    	AdvisorNavigator man = new EconNavigator();
		man.setupRelationships("808");
		man.concentrationAssignmentProcess();
		
		ArrayList assignList = man.gatherAdvisorAssignments();
		
	}
   public void executeAll() throws SQLException, IOException{
//	   executSocialSciencesNavigator();
//       executGSSWNavigator();
//      executEconNavigator();
      executManStudiesNavigator("806");
   }
	public static void main(String[] args) throws SQLException, IOException {
        RunAssignments test = new RunAssignments();
//        test.executLawNavigator();
//        StringBuilder fourth = new StringBuilder("InterviewBit");
//        StringBuffer third = new StringBuffer("InterviewBit");
//        String s = "goat";
//        s.concat("pick");
//        s = s.concat("pick");
        //test.executSocialSciencesNavigator();
        //test.executGSSWNavigator();
       // test.executEconNavigator();
        test.executManStudiesNavigator("806");
        //test.executSCITECHNavigator();
        
		System.exit(0);
		
	}
	
}
