package advisor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import advisor.db.BannerDb;

public class CsvInput {
	 private BannerDb addb;
   public  CsvInput() {
		 addb = new BannerDb(0);
		 addb.openConn();
   }
   public ArrayList<Advisor> ReadInStudents() {
	   ArrayList<Advisor> list = new  ArrayList<Advisor>();
	   try {
			Scanner sc = new Scanner(new File("csv\\humanities.csv"));
			int cnt = 0;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				Advisor a = new Advisor();
				if (cnt > 0) {
					 a.setStudentId(line);
					 a.setStuPidm(addb.getPidm(a.getStudentId()));
					 list.add(a);
				}
				 cnt++;
			}
	   } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return list;
   }
   public ArrayList<Advisor> ReadInAdvisors() {
	   ArrayList<Advisor> list = new  ArrayList<Advisor>();
	   try {
			Scanner sc = new Scanner(new File("csv\\advisors_sports.csv"));
			int cnt = 0;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				Advisor a = new Advisor();
				if (cnt > 0) {
					 a.setAdvisorId(line);
					 a.setAdvPidm(addb.getPidm(a.getAdvisorId()));
					 list.add(a);
				}
				 cnt++;
			}
	   } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	   return list;
   }
   public ArrayList<Advisor> ApplyAdvisors() {
	   ArrayList<Advisor> studentList = ReadInStudents();
	   ArrayList<Advisor> advisorList = ReadInAdvisors();
	   int cnt = 0;
	   int index = 0;
	   int studentCounter = studentList.size();
	   int advisorCounter = advisorList.size();
	   int divAssignCount = Math.round(studentCounter/advisorCounter);
	   //int remAssignCount = Math.floorMod(studentCounter, advisorCounter);
	   
	   Iterator<Advisor> studentIterator = studentList.iterator();
	   while (studentIterator.hasNext()) {
		   if (cnt == divAssignCount) {
			   cnt = 0;
			   index++;
		   } else {
			   cnt ++;
		   }
		   Advisor advisor = advisorList.get(index);
		   Advisor student = studentIterator.next();
		   int studentIndex = studentList.indexOf(student);
		   student.setAdvisorId(advisor.getAdvisorId());
		   student.setAdvPidm(advisor.getAdvPidm());
		   studentList.set(studentIndex, student);
	   }
	   return studentList;
   }
   public void ProcessAssignments() {
	   ArrayList<Advisor> studentList = ApplyAdvisors();
	   Iterator<Advisor> studentIterator = studentList.iterator();
	   while (studentIterator.hasNext()) {
		   Advisor advisor = studentIterator.next();
		   addb.insertAdvisor(advisor);
	   }
   }
   public void Read() {
	 	
	   try {
		
		
		Scanner sc = new Scanner(new File("csv\\humanities.csv"));
		
		int cnt = 0;
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (cnt > 0) {
				String[] values = line.split(",");
			 System.out.println(values[0]+","+values[1]);
			 Advisor advisors = new Advisor();
             advisors.setStudentId(values[0]);
             advisors.setAdvisorId(values[1]);
			 advisors.setStuPidm(addb.getPidm(advisors.getStudentId()));
			 advisors.setAdvPidm(addb.getPidm(advisors.getAdvisorId()));

				/* Turn off for testing purposes */
             addb.insertAdvisor(advisors);
			}
			cnt++;
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   addb.closeConn();
   }
   public static void main(String[] args) {
	   CsvInput csvInput = new  CsvInput();
//	   csvInput.ApplyAdvisors();
//	   csvInput.ProcessAssignments();
	   csvInput.Read();
   }
}
