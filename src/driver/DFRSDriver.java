package driver;

import java.util.Scanner;

import client.ManagerClient;
import client.PassengerClient;

public class DFRSDriver {
	public static void main(String[] args) {
		showMenu();
		int userInput = 0;
		Scanner input = new Scanner(System.in);
		
		while(true)
        {
            Boolean valid = false;
            while(!valid)
            {
                try {
                	userInput = input.nextInt();
                    valid = true;
                }
                catch(Exception e)
                {
                    System.out.println("Invalid Input, please enter an Integer");
                    valid = false;
                    input.nextLine();
                }
            }
            jumpToClient(userInput);
        }
		
		
	}
	
	private static void showMenu() {
		System.out.println("\n***********  Welcome to DFRS ***********\n");
		System.out.println("Please select an option 1 or 2");
		System.out.println("1. Login as a manager");
		System.out.println("2. Login as a passenger");
	}
	
	private static void jumpToClient(int userChoice) {
		switch(userChoice) {
		case 1:
			System.out.println("Jumping to manager client...");
			new ManagerClient().showMenu();
			break;
		case 2:
			System.out.println("Jumping to passenger client...");
			new PassengerClient().showMenu();
			break;
		default:
			System.out.println("Not a valid option, choose again.");
			showMenu();
		}
	}
}
