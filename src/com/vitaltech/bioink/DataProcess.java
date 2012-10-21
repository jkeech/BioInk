package com.vitaltech.bioink;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DataProcess {
	//List of current users
	public ConcurrentHashMap<String,User> users;
	private Scene scene;
	private Timer utimer = new Timer();
	
	//Positioning range constants
	private final float minPos = -10;
	private final float maxPos = 10;
	
	//Heart rate range constant
	private final float minHR = 0;
	private final float maxHR = 250;
	
	//Respiration rate 
	private final float minResp = 0;
	private final float maxResp = 70;
	
	//Data Processing constructor
	public DataProcess(int updateInterval){
		users = new ConcurrentHashMap<String,User>(13);
		utimer.schedule(new CalculationTimer(), 0, updateInterval);
	}
	
	public void addScene(Scene ss){
		this.scene = ss;
	}
	
	//flag that controls whether or not the first HRV has been calculated
	public boolean active_hrv = false;
	
	//Method allows bluetooth mod to push data into data Process mod
	//User is specified by its ID
	public void push(String uid, BiometricType dtype, float value){
		if(!users.containsKey(uid)){
			User tmp = new User();
			tmp.id = uid;
			users.put(uid,tmp); // insert into the dictionary if it does not exist
		}
		
		switch(dtype){
		case HEARTRATE: 
			users.get(uid).heartrate = value;
			break;
		case RESPIRATION:
			users.get(uid).respiration = value;
			break;
		case RR: 
			//To be added once the RR interval structures have been built
			break;
		default:
			break;
		}
		
	}
	
	//map respiration rate from [minhr, maxhr] to [0, 1]
	public float mapHeartRate(String uid){
		float temp = 0;
		temp = (users.get(uid).heartrate - minHR ) / (maxHR - minHR);
		
		//validation
		temp = Math.max(Math.min(temp, 1), 0);
		
		scene.update(uid, DataType.ENERGY, temp);
		
		return temp;
	}
	
	//map respiration rate from [minresp, maxresp] to [0, 1]
	public float mapRespirationRate(String uid){
		float temp = 0;
		temp = (users.get(uid).respiration - minResp ) / (maxResp - minResp);
		
		//validation
		temp = Math.max(Math.min(temp, 1), 0);
		
		scene.update(uid, DataType.COLOR, temp);
		return temp;
	}
	
	//map position of given biometrics from their own intervals to [minpos, maxpos]
	//Note: the current biometric values are being mapped into a cube of side length maxpos - minpos
	//aka not a sphere yet
	public void mapPosition(String uid){
		float temp = 0;
		float x = 0;
		float y = 0;
		float z = 0;
		
		//extract user heart rate and calculate x-axis positioning
		temp = users.get(uid).heartrate;
		x = (temp - minHR ) / (maxHR - minHR);
		x = x * (maxPos - minPos);
		x = x + minPos;
		
		//validation
		x = Math.max(Math.min(x, maxPos), minPos);
		
		//extract user respiration rate and calculate y-axis positioning
		temp = users.get(uid).respiration;
		y = (temp - minResp ) / (maxResp - minResp);
		y = y * (maxPos - minPos);
		y = y + minPos;
		
		//validation
		y = Math.max(Math.min(y, maxPos), minPos);
		
		//update x and y values
		scene.update(uid, DataType.X, x);
		scene.update(uid, DataType.Y, y);
		
		if(active_hrv){
			//calculate and update Z position value
			z = 1;
		}
	}
	
	/*
	 * This method calculates the biometric and positional mappings
	 */
	public void calculateTargets(){
		Collection<User> c = users.values();
		for(User user : c){
			mapRespirationRate(user.id);
			mapHeartRate(user.id);
			mapPosition(user.id);
		}
	}
	
	/*
	 * This class runs the mappings based on the timer interval
	 */
	private class CalculationTimer extends TimerTask {
		public void run(){
			calculateTargets();
		}
	}
	
	/*Goes into main activity
	 * DataProcess dp = new DataProcess(1000);
	 * dp.addScene(scene);
	*/
}
