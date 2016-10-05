package client;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Date;
import java.util.Scanner;

import model.City;
import model.FlightClass;
import model.Passenger;
import model.PassengerRecord;
import server.DFRSInterface;

public class PassengerClient {
	
	private Passenger passenger;
	private City departureCity;
	private PassengerRecord passengerRecord;
	private void showCities(){
		System.out.println("1. Montreal");
		System.out.println("2. Washington");
		System.out.println("3. New Delhi");
	}

	public void showMenu() {
		
		boolean valid = false;
		Scanner input = new Scanner(System.in);
		System.out.println("\n***********  Welcome to Passenger Client ***********\n");
		System.out.println("Please input your first name:");
		String firstName = input.nextLine();
		System.out.println("Please input your last name:");
		String lastName = input.nextLine();
		System.out.println("Please input your address");
		String address = input.nextLine();
		System.out.println("Please input your phone number");
		String phoneNumber = input.nextLine();
		passenger = new Passenger(firstName, lastName, address, phoneNumber);
		
		System.out.println("Please select the departure city: ");
		showCities();
		while(!valid){
			try {
				int departure = input.nextInt();
				if(departure <= 3 && departure >0) {
					valid = true;
					departureCity = City.values()[departure - 1];
				} else {
					 valid = false;
					 System.out.println("Invalid Input, please enter 1, 2 or 3 ");
					 input.nextLine();
				}
				
			} catch(Exception e) {
				System.out.println("Invalid Input, please enter an Integer");
                valid = false;
                input.nextLine();
			}
		}	
		System.out.println("Please select the destination: ");
		showCities();
		valid = false;
		City destinationCity = null;
		while(!valid){
			try {
				int destination = input.nextInt();
				if(destination <= 3 && destination >0) {
					valid = true;
					destinationCity = City.values()[destination - 1];
				} else {
					 valid = false;
					 System.out.println("Invalid Input, please enter 1, 2 or 3 ");
					 input.nextLine();
				}
			} catch(Exception e) {
				System.out.println("Invalid Input, please enter an Integer");
                valid = false;
                input.nextLine();
			}
		}	
		System.out.println("Please select the class you want book: ");
		System.out.println("1. 	economy");
		System.out.println("2. 	business");
		System.out.println("3. 	fit");
		valid = false;
		FlightClass flightClass = null;
		while(!valid){
			try {
				int fc = input.nextInt();
				if(fc <= 3 && fc >0) {
					valid = true;
					flightClass = FlightClass.values()[fc - 1];
				} else {
					 valid = false;
					 System.out.println("Invalid Input, please enter 1, 2 or 3 ");
					 input.nextLine();
				}
			} catch(Exception e) {
				System.out.println("Invalid Input, please enter an Integer");
                valid = false;
                input.nextLine();
			}
		}	
		System.out.println("This is the availble dates for this flight, please select the time: ");
		//TODO dates query in db
		Date dateOfFlight = new Date();
		
		passengerRecord = new PassengerRecord(passenger, destinationCity, flightClass, dateOfFlight);
		String departure = departureCity.toString();
		
		try {
			System.setSecurityManager(new RMISecurityManager());
			DFRSInterface server = (DFRSInterface)Naming.lookup("rmi://localhost:" + ServerInfo.getServerMaps().get(departure) + "/" + departure);
			
			server.bookFlight(passengerRecord);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
