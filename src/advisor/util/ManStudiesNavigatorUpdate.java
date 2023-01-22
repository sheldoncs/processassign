package advisor.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import advisor.db.AdvisorDb;
import advisor.db.BannerDb;

public class ManStudiesNavigatorUpdate extends AdvisorNavigator {
	
	HashMap advisorList = new HashMap();
	HashMap<String, Advisor> advisorCList = new HashMap<String, Advisor>();
	Concentration holdConcentration = null;
	int studentCnt = 0;
	int studentConcentrationCnt = 0;
	boolean inserted = false;
	ArrayList<Student> programStudentList = null;
	public ManStudiesNavigatorUpdate(){
		super();
	}
	@SuppressWarnings("unchecked")
	protected void concentrationAssignmentProcess() throws SQLException {
		// TODO Auto-generated method stub
		
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		Set<String> deptSet = deptList.keySet();
		 
		deptSet.forEach(deptKey -> {
			department = (Department) deptList.get(deptKey);
			deptCode = deptKey;
			ArrayList<Program> programList = department.getProgramList();
			programList.forEach(program->{
				holdConcentration = null;
				programStudentList = program.getStudentList();
				advisorCList.clear();
							
				noConcentrations(program, bannerDb); 
				
				programStudentList.stream().filter(e -> e.hasConcentration == true).forEach(student -> {
					inserted = false;
					//if (student.hasConcentration) {
					Concentration concentration = student.getConcentration();
						
//						if (holdConcentration != null) {
//							holdConcentration = concentration;
//						} else {
//							if (holdConcentration != concentration) {
//								advisorCList.clear();
//								holdConcentration = concentration;
//							}
//						}
						Level level = student.getLevel(); 
						ArrayList<Advisor> list = advisorDb.getAdvisorByProgramByConcentration(program.getProgramCode(), level.getClasCode(), deptCode, semester, concentration.getConc()); 
						
						list.stream().filter(adv-> adv.getClas() ==level.getClasCode()).forEach(advisor -> {
					    	
					    	boolean flag = false;
//					    	Advisor adv =(Advisor) advisorCList.get(advisor.getAdvisorId());
					    
					    	List<Advisor> av = advisorCList.values().stream()
					    			                     .filter(e->e.getAdvisorId().equals(advisor.getAdvisorId()) 
					    			                          && e.getClas().equals(level.getClasCode()) 
					    			                              && e.getConcCode().equals(concentration.getConc()) )
					    			                                   .map(e->e).collect(Collectors.toList());
							if (!av.isEmpty()) {
								flag = true;
							}
//					    	if  (adv != null) {
////								if (adv.getClas().equals(level.getClasCode())){
//									flag = true;
////								}
//							}
                            if (!flag) {	
//								if (deptCode.equals("806")) {
//									if (advisor.getClas().equals(level.getClasCode()))
//									{
                                        student.setAdvisor(advisor);
                                        
										if (!inserted) {
											this.assignConcentrationStudent(concentration, program, deptKey, bannerDb,
													semester, advisor, advisorCList, student, advisorDb, level);
											inserted = true;
											List<Advisor> advLst = advisorCList.values().stream()
				    			                     .filter(e->e.getClas().equals(level.getClasCode()) 
				    			                              && e.getConcCode().equals(concentration.getConc()) )
				    			                                   .map(e->e).collect(Collectors.toList());
											if (advLst.size() == list.size()) {
												
												advisorCList = advisorCList.entrySet() .stream()
												                      .filter(e -> !e.getValue().getClas().equals(level.getClasCode()) 
												                    		         && !e.getValue().getConcCode().equals(level.getClasCode()))
												                                       .map(f->f).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));
														
											}
											return;
										}
//									}
//								}
							}
					    });
					//}
				});
				
			});
			
			
		});
		
	}
	protected void assignmentProcess() throws SQLException {
			// TODO Auto-generated method stub
		    
		BannerDb bannerDb = new BannerDb(0);
		db = bannerDb;
		bannerDb.openConn();
		
		
		
			Set deptSet = deptList.keySet();
			Iterator deptIterator = deptSet.iterator();
			ArrayList programConcentrationList = null;
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

					System.out.println("program = " + program.getProgramCode());
                    	
                    noConcentrations(program, bannerDb);
                    
					if (program.isHasConcentrations()) {

						/* get concentration list of current program */
						programConcentrationList = program.getConcentrationList();

						/* Setup concentration Iterator */
						Iterator programConcentrationIterator = programConcentrationList
								.iterator();

						/* iterate through the program concentrations */
						while (programConcentrationIterator.hasNext()) {

							Concentration concentration = (Concentration) programConcentrationIterator
									.next();
							
							
							ArrayList concentrationStudentList = concentration
									.getStudentList();
							
							
							//ArrayList concentrationAdvisorList = concentration
								//	.getAdvisorList();

							ListIterator concentrationAdvisorIterator = null;
							
							
							/* get advisors assigned to the concentration */
							ListIterator concentrationStudentIterator = concentrationStudentList
								.listIterator();

							/* Iterate through students who are in the concentration */
							
							advisorList.clear();
							/*
							while (concentrationStudentIterator.hasNext()) {
								Student student = (Student) concentrationStudentIterator
										.next();
								 Concentration c = student.getConcentration();
							    
								 if (c.isLaststop() == true) {
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
								
								Student student = (Student) concentrationStudentIterator
										.next();
								
								Level studentLevel = student.getLevel();
								
								ArrayList list = advisorDb.getAdvisorByProgramByConcentration(program.getProgramCode(), studentLevel.getClasCode(), deptCode, semester, concentration.getConc()); 
								
								
								concentrationAdvisorIterator = list.listIterator();
								
								
								
							while (concentrationAdvisorIterator.hasNext()) {

								boolean flag = false;
								advisor = (Advisor) concentrationAdvisorIterator
										.next();

								
								Advisor adv = (Advisor) advisorList.get(advisor.getAdvisorId());
								if (adv != null) {

									if (adv.getClas().equals(studentLevel.getClasCode())){
									 flag = true;
									}
								}

								if (!flag) {
									//if ((deptCode.equals("806")) || (deptCode.equals("810"))) {
                                        Concentration c = student.getConcentration();
                                        /*
                                        
                                        if (c.isLaststop() == true)
        								{
        									c.setLaststop(false);
        									advisorDb.updateLastStopConcentration(c);
        									advisorList.put(advisor.getAdvisorId(),advisor);
        									
        									if (concentrationAdvisorIterator.hasNext()) {
        										advisor = (Advisor) concentrationAdvisorIterator
        												.next();
        									   
        									} else {
        									
        										advisorList.clear();
        										while (concentrationAdvisorIterator.hasPrevious())
        											concentrationAdvisorIterator.previous();
        										
        										break;
        									}
        								} 
                                        */
                                        
										  if (studentLevel.getClasCode().equals(advisor.getClas()))
										  {
											
										    student.setAdvisor(advisor);
										    
										    
										    this.assignConcentrationStudent(c, program, deptKey, bannerDb, semester, advisor, advisorList, 
										    		                        student,advisorDb, studentLevel);
										    
										    
										  
										    break;
										 } 
                                        
									//}

									
									
								}
								
							}
							
							if (!concentrationAdvisorIterator.hasNext()) {
								advisorList.clear();
								while (concentrationAdvisorIterator.hasPrevious())
									concentrationAdvisorIterator.previous();
								
							    Concentration c = student.getConcentration();
							    Advisor adv = (Advisor)concentrationAdvisorIterator.next();
							    Level studLevel = student.getLevel();
							    while (!adv.getClas().equals(studLevel.getClasCode()))
							    	adv = (Advisor)concentrationAdvisorIterator.next();
							    
								this.assignConcentrationStudent(c, program, deptKey, bannerDb, semester, adv, advisorList, 
	    		                        student,advisorDb, studentLevel); 
							
							}    
								
							
							
						  }
						}

					} 
				}

			
		    }
			
			bannerDb.closeConn();
		}

	    private void assignConcentrationStudent(Concentration concentration, Program program, String deptCode, 
	    		 BannerDb bannerDb, String semester, Advisor advisor, HashMap advisorList, 
	    		 Student student, AdvisorDb advisorDb, Level studentLevel){
	    	 
	    	concentration.setProgramCode(program.getProgramCode());
		    concentration.setStuID(student.getId());
		    concentration.setClas(studentLevel.getClasCode());
		    concentration.setStudentType(student.getStuType());
		    concentration.setAdvisorId(advisor.getAdvisorId());
		    concentration.setDeptCode(deptCode);
		    boolean inserted = advisorDb.insertConcentration(concentration, bannerDb,semester, advisor);
		    if (inserted) {
		    	MessageLogger.out.println("Concentration Added "+ concentration.getConc());
		    	advisor.setStudentId(student.getId());
		    	advisor.setConcCode(concentration.getConc());
		    	advisor.setClas(studentLevel.getClasCode());
		       advisorList.put(advisor.getAdvisorId(),advisor);
		    }
	    	
	    	
	    }

	    private void noConcentrations(Program program, BannerDb bannerDb) {

		/** ITERATE THROUGH THE STUDENTS IN THE PROGRAM **/
		
		/* Get students assigned to the program */
		ArrayList programStudentList = program.getStudentList();
        
		ArrayList list = null;
		ListIterator advisorListIterator = null;

		Iterator studentIterator = programStudentList.iterator();
		
		/* Iterate through list of students in program */

		
		
		while (studentIterator.hasNext()) {
			
			
			
			
			Advisor advisor = null;
			Student student = (Student) studentIterator.next();
            Level levl = student.getLevel();
            
			
			
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
                    
					/*
					 * Advisor may be able to advise on different levels of the
					 * program
					 */
				
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
							break;
						}

					}
				}
			}
			if (list != null){  
			 if (!list.isEmpty()){		
			  if (advisorListIterator != null) { 
				  if (!advisorListIterator.hasNext()) { 
					  advisorList.clear(); 
					  while (advisorListIterator.hasPrevious())
			                  advisorListIterator.previous(); 
					  
					  Advisor adv = (Advisor)advisorListIterator.next();
					  
					  Level studLevel = student.getLevel();
					    while (!adv.getClas().equals(studLevel.getClasCode()))
					    	adv = (Advisor)advisorListIterator.next();
					  
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
	    
    public void peruseStudentList(){
    	
    	ArrayList list = db.getStudentList();
    	Iterator iterator = list.iterator();
    	while (iterator.hasNext()){
    		
    		Student student = (Student)iterator.next();
    		advisorDb.concentrationInserted(student.getProgram().programCode, "201710", student.getConcentration().conc, student.getId());
    	}
    	
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
			BannerDb bannerDb = new BannerDb(0);
			bannerDb.openConn();
			ArrayList assignList = advisorDb.getAdvisorAssignments(bannerDb, department);
			bannerDb.closeConn();
			return assignList;
		}

		

}
