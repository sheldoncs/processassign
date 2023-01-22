package advisor.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;

public class EconNavigator extends AdvisorNavigator {
	
	HashMap advisorList = new HashMap();
	HashMap advisorCList = new HashMap();
	
	public EconNavigator(){
		super();
	}

	protected void concentrationAssignmentProcess() throws SQLException {
		// TODO Auto-generated method stub
		
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		Set deptSet = deptList.keySet();
		Iterator deptIterator = deptSet.iterator();
		ArrayList programStudentList = null;
		 
		while (deptIterator.hasNext()) {
			
			String deptKey = deptIterator.next().toString();
			deptCode = deptKey;
			department = (Department) deptList.get(deptKey);
			
			ArrayList programList = department.getProgramList();
			Iterator programIterator = programList.iterator();
			
			while (programIterator.hasNext()) {
				
				
				
				Program program = (Program) programIterator.next();
			
			   MessageLogger.out.println(program.getProgramCode());
				
				programStudentList = program.getStudentList();
				ListIterator programStudentIterator = programStudentList.listIterator();
				
				noConcentrations(program, bannerDb); 
				
				while (programStudentIterator.hasNext()){
					
					Student student = (Student)programStudentIterator.next();
					if (student.hasConcentration){
						
						Concentration concentration = student.getConcentration();
						Level level = student.getLevel(); 
						
						ArrayList list = advisorDb.getAdvisorByProgramByConcentration(program.getProgramCode(), level.getClasCode(), deptCode, semester, concentration.getConc()); 
						
						ListIterator concentrationAdvisorIterator = list.listIterator();
						
						while (concentrationAdvisorIterator.hasNext()) {
							
							boolean flag = false;
							Advisor advisor = (Advisor) concentrationAdvisorIterator.next();

							/*
							 * Advisor may be able to advise on different levels
							 * of the program
							 */
							
							Advisor adv =(Advisor) advisorList.get(advisor.getAdvisorId());
							if  (adv != null){
								if (adv.getClas().equals(level.getClasCode())){
									flag = true;
								}
							}
							
							if (!flag) {
								
								if (deptCode.equals("801") || deptCode.equals("810") || deptCode.equals("808")) {
									
									if (advisor.getClas().equals(level.getClasCode()))
									{
									
                                        student.setAdvisor(advisor);
										this.assignConcentrationStudent(concentration, program, deptKey, bannerDb, semester, advisor, advisorList, 
			    		                        student,advisorDb, level);
										break;
										
									}
									
								}
								
								
							}
							
							
						}
						if (!list.isEmpty()){
							if (!concentrationAdvisorIterator.hasNext()) {
								advisorCList.clear();
								while (concentrationAdvisorIterator.hasPrevious())
								  concentrationAdvisorIterator.previous();
								
								Concentration c = student.getConcentration();
								Level levl = student.getLevel();
							    Advisor adr = (Advisor)concentrationAdvisorIterator.next();
							    
							    Level studLevel = student.getLevel();
							    while (!adr.getClas().equals(studLevel.getClasCode()))
							    	adr = (Advisor)concentrationAdvisorIterator.next();
							    
							    this.assignConcentrationStudent(c, program, deptKey, bannerDb, semester, adr, advisorList, 
	    		                        student,advisorDb, levl);
							}
						}
					}
				}
				
			}
		}
		
		
	} 
	protected void assignmentProcess() throws SQLException {
		// TODO Auto-generated method stub
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		Set deptSet = deptList.keySet();
		Iterator deptIterator = deptSet.iterator();
		ArrayList programConcentrationList = null;
		ArrayList studentList = null;
		boolean gsswRun = false;
		
		
		
		/* Iterate through each department */
		while (deptIterator.hasNext()) {

			String deptKey = deptIterator.next().toString();
			deptCode = deptKey;
			department = (Department) deptList.get(deptKey);
			ArrayList programList = department.getProgramList();
			Iterator programIterator = programList.iterator();
			
			
			
			/* Iterate through the programs in the department */
			while (programIterator.hasNext()) {

				Program program = (Program) programIterator.next();

				if (program.programCode.indexOf("SSSC") >= 0)
				  System.out.println("program = " + program.getProgramCode());

				
				if (!gsswRun) {
					if (deptCode.equals("810") || deptCode.equals("808") || deptCode.equals("500")) {
						noConcentrations(program, bannerDb);
						gsswRun = true;
					}
				}
				
				//noConcentrations(program, bannerDb); 
								
				if (program.getProgramCode().equals("ECON-BSC-C-F"))
					System.out.println();

				
				
				if (program.isHasConcentrations()) {

					/* get concentration list of current program */
					programConcentrationList = program.getConcentrationList();

					//studentList = program.getStudentList();
					
					/* Setup concentration Iterator */
					Iterator programConcentrationIterator = programConcentrationList
							.iterator();

					/* iterate through the program concentrations */
					while (programConcentrationIterator.hasNext()) {

						Concentration concentration = (Concentration) programConcentrationIterator
								.next();
						
						
						ArrayList concentrationStudentList = concentration
								.getStudentList();
						ArrayList concentrationAdvisorList = concentration
								.getAdvisorList();

						/* get advisors assigned to the concentration */
						ListIterator concentrationAdvisorIterator = concentrationAdvisorList
								.listIterator();
						ListIterator concentrationStudentIterator = concentrationStudentList
								.listIterator();

						/* Iterate through students who are in the concentration */
						advisorCList.clear();
						/*
						while (concentrationStudentIterator.hasNext()) {
							Student student = (Student) concentrationStudentIterator
									.next();
							 Concentration c = student.getConcentration();
							if (c.isLaststop() == true)
							{
								c.setLaststop(false);
								advisorDb.updateLastStopConcentration(c);
								break;
							}
						}
						
						if (!concentrationStudentIterator.hasNext()){
							while (concentrationStudentIterator.hasPrevious())
								concentrationStudentIterator.previous();
						}
						*/
						
						while (concentrationStudentIterator.hasNext()) {

							
							Advisor advisor = null;
							
							
							boolean advisorAssigned = false;
							Student student = (Student) concentrationStudentIterator
									.next();
							
							Level studentLevel = student.getLevel();
							
							ArrayList list = advisorDb.getAdvisorByProgramByConcentration(program.getProgramCode(), studentLevel.getClasCode(), deptCode, semester, concentration.getConc()); 
							
							concentrationAdvisorIterator = list.listIterator();
							
							while (concentrationAdvisorIterator.hasNext()) {
								
								boolean flag = false;
								
								advisor = (Advisor) concentrationAdvisorIterator
										.next();
								
								
								Advisor adv =(Advisor) advisorCList.get(advisor.getAdvisorId());
								if  (adv != null){
									if (adv.getClas().equals(studentLevel.getClasCode())){
										flag = true;
									}
								}
								
								if (!flag) {

									Concentration c = student.getConcentration();
									/*
									if (c.isLaststop() == true)
    								{
    									c.setLaststop(false);
    									advisorDb.updateLastStopConcentration(c);
    									
    									if (concentrationAdvisorIterator.hasNext()) {
    										advisor = (Advisor) concentrationAdvisorIterator
    												.next();
    									   
    									} else {
    									
    										break;
    									}
    								} */
									
									if (deptCode.equals("801") || deptCode.equals("810")) {
										if (advisor.getClas().equals(studentLevel.getClasCode()))
										{
										student.setAdvisor(advisor);
										
										this.assignConcentrationStudent(c, program, deptKey, bannerDb, semester, advisor, advisorList, 
			    		                        student,advisorDb, studentLevel);
										/*
										concentration.setProgramCode(program.getProgramCode());
										concentration.setStuID(student.getId());
										concentration.setClas(studentLevel.getClasCode());
										concentration.setAdvisorId(advisor.getAdvisorId());
										concentration.setDeptCode(deptCode);
										concentration.setLaststop(true);
										advisorDb.insertConcentration(concentration, bannerDb,semester, advisor);
										advisorCList.put(advisor.getAdvisorId(),advisor);

										advisorAssigned = true;
                                        */
										
										break;
										}
									}
								}
								
								//if (advisorAssigned && concentrationAdvisorIterator.hasNext()){
									//break;
								//}
								
								if (!concentrationAdvisorIterator.hasNext()) {
									advisorCList.clear();
									while (concentrationAdvisorIterator.hasPrevious())
									  concentrationAdvisorIterator.previous();
									
									Concentration c = student.getConcentration();
								    Advisor adr = (Advisor)concentrationAdvisorIterator.next();
								    
								    Level studLevel = student.getLevel();
								    while (!adv.getClas().equals(studLevel.getClasCode()))
								    	adv = (Advisor)concentrationAdvisorIterator.next();
								    
									this.assignConcentrationStudent(c, program, deptKey, bannerDb, semester, adr, advisorList, 
		    		                        student,advisorDb, studentLevel);
								}
								
							}
							
                            /*
							if (!concentrationAdvisorIterator.hasNext()) {
								
								advisorCList.clear();
								
								student.setAdvisor(advisor);
								concentration.setProgramCode(program.getProgramCode());
								concentration.setStuID(student.getId());
								concentration.setClas(studentLevel.getClasCode());
								concentration.setAdvisorId(advisor.getAdvisorId());
								advisorDb.insertConcentration(concentration, bannerDb, semester);
								
								advisorCList.put(advisor.getAdvisorId(),advisor);
								
							}*/

							
						}
					}

				}  else {
					 noConcentrations(program, bannerDb);
				}
			}

		
		
	  }
    bannerDb.closeConn();
		
	}
    
