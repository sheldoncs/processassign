package advisor.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;

public abstract class AdvisorNavigator {

	protected AdvisorDb advisorDb;
	protected ArrayList studentList;

	HashMap programList;
	protected HashMap deptList;
	protected HashMap clasList;
	protected HashMap advisorList;
	protected HashMap levelList;
	protected HashMap majorList;
	protected String levelstr[] = {};
	protected String deptCode = null;
	protected Department department;
	protected String semester;
	BannerDb db; 
	public AdvisorNavigator() {

		
		advisorDb = new AdvisorDb("", "admin", "kentish", "owl2");
		advisorDb.changeSchema("advisors");
      
	}

	public void setupRelationships(String deptCode) throws SQLException {
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		
		semester = bannerDb.getCurrentTerm();
		
		bannerDb.setAdvisorDb(advisorDb);
		deptList = bannerDb.getAllDepartments(deptCode, semester);
		
		bannerDb.closeConn();
		
		System.out.println(deptList.size());
	}

	protected abstract void assignmentProcess() throws SQLException;
	protected abstract void concentrationAssignmentProcess() throws SQLException;
	
	public abstract ArrayList<Assignments> gatherAdvisorAssignments() throws SQLException;
	
	public abstract ArrayList<Assignments> gatherConcentrationAdvisorAssignments() throws SQLException;
	public abstract  void peruseStudentList();
	 
    public void outputToExcel(ArrayList<Assignments> assignList, String filename, boolean isConcentration) throws IOException, RowsExceededException, WriteException, BiffException{
    	
    	ExcelOutput eXoutput = new ExcelOutput();
    	eXoutput.setFileName(filename);
		eXoutput.setAssignList(assignList);
		eXoutput.setupWorkBook(); 
		eXoutput.writeOut(isConcentration);
		eXoutput.closeWorkBook();
		closeDatabases();
    	
    }
	public void closeDatabases() {

		
		advisorDb.closeDb();

	}
}
