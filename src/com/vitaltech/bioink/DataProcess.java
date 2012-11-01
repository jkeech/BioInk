package com.vitaltech.bioink;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class DataProcess {
	//List of current users
	public ConcurrentHashMap<String,User> users;
	public List<MergedUser> merged;
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
	
	//Heart rate variability
	public final float minHRV = 0;
	public final float maxHRV = 200;
	
	//Distance that determines if two users are similar
	public final float mdis = 0.1f;
	
	//Data Processing constructor
	public DataProcess(int updateInterval){
		users = new ConcurrentHashMap<String,User>(23);
		merged = Collections.synchronizedList(new ArrayList<MergedUser>());
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
	
	//Method allows bluetooth mod to push data into data Process mod
	//User is specified by its id string
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
		case HRV:
			users.get(uid).hrv = value;
			users.get(uid).hrv_active = true;
			break;
		case RR:
			users.get(uid).addRR(value);
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
	public void mapPosition(String uid){
		if(users.get(uid).hrv_active){
			map3DPosition(uid);
		}else{
			map2DPosition(uid);
		}
	}
	
	public void map3DPosition(String uid){
		float temp = 0;
		float ratio = 1;
		float x = 0;
		float y = 0;
		float z = 0;
		//mapped coordinate values
		float cx = 0;
		float cy = 0;
		float cz = 0;
		//absolute values of calculated coordinates
		float abx = 0;
		float aby = 0;
		float abz = 0;
		float magnitude = 0;
		
		//map user heart rate to [-1,1]
		temp = users.get(uid).heartrate;
		x = temp - ((maxHR + minHR) / 2);
		x = x / ((maxHR - minHR) / 2);
		
		//map user respiration rate to [-1,1]
		temp = users.get(uid).respiration;
		y = temp - ((maxResp + minResp) / 2);
		y = y / ((maxResp - minResp) / 2);
		
		//map hrv to [-1,1]
		temp = users.get(uid).hrv;
		z = temp - ((maxHRV + minHRV) / 2);
		z = z / ((maxHRV - minHRV) / 2);
		
		//map cube to r=1 sphere 
		abx = Math.abs(x);
		aby = Math.abs(y);
		abz = Math.abs(z);
		
		//case A: upper most
		if(y > 0 && y > abx && y > abz){
			cy = 1;
			cx = abx / aby;
			cz = abz / aby;
		}else if(y > 0 && abx == abz){
			
		}
		
		//case B: lower most
		if((-y > 0) && (-y > abx) && (-y > abz)){
			cy = 1;
			cx = abx / aby;
			cz = abz / aby;
		}
		
		//case C: right most
		if(x > 0 && x > aby && x > abz){
			cx = 1;
			cy = aby / abx;
			cz = abz / abx;
		}
		
		//case D: left most
		if((-x > 0) && (-x > aby) && (-x > abz)){
			cx = 1;
			cy = aby / abx;
			cz = abz / abx;
		}
		
		//case E: front
		if(z > 0 && z > abx && z > aby){
			cz = 1;
			cx = abx / abz;
			cy = aby / abz;
		}
		
		//case F: back
		if((-z > 0) && (-z > abx) && (-z > aby)){
			cz = 1;
			cx = abx / abz;
			cy = aby / abz;
		}
		
		//maps thing to thing
		//special case for the corners of the cube. 
		if(abx == aby || abz == abx || aby == abz){
			magnitude = (float) Math.sqrt(3);
		}else{
			magnitude = cx * cx + cy * cy + cz * cz;
			magnitude = (float) Math.sqrt(magnitude);
		}
		
		if(magnitude == 0){
			ratio = 1;
		}else{
			ratio = 1 / magnitude;
		}
		
		x = x * ratio;
		y = y * ratio;
		z = z * ratio;
		
		//scale to display sphere
		y = y * (maxPos - minPos) / 2;
		y = y + ((maxPos + minPos) / 2);
		x = x * (maxPos - minPos) / 2;
		x = x + ((maxPos + minPos) / 2);
		z = z * (maxPos - minPos) / 2;
		z = z + ((maxPos + minPos) / 2);
		
		/*Log.d("dp", uid + " x: " + x);
		Log.d("dp", uid + " y: " + y);
		Log.d("dp", uid + " z: " + z);*/
		
		//validation
		y = Math.max(Math.min(y, maxPos), minPos);
		x = Math.max(Math.min(x, maxPos), minPos);
		z = Math.max(Math.min(z, maxPos), minPos);
		
		//update x and y values
		scene.update(uid, DataType.X, x);
		scene.update(uid, DataType.Y, y);
		scene.update(uid, DataType.Z, z);
	}
	
	public void map2DPosition(String uid){
		float temp = 0;
		float ratio = 1;
		float x = 0;
		float y = 0;
		//mapped coordinate values
		float cx = 0;
		float cy = 0;
		//absolute values of calculated coordinates
		float abx = 0;
		float aby = 0;
		float magnitude = 0;
		
		//map user heart rate to [-1,1]
		temp = users.get(uid).heartrate;
		x = temp - ((maxHR + minHR) / 2);
		x = x / ((maxHR - minHR) / 2);
		
		//map user respiration rate to [-1,1]
		temp = users.get(uid).respiration;
		y = temp - ((maxResp + minResp) / 2);
		y = y / ((maxResp - minResp) / 2);
		
		//map cube to r=1 sphere 
		abx = Math.abs(x);
		aby = Math.abs(y);
		
		//case A: upper most
		if(y > 0 && y > abx){
			cy = 1;
			cx = abx / aby;
		}
		
		//case B: lower most
		if((-y > 0) && (-y > abx)){
			cy = 1;
			cx = abx / aby;
		}
		
		//case C: right most
		if(x > 0 && x > aby){
			cx = 1;
			cy = aby / abx;
		}
		
		//case D: left most
		if((-x > 0) && (-x > aby)){
			cx = 1;
			cy = aby / abx;
		}
		
		//maps thing to thing
		if(abx == aby){
			magnitude = (float) Math.sqrt(2);
		}else{
			magnitude = cx * cx + cy * cy;
			magnitude = (float) Math.sqrt(magnitude);
		}
		
		if(magnitude == 0){
			ratio = 1;
		}else{
			ratio = 1 / magnitude;
		}
		
		x = x * ratio;
		y = y * ratio;
		
		//scale to display sphere
		y = y * (maxPos - minPos) / 2;
		y = y + ((maxPos + minPos) / 2);
		x = x * (maxPos - minPos) / 2;
		x = x + ((maxPos + minPos) / 2);

		/*Log.d("dp", "x: " + x);
		Log.d("dp", "y: " + y);*/
		
		//validation
		y = Math.max(Math.min(y, maxPos), minPos);
		x = Math.max(Math.min(x, maxPos), minPos);
		
		//update x and y values
		scene.update(uid, DataType.X, x);
		scene.update(uid, DataType.Y, y);
	}
	
	/*
	 * This method calls for the biometric and positional mappings and pushes the values
	 * onto the rendering engine
	 */
	public void calculateTargets(){
		Collection<User> c = users.values();
		for(User user : c){
			mapRespirationRate(user.id);
			mapHeartRate(user.id);
			//user.calculateHRV();
			
			//check if needs to be split
			splitUsers();
			//check if needs to be merged
			mergeUsers();
			
			//push position for blobs
			//if merged, push the average or some shit
			//if not merged, push normal 
			mapPosition(user.id);
		}
	}
	
	//This method goes through every group of merged users 
	//and calculates the average of their biometrics 
	private void updateMergedUsers(){
		for(MergedUser mu: merged){
			updateMergedMetrics(mu);
		}
	}
	
	private void splitUsers(){
		for(MergedUser mu: merged){
			boolean changed = false;
			
			for(String uu: mu.members){
				float dis = distance(mu, uu);
				
				//user is no longer within mdis of the average
				if(dis < mdis){
					users.get(uu).merged = false;
					mu.members.remove(uu);
					changed = true;
				}
			}
			
			//if an user was removed, need to recalculate the average of the metrics
			if(changed){
				updateMergedMetrics(mu);
			}
		}
	}
	
	private void mergeUsers(){
		//cycle through non-merged users
		Collection<User> c = users.values();
		for(User user : c){
			if(!user.merged){
				for(MergedUser mu: merged){
					float dis = distance(mu, user.id);
					//the user is close to a merged user
					if(dis < mdis){
						user.merged = true;
						mu.members.add(user.id);
						updateMergedMetrics(mu);
						break;
					}
				}
			}
		}
		
		//cycle through pairs of unmerged users
		//untested method for traversing values on a hash table twice
		for(User u1: c){
			for(User u2: c){
				float dis = distance(u1.id, u2.id);
				//new pair of similar users discovered
				if(dis < mdis){
					u1.merged = true;
					u2.merged = true;
					MergedUser temp = new MergedUser(u1.id, u2.id);
					updateMergedMetrics(temp);
					merged.add(temp);
				}
			}
		}
	}
	
	public float distance(String u1, String u2){
		float dd = 0;
		float hr1 = users.get(u1).heartrate;
		float hr2 = users.get(u2).heartrate;
		float re1 = users.get(u1).respiration;
		float re2 = users.get(u2).respiration;
		float hv1 = users.get(u1).hrv;
		float hv2 = users.get(u2).hrv;
		
		dd = (hr1 - hr2) * (hr1 - hr2) + (re1 - re2) * (re1 - re2) + (hv1 - hv2) * (hv1 - hv2);
		dd = (float) Math.sqrt(dd);
		
		return dd;
	}
	
	public float distance(MergedUser mu, String uu){
		float dd = 0;
		float hr1 = mu.heartrate;
		float hr2 = users.get(uu).heartrate;
		float re1 = mu.respiration;
		float re2 = users.get(uu).respiration;
		float hv1 = mu.hrv;
		float hv2 = users.get(uu).hrv;
		
		dd = (hr1 - hr2) * (hr1 - hr2) + (re1 - re2) * (re1 - re2) + (hv1 - hv2) * (hv1 - hv2);
		dd = (float) Math.sqrt(dd);
		
		return dd;
	}
	
	public void updateMergedMetrics(MergedUser mu){
		float hr = 0;
		float re = 0;
		float hv = 0;
		
		for(String uu: mu.members){
			hr = hr + users.get(uu).heartrate;
			re = re + users.get(uu).respiration;
			hv = hv + users.get(uu).hrv;
		}
		
		mu.heartrate = hr / mu.members.size();
		mu.respiration = re / mu.members.size();
		mu.hrv = hv / mu.members.size();
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
		//stop the timer
		utimer.cancel();
		//clear the list storing the RR-intervals
		Collection<User> c = users.values();
		for(User user : c){
			user.rrq.clear();
		}
	}
	
}
