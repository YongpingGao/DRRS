package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import model.FlightRecord;
import model.PassengerRecord;

public interface DFRSInterface extends Remote {


	public void bookFlight(PassengerRecord passengerRecord) throws RemoteException;
	
	public void addFlightRecord (FlightRecord fr, String recordID) throws RemoteException;
	
	public ArrayList<String> getAvailableDates(String dest) throws RemoteException;
		
	
	public String getBookedFlightCount() throws RemoteException;
	
}
