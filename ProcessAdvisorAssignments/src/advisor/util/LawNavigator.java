package advisor.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import advisor.db.BannerDb;

public class LawNavigator extends AdvisorNavigator {

	public LawNavigator(){
		
		super();
	}

	protected void assignmentProcess() throws SQLException {
		// TODO Auto-generated method stub
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		Set deptSet = deptList.keySet();
		Iterator deptIterator = deptSet.iterator();
		
		/* Iterate through each department */
		while (deptIterator.hasNext()) {
			
			String deptKey = deptIterator.next().toString();
			deptCode = deptKey;
			department = (Department) deptList.get(deptKey);
			ArrayList studentList = department.getStudentList();
			ArrayList advisorList = department.getAdvisorList();
			
			ListIterator advisorIterator = advisorList.listIterator();
			Iterator studentIterator = studentList.iterator();
			
			while (studentIterator.hasNext()) {
				
				Student student = (Student) studentIterator.next();
				
				/* Iterate through advisors in program */
			    boolean missedTurn = false;
				while (advisorIterator.hasNext()) {
					
					Advisor advisor = (Advisor) advisorIterator.next();
					
					advisorDb.resetLastStop(department.getDeptCode());
					Level level = student.getLevel();
					advisorDb.updateNextStop(advisor,department.getDeptCode());
					advisor.setStudentId(student.getId());
					advisor.setProgramCode(" ");
					advisor.setDeptCode(department.getDeptCode());
					advisor.setClas(level.getClasCode());
					advisor.setStudentType(student.getStuType());
					
					advisorDb.insertAdvisor(advisor,bannerDb);
					
					break;
					
				}
				if (!advisorIterator.hasNext()) {
					while (advisorIterator.hasPrevious())
						advisorIterator.previous();
				}

				
				
			}
			bannerDb.closeConn();
		}
		
	}
	public ArrayList<Assignments> gatherAdvisorAssignments() throws SQLException {
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		ArrayList<Assignments> assignList = advisorDb.getAdvisorAssignments(bannerDb, deptCode);
		bannerDb.closeConn();
		return assignList;
	}

	public ArrayList gatherConcentrationAdvisorAssignments()
			throws SQLException {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	public void peruseStudentList() {
		// TODO Auto-generated method stub
		
	}

	protected void concentrationAssignmentProcess() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
}
