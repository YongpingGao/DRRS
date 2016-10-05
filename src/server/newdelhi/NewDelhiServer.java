package server.newdelhi;

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
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import model.FlightRecord;
import model.FlightRecords;
import model.PassengerRecord;
import model.PassengerRecords;
import server.DFRSInterface;
import util.FileProcessing;

public class NewDelhiServer implements DFRSInterface  {

	private static final int port = 2022;
	private static final int UDP_PORT = 2032;
	private static final int UDP_PORT_WST = 2031;
	private static final int UDP_PORT_MTL = 2030;
	private DatagramSocket aSocket = null;
	private static final String serverName = "NewDelhi";
	private static final String HOST_NAME = "localhost";

	private HashMap<Character, PassengerRecords> passengerRecordsMap = new HashMap<>();
	private FlightRecords flightRecords;
	
	public static void main(String[] args) {
		try {
			NewDelhiServer server = new NewDelhiServer();
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
		 
		passengerRecordsMap = FileProcessing.sharedInstance().readFromJsonFile("src/server/newdelhi/passengerRecords.json", passengerRecordsMap.getClass());
		if(passengerRecordsMap == null) passengerRecordsMap = new HashMap<>();
		Character c = Character.toLowerCase(passengerRecord.getPassenger().getLastName().charAt(0));
		if(passengerRecordsMap.get(c) == null) {
			PassengerRecords passengerRecords = new PassengerRecords();
			passengerRecords.addPassengerRecord(passengerRecord);
			passengerRecordsMap.put(c, passengerRecords);
		} else {
			passengerRecordsMap.get(c).addPassengerRecord(passengerRecord);
		}
		
		FileProcessing.sharedInstance().writeToJsonFile("src/server/newdelhi/passengerRecords.json", passengerRecordsMap);
	}

	@Override
	public void addFlightRecord(FlightRecord fr, String recordID) throws RemoteException {
		
		flightRecords = FileProcessing.sharedInstance().readFromJsonFile("src/server/newdelhi/flightRecords.json", FlightRecords.class);
		if(flightRecords == null) flightRecords = new FlightRecords();
		for(FlightRecord r: flightRecords.getFlightRecords()){
			if(r.getRecordID().equals(recordID)){
				flightRecords.removeFlightRecord(r);
			}
		}
		flightRecords.addFlightRecord(fr);
		FileProcessing.sharedInstance().writeToJsonFile("src/server/newdelhi/flightRecords.json", flightRecords);
		
	}

	@Override
	public ArrayList<String> getAvailableDates(String dest) throws RemoteException {
		ArrayList<String> dates = new ArrayList<>();
		if(flightRecords == null) flightRecords = new FlightRecords();
		flightRecords = FileProcessing.sharedInstance().readFromJsonFile("src/server/newdelhi/flightRecords.json", FlightRecords.class);
		if(flightRecords != null) {
			for(FlightRecord fr: flightRecords.getFlightRecords()) {
				if(dest.equals(fr.getDestination().toString()) && serverName.equals(fr.getDeparture().toString())) {
					dates.add(fr.getDateOfFlight());
				}
			}
 		}
		
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
			
			String message = "New Delhi Request";
			byte[] m  = message.getBytes();
			InetAddress aHost = InetAddress.getByName(HOST_NAME);
			 
			DatagramPacket requestToWST = new DatagramPacket(m, message.length(), aHost, UDP_PORT_WST);
			aSocket.send(requestToWST);
			
			DatagramPacket requestToMTL = new DatagramPacket(m, message.length(), aHost, UDP_PORT_MTL);
			bSocket.send(requestToMTL);
			
			byte[] buffer = new byte[1000];
			byte[] buffer2 = new byte[1000];
			DatagramPacket replyFromWST = new DatagramPacket(buffer, buffer.length);
			DatagramPacket replyFromMTL = new DatagramPacket(buffer2, buffer2.length);
			aSocket.receive(replyFromWST);
			bSocket.receive(replyFromMTL);
			
			String WSTCount = new String(replyFromWST.getData());
			String MTLCount = new String(replyFromMTL.getData());
 
			StringBuilder sb = new StringBuilder("NDL" + getCountsFromDB());
			sb.append("\n");
			sb.append(WSTCount);
			sb.append("\n");
			sb.append(MTLCount);
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
				
				String NDLCount = "NDL" + getCountsFromDB();
				byte[] sendBack = NDLCount.getBytes();
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
		
		HashMap<Character, PassengerRecords> passengerRecordsMap;
		try {
			passengerRecordsMap = new Gson().fromJson(new BufferedReader(new FileReader("src/server/newdelhi/passengerRecords.json")), new TypeToken<Map<Character, PassengerRecords>>() {}.getType());
			if(passengerRecordsMap == null) return 0;
			
			for (Map.Entry<Character, PassengerRecords> entry : passengerRecordsMap.entrySet()) {
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
