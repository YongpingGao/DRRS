package client;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import model.City;
import model.FlightRecord;
import model.Manager;
import model.ManagerList;
import model.Passenger;
import server.DFRSInterface;
import util.FileProcessing;

public class ManagerClient {
	private Manager manager;
	private Calendar cal = Calendar.getInstance();;
	private void showCities(){
		System.out.println("1. Montreal");
		System.out.println("2. Washington");
		System.out.println("3. New Delhi");
	}
	
	private int[] parseInput(String string) {
		String[] temp = new String[5];
		int[] result = new int[5];
		temp = string.split("-");
		for(int i = 0; i < temp.length; i++){
			result[i] = Integer.parseInt(temp[i]);
		}
		return result;
	}

	private void showMenuLoggedIn(){
		while (true) {
			System.out.println("\n***********  Hello, " + manager.getManagerID() + " ***********\n");
			System.out.println("Conneting to " + manager.getCity() + " server");
			System.out.println("Please tell us what you want to do: ");
			System.out.println("1. edit flight record, if no records found, we will add a new one");
			System.out.println("2. get booked flight count");
			Scanner input = new Scanner(System.in);
	        Boolean valid = false;
	        int userInput = 0;
	        while(!valid)
	        {
	            try {
	            	userInput = input.nextInt();
	                valid = true;
	            }
	            catch(Exception e) {
	                System.out.println("Invalid Input, please enter an Integer");
	                valid = false;
	                input.nextLine();
	            }
	        }
	        valid = false;
	        switch(userInput) {
	        case 1: 
	        	
	        	Scanner reader = new Scanner(System.in);   
	        	System.out.println("Please input the recordID: ");
	            String recordID = reader.nextLine(); //
	 
	        	City departure = manager.getCity();
	        	City destination = null;
	    		System.out.println("Please choose the destination city:");
	    		showCities();
	    		while(!valid){
	    			try {
	    				int destinationCity = input.nextInt();
	    				if(destinationCity <= 3 && destinationCity >0) {
	    					valid = true;
	    					destination = City.values()[destinationCity - 1];
	    					if(destination == departure) {
	    						System.out.println("Destination and Departure are the same.");
	    						destination = null;
	    					}
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
	    		
	    		Date dateOfFlight = null;
	    		System.out.println("Please input the date: (example: 2016-11-04-10-20)");
	    	    String userInputDate = "";
	    	    userInputDate = input.next();
	        	int[] date = parseInput(userInputDate);
	        	cal.set(date[0], date[1], date[2], date[3], date[4]);
	        	dateOfFlight = cal.getTime();
	        	
	        	FlightRecord fr = new FlightRecord(departure, destination, dateOfFlight);
	        	String departureCity = departure.toString();
	        	try {
	    			System.setSecurityManager(new RMISecurityManager());
	    			DFRSInterface server = (DFRSInterface)Naming.lookup("rmi://localhost:" + ServerInfo.getServerMaps().get(departureCity) + "/" + departureCity); 			
	    			server.addFlightRecord(fr, recordID);
	    	       	System.out.println("Suecesfully add or edit the record!");
	    		} catch(Exception e) {
	    			e.printStackTrace();
	    		}
	        	break;
	        case 2:
//	        	String results = manager.getBookedFlightCount();
//	        	System.out.println(results);
	        	break;
	        default:
	        	System.out.println("Not a valid option, choose again.");
	        	showMenuLoggedIn();
	        }
		}
	}
	
	public void showMenu() {
		System.out.println("\n***********  Welcome to Manager Client ***********\n");
		System.out.println("Please input your ManagerID:");
		String userInput = "";
		Scanner input = new Scanner(System.in);
		while(true)
        {
            Boolean valid = false;
            while(!valid)
            {
            	userInput = input.next();
            	ManagerList ml = FileProcessing.sharedInstance().readFromJsonFile("src/client/managers.json", ManagerList.class);
                for(Manager m: ml.getManagerList()){
                	if(userInput.equalsIgnoreCase(m.getManagerID())) {
                		manager = m;
                		valid = true;
                		showMenuLoggedIn();
                	}
                	else continue;
                }
                valid = false;
                System.out.println("Can't find a record in DB. Please try again");
                input.nextLine();
            }
        }
	}
	
	public static void main(String[] args) {
		addRecordsToDB();
	}
	
	public static void addRecordsToDB(){
		ManagerList ml = new ManagerList();
		ml.addManager(new Manager("MTL0001", City.Montreal));
		ml.addManager(new Manager("MTL0002", City.Montreal));
		ml.addManager(new Manager("MTL0003", City.Montreal));
		ml.addManager(new Manager("MTL0004", City.Montreal));
		ml.addManager(new Manager("MTL0005", City.Montreal));
		
		ml.addManager(new Manager("WA0001", City.Washington));
		ml.addManager(new Manager("WA0002", City.Washington));
		ml.addManager(new Manager("WA0003", City.Washington));
		
		ml.addManager(new Manager("ND0001", City.NewDelhi));
		ml.addManager(new Manager("ND0002", City.NewDelhi));
	
		FileProcessing.sharedInstance().writeToJsonFile("src/client/managers.json", ml);
	}

}