	/*
	private void noConcentrations(Program program, BannerDb bannerDb, String clas){
		 
			
		    ArrayList programStudentList = null;
		
		    
		    if (program.getProgramCode().equals("INRE-BSC-C-F"))
		    	System.out.println();
			
		    if (clas.equals("")){
			   programStudentList = program.getStudentList();
		    }
		    else if (clas.equals("L1")){
		    	programStudentList = bannerDb.getStudentsInDepartment(deptCode, program);
		    }

			

			
			Iterator studentIterator = programStudentList.iterator();

			
			advisorList.clear();
			while (studentIterator.hasNext()) {

				
				Advisor advisor = null;
				
				
				
				Student student = (Student) studentIterator.next();
				
			  if (!student.hasConcentration){
				Level studentLevel = student.getLevel();
				ArrayList list = null;
				list = advisorDb.getAdvisorByProgramByLevel(program.getProgramCode(), studentLevel.getClasCode(), deptCode, semester);
				
				
				ListIterator advisorListIterator = list.listIterator();
				
				
				
				boolean advisorAssigned = false;
				while (advisorListIterator.hasNext()) {

					boolean flag = false;
					advisor = (Advisor) advisorListIterator
							.next();

			
					boolean missedTurn = false;
					Advisor adv =(Advisor) advisorList.get(advisor.getAdvisorId());
					if  (adv != null){
						if (adv.getClas().equals(studentLevel.getClasCode())){
							flag = true;
						}
					}
					
					if (!flag) {
						if (deptCode.equals("801") || deptCode.equals("810") || deptCode.equals("808")) {

							if (advisor.getClas().equals(studentLevel.getClasCode()))
							{
								
							  this.assignStudent(clas, advisor, advisorList, student, advisorDb, bannerDb);	
							  break;
							}
							
							

						}
						
                    
					}
				}
				
				if (!advisorListIterator.hasNext()) {
					advisorList.clear();
					while (advisorListIterator.hasPrevious())
						advisorListIterator.previous();
			
				}
				
			 }
				
			}
		 
	}*/
	private void assignConcentrationStudent(Concentration concentration, Program program, String deptCode, 
   		 BannerDb bannerDb, String semester, Advisor advisor, HashMap advisorCList, 
   		 Student student, AdvisorDb advisorDb, Level studentLevel){
   	 
   	    concentration.setProgramCode(program.getProgramCode());
		concentration.setStuID(student.getId());
		concentration.setClas(studentLevel.getClasCode());
		concentration.setAdvisorId(advisor.getAdvisorId());
		concentration.setDeptCode(deptCode);
		concentration.setLaststop(true);
		boolean inserted = advisorDb.insertConcentration(concentration, bannerDb,semester, advisor);
		
		if (inserted)
		 advisorCList.put(advisor.getAdvisorId(),advisor);
	    
	     
   }
/*	
   private void assignStudent(String deptCode, Advisor advisor, HashMap advisorList, 
   		 Student student, AdvisorDb advisorDb, BannerDb bannerDb){
   	 
	      
	      Level studentLevel = student.getLevel();
	   
	      advisor.setStudentId(student.getId());
		  advisor.setProgramCode(student.getProgram().getProgramCode());
		  advisor.setDeptCode(department.getDeptCode());
		  advisor.setClas(studentLevel.getClasCode());
		  advisor.setLastStop(1);
		  boolean inserted = advisorDb.insertAdvisor(advisor, bannerDb);
		  if (inserted)
		    advisorList.put(advisor.getAdvisorId(),advisor);
		  
		 
   	  
  }	
  */ 
   private void noConcentrations(Program program, BannerDb bannerDb) {

		/** ITERATE THROUGH THE STUDENTS IN THE PROGRAM **/
		
		/* Get students assigned to the program */
		ArrayList programStudentList = program.getStudentList();

		ArrayList list = null;
		
		if (program.programCode.equals("SSSC-BSC-C-F"))
			MessageLogger.out.println();
		
		ListIterator advisorListIterator = null;

		
		Iterator studentIterator = programStudentList.iterator();
		
		/* Iterate through list of students in program */

		while (studentIterator.hasNext()) {
			
			
			
			
			Advisor advisor = null;
			Student student = (Student) studentIterator.next();
			
			
			
           Level levl = student.getLevel();
           
			if (program.getProgramCode().equals("ACCS-BSC-C-F") && levl.getClasCode().equals("L1")){
				
				 MessageLogger.out.println();
			   
			}
			
			if (!student.hasConcentration) {
				/*
				studentCnt++;

				MessageLogger.out.println("	Running Student Count = " + studentCnt);
				*/
				Level studentLevel = student.getLevel();

				list = advisorDb.getAdvisorByProgramMinusLevel(
						program.getProgramCode(), studentLevel.getClasCode(),
						deptCode, semester);

				advisorListIterator = list.listIterator();

				/* Iterate through advisors in program */
				
				
				while (advisorListIterator.hasNext()) {

					boolean flag = false;

					advisor = (Advisor) advisorListIterator.next();
                   
					if (advisor.getAdvisorId().equals("20002627") && advisor.getClas().equals("L2"))
						MessageLogger.out.println();
					
					/*
					 * Advisor may be able to advise on different levels of the
					 * program
					 */
					boolean missedTurn = false;
					Advisor adv = (Advisor) advisorList.get(advisor
							.getAdvisorId());
					if (adv != null) {

						if (adv.getClas().equals(studentLevel.getClasCode())) {
							flag = true;
						}

					}

					if (!flag) {

						
						
						if (advisor.getClas()
								.equals(studentLevel.getClasCode())) {
							
							
							this.assignStudent(deptCode, semester, advisor, advisorList, student, advisorDb, program, bannerDb);
							
							/*
							advisor.setLastStop(1);
							advisor.setStudentId(student.getId());
							advisor.setStudentType(student.getStuType());
							advisor.setProgramCode(program.getProgramCode());
							advisor.setDeptCode(department.getDeptCode());
							Level l = student.getLevel();
							advisor.setClas(l.getClasCode());
							advisor.setSemester(semester);
							advisorDb.insertAdvisor(advisor,
									bannerDb);

							advisorList.put(advisor.getAdvisorId(), advisor);
                           */
							
							break;
						}

					}
				}
			}
				
			if (list != null ){
				if (!list.isEmpty()){
					if (advisorListIterator != null) { 
						if (!advisorListIterator.hasNext()) { 
							advisorList.clear(); 
							while (advisorListIterator.hasPrevious())
								advisorListIterator.previous(); 

							Advisor adv = (Advisor)advisorListIterator.next();

							Level studLevel = student.getLevel();
							while (!adv.getClas().equals(studLevel.getClasCode()))
								if (advisorListIterator.hasNext())
								{
									adv = (Advisor)advisorListIterator.next();
						        } else {
						        	break;
						        }
							   if (adv.getClas().equals(studLevel.getClasCode()))
							    this.assignStudent(deptCode, semester, adv, advisorList, student, advisorDb, program, bannerDb);
						} 
					} else {

					}
				}
			}
		}

	}
   
	private void assignStudent(String deptCode, String semester, Advisor advisor, HashMap advisorList, 
	    		 Student student, AdvisorDb advisorDb, Program program, BannerDb bannerDb){
	    	 
	    	                advisor.setLastStop(1);
							advisor.setStudentId(student.getId());
							advisor.setStudentType(student.getStuType());
							advisor.setProgramCode(program.getProgramCode());
							advisor.setDeptCode(department.getDeptCode());
							Level l = student.getLevel();
							advisor.setClas(l.getClasCode());
							advisor.setSemester(semester);
							boolean inserted = advisorDb.insertAdvisor(advisor,bannerDb);
                           if (inserted)
							  advisorList.put(advisor.getAdvisorId(), advisor);
	    	  
	} 
   
  public ArrayList gatherAdvisorAssignments() throws SQLException {
		// TODO Auto-generated method stub
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		ArrayList assignList = advisorDb.getAdvisorAssignments(bannerDb, deptCode);
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


	

}
