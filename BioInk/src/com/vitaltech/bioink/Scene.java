package com.vitaltech.bioink;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Scene {
	public ConcurrentHashMap<String,User> users = new ConcurrentHashMap<String,User>(7);
	private Timer updater = new Timer();
	
	public Scene(int updateInterval){
		updater.schedule(new CalculationTimer(), 0, updateInterval);
	}
	
	/*
	 * This method updates a certain DataType value for one user. The user object
	 * will be created if it does not exist. This method should be called by the
	 * data processing module.
	 */
	public void update(String id, DataType type, double val){
		users.putIfAbsent(id,new User()); // insert into the dictionary if it does not exist
		
		// update the user in the scene
		switch(type){
			case HEARTRATE: users.get(id).heartrate = val; break;
			case RESPIRATION: users.get(id).respiration = val; break;
			case TEMP: users.get(id).temp = val; break;
			case CONDUCTIVITY: users.get(id).conductivity = val; break;			
		}
	}
	
	/*
	 * This method will update each user's visualization data based on the currently set
	 * biological data.
	 */
	private void adjustTargets(){
		// TODO: iterate through users and update blob properties with
		// newly computed targets
	}
	
	/*
	 * This TimerTask will perform the calculations that update our scene objects
	 * on a given time interval.
	 */
	private class CalculationTimer extends TimerTask {
		public void run(){
			adjustTargets();
		}
	}
}
