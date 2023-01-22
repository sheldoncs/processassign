package advisor.db;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import process.remote.RemoteService;
import provisioning.concentrations.distribute.AdvisorConcentrations;
import provisioning.concentrations.distribute.StudentConcentrations;
import advisor.util.Advisor;
import advisor.util.AdvisorStudentMatch;
import advisor.util.Advisors;
import advisor.util.Assignments;
import advisor.util.Concentration;
import advisor.util.Department;
import advisor.util.Level;
import advisor.util.MessageLogger;
import advisor.util.Program;
import advisor.util.ProgramOld;
import advisor.util.Success;

public class AdvisorDb {

	protected Connection conn;
	private String term;
	private String schema = "advisors";
   public AdvisorDb() {
	   
   }
	public AdvisorDb(String mysqlconnect, String username, String password,
			String servername) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection("jdbc:mysql://" + servername
					+ ":3305/", username, password);
			conn.setAutoCommit(true);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
    public void connectTestDB(String username, String password,
			String dbname) {
    	try {
			Class.forName("com.mysql.jdbc.Driver");

			conn = DriverManager.getConnection("jdbc:mysql://localhost" 
					+ ":3306/"+dbname, username, password);
			conn.setAutoCommit(true);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
    }
    public ArrayList<Advisors> returnAdvisors(BannerDb db){
    	ArrayList<Advisors> list = new ArrayList<Advisors>();
    	
    	boolean run = false;

		String selectStatement = "select studentid,advisorid from manualadvisors where studentid = '400013064'";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
			   Advisors adv = new Advisors();
			   String str = rs.getString("advisorid").substring(0, 8);
			   adv.setStuPidm(db.getPidm(rs.getString("studentid")));
			   adv.setAdvPidm(db.getPidm(rs.getString("advisorid").substring(0, 8)));
			   adv.setStudentId(rs.getString("studentid"));
			   adv.setAdvisorId(rs.getString("advisorid").substring(0, 8));
			   list.add(adv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	return list;
    }
	public void changeSchema(String schema) {
		this.schema = schema;
	}

	public void resetLastStop(String dCode) {

		try {

			String selectStatement = "UPDATE programdetails set laststop = ? WHERE dcode = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setBoolean(1, false);
			prepStmt.setString(2, dCode);

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void resetLastStop(String deptCode, String programCode,
			String levelCode) {

		try {
			String selectStatement = ""; 
			
			selectStatement = "UPDATE programdetails set laststop = ? WHERE dcode = ? AND programcode = ?";
			
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setBoolean(1, false);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, programCode);
			
			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
    public ArrayList getAdvisorByProgramByLevel(String program, String level, String deptCode, String semester){
    	
    	ArrayList list = new ArrayList();
    	boolean extraParams = false;
    	
    	BannerDb bannerDb = new BannerDb(0);
    	bannerDb.openConn();
    	
    	String selectStatement = "";
    
     if (deptCode.equals("810") || deptCode.equals("808")){
    		
    		if (level.equals("L1")){
    			selectStatement  =  "SELECT DISTINCT a.advisorid, a.firstname,a.lastname,b.programcode  FROM advisors.advisor a, advisors.advisorlevel b "
    	    			+ "where a.advisorid = b.advisorid and b.semester = ? and b.clas = ? and b.deptcode = ?";
    		} else {
    			selectStatement = "SELECT DISTINCT a.advisorid, a.firstname,a.lastname,b.programcode  FROM advisors.advisor a, advisors.advisorlevel b "
    	    			+ "where a.advisorid = b.advisorid and b.semester = ? and b.clas = ? and b.deptcode = ? and b.programcode = ? ";
    			extraParams = true;
    		}
    		
    	} else {
    		   extraParams = true;
    	       selectStatement = "SELECT DISTINCT a.advisorid, a.firstname,a.lastname,b.programcode  FROM advisors.advisor a, advisors.advisorlevel b "
   	    			+ "where a.advisorid = b.advisorid and b.semester = ? and b.clas = ? and b.deptcode = ? and b.programcode = ? ";
    	}
    	
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
    		prepStmt.setString(1, semester);
			prepStmt.setString(2, level);
			prepStmt.setString(3, deptCode);
			
			if (extraParams)
			  prepStmt.setString(4, program);
			
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
    		
			while (rs.next()) {
			   Advisor advisor = new Advisor();
			   Program prog = new Program();
			   advisor.setAdvisorId(rs.getString(1));
			   advisor.setFirstName(rs.getString(2));
			   advisor.setLastName(rs.getString(3));
			   
			   advisor.setClas(level);
			   list.add(advisor);
			}
			bannerDb.closeConn();
			rs.close();
			prepStmt.close();
			
    	} catch (Exception e){
    		e.printStackTrace();
    	}

    	return list;
    }
    
    public void resetAllToZero(Advisor advisor){
    	
    	boolean allZero = true;
    	
    	String selectStatement = "update `advisors`.`advisorlevel` SET laststop = ? where clas = ? and programcode = ? and deptcode = ? and semester = ?";
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
    		prepStmt.setInt(1, 0);
    		prepStmt.setString(2, advisor.getClas());
			prepStmt.setString(3, advisor.getProgramCode());
			prepStmt.setString(4, advisor.getDeptCode());
			prepStmt.setString(5, advisor.getSemester());
			
			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			
			 
			
			
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	
    }
    public void resetToZero(Concentration c){
    	
    	boolean allZero = true;
    	
    	String selectStatement = "update `advisors`.`advisorlevelconcentration` SET laststop = ? where level = ? and programcode = ? and deptcode = ? and semester = ?";
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
    		prepStmt.setInt(1, 0);
    		prepStmt.setString(2, c.getClas());
			prepStmt.setString(3,c.getProgramCode());
			prepStmt.setString(4, c.getDeptCode());
			prepStmt.setString(5, c.getTerm());
			
			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			
			
			
    	}catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	
    }
    public ArrayList getAdvisorByProgramMinusLevel(String program, String level, String deptCode, String semester){
    	
    	ArrayList list = new ArrayList();
    	
    	String selectStatement = "SELECT distinct a.advisorid, a.firstname,a.lastname, b.laststop,b.programcode, b.clas FROM advisors.advisor a, advisors.advisorlevel b "
    			+ "where a.advisorid = b.advisorid and b.programcode = ? and b.semester = ? and b.deptcode = ? and b.clas = ?";
    	
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
    		prepStmt.setString(1, program);
			prepStmt.setString(2, semester);
			prepStmt.setString(3, deptCode);
			prepStmt.setString(4, level);
			
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
    		
			while (rs.next()) {
			   Advisor advisor = new Advisor();
			   advisor.setAdvisorId(rs.getString(1));
			   advisor.setFirstName(rs.getString(2));
			   advisor.setLastName(rs.getString(3));
			   advisor.setLastStop(rs.getInt(4));
			   advisor.setProgramCode(rs.getString(5));
			   advisor.setClas(rs.getString(6));
			   advisor.setSemester(semester);
			   
			   list.add(advisor);
			}
			
			rs.close();
			prepStmt.close();
			
    	} catch (Exception e){
    		e.printStackTrace();
    	}

    	return list;
    }
    public ArrayList<Advisor> getAdvisorByProgramByConcentration(String program, String level, String deptCode, String semester, String conc){
    	
    	
    	ArrayList<Advisor> list = new ArrayList<Advisor>();
    	String selectStatement = "";
    	boolean extraParams = false;
    	
    	if (deptCode.equals("810")){
    		
    		if (level.equals("L1")){
    			selectStatement  = "SELECT distinct a.advisorid, a.firstname,a.lastname, b.level  FROM advisors.advisor a, advisors.advisorlevelconcentration b "
    	    			+ "where a.advisorid = b.advisorid and b.semester = ? and b.deptcode = ?";
    		} else {
    			selectStatement = "SELECT distinct a.advisorid, a.firstname,a.lastname, b.level  FROM advisors.advisor a, advisors.advisorlevelconcentration b "
    	    			+ "where a.advisorid = b.advisorid and b.semester = ? and b.deptcode = ? and b.programcode = ? and b.conc = ?";
    			extraParams = true;
    		}
    		
    	} else {
    		   extraParams = true;
    	       selectStatement = "SELECT distinct a.advisorid, a.firstname,a.lastname, b.level  FROM advisors.advisor a, advisors.advisorlevelconcentration b "
    			     + "where a.advisorid = b.advisorid and b.semester = ? and b.deptcode = ? and b.programcode = ? and b.conc = ? and b.level = ?";
    	}
    	
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
    		
			prepStmt.setString(1, semester);
			prepStmt.setString(2, deptCode);
			if (extraParams){
			  prepStmt.setString(3, program);
			  prepStmt.setString(4, conc);
			  prepStmt.setString(5, level);
			}
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
    		
			while (rs.next()) {
			   Advisor advisor = new Advisor();
			   advisor.setAdvisorId(rs.getString(1));
			   advisor.setFirstName(rs.getString(2));
			   advisor.setLastName(rs.getString(3));
			   advisor.setClas(rs.getString(4));
			   advisor.setConcCode(conc);
			   list.add(advisor);
			}
			
			rs.close();
			prepStmt.close();
			
    	} catch (Exception e){
    		e.printStackTrace();
    	}

    	return list;
    }
    public boolean programAssigned(String progCode){
    	
		boolean found = false;
		String sqlstmt = "SELECT programcode FROM advisorlevel where programcode = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

			prepStmt.setString(1, progCode);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
               found = true;
			}
		} catch (Exception e) {
            e.printStackTrace();
		}
		
