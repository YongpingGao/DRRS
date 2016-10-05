package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class FlightRecord implements Serializable {
	
	private String recordID;
	private City departure;
	private City destination;
	
	private int economySeats;
	private int businessSeats;
	private int fitsSeats;
	
	private Date dateOfFlight;
	
 
	
	public FlightRecord(City departure, City destination, Date dateOfFlight) {
		this.departure = departure;
		this.destination = destination;
		this.dateOfFlight = dateOfFlight;
		this.recordID = Long.toString(System.currentTimeMillis() / 1000L);
		initilizeSeatNumber();
	}

	private void initilizeSeatNumber() {
		if(departure == City.Montreal && destination == City.Washington) {
			economySeats = 55;
			businessSeats = 20;
			fitsSeats = 10;
		} else if(departure == City.Washington && destination == City.Montreal) {
			economySeats = 65;
			businessSeats = 10;
			fitsSeats = 10;
		} else if(departure == City.Montreal && destination == City.NewDelhi) {
			economySeats = 54;
			businessSeats = 20;
			fitsSeats = 20;
		} else if(departure == City.NewDelhi && destination == City.Montreal) {
			economySeats = 45;
			businessSeats = 30;
			fitsSeats = 20;
		} else if(departure == City.NewDelhi && destination == City.Washington) {
			economySeats = 65;
			businessSeats = 10;
			fitsSeats = 5;
		} else if(departure == City.Washington && destination == City.NewDelhi) {
			economySeats = 75;
			businessSeats = 20;
			fitsSeats = 20;
		} else {
			economySeats = 0;
			businessSeats = 0;
			fitsSeats = 0;
		}
	}

	public String getRecordID() {
		return recordID;
	}
	
	
	
	
}
