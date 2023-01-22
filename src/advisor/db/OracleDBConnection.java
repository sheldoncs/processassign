package advisor.db;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import advisor.util.MessageLogger;
import advisor.util.NewDateFormatter;

public class OracleDBConnection {
   
	protected Connection conn;
	protected Connection conn1;
	protected String effTerm;
	protected AdvisorDb advisorDb;

    public OracleDBConnection(int v){
		
	}
    
	public OracleDBConnection(){
	   
	}
	
	public OracleDBConnection(String connectString){
	
	}
	
	public void openConn(){
		
		
		
		try {
			
			
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@bandb-prod.ec.cavehill.uwi.edu:8000:PROD";
//			String url = "jdbc:oracle:thin:@bandb-dev.ec.cavehill.uwi.edu:8003:TEST";
			conn = DriverManager.getConnection(url, "svc_update", "e98ce36209");
			conn.setAutoCommit(true);
         
        
			
           
	       } catch (ClassNotFoundException e){
		     e.printStackTrace();
	       } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	       }
	       
	       
	}
    public void openConnMockDB(){
		
		
		
		try {
			
			
			
		      Class.forName("oracle.jdbc.driver.OracleDriver");
              String url = "jdbc:oracle:thin:@antares.cavehill.uwi.edu:1521:MOCKDB";
              conn = DriverManager.getConnection(url,"20003569", "kentish1968");
              conn.setAutoCommit(true);
        
			
           
	       } catch (ClassNotFoundException e){
		     e.printStackTrace();
	       } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	       }
	       
	}
	public void connectPeopleSoft(){
		
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@kronos1.cavehill.uwi.edu:1521:HRPRD90";
        conn = DriverManager.getConnection(url,"admin", "proxyfield");
        conn.setAutoCommit(true);
        MessageLogger.out.println("Connection Made");
		}  
		catch (ClassNotFoundException e){
		     e.printStackTrace();
	    } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	    }
	}
	
	public String getCurrentTerm(){
		 String term = null;
			
		 
			String sqlstmt = "select min(stvterm_code) as mintermcode from stvterm where stvterm_start_date < ? and stvterm_end_date > ?  and stvterm_desc not like  '%Year%Long%' order by stvterm_code";
			NewDateFormatter df = new NewDateFormatter();
			
			try {
				  
				  PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
				  prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
			      prepStmt.setDate(2, java.sql.Date.valueOf(df.getSimpleDate()));
			     
			      ResultSet rs = prepStmt.executeQuery();
			      while (rs.next()){
			    	  
			    	  term = rs.getString(1);
			      }
			      if ((term != null) && (term.indexOf("40") >=0)){
				      
			    	   term = null;
			        
			      }
			      
			      if (term == null){
						sqlstmt = "select min(stvterm_code) as mintermcode from stvterm where stvterm_start_date >= ? and stvterm_desc not like  '%Year%Long%' order by stvterm_code";
						df = new NewDateFormatter();
						System.out.println(java.sql.Date.valueOf((df.getSimpleDate())));
						prepStmt = conn.prepareStatement(sqlstmt);
						prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
						
						rs = prepStmt.executeQuery();
					    while (rs.next()){
					    	  term = rs.getString(1);
					    }
				  }
			      term = term.substring(0, 4)+"10";
			      rs.close();
			      prepStmt.close();
			}
			catch(SQLException e){
				
				  e.printStackTrace();
				 
			} 
			
			
			/*Only use for overrides*/
           // AdvisorDb adDb = new AdvisorDb("", "admin", "kentish", "owl2");
           // adDb.changeSchema("advisors");

          // String oTerm = adDb.getOverideSemester();
			
//			if (oTerm == null)
			  //term = term.substring(0,4)+"10";
//			else 
//			  term = oTerm.substring(0,4)+"10";	
			
//			adDb.closeDb();
			
			return term;
	 }
	
	 public void openPeopleSoftConnection(){
		 try {
				
			    Class.forName("oracle.jdbc.driver.OracleDriver");
		        String url = "jdbc:oracle:thin:@196.1.163.244:1521:HRSYS8";
		        conn = DriverManager.getConnection(url,"admin", "proxyfield");
		        conn.setAutoCommit(true);
		        MessageLogger.out.println("PeopleSoft Connection Open");
		        
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			} catch (SQLException ex){
				ex.printStackTrace();
				MessageLogger.out.println(ex.getMessage());  
			}
	 }
	 
	 protected String lPad(String MyValue, String MyPadCharacter, int MyPaddedLength){
		 String padString="";
		 int padLength = 0;
		 
		 padLength = MyPaddedLength - MyValue.length();
		 for (int i = 0; i < padLength; i++ ){
			 padString = padString + MyPadCharacter;
		 }
		 padString = padString + MyValue;
		 
		 return padString;
	 }
	 public void setEffectiveTerm(String effTerm){
			this.effTerm = effTerm;
	 }
	 public void closeConn(){
		 try {
		 conn.close();
		 } catch(SQLException e){
			  e.printStackTrace();
			}
		 
	 }

	 
	 public static void main(String[] args) throws RemoteException,SQLException {
			//OracleDBConnection db = new OracleDBConnection();
		    OracleDBConnection db = new OracleDBConnection(0);
		    db.connectPeopleSoft();
		   // db.connectPeopleSoft();
		    
		   // System.out.println(db.getCurrentTerm());
		    //System.out.println(db.getCurrentTerm());
			//db.openPeopleSoftConnection();
	 }
	 
}
