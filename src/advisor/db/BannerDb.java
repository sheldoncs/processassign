package advisor.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import advisor.util.Advisor;
import advisor.util.AdvisorStudentMatch;
import advisor.util.Advisors;
import advisor.util.Concentration;
import advisor.util.Department;
import advisor.util.Level;
import advisor.util.Major;
import advisor.util.MessageLogger;
import advisor.util.NewDateFormatter;
import advisor.util.Program;
import advisor.util.ProgramOld;
import advisor.util.Student;

public class BannerDb extends OracleDBConnection {
	int cnt = 0;
	int accum = 0;
	ArrayList studentList = new ArrayList();
	public BannerDb(int v) {
		super(0);
	}
	
	public String getProgramSQL() {

		String selectStatement = "select distinct SGBSTDN.SGBSTDN_PROGRAM_1 AS PROGRAM "
				+ "from SATURN.SGBSTDN SGBSTDN "
				+ "where SGBSTDN.SGBSTDN_TERM_CODE_EFF IN (?,?,?) "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
				+ "( select SGBSTDN1.SGBSTDN_PIDM, "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) as Max_SGBSTDN_TERM_CODE_EFF "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) "
				+ "order by SGBSTDN.SGBSTDN_PROGRAM_1 ";

		return selectStatement;

	}

	private String getProgramWithConcentrationsSQL() {

		String selectStatement = "select DISTINCT "
				+ "SGBSTDN.SGBSTDN_TERM_CODE_EFF, "
				+ "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1, "
				+ "STVMAJR.STVMAJR_DESC, "
				+ "SGBSTDN.SGBSTDN_PROGRAM_1, "
				+ "SGBSTDN.SGBSTDN_STYP_CODE  "
				+ "from SATURN.SGBSTDN SGBSTDN, SATURN.STVMAJR STVMAJR "
				+ "where SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 is not null "
				+ "and SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 = STVMAJR.STVMAJR_CODE "
				+ "and SGBSTDN.SGBSTDN_PROGRAM_1 = ? and SGBSTDN.SGBSTDN_TERM_CODE_EFF in (?,?,?) "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  =  "
				+ "( select SGBSTDN1.SGBSTDN_PIDM, "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) "
				+ "order by SGBSTDN.SGBSTDN_PROGRAM_1, SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1";

		return selectStatement;

	}
	private String getProgramWithOutConcentrationsSQL() {

		String selectStatement = "select DISTINCT "
				+ "SGBSTDN.SGBSTDN_TERM_CODE_EFF, "
				+ "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1, "
				+ "STVMAJR.STVMAJR_DESC, "
				+ "SGBSTDN.SGBSTDN_PROGRAM_1, "
				+ "SGBSTDN.SGBSTDN_STYP_CODE  "
				+ "from SATURN.SGBSTDN SGBSTDN, SATURN.STVMAJR STVMAJR "
				+ "where SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 is null "
				+ "and SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 = STVMAJR.STVMAJR_CODE "
				+ "and SGBSTDN.SGBSTDN_PROGRAM_1 = ? and SGBSTDN.SGBSTDN_TERM_CODE_EFF = ? "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  =  "
				+ "( select SGBSTDN1.SGBSTDN_PIDM, "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) "
				+ "order by SGBSTDN.SGBSTDN_PROGRAM_1";

		return selectStatement;

	}
	private String getProgramsInDepartmentSQL() {

		// "and SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 = STVMAJR.STVMAJR_CODE " +

		String selectStatement = "select DISTINCT "
				+ "SGBSTDN.SGBSTDN_DEPT_CODE, "
				+ "SGBSTDN.SGBSTDN_PROGRAM_1, "
				+ "SMRPRLE.SMRPRLE_PROGRAM_DESC "
				+ "from SATURN.SGBSTDN SGBSTDN, SATURN.STVMAJR STVMAJR, SATURN.SMRPRLE SMRPRLE "
				+ "where "
				+ "SMRPRLE.SMRPRLE_PROGRAM = SGBSTDN.SGBSTDN_PROGRAM_1 "
				+ "and SGBSTDN.SGBSTDN_DEPT_CODE = ? and SGBSTDN.SGBSTDN_TERM_CODE_EFF in (?,?,?) AND SGBSTDN_LEVL_CODE = ?"
//				+ "and SMRPRLE.SMRPRLE_PROGRAM = 'MANG-BSC-C-F' "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  =  "
				+ "( select SGBSTDN1.SGBSTDN_PIDM, "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) "
				+ "order by SGBSTDN.SGBSTDN_PROGRAM_1";

		return selectStatement;
	}

	public String getMajorsSQL() {
		String selectStatement = "select distinct stvmajr_code, stvmajr_desc "
				+ "from stvmajr ";

		return selectStatement;
	}

	public String getClasSQL() {
		String selectStatement = "select stvclas_code, stvclas_desc from stvclas where  stvclas_code <> ?";
		return selectStatement;
	}

	public String getSpecificClasSQL() {
		String selectStatement = "select stvclas_code, stvclas_desc from stvclas where  stvclas_code = ?";
		return selectStatement;
	}

	public String getProgramsSQL() {

		String selectStatement = "select distinct smrprle_program, smrprle_program_desc  "
				+ "from SATURN.SMRPRLE  SMRPRLE "
				+ "where SMRPRLE.SMRPRLE_LEVL_CODE_STU = ? order by smrprle_program";

		return selectStatement;
	}

	public String getSpecificProgramSQL() {

		String selectStatement = "select distinct smrprle_program, smrprle_program_desc  "
				+ "from SATURN.SMRPRLE  SMRPRLE "
				+ "where SMRPRLE.SMRPRLE_LEVL_CODE_STU = ? and smrprle_program = ? order by smrprle_program";

		return selectStatement;
	}

	public String getDepartmentsSQL() {

		String selectStatement = "select distinct STVDEPT_CODE, STVDEPT_DESC "
				+ "from STVDEPT where STVDEPT_CODE <> '0000'";

		return selectStatement;
	}

	public String getSpecificDepartmentSQL() {

		String selectStatement = "select distinct STVDEPT_CODE, STVDEPT_DESC "
				+ "from STVDEPT where STVDEPT_CODE = ?";

		return selectStatement;
	}

	public String getListOfStudents(String program) {

		String sqlstmt = "";
		if (!program.equals("N/A")) {
			sqlstmt = "select distinct AS_STUDENT_REGISTRATION_DETAIL.ID, "
					+ "SMRPRLE.SMRPRLE_PROGRAM, SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, "
					+ "AS_STUDENT_REGISTRATION_DETAIL.CLAS_CODE, AS_STUDENT_REGISTRATION_DETAIL.LAST_NAME, "
					+ "AS_STUDENT_REGISTRATION_DETAIL.FIRST_NAME, SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) AS YEAR, "
					+ "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 AS CONC_MAJOR, SGBSTDN.SGBSTDN_DEPT_CODE "
					+ "from SATURN.SMRPRLE SMRPRLE, "
					+ "BANINST1.AS_STUDENT_REGISTRATION_DETAIL AS_STUDENT_REGISTRATION_DETAIL, SATURN.SGBSTDN SGBSTDN "
					+ "where ( SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.PIDM_KEY = SGBSTDN.SGBSTDN_PIDM "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY = SGBSTDN.SGBSTDN_TERM_CODE_EFF ) "
					+ "and SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) = ? "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.COLL_CODE = ? "
					+ "and SMRPRLE.SMRPRLE_PROGRAM = ? "
					+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
					+ "( select SGBSTDN1.SGBSTDN_PIDM, "
					+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
					+ "from SATURN.SGBSTDN SGBSTDN1 "
					+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
					+ " group by SGBSTDN1.SGBSTDN_PIDM ) ";
		} else {
			sqlstmt = "select distinct AS_STUDENT_REGISTRATION_DETAIL.ID, "
					+ "SMRPRLE.SMRPRLE_PROGRAM, SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, "
					+ "AS_STUDENT_REGISTRATION_DETAIL.CLAS_CODE, AS_STUDENT_REGISTRATION_DETAIL.LAST_NAME, "
					+ "AS_STUDENT_REGISTRATION_DETAIL.FIRST_NAME, SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) AS YEAR, "
					+ "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 AS CONC_MAJOR, STVDEPT.STVDEPT_CODE, STVDEPT.STVDEPT_DESC "
					+ "from SATURN.SMRPRLE SMRPRLE, "
					+ "BANINST1.AS_STUDENT_REGISTRATION_DETAIL AS_STUDENT_REGISTRATION_DETAIL, SATURN.SGBSTDN SGBSTDN, SATURN.STVDEPT STVDEPT "
					+ "where ( SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.PIDM_KEY = SGBSTDN.SGBSTDN_PIDM "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY = SGBSTDN.SGBSTDN_TERM_CODE_EFF "
					+ "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE) "
					+ "and SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) = ? "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.COLL_CODE = ? "
					+ "and AS_STUDENT_REGISTRATION_DETAIL.LEVL_CODE = 'UG' "
					+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
					+ "( select SGBSTDN1.SGBSTDN_PIDM, "
					+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF "
					+ "from SATURN.SGBSTDN SGBSTDN1 "
					+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
					+ " group by SGBSTDN1.SGBSTDN_PIDM ) ";
		}
		return sqlstmt;

	}
    public String getListOfStudents(){
    	
    	String sqlstmt = "select SPRIDEN.SPRIDEN_ID,SMRPRLE.SMRPRLE_PROGRAM,SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, " +
			       "SOVCLAS.SOVCLAS_CLAS_CODE,SPRIDEN.SPRIDEN_LAST_NAME,SPRIDEN.SPRIDEN_FIRST_NAME,SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) AS YEAR, " +
			       "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1,STVDEPT.STVDEPT_CODE,STVDEPT.STVDEPT_DESC,SGBSTDN.SGBSTDN_STYP_CODE "+
			  "from SATURN.SGBSTDN SGBSTDN,SATURN.SPRIDEN SPRIDEN,SATURN.SPBPERS SPBPERS,GENERAL.GOBINTL GOBINTL, " +
			  "SATURN.STVMAJR STVMAJR,SATURN.STVNATN STVNATN,SATURN.SMRPRLE SMRPRLE,SATURN.STVDEPT STVDEPT,BANINST1.SOVCLAS SOVCLAS " +
			   "where ( SGBSTDN.SGBSTDN_PIDM = GOBINTL.GOBINTL_PIDM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPRIDEN.SPRIDEN_PIDM " +
			         "and GOBINTL.GOBINTL_NATN_CODE_LEGAL = STVNATN.STVNATN_CODE " +
			         "and SGBSTDN.SGBSTDN_MAJR_CODE_1 = STVMAJR.STVMAJR_CODE " +
			         "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE "+
			         "and SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPBPERS.SPBPERS_PIDM "+
			         "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SOVCLAS.SOVCLAS_TERM_CODE " + 
			         "and SGBSTDN.SGBSTDN_PIDM = SOVCLAS.SOVCLAS_PIDM) "+
			         "and (SPRIDEN.SPRIDEN_CHANGE_IND is null " +
			         "and SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) = ? " +
			         "and STVDEPT.STVDEPT_CODE = ? " +
			         "and SGBSTDN.SGBSTDN_LEVL_CODE = ? " + 
			         //"and SMRPRLE.SMRPRLE_PROGRAM = ? " +
			         "and SOVCLAS.SOVCLAS_CLAS_CODE = 'L1' " +
			         "AND ((SGBSTDN.SGBSTDN_STST_CODE) = 'AS' OR (SGBSTDN.SGBSTDN_STST_CODE) = 'EX') " +
			         "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = " +
			         "( select SGBSTDN1.SGBSTDN_PIDM, " +
			                  "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF " +
			             "from SATURN.SGBSTDN SGBSTDN1 " +
			            "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM " +
			            "group by SGBSTDN1.SGBSTDN_PIDM ) )" +
			        " ORDER BY SMRPRLE.SMRPRLE_PROGRAM, SOVCLAS.SOVCLAS_CLAS_CODE";

		return sqlstmt;

    	
    }
	public String getListOfStudentsByProgramSQL() {

		
		
		String sqlstmt = "select SPRIDEN.SPRIDEN_ID,SMRPRLE.SMRPRLE_PROGRAM,SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, " +
			       "SOVCLAS.SOVCLAS_CLAS_CODE,SPRIDEN.SPRIDEN_LAST_NAME,SPRIDEN.SPRIDEN_FIRST_NAME,SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) AS YEAR, " +
			       "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1,STVDEPT.STVDEPT_CODE,STVDEPT.STVDEPT_DESC,SGBSTDN.SGBSTDN_STYP_CODE "+
			  "from SATURN.SGBSTDN SGBSTDN,SATURN.SPRIDEN SPRIDEN,SATURN.SPBPERS SPBPERS,GENERAL.GOBINTL GOBINTL, " +
			  "SATURN.STVMAJR STVMAJR,SATURN.STVNATN STVNATN,SATURN.SMRPRLE SMRPRLE,SATURN.STVDEPT STVDEPT,BANINST1.SOVCLAS SOVCLAS " +
			   "where ( SGBSTDN.SGBSTDN_PIDM = GOBINTL.GOBINTL_PIDM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPRIDEN.SPRIDEN_PIDM " +
			         "and GOBINTL.GOBINTL_NATN_CODE_LEGAL = STVNATN.STVNATN_CODE " +
			         "and SGBSTDN.SGBSTDN_MAJR_CODE_1 = STVMAJR.STVMAJR_CODE " +
			         "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE "+
			         "and SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPBPERS.SPBPERS_PIDM "+
			         "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SOVCLAS.SOVCLAS_TERM_CODE " + 
			         "and SGBSTDN.SGBSTDN_PIDM = SOVCLAS.SOVCLAS_PIDM) "+
			         "and (SPRIDEN.SPRIDEN_CHANGE_IND is null " +
			         "and SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) = ? " +
			         "and STVDEPT.STVDEPT_CODE = ? " +
			         "and SGBSTDN.SGBSTDN_LEVL_CODE = ? " + 
			         "and SMRPRLE.SMRPRLE_PROGRAM = ? " +
			         "AND ((SGBSTDN.SGBSTDN_STST_CODE) = 'AS' OR (SGBSTDN.SGBSTDN_STST_CODE) = 'EX') " +
			         "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = " +
			         "( select SGBSTDN1.SGBSTDN_PIDM, " +
			                  "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF " +
			             "from SATURN.SGBSTDN SGBSTDN1 " +
			            "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM " +
			            "group by SGBSTDN1.SGBSTDN_PIDM ) )" +
			        " ORDER BY SMRPRLE.SMRPRLE_PROGRAM, SOVCLAS.SOVCLAS_CLAS_CODE, SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1";

		return sqlstmt;

	}
    public String getTestSQL() {

		
		
		String sqlstmt = "select SPRIDEN.SPRIDEN_ID,SMRPRLE.SMRPRLE_PROGRAM,SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, " +
			       "SOVCLAS.SOVCLAS_CLAS_CODE,SPRIDEN.SPRIDEN_LAST_NAME,SPRIDEN.SPRIDEN_FIRST_NAME,SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) AS YEAR, " +
			       "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1,STVDEPT.STVDEPT_CODE,STVDEPT.STVDEPT_DESC,SGBSTDN.SGBSTDN_STYP_CODE "+
			  "from SATURN.SGBSTDN SGBSTDN,SATURN.SPRIDEN SPRIDEN,SATURN.SPBPERS SPBPERS,GENERAL.GOBINTL GOBINTL, " +
			  "SATURN.STVMAJR STVMAJR,SATURN.STVNATN STVNATN,SATURN.SMRPRLE SMRPRLE,SATURN.STVDEPT STVDEPT,BANINST1.SOVCLAS SOVCLAS " +
			   "where ( SGBSTDN.SGBSTDN_PIDM = GOBINTL.GOBINTL_PIDM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPRIDEN.SPRIDEN_PIDM " +
			         "and GOBINTL.GOBINTL_NATN_CODE_LEGAL = STVNATN.STVNATN_CODE " +
			         "and SGBSTDN.SGBSTDN_MAJR_CODE_1 = STVMAJR.STVMAJR_CODE " +
			         "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE "+
			         "and SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM " +
			         "and SGBSTDN.SGBSTDN_PIDM = SPBPERS.SPBPERS_PIDM "+
			         "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SOVCLAS.SOVCLAS_TERM_CODE " + 
			         "and SGBSTDN.SGBSTDN_PIDM = SOVCLAS.SOVCLAS_PIDM) "+
			         "and (SPRIDEN.SPRIDEN_CHANGE_IND is null " +
			         "and SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) = ? " +
			         "and STVDEPT.STVDEPT_CODE = ? " +
			         "and SGBSTDN.SGBSTDN_LEVL_CODE = ? " + 
			         "AND ((SGBSTDN.SGBSTDN_STST_CODE) = 'AS' OR (SGBSTDN.SGBSTDN_STST_CODE) = 'EX') " +
			         "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = " +
			         "( select SGBSTDN1.SGBSTDN_PIDM, " +
			                  "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF " +
			             "from SATURN.SGBSTDN SGBSTDN1 " +
			            "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM " +
			            "group by SGBSTDN1.SGBSTDN_PIDM ) )" +
			        " ORDER BY SMRPRLE.SMRPRLE_PROGRAM, SOVCLAS.SOVCLAS_CLAS_CODE";

		return sqlstmt;

	}
	public String getListOfLawStudentsSQL() {
		
		
		

	
		
		
		String sqlstmt = "select SPRIDEN.SPRIDEN_ID,SMRPRLE.SMRPRLE_PROGRAM,SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, " +
	       "SOVCLAS.SOVCLAS_CLAS_CODE,SPRIDEN.SPRIDEN_LAST_NAME,SPRIDEN.SPRIDEN_FIRST_NAME,SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) AS YEAR, " +
	       "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1,STVDEPT.STVDEPT_CODE,STVDEPT.STVDEPT_DESC,SGBSTDN.SGBSTDN_STYP_CODE "+
	  "from SATURN.SGBSTDN SGBSTDN,SATURN.SPRIDEN SPRIDEN,SATURN.SPBPERS SPBPERS,GENERAL.GOBINTL GOBINTL, " +
	  "SATURN.STVMAJR STVMAJR,SATURN.STVNATN STVNATN,SATURN.SMRPRLE SMRPRLE,SATURN.STVDEPT STVDEPT,BANINST1.SOVCLAS SOVCLAS " +
	   "where ( SGBSTDN.SGBSTDN_PIDM = GOBINTL.GOBINTL_PIDM " +
	         "and SGBSTDN.SGBSTDN_PIDM = SPRIDEN.SPRIDEN_PIDM " +
	         "and GOBINTL.GOBINTL_NATN_CODE_LEGAL = STVNATN.STVNATN_CODE " +
	         "and SGBSTDN.SGBSTDN_MAJR_CODE_1 = STVMAJR.STVMAJR_CODE " +
	         "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE "+
	         "and SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM " +
	         "and SGBSTDN.SGBSTDN_PIDM = SPBPERS.SPBPERS_PIDM "+
	         "and SGBSTDN.SGBSTDN_TERM_CODE_EFF = SOVCLAS.SOVCLAS_TERM_CODE " + 
	         "and SGBSTDN.SGBSTDN_PIDM = SOVCLAS.SOVCLAS_PIDM) "+
	         "and (SPRIDEN.SPRIDEN_CHANGE_IND is null " +
	         "and SUBSTR(SGBSTDN.SGBSTDN_TERM_CODE_EFF,0,4) = ? " +
	         "and STVDEPT.STVDEPT_CODE = ? " +
	         "and SGBSTDN.SGBSTDN_LEVL_CODE = ? " + 
	         "AND ((SGBSTDN.SGBSTDN_STST_CODE) = 'AS' OR (SGBSTDN.SGBSTDN_STST_CODE) = 'EX') " +
	         "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = " +
	         "( select SGBSTDN1.SGBSTDN_PIDM, " +
	                  "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF " +
	             "from SATURN.SGBSTDN SGBSTDN1 " +
	            "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM " +
	            "group by SGBSTDN1.SGBSTDN_PIDM ) )";
		
		return sqlstmt;

	}

	private String getStudentsDoingConcentrationInProgramSQL() {

		String sqlstmt = "select distinct AS_STUDENT_REGISTRATION_DETAIL.ID, "
				+ "SMRPRLE.SMRPRLE_PROGRAM, SMRPRLE.SMRPRLE_PROGRAM_DESC, SGBSTDN.SGBSTDN_COLL_CODE_1, "
				+ "AS_STUDENT_REGISTRATION_DETAIL.CLAS_CODE, AS_STUDENT_REGISTRATION_DETAIL.LAST_NAME, "
				+ "AS_STUDENT_REGISTRATION_DETAIL.FIRST_NAME, SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) AS YEAR, "
				+ "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 AS CONC_MAJOR, STVDEPT.STVDEPT_CODE, STVDEPT.STVDEPT_DESC, SGBSTDN.SGBSTDN_STYP_CODE "
				+ "from SATURN.SMRPRLE SMRPRLE, "
				+ "BANINST1.AS_STUDENT_REGISTRATION_DETAIL AS_STUDENT_REGISTRATION_DETAIL, SATURN.SGBSTDN SGBSTDN, SATURN.STVDEPT STVDEPT "
				+ "where ( SGBSTDN.SGBSTDN_PROGRAM_1 = SMRPRLE.SMRPRLE_PROGRAM "
				+ "and AS_STUDENT_REGISTRATION_DETAIL.PIDM_KEY = SGBSTDN.SGBSTDN_PIDM "
				+ "and AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY = SGBSTDN.SGBSTDN_TERM_CODE_EFF "
				+ "and SGBSTDN.SGBSTDN_DEPT_CODE = STVDEPT.STVDEPT_CODE) "
				+ "and SUBSTR(AS_STUDENT_REGISTRATION_DETAIL.TERM_CODE_KEY,0,4) = ? "
				+ "and AS_STUDENT_REGISTRATION_DETAIL.LEVL_CODE = ? and SMRPRLE.SMRPRLE_PROGRAM = ? "
				+ "AND SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1 = ? "
				+ "and ( SGBSTDN.SGBSTDN_PIDM, SGBSTDN.SGBSTDN_TERM_CODE_EFF )  = "
				+ "( select SGBSTDN1.SGBSTDN_PIDM,  "
				+ "Max( SGBSTDN1.SGBSTDN_TERM_CODE_EFF ) AS Max_SGBSTDN_TERM_CODE_EFF  "
				+ "from SATURN.SGBSTDN SGBSTDN1 "
				+ "where SGBSTDN1.SGBSTDN_PIDM = SGBSTDN.SGBSTDN_PIDM "
				+ "group by SGBSTDN1.SGBSTDN_PIDM ) " +
				"ORDER BY SMRPRLE.SMRPRLE_PROGRAM, SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1, AS_STUDENT_REGISTRATION_DETAIL.CLAS_CODE" ;

		return sqlstmt;

	}

	public void setAdvisorDb(AdvisorDb advisorDb) {
		this.advisorDb = advisorDb;
	}

	public int getPidm(String id) {

		int pidm = 0;

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_id = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, id);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				pidm = rs.getInt(1);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return pidm;

	}

	private String getMajorSQL() {

		String sqlstmt = "select stvmajr_code, stvmajr_desc from stvmajr where stvmajr_code = ?";

		return sqlstmt;

	}

	public String getMajor(String c) throws SQLException {

		String major = null;

		PreparedStatement prepStmt = conn.prepareStatement(getMajorSQL());
		prepStmt.setString(1, c);

		ResultSet rs = prepStmt.executeQuery();

		if (rs.next()) {

			major = rs.getString(2);

		}
		prepStmt.close();
		return major;
	}

	public HashMap<String, Department> getAllDepartments(String deptCode, String term) throws SQLException {

		HashMap deptList = new HashMap();

		PreparedStatement prepStmt = conn
				.prepareStatement(getSpecificDepartmentSQL());
		prepStmt.setString(1, deptCode);

		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {

			Department dept = new Department();
			dept.setDeptCode(rs.getString(1));
			dept.setDeptDesc(rs.getString(2));

			if ( (deptCode.equals("500")) || (deptCode.equals("806") || (deptCode.equals("801") || (deptCode.equals("808") || (deptCode.equals("810") || (deptCode.equals("700") || (deptCode.equals("711")))))))) {
				getProgramsInDepartment(deptCode, dept, term);
				deptList.put(dept.getDeptCode(), dept);
			} 
		}
		/*else if (deptCode.equals("500")) {
		getLawStudentsInDepartment(deptCode, dept);
		addAdvisorsToLawDepartment(dept);
		deptList.put(dept.getDeptCode(), dept);
	}*/
		rs.close();
		prepStmt.close();

		return deptList;
	}

	public String getSpecificDepartment(String c) throws SQLException {

		String dept = null;
		PreparedStatement prepStmt = conn
				.prepareStatement(getSpecificDepartmentSQL());
		prepStmt.setString(1, c);

		ResultSet rs = prepStmt.executeQuery();

		if (rs.next()) {

			dept = rs.getString(2);

		}
		prepStmt.close();

		return dept;
	}

	private void getProgramsInDepartment(String deptCode, Department department, String semester)
			throws SQLException {

		
		PreparedStatement prepStmt = conn
				.prepareStatement(getProgramsInDepartmentSQL());
        
        String[] range = termRange(semester);
        
		prepStmt.setString(1, deptCode.trim());
		prepStmt.setString(2, semester.trim());
		prepStmt.setString(3, range[0]);
		prepStmt.setString(4, range[1]);
		
		prepStmt.setString(5, "UG");
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {

			
			
			Program program = new Program();
			program.setProgramCode(rs.getString(2));
			program.setProgramDesc(rs.getString(3));
			
			String programCode = program.getProgramCode();
			
			//if (!deptCode.equals("500")){
				
				
				 getProgramWithConcentrations(programCode, program, department, semester);
				
			    
			//}

		
			//if (!deptCode.equals("500")) {
				
				if (advisorDb.programAssigned(program.getProgramCode())){
				
				
				 
				  getStudentsInProgram(deptCode, program, department);
				  addAdvisorsToPrograms(program, department);
				  
				
				}
			//}
			department.setProgramList(program);
		}
		
		MessageLogger.out.println("Total Students in List = " +studentList.size());
        rs.close();
		prepStmt.close();
	}
	public ArrayList getStudentList (){
		MessageLogger.out.println("Total Students in List = " +studentList.size());
		return studentList;
	}


	private void addAdvisorsToPrograms(Program program, Department department)
			throws SQLException {

		program.setTerm(getCurrentTerm());

		advisorDb.setupAdvisorList(program, department, this);

	}

	private void addAdvisorsToLawDepartment(Department department)
			throws SQLException {

		department.setTerm(getCurrentTerm());

		advisorDb.setupAdvisorList(null, department, this);

	}

	public ArrayList getStudentsInDepartment(String deptCode, Program program) {
		
		String term = getCurrentTerm();
        ArrayList list = new ArrayList();
        
		PreparedStatement prepStmt;
		try {
			prepStmt = conn
					.prepareStatement(getListOfStudents());
		

		term = term.substring(0, 4);
		prepStmt.setString(1, term);
		prepStmt.setString(2, deptCode);
		prepStmt.setString(3, "UG");
		//prepStmt.setString(4, program.getProgramCode());
		
		ResultSet rs = prepStmt.executeQuery();
		
		  while (rs.next()) {
			  
			if (rs.getString(1).equals("415001680"))
				System.out.println();
			
			Student student = new Student();
			student.setFirstName(rs.getString(7));
			student.setLastName(rs.getString(6));
			student.setId(rs.getString(1));
			student.setLevel(getSpecificClas(rs.getString(5)));
			student.setStuType(rs.getString(12));
			Program prog = new Program();
			prog.setProgramCode(rs.getString(2));
			prog.setProgramDesc(this.getSpecificProgram(rs.getString(2)));
			student.setProgram(prog);
			
			list.add(student);
		  }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public void testNumberOfStudents() throws SQLException{
		String term = getCurrentTerm();
		int concCnt = 0;
		int normalCnt = 0;
		
		int localCnt = 0;
		PreparedStatement prepStmt = conn
		.prepareStatement(getTestSQL());
		
		term = term.substring(0, 4);
		prepStmt.setString(1, term);
		prepStmt.setString(2, "806");
		prepStmt.setString(3, "UG");
		
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()) {
			if (rs.getString(9) == null)
			{
				normalCnt++;
			} else {
				concCnt++;
			}
				
		}
		MessageLogger.out.println("With Concentration = "+concCnt);
		MessageLogger.out.println("Without Concentration = "+normalCnt);
	}
	public ArrayList getStudentsInProgram(String deptCode, Program program,
			Department department) throws SQLException {

		
		String term = getCurrentTerm();
        int programCnt = 0;
		PreparedStatement prepStmt = conn
				.prepareStatement(getListOfStudentsByProgramSQL());

		term = term.substring(0, 4);
		prepStmt.setString(1, term);
		prepStmt.setString(2, deptCode);
		prepStmt.setString(3, "UG");
		prepStmt.setString(4, program.getProgramCode());

		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {

			//if (!hasAdvisor(rs.getString(1))) {
				
				
				
				Student student = new Student();
				student.setFirstName(rs.getString(7));
				student.setLastName(rs.getString(6));
				student.setId(rs.getString(1));
				student.setDept(department);
				student.setProgram(program);
				student.setLevel(getSpecificClas(rs.getString(5)));
				student.setStuType(rs.getString(12));
				
				
				
				if (rs.getString(9) != null) {
					Concentration concentration = new Concentration();
					concentration.setTerm(getCurrentTerm().subSequence(0, 4)
							+ "10");
					concentration.setConc(rs.getString(9));
					concentration .setClas(rs.getString(5));
					concentration
							.setConcDesc(getMajor(concentration.getConc()));
					student.setConcentration(concentration);
					student.setHasConcentration(true);

					/* Student is added to concentration */
					concentration.setStudent(student);
					concentration.setLaststop(false);
					/* Concentration flag set for program */
					program.setHasConcentrations(true);
					concentration.setStudentList(student);
					student.setConcentration(concentration);
					student.setHasConcentration(true);
					
					program.setConcentrationList(concentration);
					program.setStudentList(student);
				} else {
					student.setHasConcentration(false);
					program.setStudentList(student);
				}
				programCnt++;
				
				studentList.add(student);
				
				
			//}
            
		}
		accum = programCnt + accum;
		MessageLogger.out.println("Program " + program.getProgramCode() + "Students in program = " + programCnt +", Total Students " + accum);
		rs.close();
		prepStmt.close();
        return studentList;
	}

	public void getLawStudentsInDepartment(String deptCode,
			Department department) throws SQLException {

		String term = getCurrentTerm();

		PreparedStatement prepStmt = conn
				.prepareStatement(getListOfLawStudentsSQL());

		term = term.substring(0, 4);
		prepStmt.setString(1, term);
		prepStmt.setString(2, deptCode);
		prepStmt.setString(3, "UG");

		ResultSet rs = prepStmt.executeQuery();
        /*  
		SPRIDEN.SPRIDEN_ID,SMRPRLE.SMRPRLE_PROGRAM,SGBSTDN.SGBSTDN_STYP_CODE,SGBSTDN.SGBSTDN_COLL_CODE_1, " +
	       "SOVCLAS.SOVCLAS_CLAS_CODE,SPRIDEN.SPRIDEN_LAST_NAME,SPRIDEN.SPRIDEN_FIRST_NAME,SGBSTDN.SGBSTDN_TERM_CODE_EFF, " +
	       "SGBSTDN.SGBSTDN_MAJR_CODE_CONC_1,STVDEPT.STVDEPT_CODE,STVDEPT.STVDEPT_DESC "
		*/
		while (rs.next()) {

			if (!hasAdvisor(rs.getString(1))) {

				Student student = new Student();
				student.setFirstName(rs.getString(7));
				student.setLastName(rs.getString(6));
				student.setId(rs.getString(1));
				student.setDept(department);
				student.setStuType(rs.getString(12));
				student.setLevel(getSpecificClas(rs.getString(5)));
                
				department.setStudentList(student);
			}
		}
		rs.close();
		prepStmt.close();

	}

	public HashMap getAllPrograms(Department department, String term) throws SQLException {

		HashMap programMap = new HashMap();

		PreparedStatement prepStmt = conn.prepareStatement(getProgramsSQL());
		prepStmt.setString(1, "UG");

		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Program program = new Program();

			program.setProgramCode(rs.getString(1));
			program.setProgramDesc(rs.getString(2));

			// if (program.getProgramCode().trim().equals("MANG-BSC-C-F"))
			// System.out.println(program.getProgramCode().trim());
			getProgramWithConcentrations(program.getProgramCode(), program,
					department, term);
			programMap.put(program.getProgramCode(), program);
		}
		prepStmt.close();
		return programMap;
	}

	public String getSpecificProgram(String p) throws SQLException {

		String programDesc = null;

		PreparedStatement prepStmt = conn
				.prepareStatement(getSpecificProgramSQL());
		prepStmt.setString(1, "UG");
		prepStmt.setString(2, p);

		ResultSet rs = prepStmt.executeQuery();

		if (rs.next()) {
			programDesc = rs.getString(2);

		}
		prepStmt.close();
		return programDesc;
	}

	private void getProgramWithConcentrations(String programCode,
			Program program, Department department, String term) throws SQLException {

		PreparedStatement prepStmt = conn
				.prepareStatement(getProgramWithConcentrationsSQL());
		prepStmt.setString(1, programCode);
		prepStmt.setString(2, term);
		String[] range = termRange(term);
		prepStmt.setString(3, range[0]); 
		prepStmt.setString(4, range[1]);
		
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {

			program.setHasConcentrations(true);
			Concentration concentration = new Concentration();
		
			concentration.setConc(rs.getString(2).trim());
			concentration.setConcDesc(getMajor(rs.getString(2)));
			concentration.setStudentType(rs.getString(5));
            concentration.setLaststop(false);
			
			advisorDb.setupAdvisorConcentrationList(programCode, concentration,
					term);
			program.setConcentrationList(concentration);

			getStudentsDoingConcentrationInProgram(concentration, program,
					department);
		}

		prepStmt.close();
        rs.close();
	}
    
	private void getStudentsDoingConcentrationInProgram(
			Concentration concentration, Program program, Department department) {
        
		int localCnt = 0;
		try {
			String term = getCurrentTerm();

			PreparedStatement prepStmt = conn
					.prepareStatement(getStudentsDoingConcentrationInProgramSQL());
			term = term.substring(0, 4);
			prepStmt.setString(1, term);
			prepStmt.setString(2, "UG");
			prepStmt.setString(3, program.getProgramCode());
			prepStmt.setString(4, concentration.getConc());

			ResultSet rs = prepStmt.executeQuery();
			
			boolean exist = advisorDb.concentrationAssigned(program.getProgramCode(), concentration.getConc());
			
			if (exist) {
				while (rs.next()) {
					
					//if (!hasAdvisor(rs.getString(1))) {
						localCnt++;
						
						Student student = new Student();
						student.setFirstName(rs.getString(7));
						student.setLastName(rs.getString(6));
						student.setId(rs.getString(1));
						student.setDept(department);

						student.setProgram(program);
						student.setLevel(getSpecificClas(rs.getString(5)));
						student.setStuType(rs.getString(12));

						
						concentration.setStudentList(student);
						concentration.setLaststop(false);
						student.setConcentration(concentration);
						//System.out.println("Total Assigned to concentrations " + concentration.getConc() +" "+ localCnt);
					//}

				}
			}
			
			rs.close();
			prepStmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private boolean hasAdvisor(String id) throws SQLException {

		String sqlstmt = "select distinct SGRADVR.SGRADVR_PIDM, " +
			       "SGRADVR.SGRADVR_TERM_CODE_EFF " +
			       "from SATURN.SGRADVR SGRADVR " +
			      "where SGRADVR.SGRADVR_PIDM = ? " +
			       "and SGRADVR.SGRADVR_TERM_CODE_EFF in (?,?,?) " +
			      "and ( SGRADVR.SGRADVR_PIDM, SGRADVR.SGRADVR_TERM_CODE_EFF )  = " +
			            "( select SGRADVR1.SGRADVR_PIDM, " +
			                     "Max( SGRADVR1.SGRADVR_TERM_CODE_EFF ) as Max_SGRADVR_TERM_CODE_EFF " +
			                "from SATURN.SGRADVR SGRADVR1  " +
			               "where SGRADVR1.SGRADVR_PIDM = SGRADVR.SGRADVR_PIDM " +
			               "group by SGRADVR1.SGRADVR_PIDM)";
		
		boolean found = false;

		long pidm = getPidm(id);

		PreparedStatement prepStmt = conn
				.prepareStatement(sqlstmt);
		prepStmt.setLong(1, pidm);
		String term = getCurrentTerm();
		String [] range = termRange(term);
		prepStmt.setString(2, term);
		prepStmt.setString(3, range[0]);
		prepStmt.setString(4, range[1]);
		
		ResultSet rs = prepStmt.executeQuery();

		if (rs.next())
			found = true;

		prepStmt.close();
		return found;

	}

	public HashMap getAllClas() throws SQLException {

		HashMap clasMap = new HashMap();

		PreparedStatement prepStmt = conn.prepareStatement(getClasSQL());
		prepStmt.setString(1, "PR");

		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Level level = new Level();

			level.setClasCode(rs.getString(1));
			level.setClasDesc(rs.getString(2));
			clasMap.put(level.getClasCode(), level);
		}
		prepStmt.close();
		return clasMap;
	}

	public Level getSpecificClas(String clasCode) throws SQLException {

		Level level = new Level();
		PreparedStatement prepStmt = conn
				.prepareStatement(getSpecificClasSQL());
		prepStmt.setString(1, clasCode);
		ResultSet rs = prepStmt.executeQuery();
		if (rs.next()) {

			level.setClasCode(rs.getString(1));
			level.setClasDesc(rs.getString(2));
		}
		rs.close();
		prepStmt.close();
		return level;

	}

	public HashMap getAllMajors() throws SQLException {

		HashMap majorList = new HashMap();

		PreparedStatement prepStmt = conn.prepareStatement(getMajorsSQL());

		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Major major = new Major();
			major.setMajCode(rs.getString(1));
			major.setMajDesc(rs.getString(2));
			majorList.put(major.getMajCode(), major);
		}
		prepStmt.close();
		return majorList;
	}
    public void removeAdvisor(long pidm,String term) throws SQLException{
    	term = term.substring(0, 4)+"10";
    	String sqlstmt = "";
    	
    	sqlstmt = "DELETE FROM SGRADVR WHERE SGRADVR_ADVR_PIDM = ? AND SGRADVR_TERM_CODE_EFF = ? AND SGRADVR_ADVR_CODE = 'ADVR'";
    	PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
    	prepStmt.setLong(1, pidm);
    	prepStmt.setString(2, term);
    	
    	prepStmt.executeUpdate();
		prepStmt.close();
    }
    public void manualInsert() {
    	AdvisorDb db = new AdvisorDb("", "admin","kentish","owl2");
    	//db.connectTestDB("admin","kentish", "test");
    	db.changeSchema("advisors");
    	ArrayList<Advisors> list =  db.returnAdvisors(this);
    	Iterator<Advisors> ii = list.iterator();
    	while (ii.hasNext()) {
    		Advisors advisor = ii.next();
    		insertAdvisor(advisor);
    	}
    }
	public void insertAdvisor(Advisor advisors) {

		String sqlstmt = "";
		boolean exist = false;
		
		if (!adviseeExist(advisors.getStuPidm(), advisors.getAdvPidm(),
				getCurrentTerm())) {

			sqlstmt = "INSERT INTO SGRADVR (SGRADVR_PIDM, SGRADVR_TERM_CODE_EFF, SGRADVR_ADVR_PIDM, SGRADVR_ADVR_CODE, SGRADVR_PRIM_IND,SGRADVR_ACTIVITY_DATE) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";

		} else {
			
			exist = true;
			sqlstmt = "UPDATE SGRADVR SET SGRADVR_ACTIVITY_DATE = ? WHERE SGRADVR_PIDM = ? AND SGRADVR_TERM_CODE_EFF = ? AND SGRADVR_ADVR_PIDM = ?";
			
		}

		NewDateFormatter df = new NewDateFormatter();

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

			if (!exist) {
				prepStmt.setLong(1, advisors.getStuPidm());
				prepStmt.setString(2, getCurrentTerm().substring(0, 4) + "10");
				prepStmt.setLong(3, advisors.getAdvPidm());
				prepStmt.setString(4, "ADVR");
				prepStmt.setString(5, "Y");
				prepStmt.setDate(6,
						java.sql.Date.valueOf(df.getSimpleOracleDate()));
			} else {
				prepStmt.setDate(1,
						java.sql.Date.valueOf(df.getSimpleOracleDate()));
				prepStmt.setLong(2, advisors.getStuPidm());
				prepStmt.setString(3, getCurrentTerm().substring(0, 4) + "10");
				prepStmt.setLong(4, advisors.getAdvPidm());
			}

			/** Use when activating the Banner Insert to SGRADVR **/

			prepStmt.executeUpdate();
			prepStmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<Advisor> gatherFirstAssignments(String term){
		String sqlstmt = "select distinct a.sgradvr_pidm as STUDENT_PIDM,  A.SGRADVR_ADVR_PIDM AS ADVISOR_PIDM, " +
	                              "B.SGBSTDN_DEPT_CODE  from sgradvr a, sgbstdn b " +
				                  "where a.sgradvr_pidm = b.sgbstdn_pidm " +
	                              "and sgradvr_term_code_eff = ? and  B.SGBSTDN_DEPT_CODE = '500'";
		ArrayList<Advisor> list = new ArrayList<Advisor>();
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, term);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
               Advisor advisor = new Advisor();
               advisor.setStuPidm(rs.getLong("STUDENT_PIDM"));
			   advisor.setAdvPidm(rs.getLong("ADVISOR_PIDM"));
			   advisor.setSemester(term);
              list.add(advisor);
              
			}
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return list;
	}
	
	private boolean adviseeExist(long adviseepidm, long advisorpidm, String term) {
		boolean found = false;
		String termCode = term.substring(0, 4) + "10";
		String sqlstmt = "select * from sgradvr where sgradvr_pidm = ? and sgradvr_advr_pidm = ? and sgradvr_term_code_eff = ? ";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setLong(1, adviseepidm);
			prepStmt.setLong(2, advisorpidm);
			prepStmt.setString(3, termCode);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				found = true;

			}
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		

		return found;
	}

	/* Gather Advisors by Faculty */
	public void gatherFaculties() {

		AdvisorDb db = new AdvisorDb("", "admin", "kentish", "owl2");
		// db.changeSchema("advisors");
		db.changeSchema("advisors");
		// AdvisorDb db = new AdvisorDb("","admin","kentish","localhost");

		db.setTerm(getCurrentTerm().substring(0, 4) + "10");

		// db.updateAutomationControlTerm(db.getTerm());
		/* db.updateAutomationControlTerm(db.getTerm(), "SS"); */

		ArrayList list = db.getFaculties();

		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {

			String collCode = iterator.next().toString();

			HashMap hmap = db.getAvailableDepts();
			if (hmap.containsKey(collCode)) {
				if (collCode.equals("LW")) {
					/**
					 * System.out.println(); if (!collCode.equals("LW"))
					 * gatherAllAdvisors(collCode, db); else
					 **/
					/*
					 * if (db.startAutomation(collCode)) {
					 * distributeStudentsToAdvisor(collCode, db); }
					 */
				} else if (collCode.equals("SS")) {

					if (db.startAutomation(collCode)) {
						collectStudentByProgram(collCode, db);
					}
				}
			}
		}

		db.closeDb();
	}

	private void setFlags(String collCode, AdvisorDb db) {

		db.setTerm(getCurrentTerm().substring(0, 4) + "10");

		/* Does Record Flag To Start Run on Faculty Exist? */
		if (!db.existRunRecordForFacultyAndSemester(collCode, db.getTerm()))
			db.insertRun(collCode, db.getTerm());

		/* Does Automation Record for Faculty and Semester Exist ? */
		if (!db.existTermAutomationRecord(collCode, db.getTerm())) {
			db.insertTermAutomationRecord(collCode, db.getTerm());

		}

	}

	public ArrayList collectPrograms() {

		ArrayList list = new ArrayList();

		PreparedStatement prepStmt = null;

		try {
			prepStmt = conn.prepareStatement(getProgramSQL());
			prepStmt.setString(1, getCurrentTerm());
			String[] range = termRange(getCurrentTerm());
			prepStmt.setString(2, range[0]);
			prepStmt.setString(3, range[1]);

			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(1));

			}
			prepStmt.close(); 
			return list;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public String[] termRange(String term) {

		String[] range = new String[2];
		range[0] = new String();
		range[1] = new String();

		String year = term.substring(0, 4);

		if (term.substring(4, 6).equals("10")) {
			range[0] = year + "20";
			range[1] = year + "30";
		} else if (term.substring(4, 6).equals("20")) {
			range[0] = year + "10";
			range[1] = year + "30";
		} else if (term.substring(4, 6).equals("30")) {
			range[0] = year + "10";
			range[1] = year + "20";
		}

		if (term.substring(4, 6).equals("30")) {
			int yr = Integer.parseInt(year) + 1;
			range[0] = Integer.toString(yr) + "10";
			System.out.println(range[0]);
		}
		return range;
	}

	public void collectStudentByProgram(String collCode, AdvisorDb db) {
		/* Set Flags */
		setFlags(collCode, db);

		ArrayList programList = collectPrograms();
		Iterator programIterator = programList.iterator();

		db.resetLastStop(collCode, "", "");

		int advisorcnt = 0;

		while (programIterator.hasNext()) {

			ArrayList listAdvisors = null;
			ListIterator advisorIterator = null;

			String program = (String) programIterator.next();

			boolean advisorsSet = false;

			ArrayList studList = collectStudentDept(collCode, program);

			Advisor advisors = null;
			Iterator studIterator = studList.iterator();
			while (studIterator.hasNext()) {

				AdvisorStudentMatch asm = (AdvisorStudentMatch) studIterator
						.next();
				/**/
				if (program.equals("ACCS-BSC-T-F"))
					System.out.println();
				if (!advisorsSet) {
					listAdvisors = db.selectAdvisors(collCode, program,
							asm.getClas());
					advisorIterator = listAdvisors.listIterator();
					advisorsSet = true;
					advisorcnt = listAdvisors.size();
				}

				String term = asm.getTerm();
				String rghtStr = term.substring(4, 6);
				String lftStr = term.substring(0, 4);

				if (rghtStr.trim().equals("20") || rghtStr.trim().equals("30"))
					asm.setTerm(lftStr + "10");

				/* List of students per major/program */
				if (asm.getMajCode().equals(program)) {
					if (listAdvisors != null)
						assignAdvisor(advisors, advisorIterator, collCode, asm,
								db, advisorcnt, listAdvisors, program,
								asm.getClas());
				}
			}
		}
	}

	private void assignAdvisor(Advisor advisors, ListIterator iteratorAdvisor,
			String collCode, AdvisorStudentMatch asm, AdvisorDb db,
			int advisorcnt, ArrayList listAdvisors, String program, String clas) {

		/* Iterate to NEXT available ADVISOR */

		if (!iteratorAdvisor.hasNext())
			while (iteratorAdvisor.hasPrevious())
				iteratorAdvisor.previous();

		if (iteratorAdvisor.hasNext()) {

			advisors = (Advisor) iteratorAdvisor.next();
			/* Assign ADVISOR to student */
			if (!collCode.equals("SS")) {

				asm.setAdvisorId(advisors.getAdvisorId());
				db.resetLastStop(collCode, "", "");
				db.updateNextStop(advisors, collCode);

				// db.insertAdvisor(asm, this);
			} else {

				/*
				 * if (asm.getMajCode().equals("ACCS-BSC-T-F"))
				 * System.out.println();
				 */

				if (!asm.isConcentration()) {
					ProgramOld nextStaff = db.arrangeNextProgramAdvisor(
							asm.getMajCode(), asm.getTerm(), asm.getClas());
					if (nextStaff != null) {
						asm.setAdvisorId(nextStaff.getAdvisorId());
						// db.insertAdvisor(asm, this);
					}
				}

			}

			if (asm.isConcentration()) {

				if (asm.getDeptCode().equals("SS")) {

					Concentration conc = asm.getConcentration();

					Concentration nextStaff = db.arrangeNextConcAdvisor(
							asm.getMajCode(), conc.getConc(), asm.getTerm(),
							asm.getClas());
					if (nextStaff != null) {
						conc.setAdvisorId(nextStaff.getAdvisorId());
						conc.setSeqid(nextStaff.getSeqid());
						/*
						 * find advisor who teaches this concentration then
						 * update
						 */
						db.insertConcentration(conc, this, getCurrentTerm(), advisors);
					}
				}
			}

			advisorcnt--;
			// System.out.println("Advisor # = " + advisorcnt);

		} else {

			/*
			 * int seqid = 0; seqid = resetAdvisorArray(collCode, db, asm,
			 * seqid);
			 * 
			 * db.insertAdvisor(asm, this);
			 */
			/** Reset last stop */

			/* db.resetLastStop(collCode); */

			/**
			 * Move to next advisor in database sequence and update new stop
			 */
			/** Clear last Stop */
			// db.resetLastStop(collCode);
			/* Update Last Stop */

			/*
			 * db.updateLastStop(collCode, seqid + 1);
			 */
			/** Select new advisor list */
			/*
			 * listAdvisors = db.selectAdvisors(collCode, program, clas);
			 * 
			 * iteratorAdvisor = listAdvisors.listIterator(); while
			 * (iteratorAdvisor.hasPrevious()) iteratorAdvisor.previous();
			 */
		}

	}

	public ArrayList collectStudent(String deptCode, String programCode,
			HashMap departmentMap, HashMap programMap, HashMap clasMap) {

		ArrayList list = new ArrayList();

		try {
			String term = getCurrentTerm();

			PreparedStatement prepStmt = conn
					.prepareStatement(getListOfStudentsByProgramSQL());
			term = term.substring(0, 4);
			prepStmt.setString(1, term);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, "UG");
			prepStmt.setString(4, programCode);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				Student student = new Student();
				student.setFirstName(rs.getString(7));
				student.setLastName(rs.getString(6));
				student.setId(rs.getString(1));
				student.setDept((Department) departmentMap.get(deptCode));

				student.setProgram((Program) programMap.get(programCode));
				student.setLevel((Level) clasMap.get(rs.getString(5)));

				Program program = (Program) programMap.get(programCode);
				if (rs.getString(9) != null) {
					Concentration concentration = new Concentration();
					concentration.setTerm(getCurrentTerm().subSequence(0, 4)
							+ "10");
					concentration.setConc(rs.getString(9));
					concentration
							.setConcDesc(getMajor(concentration.getConc()));
					student.setConcentration(concentration);
					student.setHasConcentration(true);

					/* Student is added to concentration */
					concentration.setStudent(student);
					concentration.setLaststop(false);
					/* Concentration flag set for program */
					program.setHasConcentrations(true);

					/* Concentration is added to the program */
					program.setConcentrationList(concentration);
				} else {

				}

				program.setStudentList(student);
				list.add(student);
			}
			prepStmt.close();
			return list;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	private ArrayList collectStudentDept(String collCode, String program) {

		int localCnt = 0;

		ArrayList list = new ArrayList();
		try {

			String term = getCurrentTerm();
			PreparedStatement prepStmt = conn
					.prepareStatement(getListOfStudents(program));
			term = term.substring(0, 4);
			prepStmt.setString(1, term);
			prepStmt.setString(2, collCode);
			if (!program.equals("N/A")) {
				prepStmt.setString(3, program);
			}
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				localCnt++;
				AdvisorStudentMatch asm = new AdvisorStudentMatch();
				asm.setSpridenId(rs.getString(1));
				asm.setMajCode(rs.getString(2)); /* PROGRAM */
				asm.setDeptCode(rs.getString(4));
				asm.setClas(rs.getString(5));
				asm.setLastname(rs.getString(6));
				asm.setFirstname(rs.getString(7));
				asm.setdCode(rs.getString(10));

				asm.setTerm(getCurrentTerm());

				if (rs.getString(9) != null) {
					Concentration conc = new Concentration();
					conc.setMajCode(rs.getString(2));
					conc.setTerm(getCurrentTerm().subSequence(0, 4) + "10");
					conc.setConc(rs.getString(9));
					conc.setStuID(rs.getString(1));
					asm.setConcentration(conc);

					asm.setConcentration(true);
				}

				
				term = asm.getTerm();
				list.add(asm);
			}
			prepStmt.close();
			System.out.println("No. " + collCode + "  of students " + localCnt
					+ " term =" + term);
			return list;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}
	public void selectAdvisorAssignments(){
		this.advisorDb.changeSchema("advisors");
		ArrayList list = this.advisorDb.selectAdvisorAssignments();
		Iterator ii = list.iterator();
		System.out.println("PROCESSING ADVISORS...");
		while (ii.hasNext()){
		
			Advisor advisor = (Advisor)ii.next();
			this.insertAdvisorInfo(advisor.getAdvisorId(), advisor.getClas(), advisor.getProgramCode(),
			           advisor.getSemester(), advisor.getDeptCode(), advisor.getConcCode());
			
		}
		
		System.out.println("END PROCESSING ADVISORS...");
	}
	public void selectAllAssignments(){
	
		this.advisorDb.changeSchema("advisors");
		ArrayList list = this.advisorDb.selectAllAssignments();
		Iterator ii = list.iterator();
		System.out.println("PROCESSING ALL...");
		while (ii.hasNext()){
			
			Advisor advisor = (Advisor)ii.next();
			
			insertAssignments(advisor.getStudentId(), advisor.getAdvisorId(), advisor.getClas(), advisor.getProgramCode(),
					           advisor.getSemester(), advisor.getDeptCode(), advisor.getConcCode());                 
			
		}
		System.out.println("PROCESSING ALL.");
	}
	
	public void insertAdvisorInfo(String advisor_id, String clas, String program_code,
                                                   String term, String dept_code, String conc_Code){
		String sqlstmt = "INSERT INTO SATURN.UWI_CH_ADV_ASSIGNMENTS (advisor_id, clas, program_code, term, dept_code, concentration_code) VALUES " +
	               "(?,?,?,?,?,?)";
	
	try {
		PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
		prepStmt.setString(1, advisor_id);
		prepStmt.setString(2, clas);
		prepStmt.setString(3, program_code);
		prepStmt.setString(4, term);
		prepStmt.setString(5, dept_code);
		prepStmt.setString(6, conc_Code);
		
		prepStmt.executeUpdate();
		prepStmt.close();
		
	}
	
	catch (Exception ex) {
		ex.printStackTrace();
	}
	}
	public void insertAssignments(String spriden_id, String advisor_id, String clas, String program_code,
			                       String term, String dept_code, String conc_Code){

		String sqlstmt = "INSERT INTO SATURN.UWI_CH_STUDENT_ADV_ASSIGNMENTS (spriden_id, advisor_id, clas, program_code, term, dept_code, concentration_code) VALUES " +
		               "(?,?,?,?,?,?,?)";
		
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, spriden_id);
			prepStmt.setString(2, advisor_id);
			prepStmt.setString(3, clas);
			prepStmt.setString(4, program_code);
			prepStmt.setString(5, term);
			prepStmt.setString(6, dept_code);
			prepStmt.setString(7, conc_Code);
			
			prepStmt.executeUpdate();
			prepStmt.close();
			
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void updateAdvisor(Advisor advisor, String[] terms) {
		String sqlstmt = "UPDATE SGRADVR SET SGRADVR_ADVR_PIDM = ? WHERE SGRADVR_PIDM = ? AND SGRADVR_TERM_CODE_EFF =  ?";
		
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
				for (int i = 0; i < terms.length; i++) {
				
	               if (Integer.parseInt(terms[i]) > Integer.parseInt(advisor.getSemester()))
	               {
					prepStmt.setLong(1,
							advisor.getAdvPidm());
					prepStmt.setLong(2, advisor.getStuPidm());
					prepStmt.setString(3, terms[i]);
					
				   prepStmt.executeUpdate();
				  
				}
			 }
				 prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void insertAdvisor(Advisors advisors) {

		String sqlstmt = "";
		boolean exist = false;
		String term = getCurrentTerm();
		System.out.println("before insert");
		if (!adviseeExist(advisors.getStuPidm(), advisors.getAdvPidm(),
				term)) {
			System.out.println(advisors.getAdvisorId()+ ", " +advisors.getId());
			sqlstmt = "INSERT INTO SGRADVR (SGRADVR_PIDM, SGRADVR_TERM_CODE_EFF, SGRADVR_ADVR_PIDM, SGRADVR_ADVR_CODE, SGRADVR_PRIM_IND,SGRADVR_ACTIVITY_DATE) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";

		} else {
			exist = true;
			sqlstmt = "UPDATE SGRADVR SET SGRADVR_ADVR_PIDM = ? WHERE SGRADVR_PIDM = ? AND SGRADVR_TERM_CODE_EFF = ? AND SGRADVR_ADVR_PIDM = ? AND SGRADVR_ADVR_CODE = ? AND SGRADVR_PRIM_IND = ?";
		}

		NewDateFormatter df = new NewDateFormatter();

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

			if (!exist) {
				prepStmt.setLong(1, advisors.getStuPidm());
				prepStmt.setString(2, term);
				prepStmt.setLong(3, advisors.getAdvPidm());
				prepStmt.setString(4, "ADVR");
				prepStmt.setString(5, "Y");
				prepStmt.setDate(6,
						java.sql.Date.valueOf(df.getSimpleOracleDate()));
			} else {
				prepStmt.setLong(1,
						advisors.getAdvPidm());
				prepStmt.setLong(2, advisors.getStuPidm());
				prepStmt.setString(3, getCurrentTerm().substring(0, 4) + "10");
				prepStmt.setLong(4, advisors.getAdvPidm());
				prepStmt.setString(5, "ADVR");
				prepStmt.setString(6, "Y");
			}

			/** Use when activating the Banner Insert to SGRADVR **/

			prepStmt.executeUpdate();
			prepStmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public String getStudentName(String id) {

		String name = null;
		String selectStatement = "select SPRIDEN_FIRST_NAME, SPRIDEN_LAST_NAME from spriden where spriden_id = ? and SPRIDEN.SPRIDEN_CHANGE_IND is null";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, id);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {

				name = rs.getString(1) + " " + rs.getString(2);

			}

			prepStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return name;
	}
   
	public static void main(String[] args) throws SQLException {

		BannerDb db = new BannerDb(0);
//		AdvisorDb adb = new AdvisorDb("", "admin", "kentish", "owl2");
//		db.setAdvisorDb(adb);
		db.openConn();
		//db.removeAdvisor(db.getPidm("20002859"), "202020");
		db.manualInsert();
		//db.selectAllAssignments();
//		db.selectAdvisorAssignments();
		db.closeConn();
		System.exit(0);
	}

}
