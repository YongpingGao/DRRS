package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.FlightRecord;
import model.PassengerRecord;

public interface DFRSInterface extends Remote {

//	public String testInputText(String inputText) throws RemoteException;
//	public String reverse(String inputText) throws RemoteException;
//	public String scramble(String inputText) throws RemoteException;
	public void bookFlight(PassengerRecord passengerRecord) throws RemoteException;
	
	public int getBookedFlightCount() throws RemoteException;
	
	public void addFlightRecord (FlightRecord fr, String recordID) throws RemoteException;
	
}
