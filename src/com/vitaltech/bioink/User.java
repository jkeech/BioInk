package com.vitaltech.bioink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * This class models a single user within the data processing module
 */
public class User {
	// biological data retrieved from sensors
	public String id;
	public float heartrate = 0;
	public float respiration = 0;
	public float hrv = DataProcess.MAX_HRV / 2;
	boolean sign = false;
	public boolean hrv_active = false;
	public boolean merged = false;
	public List<Float> rrq;
	
	public final int qsize = 20;
	
	public User(){
		rrq = Collections.synchronizedList(new ArrayList<Float>());
	}
	
	public void addRR(float value){
		int size = rrq.size();
		
		//check if new RR interval value has been received. if so, add to the list
		if(rrq.isEmpty()){
			rrq.add(new Float(value));
		}else if(value != rrq.get(size - 1)){
			if(rrq.size() < qsize){
				rrq.add(new Float(value));
			}else{
				//list is full, remove oldest value and add new to the end of the list
				rrq.remove(0);
				rrq.add(new Float(value));
			}
			
			//check if hrv was inactive and needs to be activated
			if(qsize == rrq.size() && !hrv_active){
				hrv_active = true;
			}
		}else{
			//no new value to be added, exit
		}
	}
	
	// returns the value of a given biometric type for the user
	public float get(BiometricType t){
		switch(t){
			case HEARTRATE: return heartrate;
			case RESPIRATION: return respiration;
			case HRV: return hrv;
			default: return 0;
		}
	}
	
	public float calculateHRV(){
		float rmssd = 0f;
		
		if(hrv_active){
			float ssd = 0;
			float previous = -1;
			
			synchronized(rrq) {
			    Iterator<Float> i = rrq.iterator(); // Must be in synchronized block
			    
			    while(i.hasNext()){
			    	float rri = Math.abs(i.next()); 
			    	if(previous == -1){
			    		previous = Math.abs(rri);
			    		continue;
			    	}else{
			    		//calculate consecutive difference
			    		float diff = previous - rri;
			    		diff = diff * diff;
			    		//add the new square difference to the total
			    		ssd = ssd + diff;
			    		//update the previous value
			    		previous = rri;
			    	}
			    }
			}
			//calculate the MSSD
			ssd = ssd / (rrq.size() - 1);
			//calculate the RMSSD value and update it to the HRV of the user
			rmssd = (float) Math.sqrt(ssd);
			rmssd = Math.max(Math.min(rmssd, DataProcess.MAX_HRV), 0);
			this.hrv = rmssd;
		}
		
		return rmssd;
	}
}
