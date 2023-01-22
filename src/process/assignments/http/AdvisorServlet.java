package process.assignments.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import process.remote.RemoteService;
import advisor.util.AdvisorTask;
import advisor.util.MessageLogger;
import advisor.util.Success;



public class AdvisorServlet extends JAXMServlet {

	private static final long serialVersionUID = 1L;
	private ArrayList list;
    private String excelFilePath;
    private Timer timer;
    
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		Properties endPoint = (Properties)servletConfig.getServletContext().getAttribute("EndPointProperties");
		excelFilePath = endPoint.getProperty("excelfilepath");
		
      AdvisorTask task = new AdvisorTask(effTerm, endPoint);
		
		timer = new Timer();
		timer.schedule(task,0,5 *(60*60*1000));
		
	}

	protected Document performTask(String requestName, int id,
			Iterator paramater) {
		MessageLogger.out.println("No Error " + "performTask");
		// TODO Auto-generated method stub
		RemoteService rs = new RemoteService();
		
		
		try {

			HashMap hmParameter = parseParameter(paramater, requestName);
			
			MessageLogger.out.println(requestName);
			
			if (requestName.equals("processAssignments")) {
				
				
				String deptCode = hmParameter.get("deptCode").toString();
				MessageLogger.out.println("Dept Code Is "+deptCode);
				Success success = rs.processAssignments(deptCode);
				
				return serializeSuccess(id, success);
			
			} else if (requestName.equals("confirmAdvisor")) {
				
				Success success = rs.confirmAdvisor();
				
				return serializeSuccess(id, success);
			}
			
			
	
		} catch (Exception e) {
			// DataFormatException
			MessageLogger.out.println("Error during Serialization :"
					+ e.getMessage());
			e.printStackTrace(MessageLogger.out);
		}

		return null;
	}

	private HashMap parseParameter(Iterator parameter, String requestName) {

		HashMap hmParameter = new HashMap();

		while (parameter.hasNext()) {
			SOAPElement subElement = (SOAPElement) parameter.next();
			Name subElementName = subElement.getElementName();

			MessageLogger.out.println("ParameterName ==> "
					+ subElementName.getLocalName());
			MessageLogger.out.println(subElement.getValue());

			if (requestName.equals("processAssignments")) {
				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("deptCode", subElement.getValue());
			}
			
			
			
		}

		return hmParameter;
	}

	
	private Document serializeSuccess(int id, Success success) throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element advisorElement = doc.createElement("Success");
		doc.appendChild(advisorElement);
		advisorElement.setAttribute("id", "ID" + id);
		advisorElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				SERVICE_PREFIX + ":Success");

		Element semesterElement = doc.createElement("success");
		semesterElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		semesterElement.appendChild(doc.createTextNode(success.getSuccess()));
		advisorElement.appendChild(semesterElement);

		return doc;

	}

}
