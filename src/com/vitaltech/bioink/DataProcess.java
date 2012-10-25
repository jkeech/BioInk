package com.vitaltech.bioink;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class DataProcess {
	//List of current users
	public ConcurrentHashMap<String,User> users;
	private Scene scene;
	private Timer utimer = new Timer();
	private CalculationTimer task;
	private int uinterval = 0;
	
	//Positioning range constants
	private final float minPos = -1;
	private final float maxPos = 1;
	
	//Heart rate range constant
	public final float minHR = 0;
	public final float maxHR = 250;
	
	//Respiration rate 
	public final float minResp = 0;
	public final float maxResp = 70;
	
	//Data Processing constructor
	public DataProcess(int updateInterval){
		users = new ConcurrentHashMap<String,User>(13);
		uinterval = updateInterval;
		resume();
	}
	
	public void pause(){
		task.cancel();
	}
	
	public void resume(){
		task = new CalculationTimer();
		utimer.schedule(task, 0, uinterval);
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
		float magnitude = 0;
		
		//map user heart rate to [-1,1]
		temp = users.get(uid).heartrate;
		//x = (temp - minHR ) / (maxHR - minHR);
		x = temp - ((maxHR + minHR) / 2);
		x = x / ((maxHR - minHR) / 2);
		//validation
		//x = Math.max(Math.min(x, maxPos), minPos);
		
		//map user respiration rate to [-1,1]
		temp = users.get(uid).respiration;
		//y = (temp - minResp ) / (maxResp - minResp);
		y = temp - ((maxResp + minResp) / 2);
		y = y / ((maxResp - minResp) / 2);
		//validation
		//y = Math.max(Math.min(y, maxPos), minPos);
		
		//normalize the data vector
		magnitude = x * x + y * y + z * z;
		magnitude = (float) Math.sqrt(magnitude);
		x = x / magnitude;
		y = y / magnitude;
		
		//scale to display sphere
		y = y * (maxPos - minPos) / 2;
		y = y + ((maxPos + minPos) / 2);
		x = x * (maxPos - minPos) / 2;
		x = x + ((maxPos + minPos) / 2);
		
//		Log.d("dp", "x: " + x);
//		Log.d("dp", "y: " + y);
		//Log.d("dp", "z: " + z);
		
		//update x and y values
		scene.update(uid, DataType.X, x);
		scene.update(uid, DataType.Y, y);
		
		if(active_hrv){
			//calculate and update Z position value
			z = 1;
		}
	}
	
	public void mapSphericalPosition(String uid){
		float temp = 0;
		
		//coordinates
		float x = 0;
		float y = 0;
		float z = 0;
		
		//spherical coordinates
		float sigma = 0; //heart rate 0-180
		float delta = 0; //respiration rate 0-360
		float ro = 0; //hrv
		
		//extract user heart rate and calculate sigma positioning
		temp = users.get(uid).heartrate;
		sigma = (temp - minHR ) / (maxHR - minHR); //map to 0-1
		sigma = sigma * 180;
		sigma = (float) Math.toRadians(sigma);
		
		//extract user respiration rate and calculate delta positioning
		temp = users.get(uid).respiration;
		delta = (temp - minResp ) / (maxResp - minResp);
		delta = delta * 360;
		delta = (float) Math.toRadians(delta);
		
		//extract hrv and calculate ro position
		ro = 1;
		
		x = (float) (ro * Math.sin(sigma) * Math.cos(delta));
		x = x * (maxPos - minPos) / 2;
		x = x + ((maxPos + minPos) / 2);
		//validation
		x = Math.max(Math.min(x, maxPos), minPos);
		
		y = (float) (ro * Math.sin(sigma) * Math.sin(delta));
		y = y * (maxPos - minPos) / 2;
		y = y + ((maxPos + minPos) / 2);
		//validation
		y = Math.max(Math.min(y, maxPos), minPos);
		
		z = (float) (ro * Math.cos(sigma));
		//Z transformation from one spehere to another omitted on purpose
		
//		Log.d("dp", "x: " + x);
//		Log.d("dp", "y: " + y);
		//Log.d("dp", "z: " + z);
		
		//update x and y values
		scene.update(uid, DataType.X, x);
		scene.update(uid, DataType.Y, y);
		scene.update(uid, DataType.Z, z);
		
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
			//mapSphericalPosition(user.id);
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
	
	public void quitDP(){
		utimer.cancel();
	}
	
}
