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
	public final float mdis = 0.08f;
	
	//Data Processing constructor
	public DataProcess(int updateInterval){
		users = new ConcurrentHashMap<String,User>(23);
		merged = new ArrayList<MergedUser>();
		//Collections.synchronizedList(new ArrayList<MergedUser>());
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
		synchronized(users){
			if(!users.containsKey(uid)){
				User tmp = new User();
				tmp.id = uid;
				users.put(uid,tmp); // insert into the dictionary if it does not exist
			}
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
		float uhr = users.get(uid).heartrate;
		float ure = users.get(uid).respiration;
		float uhv = users.get(uid).hrv;
		
		if(users.get(uid).hrv_active){
			map3DPosition(uid, uhr, ure, uhv);
		}else{
			map2DPosition(uid, uhr, ure);
		}
	}
	
	public void mapMergedPosition(MergedUser mu){
		float uhr = mu.heartrate;
		float ure = mu.respiration;
		float uhv = mu.hrv;
		
		for(String uu: mu.members){
			
			if(users.get(uu).hrv_active){
				map3DPosition(uu, uhr, ure, uhv);
			}else{
				map2DPosition(uu, uhr, ure);
			}
			
			if(mu.members.indexOf(uu) == 0){
				//first element on the member list
				scene.update(uu, DataType.VOLUME, 1.5f * mu.members.size());
			}else{
				scene.update(uu, DataType.VOLUME, 0.01f);
			}
		}
	}
	
	public void map3DPosition(String uid, float uhr, float ure, float uhv){
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
		x = uhr - ((maxHR + minHR) / 2);
		x = x / ((maxHR - minHR) / 2);
		
		//map user respiration rate to [-1,1]
		y = ure - ((maxResp + minResp) / 2);
		y = y / ((maxResp - minResp) / 2);
		
		//map hrv to [-1,1]
		z = uhv - ((maxHRV + minHRV) / 2);
		z = z / ((maxHRV - minHRV) / 2);
		
		//map cube to r=1 sphere 
		abx = Math.abs(x);
		aby = Math.abs(y);
		abz = Math.abs(z);
		
		//individual cases for the six faces of the cube
		//case A: upper most
		if(y > 0 && y > abx && y > abz){
			cy = 1;
			cx = abx / aby;
			cz = abz / aby;
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
		
		//individual cases for pairs of coordinates being equal
		if(abx == aby && abx != abz){
			if(abx > abz){
				cx = 1;
				cy = 1;
				cz = abz / abx;
			}//else case already being man handled
		}
		
		if(abx == abz && abx != aby){
			if(abx > aby){
				cx = 1;
				cz = 1;
				cy = aby / abx;
			}
		}
		
		if(aby == abz && aby != abx){
			if(aby > abx){
				cy = 1;
				cz = 1;
				cx = aby / abx;
			}
		}
		
		//maps square to sphere using ratio
		//special case, all 3 coordinates are equal 
		if(abx == aby && aby == abz){
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
	
	public void map2DPosition(String uid, float uhr, float ure){
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
		x = uhr - ((maxHR + minHR) / 2);
		x = x / ((maxHR - minHR) / 2);
		
		//map user respiration rate to [-1,1]
		y = ure - ((maxResp + minResp) / 2);
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
		
		//maps square to circle using ratio
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
		updateMergedUsers();
		
		splitUsers();
		
		mergeUsers();
		
		mapBiometrics();
	}
	
	//This method goes through every group of merged users 
	//and calculates the average of their biometrics 
	private void updateMergedUsers(){
		for(MergedUser mu: merged){
			updateMergedMetrics(mu);
		}
	}
	
	private void splitUsers(){
		List<MergedUser> removedmus = new ArrayList<MergedUser>();
		
		for(MergedUser mu: merged){
			String removeduu = "";
			boolean changed = false;
			
			for(String uu: mu.members){
				float dis = distance(mu, uu);
				
				//user is no longer within mdis of the average
				if(dis > mdis){
					users.get(uu).merged = false;
					scene.update(uu, DataType.VOLUME, 1);
					removeduu = uu;
					changed = true;
					break;
				}
			}
			
			//if an user was removed, need to recalculate the average of the metrics
			if(changed){
				mu.members.remove(removeduu);
				if(mu.members.size() < 2){
					//the users aren't similar anymore, the MU structured needs to be removed
					for(String uu: mu.members){
						users.get(uu).merged =  false;
						scene.update(uu, DataType.VOLUME, 1);
					}
					mu.members.clear();
					removedmus.add(mu);
				}else{
					updateMergedMetrics(mu);
				}
			}
		}
		
		for(MergedUser mus:removedmus){
			merged.remove(mus);
		}
		
	}
	
	private void mergeUsers(){
		//cycle through non-merged users
		synchronized(users){
			Collection<User> c = users.values();
			
			for(User user : c){
				if(!user.merged){
					for(MergedUser mu: merged){
						float dis = distance(mu, user.id);
						//the user is close to a merged user
						if(dis <= mdis){
							//Log.d("dp", "Added mu: " + user.id);
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
					if(dis <= mdis && (u1.id != u2.id) && !u1.merged && !u2.merged){
						u1.merged = true;
						u2.merged = true;
						//Log.d("dp", "Added mu: " + u1.id);
						//Log.d("dp", "Added mu: " + u2.id);
						MergedUser temp = new MergedUser(u1.id, u2.id);
						updateMergedMetrics(temp);
						merged.add(temp);
					}
				}
			}
		}
		
		//cycle through pairs of merged user structures
		boolean flag = false;
		MergedUser deletemu = null;
		for(MergedUser mu1: merged){
			for(MergedUser mu2: merged){
				float dis = distance(mu1, mu2);
				//new pair of similar groups of users that need to be multi merged
				if(dis <= mdis && (mu1 != mu2)){
					
					for(String uu: mu2.members){
						mu1.members.add(uu);
					}
					
					updateMergedMetrics(mu1);
					deletemu = mu2;
					flag = true;
					break;
				}
			}
			if(flag) break;
		}
		if(flag){
			deletemu.members.clear();
			merged.remove(deletemu);
		}
	}
	
	public void mapBiometrics(){
		synchronized(users){
			Collection<User> c = users.values();
			for(User user : c){
				mapRespirationRate(user.id);
				mapHeartRate(user.id);
				//user.calculateHRV();
				
				if(!user.merged){
					mapPosition(user.id);
				}
			}
		}
		
		for(MergedUser mu: merged){
			mapMergedPosition(mu);
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
		
		//scale all metrics to [0,1]
		hr1 = (hr1 - minHR ) / (maxHR - minHR);
		hr2 = (hr2 - minHR ) / (maxHR - minHR);
		re1 = (re1 - minResp ) / (maxResp - minResp);
		re2 = (re2 - minResp ) / (maxResp - minResp);
		hv1 = (hv1 - minHRV ) / (maxHRV - minHRV);
		hv2 = (hv2 - minHRV ) / (maxHRV - minHRV);
		
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
		
		//scale all metrics to [0,1]
		hr1 = (hr1 - minHR ) / (maxHR - minHR);
		hr2 = (hr2 - minHR ) / (maxHR - minHR);
		re1 = (re1 - minResp ) / (maxResp - minResp);
		re2 = (re2 - minResp ) / (maxResp - minResp);
		hv1 = (hv1 - minHRV ) / (maxHRV - minHRV);
		hv2 = (hv2 - minHRV ) / (maxHRV - minHRV);
		
		dd = (hr1 - hr2) * (hr1 - hr2) + (re1 - re2) * (re1 - re2) + (hv1 - hv2) * (hv1 - hv2);
		dd = (float) Math.sqrt(dd);
		
		return dd;
	}
	
	public float distance(MergedUser mu1, MergedUser mu2){
		float dd = 0;
		float hr1 = mu1.heartrate;
		float hr2 = mu2.heartrate;
		float re1 = mu1.respiration;
		float re2 = mu2.respiration;
		float hv1 = mu1.hrv;
		float hv2 = mu2.hrv;
		
		//scale all metrics to [0,1]
		hr1 = (hr1 - minHR ) / (maxHR - minHR);
		hr2 = (hr2 - minHR ) / (maxHR - minHR);
		re1 = (re1 - minResp ) / (maxResp - minResp);
		re2 = (re2 - minResp ) / (maxResp - minResp);
		hv1 = (hv1 - minHRV ) / (maxHRV - minHRV);
		hv2 = (hv2 - minHRV ) / (maxHRV - minHRV);
		
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
