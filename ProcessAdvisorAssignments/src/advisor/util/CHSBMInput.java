package advisor.util;

import java.io.File;
import java.util.ArrayList;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class CHSBMInput {
	
	private ArrayList<Advisor> advisors;
	private  static final int ADVISORID = 0;
	private static final int FIRSTNAME = 1;
	private static final int LASTNAME = 2;
	private static final int CLASS = 3;
	private static final int PROGRAMCODE = 4;
	private static final int PROGRAMDESC = 5;
	private static final int CONCENTRATION = 6;
	private static final int MAJDESC = 7;
	private static final int DEPTCODE = 8;
	
	
	private static String inputFile = "xls/CHSBMAdvisors202220.xls";
	public ArrayList<Advisor> gatherAdvisorInformation() {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		advisors =  new ArrayList<Advisor>();
		
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			Sheet sheet = w.getSheet(0);
			for (int j = 0; j < sheet.getRows(); j++) {
				Advisor advisor = new Advisor();
				Department dept = new Department();
				Program program = new Program();
				Major major = new Major();
				advisor.setMajor(major);
				advisor.setProgram(program);
				advisor.setDepartment(dept);
				for (int i = 0; i < sheet.getColumns(); i++) {
					if (j > 0) {
						Cell cell = sheet.getCell(i, j);
						this.setupAdvisor(advisor, i, cell);
					}

				}
				advisors.add(advisor);
	        }
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return advisors;
	}
    private void setupAdvisor(Advisor advisor, int cellType, Cell cell) {
    	Program program = advisor.getProgram();
    	Major major = advisor.getMajor();
    	Department dept = advisor.getDepartment();
    	switch(cellType) {
		  case ADVISORID:
		    advisor.setAdvisorId(cell.getContents());
		    break;
		  case FIRSTNAME:
		    advisor.setFirstName(cell.getContents());
		    break;
		  case LASTNAME:
			  advisor.setLastName(cell.getContents());
			    break;
		  case CLASS:
			    advisor.setClas(cell.getContents());
			    break;
		  case PROGRAMCODE:
			  program.setProgramCode(cell.getContents());
			  advisor.setProgram(program);
			  break;
		  case PROGRAMDESC:
			  program.setProgramDesc(cell.getContents());
			  advisor.setProgram(program);
			  break;
		  case CONCENTRATION:
			  advisor.setConcCode(cell.getContents());
			  break;
		  case MAJDESC:
			  major.setMajDesc(cell.getContents());
			  advisor.setMajor(major);
			  break;
		  case DEPTCODE:
			  dept.setDeptCode(cell.getContents());
			  advisor.setDepartment(dept);
			  break;
		}
    }
    public void updateAdvisorSetup() {
    	BannerDb bannerDb = new BannerDb(0);
    	bannerDb.openConn();
    	String term = bannerDb.getCurrentTerm().substring(0,4)+"10";
    	AdvisorDb advDb = new AdvisorDb("","admin","kentish","owl2");
    	advDb.cleanupAdvisorlevel(bannerDb, term);
    	advDb.cleanupAdvisorleveWithConcentration(bannerDb, term);
    	advDb.changeSchema("advisors");
		ArrayList<Advisor> advisors = gatherAdvisorInformation();
		advisors.forEach(advisor -> {
			if (advisor.getAdvisorId() != null) {
				if (advisor.getConcCode() == "") {
					advDb.insertAdvisorLevel(advisor, bannerDb);
				} else {
					advDb.insertAdvisorleveWithConcentration(advisor, bannerDb);
				}
			}
		});
		System.out.println("Done");
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CHSBMInput setup = new CHSBMInput();
		setup.updateAdvisorSetup();
	}

}
