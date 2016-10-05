package model;

import java.util.HashMap;

public class PassengerRecordsMap {
	private static HashMap<Character, PassengerRecords> passengerRecordsMap;
	
	public static HashMap<Character, PassengerRecords> getPassengerRecordsMap() {
		return passengerRecordsMap;
	}

}
