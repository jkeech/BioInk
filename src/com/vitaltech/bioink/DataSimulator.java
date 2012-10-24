package com.vitaltech.bioink;

import java.util.Random;

public class DataSimulator {
	private DataProcess dp;
	
	public DataSimulator(DataProcess dp){
		this.dp = dp;
	}
	
	public void run(){
    	try {
    		
    		/*
    		dp.push("user1", BiometricType.HEARTRATE, dp.maxHR/2);
    		dp.push("user1", BiometricType.RESPIRATION, dp.maxResp/2);
    		Thread.sleep(1);
    		*/
    		//rotationQuads(2000);
    		randomRange(2000);
    		
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
	
	public void rotationQuads(int interval) throws InterruptedException{
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
			hr1 = (float) ((ro % 4) + 1) * dp.maxHR / 4;
			rr1 = (float) ((ro % 4) + 1) * dp.maxResp / 4;
			
			dp.push("user1", BiometricType.HEARTRATE, hr1);
    		dp.push("user1", BiometricType.RESPIRATION, rr1);
    		
    		//Thread.sleep(interval);
    		
    		hr2 = (float) ((ro + 3) % 4 + 1) * dp.maxHR / 4;
			rr2 = (float) ((ro + 1) % 4 + 1) * dp.maxResp / 4;
			
    		dp.push("user2", BiometricType.HEARTRATE, hr2);
    		dp.push("user2", BiometricType.RESPIRATION, rr2);
    		
    		//Thread.sleep(interval);
    		
    		hr3 = (float) ((ro + 2) % 4 + 1) * dp.maxHR / 4;
			rr3 = (float) ((ro + 2) % 4 + 1) * dp.maxResp / 4;
			
    		dp.push("user3", BiometricType.HEARTRATE, hr3);
    		dp.push("user3", BiometricType.RESPIRATION, rr3);
    		
			//Thread.sleep(interval);
			
			hr4 = (float) ((ro + 1) % 4 + 1) * dp.maxHR / 4;
			rr4 = (float) ((ro + 3) % 4 + 1) * dp.maxResp / 4;
			
    		dp.push("user4", BiometricType.HEARTRATE, hr4);
    		dp.push("user4", BiometricType.RESPIRATION, rr4);
    		
    		ro++;
			Thread.sleep(interval);
		}
	}
}
