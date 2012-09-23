package com.vitaltech.bioink;


import java.util.concurrent.ConcurrentHashMap;

public class Scene {
	private ConcurrentHashMap<String,User> users = new ConcurrentHashMap<String,User>(7);
	
	/*
	 * This method updates a certain DataType value for one user. The user object
	 * will be created if it does not exist. This method should be called by the
	 * data processing module.
	 */
	void update(String id, DataType type, double val){
		users.putIfAbsent(id,new User()); // insert into the dictionary if it does not exist
		
		// update the user in the scene
		switch(type){
			case HEARTRATE: users.get(id).heartrate = val; break;
			case RESPIRATION: users.get(id).respiration = val; break;
			case TEMP: users.get(id).temp = val; break;
			case CONDUCTIVITY: users.get(id).conductivity = val; break;			
		}
	}
}