		return found;
	}
   public boolean concentrationAssigned(String progCode, String conc){
    	
		boolean found = false;
		String sqlstmt = "SELECT programcode FROM advisorlevelconcentration where programcode = ? and conc = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

			prepStmt.setString(1, progCode);
			prepStmt.setString(2, conc);
			
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
               found = true;
			}
		} catch (Exception e) {
            e.printStackTrace();
		}
		
		return found;
	}
	public ProgramOld arrangeNextProgramAdvisor(String majCode, String term,
			String clas) {

		boolean found = false;
		boolean lastStopSet = false;
		String id = null;

		String selectStatement = "select advisorid,majcode,laststop, semester from teachingprogram where majcode = ? and semester = ? and clas = ?";

		HashMap hmap = new HashMap();
		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, majCode);
			prepStmt.setString(2, term);
			prepStmt.setString(3, clas);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				found = true;
				ProgramOld program = new ProgramOld();
				program.setAdvisorId(rs.getString(1));
				id = program.getAdvisorId();
				program.setMajCode(rs.getString(2));
				program.setLaststop(rs.getBoolean(3));
				if (rs.getBoolean(3))
					lastStopSet = true;
				program.setTerm(rs.getString(4));
				hmap.put(rs.getString(1), program);

			}
			if (!lastStopSet && found) {
				ProgramOld program = (ProgramOld) hmap.get(id);
				program.setLaststop(true);
				hmap.put(id, program);
				updateProgram(hmap);
			}

			ProgramOld p = arrangeProgram(hmap);

			if (found && p != null)
				p.setSeqid(findSequence(p.getAdvisorId()));

			prepStmt.close();

			return p;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}

	/***************************************************/
	/** Find the Next Advisor for program Concentration **/
	/***************************************************/
	public Concentration arrangeNextConcAdvisor(String majCode, String conc,
			String term, String clas) {

		boolean found = false;
		boolean lastStopSet = false;
		String id = null;

		String selectStatement = "select advisorid,majcode,concentration,laststop, semester from teachingconcentration where majcode = ? "
				+ "and concentration = ? and semester = ? and clas = ?";

		HashMap hmap = new HashMap();
		try {

			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, majCode);
			prepStmt.setString(2, conc);
			prepStmt.setString(3, term);
			prepStmt.setString(4, clas);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				found = true;
				Concentration concentation = new Concentration();
				concentation.setAdvisorId(rs.getString(1));
				id = concentation.getAdvisorId();
				concentation.setMajCode(rs.getString(2));
				concentation.setConc(rs.getString(3));
				concentation.setLaststop(rs.getBoolean(4));
				if (rs.getBoolean(4))
					lastStopSet = true;
				concentation.setTerm(rs.getString(5));
				hmap.put(rs.getString(1), concentation);

			}

			if (!lastStopSet && found) {
				Concentration c = (Concentration) hmap.get(id);
				c.setLaststop(true);
				hmap.put(id, c);
				updateConcentration(hmap);
			}

			Concentration c = arrangeConcentration(hmap);

			if (found && c != null)
				c.setSeqid(findSequence(c.getAdvisorId()));

			prepStmt.close();

			return c;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}
    public String getOverideSemester(){
    	
    	String selectStatement = "select term, override from overrideterm";
    	String term = null;
    	try {
    		
    		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				boolean override =  rs.getBoolean(2);
				if (override)
				  term = rs.getString(1);
			}
			rs.close();
			prepStmt.close();
            
    		
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	return term; 
    }
	private int findSequence(String id) {

		int seq = 0;
		String selectStatement = "select sequenceid from advisor where advisorid = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, id);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				seq = rs.getInt(1);
			}
			prepStmt.close();

			return seq;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	private ProgramOld arrangeProgram(HashMap hmap) {

		Set kSet = hmap.keySet();
		boolean stopFound = false;
		int size = kSet.size();

		int cnt = 0;

		Iterator kIterator = kSet.iterator();
		while (kIterator.hasNext()) {
			cnt++;
			String key = (String) kIterator.next();

			ProgramOld program = (ProgramOld) hmap.get(key);
			if (program.isLaststop()) {
				if (cnt < size) {
					if (!stopFound == true) {
						stopFound = true;
						((ProgramOld) hmap.get(key)).setLaststop(false);
					} else {
						program.setLaststop(true);

						break;
					}
				} else if (cnt == size) {
					if (!stopFound) {
						stopFound = true;
						((ProgramOld) hmap.get(key)).setLaststop(false);
						resetProgramStop(hmap);
						break;
					}

				}
			} else if (!program.isLaststop()) {
				if (stopFound) {
					((ProgramOld) hmap.get(key)).setLaststop(true);

					break;
				}
			}

		}

		updateProgram(hmap);

		return getCurrentProgramAdvisor(hmap);
	}

	/*************************************************************/
	/** Update the program flag table to indicate last stop **/
	/*************************************************************/

	private void updateProgram(HashMap hmap) {

		Set set = hmap.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			ProgramOld concentration = (ProgramOld) hmap.get(it.next()
					.toString());

			try {

				String selectStatement = "UPDATE teachingprogram set laststop = ? WHERE  majcode = ? and semester = ? and advisorid = ?";
				PreparedStatement prepStmt = conn
						.prepareStatement(selectStatement);

				prepStmt.setBoolean(1, concentration.isLaststop());
				prepStmt.setString(2, concentration.getMajCode());
				prepStmt.setString(3, concentration.getTerm());
				prepStmt.setString(4, concentration.getAdvisorId());

				prepStmt.execute("use " + schema);

				prepStmt.executeUpdate();

				prepStmt.close();

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}

	}

	/**************************************************/
	/** Select next stop flag, indicating next Advisor **/
	/**************************************************/
	private Concentration arrangeConcentration(HashMap hmap) {

		Set kSet = hmap.keySet();
		boolean stopFound = false;
		int size = kSet.size();

		int cnt = 0;

		Iterator kIterator = kSet.iterator();
		while (kIterator.hasNext()) {
			cnt++;
			String key = (String) kIterator.next();

			Concentration concentration = (Concentration) hmap.get(key);
			if (concentration.isLaststop()) {
				if (cnt < size) {
					if (!stopFound == true) {
						stopFound = true;
						((Concentration) hmap.get(key)).setLaststop(false);
					} else {
						concentration.setLaststop(true);

						break;
					}
				} else if (cnt == size) {
					if (!stopFound) {
						stopFound = true;
						((Concentration) hmap.get(key)).setLaststop(false);
						resetStop(hmap);
						break;
					}

				}
			} else if (!concentration.isLaststop()) {
				if (stopFound) {
					((Concentration) hmap.get(key)).setLaststop(true);

					break;
				}
			}

		}

		updateConcentration(hmap);

		return getCurrentConcAdvisor(hmap);
	}

	/** Reset Concentration Flag **/
	public void resetConcentrationStartFlag(String programCode, String conc,
			String semester) throws SQLException {

		String selectStatement = "UPDATE teachingconcentration set laststop = ? WHERE  programCode = ? and semester = ? and concentration = ?";
		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

		prepStmt.setBoolean(1, false);
		prepStmt.setString(2, programCode);
		prepStmt.setString(3, semester);
		prepStmt.setString(4, conc);

		prepStmt.execute("use " + schema);

		prepStmt.executeUpdate();
		prepStmt.close();
		
	}

	public void updateConcentrationStartFlag(String advisorId,
			String programCode, String conc, String semester)
			throws SQLException {

		String selectStatement = "UPDATE teachingconcentration set laststop = ? WHERE  programCode = ? and semester = ? and concentration = ? and advisorid = ?";
		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

		prepStmt.setBoolean(1, true);
		prepStmt.setString(2, programCode);
		prepStmt.setString(3, semester);
		prepStmt.setString(4, conc);
		prepStmt.setString(5, advisorId);

		prepStmt.execute("use " + schema);

		prepStmt.executeUpdate();

		prepStmt.close();

	}

	/*************************************************************/
	/** Update the concentration flag table to indicate last stop **/
	/**
	 * @throws SQLException
	 ***********************************************************/

	private void updateConcentration(HashMap hmap) {

		Set set = hmap.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Concentration concentration = (Concentration) hmap.get(it.next()
					.toString());

			try {

				String selectStatement = "UPDATE teachingconcentration set laststop = ? WHERE  majcode = ? and semester = ? and advisorid = ?";
				PreparedStatement prepStmt = conn
						.prepareStatement(selectStatement);

				prepStmt.setBoolean(1, concentration.isLaststop());
				prepStmt.setString(2, concentration.getMajCode());
				prepStmt.setString(3, concentration.getTerm());
				prepStmt.setString(4, concentration.getAdvisorId());

				prepStmt.execute("use " + schema);

				prepStmt.executeUpdate();

				prepStmt.close();

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}

	}

	/****************************************************/
	/** Select current Advisor for Concentration Program **/
	/****************************************************/

	private Concentration getCurrentConcAdvisor(HashMap hmap) {

		Set set = hmap.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {

			Concentration c = (Concentration) hmap.get(it.next().toString());

			if (c.isLaststop())
				return c;
		}
		return null;
	}

	private ProgramOld getCurrentProgramAdvisor(HashMap hmap) {

		Set set = hmap.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {

			ProgramOld c = (ProgramOld) hmap.get(it.next().toString());

			if (c.isLaststop())
				return c;
		}
		return null;
	}

	/***********************************************************/
	/** Reset last stop at the end of the list - Concentration **/
	/***********************************************************/

	private Concentration resetStop(HashMap hmap) {

		Set kSet = hmap.keySet();
		Concentration concentration = null;

		Iterator kIterator = kSet.iterator();
		if (kIterator.hasNext()) {
			String key = kIterator.next().toString();
			((Concentration) hmap.get(key)).setLaststop(true);

		}
		return concentration;
	}

	/*****************************************************/
	/** Reset last stop at the end of the list - Program **/
	/*****************************************************/

	private ProgramOld resetProgramStop(HashMap hmap) {

		Set kSet = hmap.keySet();
		ProgramOld program = null;

		Iterator kIterator = kSet.iterator();
		if (kIterator.hasNext()) {
			String key = kIterator.next().toString();
			((ProgramOld) hmap.get(key)).setLaststop(true);

		}
		return program;
	}

	public Advisor flagFirstAdvisorRecord(String collCode) {

		Advisor ads = new Advisor();
		try {

			String selectStatement = "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,laststop from advisor where deptcode = ? ";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, collCode);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			rs.first();
			ads.setDeptCode(rs.getString(1));
			ads.setProgramCode(rs.getString(2));
			// ads.setLevl(rs.getString(3));
			ads.setSemester(rs.getString(4));
			ads.setSequenceId(rs.getInt(5));
			ads.setAdvisorId(rs.getString(6));
			ads.setLastStop(rs.getInt(7));

			updateNextStop(ads, collCode);

			prepStmt.close();

			return ads;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public void updateNextStop(Advisor adv, String deptCode) {

		try {

			String selectStatement = "UPDATE programdetails set laststop = ? WHERE dcode = ? and advisorid = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setBoolean(1, true);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, adv.getAdvisorId());

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public Advisor lastAdvisorRecord(String collCode) {

		Advisor ads = new Advisor();

		try {

			String selectStatement = "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,laststop from advisor where deptcode = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			// prepStmt.setString(1, term);
			prepStmt.setString(1, collCode);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			rs.last();

			ads.setDeptCode(rs.getString(1));
			ads.setProgramCode(rs.getString(2));
			// ads.setLevl(rs.getString(3));
			ads.setSemester(rs.getString(4));
			ads.setSequenceId(rs.getInt(5));
			ads.setAdvisorId(rs.getString(6));
			ads.setLastStop(rs.getInt(7));
			
			prepStmt.close();
			
			return ads;

		} catch (SQLException ex) {
			ex.printStackTrace();

		}

		return null;
	}

	public Advisor getAdvisorBySequence(String collCode, int seqid) {

		Advisor ads = new Advisor();
		try {

			String selectStatement = "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,laststop from advisor where deptCode = ? and sequenceid = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			prepStmt.setString(1, collCode);
			prepStmt.setInt(2, seqid);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {

				ads.setDeptCode(rs.getString(1));
				ads.setProgramCode(rs.getString(2));
				// ads.setLevl(rs.getString(3));
				ads.setSemester(rs.getString(4));
				ads.setSequenceId(rs.getInt(5));
				ads.setAdvisorId(rs.getString(6));

			}
			prepStmt.close();
			return ads;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public int getStartSequence(String collCode) {

		try {

			String selectStatement = "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,laststop from advisor where laststop = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			// prepStmt.setString(1, term);
			prepStmt.setBoolean(1, true);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next())
				return rs.getInt(5);
			prepStmt.close();  
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0;

	}
    
	public void updateLastStopConcentration(Concentration concentration) {
		 
		String sqlstmt = "UPDATE advisorlevelconcentration SET laststop = ? WHERE level = ?  and programcode = ? and conc =  ? and semester = ? and laststop = ?";
		
		try {

		
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setBoolean(1, concentration.isLaststop());
			prepStmt.setString(2, concentration.getClas());
			prepStmt.setString(3, concentration.getProgramCode());
			prepStmt.setString(4, concentration.getConc());
			prepStmt.setString(5, concentration.getTerm());
			prepStmt.setBoolean(6, true);
			
			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();
			prepStmt.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void updateLastStop(Advisor advisor) {
		try {

			String sqlstmt = "UPDATE advisorlevel SET laststop = ? WHERE advisorid = ? AND clas = ?  and programcode = ? and semester = ?";
           
			
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setInt(1, advisor.getLastStop());
			prepStmt.setString(2, advisor.getAdvisorId());
			prepStmt.setString(3, advisor.getClas());
			prepStmt.setString(4, advisor.getProgramCode());
			prepStmt.setString(5, advisor.getSemester());

			prepStmt.execute("use " + schema);
			
			prepStmt.executeUpdate();
			prepStmt.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public HashMap selectAllAdvisors(HashMap clasList) throws SQLException {

		HashMap map = new HashMap();

		String selectStatement = "select distinct deptcode,programcode,clas,semester,sequenceid,advisorid,laststop from advisor";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Advisor advisor = new Advisor();
			advisor.setDeptCode(rs.getString(1));
			advisor.setProgramCode(rs.getString(2));
			advisor.setClas(rs.getString(3));
			advisor.setSemester(rs.getString(4));
			advisor.setAdvisorId(rs.getString(6));
			advisor.setLastStop(rs.getInt(7));
			if (advisor.getProgramCode() != null) {
				if (advisor.getProgramCode().equals("MANG-BSC-C-F"))
					System.out.println();
			}
			getLevelList(advisor.getAdvisorId(), advisor.getProgramCode(),
					clasList, advisor);

			ArrayList list = selectAllAdvisorsWithConcentrations(
					advisor.getAdvisorId(), advisor.getSemester());
			if (list.isEmpty())
				advisor.setConcentrationList(list);

			if (advisor.getAdvisorId() != null)
				map.put(advisor.getAdvisorId(), advisor);
		}
		prepStmt.close();
		return map;

	}

	private ArrayList selectAllAdvisorsWithConcentrations(String advisorid,
			String term) throws SQLException {

		ArrayList list = new ArrayList();

		String selectStatement = "select distinct advisorid,programcode,concentration,laststop from teachingconcentration where "
				+ "semester = ? and advisorid = ?";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		prepStmt.setString(1, term);
		prepStmt.setString(2, advisorid);

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Concentration concentration = new Concentration();
			
			concentration.setConc(rs.getString(2));
			concentration.setConcDesc(rs.getString(3));
			concentration.setLaststop(rs.getBoolean(4));
			ArrayList levelList = getConcentrationLevel(advisorid,
					concentration.getConc(),term);
			concentration.setLevelList(levelList);
		}
		prepStmt.close();
		return list;

	}
    
	private ArrayList getConcentrationLevel(String advisorId, String conc, String semester)
			throws SQLException {

		ArrayList list = new ArrayList();

		String selectStatement = "select distinct advisorid,conc,level from advisorlevelconcentration where "
				+ "advisorid = ? and conc = ? and semester = ?";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		prepStmt.setString(1, advisorId);
		prepStmt.setString(2, conc);
		prepStmt.setString(3, semester);

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Level level = new Level();
			level.setClasCode(rs.getString(3));

			list.add(level);
		}
		prepStmt.close();
		return list;

	}

	public void setupAdvisorConcentrationList(String progCode, Concentration c,
			String term) throws SQLException {

		
		String selectStatement = "select distinct advisorid, laststop, programCode, concentration from teachingconcentration where "
				+ "programCode = ? and semester = ? and concentration = ?";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		prepStmt.setString(1, progCode);
		prepStmt.setString(2, term);
		prepStmt.setString(3, c.getConc());

		if (c.getConc().equals("HRM"))
			System.out.println();
		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {

			Advisor advisor = new Advisor();
			advisor.setAdvisorId(rs.getString(1));
			advisor.setLastStop(rs.getInt(2));
			
			String conc = rs.getString(4).trim();
			setupAdvisorConcentrationLevel(progCode, conc, advisor, term);
			c.setAdvisorList(advisor);
		}
		prepStmt.close();
	}

	public void setupAdvisorList(Program program, Department dept, BannerDb db)
			throws SQLException {
		String selectStatement = "";
        
		if (program.getConcentrationList().size() == 0)
			//selectStatement = "select distinct a.advisorid, b.laststop, b.programCode,a.firstname,a.lastname from advisor a, programdetails b where "
				///	+ " b.advisorid = a.advisorid" + " and b.programCode = ? and b.semester = ?";
		selectStatement = "select distinct a.advisorid, b.laststop, b.programCode,a.firstname,a.lastname from advisor a, advisorlevel b where "
				+ " b.advisorid = a.advisorid" + " and b.programCode = ? and b.semester = ?";
		else
			selectStatement = "select distinct a.advisorid, b.laststop, b.programCode,a.firstname,a.lastname from advisor a,advisorlevelconcentration b where "
					+ " b.advisorid = a.advisorid "+ "and b.programcode = ? and b.semester = ?";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

		prepStmt.setString(1, program.getProgramCode());
		prepStmt.setString(2, program.getTerm());
		
        program.setDeptartment(dept);
		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			
			
			
			Advisor advisor = new Advisor();
			advisor.setAdvisorId(rs.getString(1));
			if (advisor.getAdvisorId() == "20004607")
				 System.out.println();
			
			advisor.setLastStop(rs.getInt(2));
			if (program != null)
				advisor.setProgramCode(program.getProgramCode());
			advisor.setFirstName(rs.getString(4));
			advisor.setLastName(rs.getString(5));
			if (program != null)
				program.setAdvisorList(advisor);
		
			setupAdvisorLevel(advisor, program, db);
			
		}
		prepStmt.close();
	}

	private void setupAdvisorLevel(Advisor advisor, Program program, BannerDb db)
			throws SQLException {

		String selectStatement = "select distinct clas from advisorlevel where advisorid = ? "
				+ "and programCode = ? and semester = ? and deptCode = ?";
		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		prepStmt.setString(1, advisor.getAdvisorId());
		prepStmt.setString(2, program.getProgramCode());
		prepStmt.setString(3, program.getTerm());
		Department dept = program.getDeptartment();
		prepStmt.setString(4, dept.getDeptCode());

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			Level level = db.getSpecificClas(rs.getString(1));
			advisor.setLevelList(level);
		}
		prepStmt.close();
	}

	private void setupAdvisorConcentrationLevel(String progCode, String conc,
			Advisor advisor, String semester) throws SQLException {

		String selectStatement = "select distinct advisorid, level from advisorlevelconcentration where "
				+ "programCode = ? and advisorid = ? and conc = ? and semester = ?";

		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		String advisorId = advisor.getAdvisorId();
		prepStmt.setString(1, progCode);
		prepStmt.setString(2, advisorId);
		prepStmt.setString(3, conc);
		prepStmt.setString(4, semester);
		//
		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {

			Level level = new Level();
			level.setClasCode(rs.getString(2));
			advisor.setLevelList(level);

		}
		prepStmt.close();
	}

	private void getLevelList(String advisorid, String programCode,
			HashMap clasList, Advisor advisor) throws SQLException {

		String selectStatement = "select distinct advisorid,clas,programcode from advisorlevel WHERE advisorid = ? AND programcode = ?";
		PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
		prepStmt.setString(1, advisorid);
		prepStmt.setString(2, programCode);

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {
			Level level = new Level();
			level.setClasCode(rs.getString(2));
			Level clasLevel = (Level) clasList.get(level.getClasCode());
			level.setClasDesc(clasLevel.getClasDesc());
			advisor.setLevelList(level);
		}
		prepStmt.close();
	}

	public ArrayList selectAdvisors(String collCode, String program, String clas) {
		ArrayList list = new ArrayList();
		String selectStatement = "";

		try {

			if ((program == "") && (clas == "")) {
				selectStatement = "select distinct deptcode,programcode,levl,semester,sequenceid,advisorid,laststop from advisor "
						+ "where deptcode = ? ";
			} else {
				/*
				 * selectStatement =
				 * "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,laststop from advisor "
				 * + "where deptcode = ? and majcode = ? and clas = ?";
				 */
				selectStatement = "SELECT distinct s.deptcode,t.programcode,s.clas,s.semester,s.sequenceid,s.advisorid,s.laststop FROM advisor s, "
						+ "teachingprogram t "
						+ "where t.advisorid = s.advisorid "
						+ "and s.deptcode = ? and t.programcode = ? and t.clas = ?";

			}
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			// prepStmt.setString(1, term);
			prepStmt.setString(1, collCode);
			if (program != "" && clas != "") {
				prepStmt.setString(2, program);
				prepStmt.setString(3, clas);
			}

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				// if (startSequence <= rs.getInt(5)) {

				Advisor ads = new Advisor();
				ads.setDeptCode(rs.getString(1));
				ads.setProgramCode(rs.getString(2));
				// ads.setLevl(rs.getString(3));
				ads.setSemester(rs.getString(4));
				ads.setSequenceId(rs.getInt(5));
				ads.setAdvisorId(rs.getString(6));

				list.add(ads);
				// }
			}
			prepStmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public ArrayList selectSpecifiedAdvisors(String deptCode, String majCode) {

		ArrayList list = new ArrayList();
		String selectStatement = "select distinct deptcode,majcode,levl,semester,sequenceid,advisorid,toConcentration from advisor where deptcode = ? and majcode = ? order by deptcode, majcode";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, deptCode);
			prepStmt.setString(2, majCode);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				Advisor ads = new Advisor();
				ads.setDeptCode(rs.getString(1));
				ads.setProgramCode(rs.getString(2));
				// ads.setLevl(rs.getString(3));
				ads.setSemester(rs.getString(4));
				ads.setSequenceId(rs.getInt(5));
				ads.setAdvisorId(rs.getString(6));
				ads.setToConcentration(rs.getBoolean(7));

				list.add(ads);
			}
			prepStmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;

	}

	public ArrayList pullStudentConcentrations(String majCode, String term) {
		ArrayList sif = new ArrayList();

		String selectStatement = "select distinct term,majcode,assignmentid,programme from studentconcentration "
				+ "where majCode = ? and term = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, majCode);
			prepStmt.setString(2, term);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				StudentConcentrations sc = new StudentConcentrations();
				sc.setTerm(rs.getString(1));
				sc.setMajCode(rs.getString(2));
				sc.setAssignmentID(rs.getInt(3));
				sc.setProgramme(rs.getString(4));
				sif.add(sc);
			}
			prepStmt.close();
			return sif;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return sif;
	}

	public ArrayList pullAdvisorConcentrations(BannerDb db) {

		ArrayList list = new ArrayList();

		String selectStatement = "select distinct term,majcode,sequenceid from advisorconcentration ";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {

				AdvisorConcentrations ac = new AdvisorConcentrations();
				ac.setMajCode(rs.getString(2));
				ac.setSequenceID(rs.getInt(3));
				list.add(ac);

			}
			prepStmt.close();
			return list;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public String findProgramme(int sequenceid) {

		String selectStatement = "select majcode as programme from advisor where sequenceid = ?";
		String programme = "";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, sequenceid);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				programme = rs.getString(1);
			}
			prepStmt.close();
			return programme;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return programme;
	}

	public void updateStudentConcentration(StudentConcentrations sc) {

		try {

			String sqlstmt = "UPDATE studentconcentration SET advseqid = ? WHERE term = ? AND assignmentid = ? AND majcode = ?";

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setInt(1, sc.getSequenceId());
			prepStmt.setString(2, sc.getTerm());
			prepStmt.setInt(3, sc.getAssignmentID());
			prepStmt.setString(4, sc.getMajCode());

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String getAdvisorID(int seqid) {

		String advisorid = "";
		String sqlstmt = "SELECT advisorid FROM advisor WHERE sequenceid = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setInt(1, seqid);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				advisorid = rs.getString(1);
			}
			prepStmt.close();
			return advisorid;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return advisorid;
	}

	public ArrayList<Assignments> getAdvisorAssignments(BannerDb db, String deptCode)
			throws SQLException {

		ArrayList<Assignments> assignList = new ArrayList<Assignments>();

		String sqlstmt = "SELECT spridenid,advisorid,firstname, lastname, clas,programcode,deptcode,studentType FROM advisorassign where term = ? and deptcode = ?";
		PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

		prepStmt.setString(1, db.getCurrentTerm());
		prepStmt.setString(2, deptCode);

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {

			Assignments assign = new Assignments();
			assign.setStudentId(rs.getString(1));
			assign.setStudentName(db.getStudentName(assign.getStudentId()));
			assign.setAdvisorId(rs.getString(2));
			assign.setAdvisorName(rs.getString(3) + " " + rs.getString(4));
			assign.setClas(rs.getString(5));
			assign.setProgram(db.getSpecificProgram(rs.getString(6)));
			assign.setDepartment(db.getSpecificDepartment(rs.getString(7)));
			assign.setStudentType(rs.getString(8));
			assignList.add(assign);
		}
		prepStmt.close();
		return assignList;

	}

	public ArrayList getAdvisorAssignments(BannerDb db, Department dept)
			throws SQLException {

		ArrayList assignList = new ArrayList();

		String sqlstmt = "SELECT stuid,advisorid,programcode,clas, deptcode, conc, studenttype FROM advisorconcentration where term = ? and deptcode = ?";
		PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);

		prepStmt.setString(1, db.getCurrentTerm());
		prepStmt.setString(2, dept.getDeptCode());

		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();
		while (rs.next()) {

			Assignments assign = new Assignments();
			assign.setStudentId(rs.getString(1));
			assign.setStudentName(db.getStudentName(assign.getStudentId()));
			assign.setAdvisorId(rs.getString(2));
			assign.setAdvisorName(getAdvisorName(assign.getAdvisorId()));
			assign.setClas(null);
			String progCode = rs.getString(3);
			assign.setProgram(db.getSpecificProgram(progCode));
			assign.setDepartment(db.getSpecificDepartment(rs.getString(5)));
			assign.setConcentration(db.getMajor(rs.getString(6)));
		    assign.setStudentType(rs.getString(7));
			assignList.add(assign);
		}
		prepStmt.close();
		return assignList;

	}

	private String getAdvisorName(String id) throws SQLException {
		String name = "";
		String sqlstmt = "SELECT firstname, lastname FROM advisor where advisorid = ?";
		PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
		prepStmt.setString(1, id);
		
		prepStmt.execute("use " + schema);
		ResultSet rs = prepStmt.executeQuery();
		if (rs.next()) {
			
			name = rs.getString(1) + " " + rs.getString(2);
		}
		prepStmt.close();
		return name;
		
	}
    public HashMap getContinuousAssignments(){
    	
    	HashMap map = new HashMap();
    	int cnt = 0;
    	String sqlstmt = "SELECT spridenid FROM advisorassign WHERE studenttype = 'C'";
    	try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()){
				cnt++;
				map.put(rs.getString(1), rs.getString(1));
			}
			
			MessageLogger.out.println("total assigned in mysql = " +cnt);
    	}catch (Exception ex) {
			ex.printStackTrace();
		}
    	
    	return map;
    }
    
	public String getStudentID(int assignid) {

		String studentid = "";
		String sqlstmt = "SELECT spridenid FROM advisorassign WHERE assignmentid = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setInt(1, assignid);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				studentid = rs.getString(1);
			}
			prepStmt.close();
			return studentid;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return studentid;
	}

	public int getMaxRows() {
		int maxRows = 0;
		try {

			String selectStatement = "select distinct * from advisor ";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.execute("use " + schema);
			prepStmt.executeQuery();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return maxRows;

	}
	public void cleanupAdvisorlevel(BannerDb db, String term) {
		 String deleteStatement = "delete from advisorlevel";
		 try {
			 PreparedStatement prepStmt = conn.prepareStatement(deleteStatement);
			 prepStmt.execute("use " + schema);
	         prepStmt.executeUpdate();
	         prepStmt.close();
		 }catch (Exception e) {
	 			
	 			e.printStackTrace();
	 	}
	}
    public void cleanupAdvisorleveWithConcentration(BannerDb db, String term) {
    	String deleteStatement = "delete from advisorlevelconcentration";
        try {
        	PreparedStatement prepStmt = conn.prepareStatement(deleteStatement);
        	prepStmt.execute("use " + schema);
        	prepStmt.executeUpdate();
        	prepStmt.close();
		}catch (Exception e) {		
	 			e.printStackTrace();
	 	}
	}
    public void  insertAdvisorleveWithConcentration(Advisor advisor, BannerDb db) {
    	 String insertStatement = "insert into advisorlevelconcentration (advisorid,level,conc,programcode,deptcode,semester) values (?,?,?,?,?,?)";
    	 
    	 try {
 			PreparedStatement prepStmt = conn.prepareStatement(
 					insertStatement,
 					PreparedStatement.RETURN_GENERATED_KEYS);
 			prepStmt.setString(1, advisor.getAdvisorId());
 			prepStmt.setString(2, advisor.getClas());
 			prepStmt.setString(3, advisor.getConcCode());
 			prepStmt.setString(4, advisor.getProgram().getProgramCode());
 			prepStmt.setString(5, advisor.getDepartment().getDeptCode());
 			prepStmt.setString(6, db.getCurrentTerm().substring(0, 4)+"10");
 			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			prepStmt.close();
 		} catch (Exception e) {
 			
 			e.printStackTrace();
 		}
    }
	public void insertAdvisorLevel(Advisor advisor, BannerDb db) {
		
        String insertStatement = "insert into advisorlevel (advisorid,clas,programcode,semester,deptcode) values (?,?,?,?,?)";
		
		try {
			PreparedStatement prepStmt = conn.prepareStatement(
					insertStatement,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, advisor.getAdvisorId());
			prepStmt.setString(2, advisor.getClas());
			prepStmt.setString(3, advisor.getProgram().getProgramCode());
			prepStmt.setString(4, db.getCurrentTerm().substring(0, 4)+"10");
			prepStmt.setString(5, advisor.getDepartment().getDeptCode());
			
			prepStmt.execute("use " + schema);
			prepStmt.getGeneratedKeys();
			prepStmt.executeUpdate();
			prepStmt.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	public boolean insertAdvisor(Advisor advisor, BannerDb addb) {

		String insertStatement = "insert into advisorassign (spridenid,advisorid,clas,firstname,lastname,chkid,programcode,term,deptcode, studentType) values (?,?,?,?,?,?,?,?,?,?)";
         boolean inserted = false;
		try {
			if (!isFound(advisor.getStudentId())) {

				int x = 0;
				
				
				PreparedStatement prepStmt = conn.prepareStatement(
						insertStatement,
						PreparedStatement.RETURN_GENERATED_KEYS);

				prepStmt.setString(1, advisor.getStudentId());
				prepStmt.setString(2, advisor.getAdvisorId());

				prepStmt.setString(3, advisor.getClas());
				prepStmt.setString(4, advisor.getLastName());
				prepStmt.setString(5, advisor.getFirstName());
				prepStmt.setBoolean(6, false);
				prepStmt.setString(7, advisor.getProgramCode());
				prepStmt.setString(8, addb.getCurrentTerm());
				prepStmt.setString(9, advisor.getDeptCode());
				prepStmt.setString(10, advisor.getStudentType());
				
				Advisor advisors = new Advisor();

				advisors.setStuPidm(addb.getPidm(advisor.getStudentId()));
				advisors.setAdvPidm(addb.getPidm(advisor.getAdvisorId()));

				/* Turn off for testing purposes */
//                addb.insertAdvisor(advisors);

				prepStmt.execute("use " + schema);
				prepStmt.getGeneratedKeys();
				prepStmt.executeUpdate();
				prepStmt.close();
				
				resetAllToZero(advisor);
				
				updateLastStop(advisor);
				
				inserted = true;
				
			} else {
				
				updateLastStop(advisor);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return inserted;
		
	}
	
	
    public void confirmAdvisorToBanner(BannerDb addb, Success success){
    	
    	Advisors advisors =null;
    	ArrayList list = new ArrayList();
    	getStandardAdvisorAssignments(addb,list, success);
    	getNonStandardAdvisorAssignments(addb, list, success);
    	
    	Iterator ii = list.iterator();
    	
    	while (ii.hasNext()){
    		advisors = (Advisors)ii.next();
//    		addb.insertAdvisor(advisors);
    	}
    	
    }
    private void getStandardAdvisorAssignments(BannerDb addb, ArrayList list, Success success){
    	
    	Advisors adv = new Advisors();
    	String sqlstmt = "select advisorid, spridenID from advisorassign where term = ?";
    	PreparedStatement prepStmt;
		try {
			prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, addb.getCurrentTerm());		
    		prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				adv.setAdvPidm(addb.getPidm(rs.getString(1)));
				adv.setStuPidm(addb.getPidm(rs.getString(2)));
				list.add(adv);
			}
			success.setSuccess("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			success.setSuccess("failure");
			e.printStackTrace();
		}
		
    }
    private void getNonStandardAdvisorAssignments(BannerDb addb, ArrayList list,Success success){
    	
    	Advisors adv = new Advisors();
    	String sqlstmt = "select advisorid,stuID from advisorconcentration where term = ?";
    	PreparedStatement prepStmt;
		try {
			prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, addb.getCurrentTerm());		
    		prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				adv.setAdvPidm(addb.getPidm(rs.getString(1)));
				adv.setStuPidm(addb.getPidm(rs.getString(2)));
				list.add(adv);
			}
			success.setSuccess("success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			success.setSuccess("failure");
			e.printStackTrace();
		}
		
		
		
    	
    }
	public void insertAdvisors(AdvisorStudentMatch asm, BannerDb addb) {

		String insertStatement = "insert into advisorassign (spridenid,sequenceid,advisorid,levl,firstname,lastname,chkid,majcode,term,deptcode,isconcentration) values (?,?,?,?,?,?,?,?,?,?,?)";
		try {

			PreparedStatement prepStmt = conn.prepareStatement(insertStatement,
					PreparedStatement.RETURN_GENERATED_KEYS);
			int foundcount = 0;

			/* If student is NOT found in Assignment TABLE */
			if (!isFound(asm.getSpridenId())) {

				prepStmt.setString(1, asm.getSpridenId());
				prepStmt.setInt(2, asm.getSequenceId());
				if (!asm.isConcentration())
					prepStmt.setString(3, asm.getAdvisorId());
				else
					prepStmt.setString(3, "");

				prepStmt.setString(4, asm.getLevl());
				prepStmt.setString(5, asm.getLastname());
				prepStmt.setString(6, asm.getFirstname());
				prepStmt.setBoolean(7, false);
				prepStmt.setString(8, asm.getMajCode());
				prepStmt.setString(9, asm.getTerm());
				prepStmt.setString(10, asm.getDeptCode());
				prepStmt.setBoolean(11, asm.isConcentration());

				Advisor advisors = new Advisor();

				advisors.setStuPidm(addb.getPidm(asm.getSpridenId()));
				advisors.setAdvPidm(addb.getPidm(asm.getAdvisorId()));

				/* Use when live */
				// if (!asm.isConcentration()) {
				// addb.insertAdvisor(advisors);
				// }
				prepStmt.execute("use " + schema);
				prepStmt.getGeneratedKeys();

				prepStmt.executeUpdate();
				prepStmt.close();
			} else {
				foundcount++;
				System.out.println("found " + asm.getSpridenId());
			}

			prepStmt.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public boolean insertConcentration(Concentration c, BannerDb addb, String semester, Advisor advisor) {

		String insertStatement = "insert into advisorconcentration (advisorid, programcode,term, conc, stuid, clas,deptCode ) values (?,?,?,?,?,?,?)";
        
		boolean inserted = false; 
		
		if (!concentrationInserted(c.getProgramCode(), semester,
				c.getConc(), c.getStuID())) {
			
			resetToZero(c);
			updateLastStopConcentration(c);
			
			try {
				PreparedStatement prepStmt = conn.prepareStatement(
						insertStatement,
						PreparedStatement.RETURN_GENERATED_KEYS);

				prepStmt.setString(1, c.getAdvisorId());
				prepStmt.setString(2, c.getProgramCode());
				prepStmt.setString(3, semester);
				prepStmt.setString(4, c.getConc());
				prepStmt.setString(5, c.getStuID());
				prepStmt.setString(6, c.getClas());
				prepStmt.setString(7, c.getDeptCode());

				prepStmt.execute("use " + schema);
				prepStmt.getGeneratedKeys();

				prepStmt.executeUpdate();

				MessageLogger.out.println("  ");
				prepStmt.close();
				inserted = true;

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
         
		return inserted;
	}

	public boolean advisorInserted(String program, String term,
			String stuid) {
	
		boolean found = false;
		try {
			String selectStatement = "select * from advisorlevel where programcode = ? and term = ? and and stuid = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, program);
			prepStmt.setString(2, term);
			prepStmt.setString(4, stuid);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				found = true;
			}
			prepStmt.close();
		} catch (SQLException ex) {

			MessageLogger.out
					.println("Error on term override (method = overrideSemester)="
							+ ex.getMessage());
		}
		
		
		return found;
	}
	public boolean concentrationInserted(String majCode, String term,
			String conc, String stuid) {

		boolean found = false;
		term = term.substring(0, 4);
		term = term + "10";
		try {
			String selectStatement = "select * from advisorconcentration where programcode = ? and term = ? and conc = ? and stuid = ?";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, majCode);
			prepStmt.setString(2, term);
			prepStmt.setString(3, conc);
			prepStmt.setString(4, stuid);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				found = true;
			}
			prepStmt.close();
		} catch (SQLException ex) {

			MessageLogger.out
					.println("Error on term override (method = overrideSemester)="
							+ ex.getMessage());
		}
		
		return found;
	}

	public String overrideSemester() {

		String term = null;
		try {
			String selectStatement = "select * from overridesemester";
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				if (rs.getBoolean(2)) {
					term = rs.getString(3);
				}
			}
			prepStmt.close();
		} catch (SQLException ex) {

			MessageLogger.out
					.println("Error on term override (method = overrideSemester)="
							+ ex.getMessage());
		}
		return term;
	}

	private int getMaxStudentAssignment() {

		int maxid = 0;

		String sqlstmt = "select max(assignmentid) as maxid from advisorassign";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				maxid = rs.getInt(1);
			}
			prepStmt.close();
			return maxid;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return maxid;

	}

	private boolean isStudentAssigned(String id) {

		boolean found = false;

		return false;

	}

	private boolean termAutomationExist() {

		boolean found = false;

		String sqlstmt = "SELECT term,start FROM automationcontrol WHERE term = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());

			ResultSet rs = prepStmt.executeQuery();
			found = rs.next();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return found;

	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void updateAutomationControlTerm(String term, String deptCode) {

		try {

			String sqlstmt = "UPDATE automationcontrol SET term = ? WHERE deptcode = ?";

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());
			prepStmt.setString(2, deptCode);

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateAutomationControlTerm(String term) {

		try {

			String sqlstmt = "UPDATE automationcontrol SET term = ?";

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList getFaculties() {
		ArrayList list = new ArrayList();

		String sqlstmt = "SELECT deptcode FROM departments";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.execute("use " + schema);
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

	public HashMap getAvailableDepts() {

		HashMap hmap = new HashMap();

		String sqlstmt = "SELECT DISTINCT deptcode FROM advisor";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {

				hmap.put(rs.getString(1), rs.getString(1));

			}
			prepStmt.close();
			return hmap;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public boolean startAutomation(String collCode) {

		boolean found = false;

		String sqlstmt = "SELECT start FROM automationcontrol WHERE deptcode = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, collCode);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {

				found = rs.getBoolean(1);
			}
			prepStmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return found;

	}

	public boolean startAutomation(String collCode, String majCode,
			String levlCode, String semester) {

		boolean found = false;

		String sqlstmt = "SELECT start FROM automationcontrol WHERE term = ? and deptcode = ? and level = ? and program = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());
			prepStmt.setString(2, collCode);
			prepStmt.setString(3, levlCode);
			prepStmt.setString(4, majCode);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {

				found = rs.getBoolean(1);
			}
			prepStmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return found;

	}

	public void updateAutomationTerm(String collCode) {
		String sqlstmt = " UPDATE automationcontrol SET term = ?, start = ? WHERE  deptcode = ? ";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());
			prepStmt.setBoolean(2, false);
			prepStmt.setString(3, collCode);

			prepStmt.execute("use " + schema);

			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public boolean existTermAutomationRecord(String collCode, String semester) {

		boolean found = false;
		String sqlstmt = "select * from automationcontrol where term = ? and deptcode = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());
			prepStmt.setString(2, collCode);
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found = true;
			}
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return found;
	}

	public boolean startAutomationByTerm(String collCode, String majCode,
			String levlCode, String semester) {

		boolean found = false;
		String sqlstmt = "select * from automationcontrol where term = ? and deptcode = ? and level = ? and program = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, getTerm());
			prepStmt.setString(2, collCode);
			prepStmt.setString(3, levlCode);
			prepStmt.setString(4, majCode);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found = true;
			}
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return found;
	}

	public void insertTermAutomationRecord(String collCode, String semester) {

		String sqlstmt = " INSERT INTO  automationcontrol(deptCode,term, start) VALUES (?, ?, ?)";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, collCode);
			prepStmt.setString(2, getTerm());
			prepStmt.setBoolean(3, false);

			prepStmt.execute("use " + schema);
			prepStmt.getGeneratedKeys();
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void insertTermAutomation(String collCode, String majCode,
			String levlCode, String semester) {

		String sqlstmt = " INSERT INTO  automationcontrol(deptCode,term, program, level, start) VALUES (?, ?, ?, ?, ?)";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepStmt.setString(1, collCode);
			prepStmt.setString(2, getTerm());
			prepStmt.setString(3, majCode);
			prepStmt.setString(4, levlCode);
			prepStmt.setBoolean(5, false);

			prepStmt.execute("use " + schema);
			prepStmt.getGeneratedKeys();
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void closeDb() {
		try {
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void updateCreated(boolean b, String term) {

		String sqlstmt = "UPDATE created SET created = ? WHERE term = ?";
		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setBoolean(1, b);
			prepStmt.setString(2, term);

			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean firstRunCompleted(String deptCode, String majCode,
			String levlCode, String termCode) {

		boolean run = false;

		String selectStatement = "select run from checkrun where deptCode = ? and majCode = ? and levelCode = ? and semester = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, deptCode);
			prepStmt.setString(2, majCode);
			prepStmt.setString(3, levlCode);
			prepStmt.setString(4, termCode);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				if (rs.getBoolean(1) == false) {
					run = false;
				} else {
					run = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return run;

	}

	public void updateRun(boolean r, String deptCode, String term) {
		String updateStmt = "UPDATE checkrun SET run = ? where deptCode = ? and semester = ?";
		try {

			PreparedStatement prepStmt = conn.prepareStatement(updateStmt);
			prepStmt.setBoolean(1, r);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, term);

			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    public void updateMatched(boolean match, String id){
    	String updateStmt = "UPDATE advisorassign SET matched = ? where spridenid = ?";
    	try {
    		PreparedStatement prepStmt = conn.prepareStatement(updateStmt);
    		prepStmt.setBoolean(1, match);
    		prepStmt.setString(2, id);
    		
    		prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			
    	}catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    public boolean foundMatched(String id){
    	
    	
    	boolean found = false;

		String selectStatement = "select matched from advisorassign where spridenid = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, id);
			
			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				
				found = true;
				if (rs.getBoolean(1) == false)
					MessageLogger.out.println("not found = " +id);
				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return found;
    }
	public void updateRun(boolean r, String deptCode, String majCode,
			String levl, String term) {
		String updateStmt = "UPDATE checkrun SET run = ? where deptCode = ? and majCode = ? and levelCode = ? and semester = ?";
		try {

			PreparedStatement prepStmt = conn.prepareStatement(updateStmt);
			prepStmt.setBoolean(1, r);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, majCode);
			prepStmt.setString(4, levl);
			prepStmt.setString(5, term);

			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void insertRun(String deptCode, String term) {
		String insertStatement = "insert into checkrun (run,deptCode,semester) values (?,?,?)";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(insertStatement,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepStmt.setBoolean(1, false);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, term);

			prepStmt.execute("use " + schema);
			prepStmt.getGeneratedKeys();

			prepStmt.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void insertRun(String deptCode, String majCode, String levl,
			String term) {

		String insertStatement = "insert into checkrun (run,deptCode,majCode,levelCode,semester) values (?,?,?,?,?)";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(insertStatement,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepStmt.setBoolean(1, false);
			prepStmt.setString(2, deptCode);
			prepStmt.setString(3, majCode);
			prepStmt.setString(4, levl);
			prepStmt.setString(5, term);

			prepStmt.execute("use " + schema);
			prepStmt.getGeneratedKeys();

			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/* Does RUN Record For Faculty Exist */
	public boolean existRunRecordForFacultyAndSemester(String deptCode,
			String term) {

		boolean run = false;
		String selectStatement = "select * from checkrun where deptCode = ? and semester = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			prepStmt.setString(1, deptCode);
			prepStmt.setString(2, term);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				run = true;
			}
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return run;
	}
	/*Select advisor assignments results*/
	public ArrayList selectAdvisorAssignments(){
		/*select * from SATURN.UWI_CH_ADV_ASSIGNMENTS*/
		ArrayList list = new ArrayList(); 
        
		String selectStmt= "SELECT advisorid,clas, programcode,semester,deptcode, conc FROM advisorlevel a " +
                "UNION ALL " +
                "SELECT advisorid,level as clas, programcode,semester,deptcode, conc " +
                "FROM advisors.advisorlevelconcentration a";
		
		try {
  			PreparedStatement prepStmt = conn.prepareStatement(selectStmt);
  			
  			prepStmt.execute("use " + schema);
			
			ResultSet rs = prepStmt.executeQuery();
  			
			while (rs.next()){
				Advisor advisor = new Advisor();
				advisor.setAdvisorId(rs.getString(1));
				advisor.setClas(rs.getString(2));
				advisor.setProgramCode(rs.getString(3));
				advisor.setSemester(rs.getString(4));
				advisor.setDeptCode(rs.getString(5));
				advisor.setConcCode(rs.getString(6));
				list.add(advisor);
			}
			rs.close();
			prepStmt.close();
			conn.close();
          }
          catch (Exception e) {
  			e.printStackTrace();
  		  }
          return list;

	}
	/*Select non-concentration and concentration results*/
    public ArrayList selectAllAssignments(){
          /*select * from SATURN.UWI_CH_STUDENT_ADV_ASSIGNMENTS*/
    	  ArrayList list = new ArrayList(); 
          
    	  String selectStmt= "SELECT spridenid, advisorid,clas, programcode,term,deptcode, conc FROM advisorassign a " +
                             "UNION ALL " +
                             "SELECT stuid as spridenid, advisorid,clas, programcode,term,deptcode, conc " +
                             "FROM advisors.advisorconcentration a";
          try {
  			PreparedStatement prepStmt = conn.prepareStatement(selectStmt);
  			
  			prepStmt.execute("use " + schema);
			
			ResultSet rs = prepStmt.executeQuery();
  			
			while (rs.next()){
				Advisor advisor = new Advisor();
				advisor.setStudentId(rs.getString(1));
				advisor.setAdvisorId(rs.getString(2));
				advisor.setClas(rs.getString(3));
				advisor.setProgramCode(rs.getString(4));
				advisor.setSemester(rs.getString(5));
				advisor.setDeptCode(rs.getString(6));
				advisor.setConcCode(rs.getString(7));
				list.add(advisor);
			}
			rs.close();
			prepStmt.close();
			conn.close();
          }
          catch (Exception e) {
  			e.printStackTrace();
  		  }
          return list;

    }
	public boolean runExist(String deptCode, String majCode, String levl,
			String term) {

		boolean run = false;

		String selectStatement = "select * from checkrun where deptCode = ? and majcode = ? and levelCode = ? and semester = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.execute("use " + schema);
			prepStmt.setString(1, deptCode);
			prepStmt.setString(2, majCode);
			prepStmt.setString(3, levl);
			prepStmt.setString(4, term);

			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				run = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return run;

	}

	public void insertCreated(String term) {
		String insertStatement = "insert into created (created,term) values (?,?)";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(insertStatement);
			prepStmt.setBoolean(1, false);
			prepStmt.setString(2, term);

			prepStmt.execute("use " + schema);
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean isFound(String id) {

		boolean found = false;
		
		
		String s = "0";

		String sqlstmt = "select spridenid from advisorassign where spridenid = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, id);

			prepStmt.execute("use " + schema);
			ResultSet rs = prepStmt.executeQuery();
			found = rs.next();
			prepStmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return found;
	}

	/*
	 * public void deleteAdvisorAssignments(){
	 * 
	 * String deleteStatement = "delete from advisorassignments";
	 * 
	 * try {
	 * 
	 * PreparedStatement prepStmt = conn.prepareStatement(deleteStatement);
	 * 
	 * 
	 * prepStmt.execute("use " + schema);
	 * 
	 * prepStmt.executeUpdate();
	 * 
	 * } catch (Exception ex){ ex.printStackTrace(); }
	 * 
	 * }
	 */
	public static void main(String[] args) throws RemoteException {
//		AdvisorDb db = new AdvisorDb("", "admin", "kentish", "test");
		AdvisorDb db = new AdvisorDb();
		db.connectTestDB("admin", "kentish", "test");
		//db.changeSchema("advisors");
		//Concentration c = db.arrangeNextConcAdvisor("MANG-BSC-C-F", "HRM",
			//	"201410", "");
		//System.out.println(c.getAdvisorId());

		//db.closeDb();
		
		RemoteService rs = new RemoteService();
		rs.processAssignments("806");
		
		try {
			rs.processAssignments("806");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(db.getMaxRows());

		// Advisors advisor = new Advisors();
		// advisor.setStudentId("20003569");
		// advisor.setSequenceId(1);

	}
}

/*
 * if (!asm.getDeptCode().equals("LW")){
 * 
 * if
 * (!firstRunCompleted(asm.getDeptCode(),asm.getMajCode(),asm.getLevl(),asm.getTerm
 * ())) {
 * 
 * prepStmt.setString(1, asm.getSpridenId()); prepStmt.setInt(2,
 * asm.getSequenceId()); if (!asm.isConcentration()) prepStmt.setString(3,
 * asm.getAdvisorId()); else prepStmt.setString(3, "");
 * 
 * prepStmt.setString(4, asm.getLevl()); prepStmt.setString(5,
 * asm.getLastname()); prepStmt.setString(6, asm.getFirstname());
 * prepStmt.setBoolean(7, false); prepStmt.setString(8,asm.getMajCode());
 * prepStmt.setString(9,asm.getTerm());
 * prepStmt.setString(10,asm.getDeptCode()); prepStmt.setBoolean(11,
 * asm.isConcentration());
 * 
 * Advisors advisors = new Advisors();
 * 
 * advisors.setStuPidm(addb.getPidm(asm.getSpridenId()));
 * advisors.setAdvPidm(addb.getPidm(asm.getAdvisorId()));
 * 
 * if (!asm.isConcentration()){ addb.insertAdvisor(advisors); }
 * prepStmt.execute("use " + schema); prepStmt.getGeneratedKeys();
 * 
 * 
 * prepStmt.executeUpdate();
 * 
 * } else {
 * 
 * prepStmt.setString(1, asm.getSpridenId()); prepStmt.setInt(2, 0);
 * prepStmt.setString(3, ""); prepStmt.setString(4, asm.getLevl());
 * prepStmt.setString(5, asm.getLastname()); prepStmt.setString(6,
 * asm.getFirstname()); prepStmt.setBoolean(7, false);
 * prepStmt.setString(8,asm.getMajCode()); prepStmt.setString(9,asm.getTerm());
 * prepStmt.setString(10,asm.getDeptCode()); prepStmt.setBoolean(11,
 * asm.isConcentration());
 * 
 * prepStmt.execute("use " + schema); prepStmt.getGeneratedKeys();
 * 
 * prepStmt.executeUpdate();
 * 
 * } } else {
 * 
 * 
 * 
 * if (asm.isConcentration()) { HashMap hmap = asm.getConcentrations(); Iterator
 * iterator = hmap.keySet().iterator();
 * 
 * while (iterator.hasNext()){
 * 
 * String key = iterator.next().toString(); String programme =
 * hmap.get(key).toString();
 * 
 * insertConcentration(key,addb, programme); } }
 */
