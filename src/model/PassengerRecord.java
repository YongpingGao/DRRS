package model;

import java.io.Serializable;
import java.util.Date;

public class PassengerRecord implements Serializable {

	private Passenger passenger;
	private City destination;
	private FlightClass flightClass;
	private String dateOfFlight;
	
	public PassengerRecord(Passenger passenger, City destination, FlightClass flightClass, String dateOfFlight) {
		this.passenger = passenger;
		this.destination = destination;
		this.flightClass = flightClass;
		this.dateOfFlight = dateOfFlight;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public City getDestination() {
		return destination;
	}

	public FlightClass getFlightClass() {
		return flightClass;
	}

	public String getDateOfFlight() {
		return dateOfFlight;
	}
	
	
	
}
