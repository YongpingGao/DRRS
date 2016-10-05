package model;

import util.FileProcessing;

public class Manager {
	private String managerID;
	private City city;
	
	public Manager(String managerID, City city) {
		this.managerID = managerID;
		this.city = city;
	}

	public String getManagerID() {
		return managerID;
	}

	public City getCity() {
		return city;
	}
	
 
	
	public String getBookedFlightCount() {
		return "";
	}
	
	
}
