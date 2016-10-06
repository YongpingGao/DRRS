package server.washington;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import model.FlightRecord;
import model.FlightRecords;
import model.Log;
import model.PassengerRecord;
import model.PassengerRecords;
import server.DFRSInterface;
import server.montreal.MontrealServer;
import util.FileProcessing;

public class WashingtonServer implements DFRSInterface{
	private static final int port = 2021;
	private static final int UDP_PORT = 2031;
	private static final int UDP_PORT_MTL = 2030;
	private static final int UDP_PORT_NDL = 2032;
	private static final String serverName = "Washington";
	private static final String HOST_NAME = "localhost";
	private DatagramSocket aSocket = null;
	private HashMap<String, PassengerRecords> passengerRecordsMap = new HashMap<>();
	private FlightRecords flightRecords;
	
	public static void main(String[] args) {
		try {
			WashingtonServer server = new WashingtonServer();
			server.exprotServer();
			System.out.println(serverName + " server is running and listening to port " + port);
			server.UDPServer();
			
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
		 
		try {
			passengerRecordsMap = new Gson().fromJson(new BufferedReader(new FileReader("src/server/washington/passengerRecords.json")), new TypeToken<HashMap<String, PassengerRecords>>() {}.getType());
			if(passengerRecordsMap == null) passengerRecordsMap = new HashMap<>();
			
			String c = Character.toString(Character.toLowerCase(passengerRecord.getPassenger().getLastName().charAt(0)));
			
			if(passengerRecordsMap.get(c) == null) {
				PassengerRecords passengerRecords = new PassengerRecords();
				passengerRecords.addPassengerRecord(passengerRecord);
				passengerRecordsMap.put(c, passengerRecords);
			} else {
				passengerRecordsMap.get(c).addPassengerRecord(passengerRecord);
			}
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		FileProcessing.sharedInstance().writeToJsonFile("src/server/washington/passengerRecords.json", passengerRecordsMap);
		
		String ts = new Date().toString();
		String who = "Passenger " + passengerRecord.getPassenger().getFullName();
		String operation = "booked a flight from Washington to " + passengerRecord.getDestination().toString() + " at " + passengerRecord.getDateOfFlight();
		new Log(ts, who, operation).writeToLog("src/server/washington/log.txt");
	}


	@Override
	public void addFlightRecord(FlightRecord fr, String recordID) throws RemoteException {
		
		flightRecords = FileProcessing.sharedInstance().readFromJsonFile("src/server/washington/flightRecords.json", FlightRecords.class);
		if(flightRecords == null) flightRecords = new FlightRecords();
		for(FlightRecord r: flightRecords.getFlightRecords()){
			if(r.getRecordID().equals(recordID)){
				flightRecords.removeFlightRecord(r);
			}
		}
		flightRecords.addFlightRecord(fr);
		FileProcessing.sharedInstance().writeToJsonFile("src/server/washington/flightRecords.json", flightRecords);
		
		String ts = new Date().toString();
		String who = "Manager";
		String operation = "add a flight from Washington to " + fr.getDestination().toString() + " at " + fr.getDateOfFlight();
		new Log(ts, who, operation).writeToLog("src/server/washington/log.txt");
		
	}

	@Override
	public ArrayList<String> getAvailableDates(String dest) throws RemoteException {
		ArrayList<String> dates = new ArrayList<>();
		if(flightRecords == null) flightRecords = new FlightRecords();
		flightRecords = FileProcessing.sharedInstance().readFromJsonFile("src/server/washington/flightRecords.json", FlightRecords.class);
		if(flightRecords != null) {
			for(FlightRecord fr: flightRecords.getFlightRecords()) {
				if(dest.equals(fr.getDestination().toString()) && serverName.equals(fr.getDeparture().toString())) {
					dates.add(fr.getDateOfFlight());
				}
			}
 		}
		
		String ts = new Date().toString();
		String who = "Passenger";
		String operation = "queried the available dates from Washington to " + dest;
		new Log(ts, who, operation).writeToLog("src/server/washington/log.txt");
		
		if(dates.size() != 0) return dates;
		else return null;
	}
	
	
	@Override
	public String getBookedFlightCount() throws RemoteException {
		DatagramSocket aSocket = null;
		DatagramSocket bSocket = null;
		try {
			aSocket = new DatagramSocket();
			bSocket = new DatagramSocket();
			
			String message = "Washington Request";
			byte[] m  = message.getBytes();
			InetAddress aHost = InetAddress.getByName(HOST_NAME);
			 
			DatagramPacket requestToMTL = new DatagramPacket(m, message.length(), aHost, UDP_PORT_MTL);
			aSocket.send(requestToMTL);
			
			DatagramPacket requestToNDL = new DatagramPacket(m, message.length(), aHost, UDP_PORT_NDL);
			bSocket.send(requestToNDL);
			
			byte[] buffer = new byte[1000];
			byte[] buffer2 = new byte[1000];
			DatagramPacket replyFromMTL = new DatagramPacket(buffer, buffer.length);
			DatagramPacket replyFromNDL = new DatagramPacket(buffer2, buffer2.length);
			aSocket.receive(replyFromMTL);
			bSocket.receive(replyFromNDL);
			
			String MTLCount = new String(replyFromMTL.getData());
			String NDLCount = new String(replyFromNDL.getData());
 
			StringBuilder sb = new StringBuilder("WST" + getCountsFromDB());
			sb.append("\n");
			sb.append(MTLCount);
			sb.append("\n");
			sb.append(NDLCount);
			
			String ts = new Date().toString();
			String who = "Manager";
			String operation = "count the number of all the flight records";
			new Log(ts, who, operation).writeToLog("src/server/washington/log.txt");
			
			return sb.toString();
		} catch(SocketException e){
			System.out.println("socket error");
			return null;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("UnknownHostException error");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException error");
			return null;
		} finally {
			if(aSocket != null) aSocket.close();
		}
	}
	
	private void UDPServer() {
		try {	 
			aSocket = new DatagramSocket(UDP_PORT);
			byte[] buffer = new byte[1000];
			System.out.println("udp is listening on port: " + UDP_PORT);
			while(true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				
				String WSTCount = "WST" + getCountsFromDB();
				byte[] sendBack = WSTCount.getBytes();
				DatagramPacket reply = new DatagramPacket(sendBack, sendBack.length, 
						request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		} catch(SocketException e){
			System.out.println("SocketException error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 
	private int getCountsFromDB() {
		// TODO Auto-generated method stub
		int count = 0;
		
		HashMap<String, PassengerRecords> passengerRecordsMap;
		try {
			passengerRecordsMap = new Gson().fromJson(new BufferedReader(new FileReader("src/server/washington/passengerRecords.json")), new TypeToken<HashMap<String, PassengerRecords>>() {}.getType());
			if(passengerRecordsMap == null) return 0;
			
			for (Map.Entry<String, PassengerRecords> entry : passengerRecordsMap.entrySet()) {
				PassengerRecords passengerRecords = entry.getValue();
				count += passengerRecords.count();
			}
			return count;
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}	
	}
}
