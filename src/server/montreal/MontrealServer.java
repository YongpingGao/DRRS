package server.montreal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import model.City;
import model.FlightRecord;
import model.FlightRecords;
import model.PassengerRecord;
import model.PassengerRecords;
import model.PassengerRecordsMap;
import server.DFRSInterface;
import util.FileProcessing;

public class MontrealServer implements DFRSInterface {
	private static final int port = 2020;
	private static final String serverName = "Montreal";

	private HashMap<Character, PassengerRecords> passengerRecordsMap;
	private FlightRecords flightRecords;
	
	public static void main(String[] args) {
		try {
			new MontrealServer().exprotServer();
			System.out.println(serverName + " server is running and listening to port " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void exprotServer() throws Exception {
		Remote remoteObj = UnicastRemoteObject.exportObject(this, port);
		Registry r = LocateRegistry.createRegistry(port);
		r.bind(serverName, remoteObj);
	}

	@Override
	public void bookFlight(PassengerRecord passengerRecord) throws RemoteException {
		passengerRecordsMap = FileProcessing.sharedInstance().readFromJsonFile("src/server/montreal/passengerRecords.json", passengerRecordsMap.getClass());
		if(passengerRecordsMap == null) passengerRecordsMap = new HashMap<>();
		Character c = Character.toLowerCase(passengerRecord.getPassenger().getLastName().charAt(0));
		if(passengerRecordsMap.get(c) == null) {
			PassengerRecords passengerRecords = new PassengerRecords();
			passengerRecords.addPassengerRecord(passengerRecord);
			passengerRecordsMap.put(c, passengerRecords);
		} else {
			passengerRecordsMap.get(c).addPassengerRecord(passengerRecord);
		}
		
		FileProcessing.sharedInstance().writeToJsonFile("src/server/montreal/passengerRecords.json", passengerRecordsMap);
	}

	@Override
	public int getBookedFlightCount() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addFlightRecord(FlightRecord fr, String recordID) throws RemoteException {
		
		flightRecords = FileProcessing.sharedInstance().readFromJsonFile("src/server/montreal/flightRecords.json", FlightRecords.class);
		if(flightRecords == null) flightRecords = new FlightRecords();
		for(FlightRecord r: flightRecords.getFlightRecords()){
			if(r.getRecordID().equals(recordID)){
				flightRecords.removeFlightRecord(r);
			}
		}
		flightRecords.addFlightRecord(fr);
		FileProcessing.sharedInstance().writeToJsonFile("src/server/montreal/flightRecords.json", flightRecords);
		
	}

 
}









