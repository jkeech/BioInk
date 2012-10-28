package com.vitaltech.bioink;

import java.util.Random;

public class DataSimulator {
	private DataProcess dp;
	
	public DataSimulator(DataProcess dp){
		this.dp = dp;
	}
	
	public void run(){
    	try {
    		rangeOverview(1500,10);
    		//randomRange(2000);
    		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	//randomizes parameters over the entire range of values
	public void randomRange(int interval) throws InterruptedException{
		Random r = new Random();
		
		while(true){
			float hr1 = r.nextFloat() * dp.maxHR;
			float rr1 = r.nextFloat() * dp.maxResp;
			
			dp.push("user1", BiometricType.HEARTRATE, hr1);
    		dp.push("user1", BiometricType.RESPIRATION, rr1);
    		
    		Thread.sleep(interval);
    		
    		float hr2 = r.nextFloat() * dp.maxHR;
			float rr2 = r.nextFloat() * dp.maxResp;
			
    		dp.push("user2", BiometricType.HEARTRATE, hr2);
    		dp.push("user2", BiometricType.RESPIRATION, rr2);
    		
			Thread.sleep(interval);
		}
	}
	
	public void rangeOverview(int interval, int steps) throws InterruptedException{
		int ro = 0;
		float hr1 = 0;
		float rr1 = 0;
		float hr2 = 0;
		float rr2 = 0;
		float hr3 = 0;
		float rr3 = 0;
		float hr4 = 0;
		float rr4 = 0;
		
		while(true){
			for(int i = 0; i <= steps; i++){
				hr1 = i * dp.maxHR / steps;
				rr1 = 0 * dp.maxResp / 3;
				hr2 = hr1;
				rr2 = 1 * dp.maxResp / 3;
				hr3 = hr1;
				rr3 = 2 * dp.maxResp / 3;
				hr4 = hr1;
				rr4 = 3 * dp.maxResp / 3;
				
				
				dp.push("user1", BiometricType.HEARTRATE, hr1);
	    		dp.push("user1", BiometricType.RESPIRATION, rr1);
	    		dp.push("user2", BiometricType.HEARTRATE, hr2);
	    		dp.push("user2", BiometricType.RESPIRATION, rr2);
	    		dp.push("user3", BiometricType.HEARTRATE, hr3);
	    		dp.push("user3", BiometricType.RESPIRATION, rr3);
	    		dp.push("user4", BiometricType.HEARTRATE, hr4);
	    		dp.push("user4", BiometricType.RESPIRATION, rr4);
	    		
	    		Thread.sleep(interval);
			}
		}
	}
}
