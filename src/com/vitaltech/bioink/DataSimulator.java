package com.vitaltech.bioink;

import java.util.Random;

public class DataSimulator {
	private DataProcess dp;
	
	public DataSimulator(DataProcess dp){
		this.dp = dp;
	}
	
	public void run(){
    	try {
    		Random r = new Random();
    		
    		/*
    		dp.push("user1", BiometricType.HEARTRATE, dp.maxHR/2);
    		dp.push("user1", BiometricType.RESPIRATION, dp.maxResp/2);
    		Thread.sleep(1);
    		*/
    		
    		int NUM_USERS = 4;
    		
    		int cur = 0;
    		while(true){
    			float hr = r.nextFloat() * dp.maxHR;
    			float rr = r.nextFloat() * dp.maxResp;
    			
    			dp.push("user"+cur, BiometricType.HEARTRATE, hr);
        		dp.push("user"+cur, BiometricType.RESPIRATION, rr);
        		
        		if(++cur == NUM_USERS)
        			cur = 0;
        		Thread.sleep(100);
    		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
