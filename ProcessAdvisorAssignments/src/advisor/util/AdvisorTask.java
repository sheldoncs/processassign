package advisor.util;

import java.util.Properties;
import java.util.TimerTask;


public class AdvisorTask extends TimerTask {

	private String effTerm;
	private Properties endPoint;
	private int ii;
	private String bindingKey;
	
	
	public AdvisorTask(String effTerm,Properties endPoint){
		
		super();
		this.effTerm=effTerm;
		this.endPoint = endPoint;
		
	}
	public void run() {
		
		try {
			
			MessageLogger.out.println("Begin Process");
			RunAssignments assign = new RunAssignments();
		    assign.executeAll();
		    MessageLogger.out.println("End Process");
		    
		} catch (Exception ex){
			ex.printStackTrace();
			MessageLogger.out.println("Error Log "+ex.getMessage());
			
			
		}
	}
}
