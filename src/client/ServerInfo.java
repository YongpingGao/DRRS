package client;

import java.util.HashMap;

public class ServerInfo {
	
	
	private static HashMap<String, Integer> serverMaps;
    static {
    	serverMaps = new HashMap<>();
        serverMaps.put("Montreal", 2020);
        serverMaps.put("Washington", 2021);
        serverMaps.put("NewDelhi", 2022);
    }
    

	public static HashMap<String, Integer> getServerMaps() {
		// TODO Auto-generated method stub
		return serverMaps;
	}
    
    
	
}
