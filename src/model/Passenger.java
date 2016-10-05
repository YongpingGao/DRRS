package model;

import java.io.Serializable;
import java.util.Date;

public class Passenger implements Serializable{

	private String firstName;
	private String lastName;
	private String address;
	private String phoneNumber;
	
	public Passenger(String firstName, String lastName, String address, String phoneNumber) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	
}
