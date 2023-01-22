package process.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import advisor.util.Success;



public interface RemoteInterface extends Remote {
	public Success processAssignments(String deptCode) throws RemoteException;
	public Success confirmAdvisor() throws RemoteException;
    
}
