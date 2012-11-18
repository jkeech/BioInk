package com.vitaltech.bioink;

import java.util.Random;

@SuppressWarnings("static-access")
public class DataSimulatorDP {
	private DataProcess dp;
	
	public DataSimulatorDP(DataProcess dp){
		this.dp = dp;
	}
	
	public void run(){
    	try {
    		//rangeOverview(500, 40, true);
    		//randomRange(1000);
    		randomRangeFast(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	public void randomRangeFast(int interval) throws InterruptedException{
		float hr1 = 100; float re1 = 15; float rr1 = 600;
		float hr2 = 100; float re2 = 15;	float rr2 = 600;
		float hr3 = 100;	float re3 = 15;	float rr3 = 600;
		float hr4 = 100;	float re4 = 15;	float rr4 = 600;
		float count = 0;
		float sign = 1;
		float sec_interval = 250 / interval;
		
		while(true){
			Thread.sleep(interval);
			generateUserF("user1", hr1, re1, sign * rr1);
			generateUserF("userdos", hr2, re2, sign * rr2);
			generateUserF("userthree", hr3, re3, sign * rr3);
			generateUserF("user4", hr4, re4, sign * rr4);
			
			count++;
			
			if(count > sec_interval){
				sign = -1 * sign;
				count = 0;
				rr1 = rr1 + randomFloatAdd4(2, 5, 20, 60);
				rr1 = Math.max(Math.min(rr1, 800), 400);
				rr2 = rr2 + randomFloatAdd4(2, 5, 20, 60);
				rr2 = Math.max(Math.min(rr2, 800), 400);
				rr3 = rr3 + randomFloatAdd4(2, 5, 20, 60);
				rr3 = Math.max(Math.min(rr3, 800), 400);
				rr4 = rr4 + randomFloatAdd4(2, 5, 20, 60);
				rr4 = Math.max(Math.min(rr4, 800), 400);
			}
			hr1 = hr1 + randomFloatAdd(2,6);
			hr1 = Math.max(Math.min(hr1, 180), 40);
			re1 = re1 + randomFloatAdd(1,3);
			re1 = Math.max(Math.min(re1, 40), 5);
			
			hr2 = hr2 + randomFloatAdd(1,4);
			hr2 = Math.max(Math.min(hr2, 160), 60);
			re2 = re2 + randomFloatAdd(1,2);
			re2 = Math.max(Math.min(re2, 35), 10);
			
			hr3 = hr3 + randomFloatAdd(2,6);
			hr3 = Math.max(Math.min(hr3, 180), 40);
			re3 = re3 + randomFloatAdd(1,3);
			re3 = Math.max(Math.min(re3, 40), 5);
			
			hr4 = hr4 + randomFloatAdd(2,7);
			hr4 = Math.max(Math.min(hr4, 190), 30);
			re4 = re4 + randomFloatAdd(1,3);
			re4 = Math.max(Math.min(re4, 40), 0);
		}
	}
	
	public float randomFloatAdd(float a, float b){
		Random r = new Random();
		float rando = r.nextFloat();
		float addition = 0;
		
		if(rando < 0.33f)
			addition = a;
		else if(rando < 0.66f)
			addition = -a;
		else if(rando < 0.825f)
			addition = b;
		else
			addition = -b;
		
		return addition;
	}
	
	public float randomFloatAdd4(float a, float b, float c, float d){
		Random r = new Random();
		float rando = r.nextFloat();
		float addition = 0;
		
		if(rando < 0.25f)
			addition = a;
		else if(rando < 0.5f)
			addition = -a;
		else if(rando < 0.625f)
			addition = b;
		else if(rando < 0.75f)
			addition = -b;
		else if(rando < 0.8125f)
			addition = c;
		else if(rando < 0.875f)
			addition = -c;
		else if(rando < 0.9375f)
			addition = d;
		else
			addition = -d;
		
		return addition;
	}
	
	//randomizes parameters over the entire range of values
	public void randomRange(int interval) throws InterruptedException{
		float rri1 = 600;
		float rri2 = 600;
		float rri3 = 600;
		float rri4 = 600;
		
		while(true){
			rri1 = generateRandomUser("user1", rri1);
    		Thread.sleep(interval);
    		
    		rri2 = generateRandomUser("user2", rri2);
			Thread.sleep(interval);
			
			rri2 = generateRandomUser("user3", rri3);
			Thread.sleep(interval);
			
			rri2 = generateRandomUser("user4", rri4);
			Thread.sleep(interval);
		}
	}
	
	public float generateRandomUser(String uid, float ddd){
		Random r = new Random();
		
		float old = ddd;
		float hr1 = r.nextFloat() * dp.MAX_HR;
		float rr1 = r.nextFloat() * dp.MAX_RESP;
		
		dp.push(uid, BiometricType.HEARTRATE, hr1);
		dp.push(uid, BiometricType.RESPIRATION, rr1);
		old = generateRR(old);
		dp.push(uid, BiometricType.RR, old);
		old = generateRR(old);
		dp.push(uid, BiometricType.RR, -old);
		old = generateRR(old);
		dp.push(uid, BiometricType.RR, old);
		old = generateRR(old);
		dp.push(uid, BiometricType.RR, -old);
		
		return old;
	}
	
	public void generateUserF(String uid, float hr, float re, float rr){
		dp.push(uid, BiometricType.HEARTRATE, hr);
		dp.push(uid, BiometricType.RESPIRATION, re);
		dp.push(uid, BiometricType.RR, rr);
	}
	
	public static float generateRR(float old){
		Random r = new Random();
		float rando = r.nextFloat();
		float neeu = 0;
		
		if(rando < 0.125f){
			neeu = 2f;
		}else if(rando < 0.25f){
			neeu = 4f;
		}else if(rando < 0.375f){
			neeu = -8f;
		}else if(rando < 0.50f){
			neeu = -4f;
		}else if(rando < 0.625f){
			neeu = -2f;
		}else if(rando < 0.75f){
			neeu = 8f;
		}else if(rando < 0.825f){
			neeu = -64f;
		}else if(rando < 0.8125f){
			neeu = -32f;
		}else if(rando < 0.8375f){
			neeu = 32f;
		}else{
			neeu = 64f;
		}
		neeu = old + neeu;
		neeu = Math.max(Math.min(neeu, 800), 400);
		
		return neeu;
	}
	
	public void rangeOverview(int interval, int steps, boolean axis) throws InterruptedException{
		float hr1 = 0;
		float rr1 = 0;
		float hr2 = 0;
		float rr2 = 0;
		float hr3 = 0;
		float rr3 = 0;
		float hr4 = 0;
		float rr4 = 0;
		float hv1 = 0;
		float hv2 = 0;
		float hv3 = 0;
		float hv4 = 0;
		
		Thread.sleep(1000);
		
		while(true){
			for(int i = 0; i <= steps; i++){
				hr1 = i * dp.MAX_HR / steps;
				hr4 = hr3 = hr2 = hr1;
				rr1 = 0 * dp.MAX_RESP / 3;
				rr2 = 1 * dp.MAX_RESP / 3;
				rr3 = 2 * dp.MAX_RESP / 3;
				rr4 = 3 * dp.MAX_RESP / 3;
				hv1 = dp.MAX_HRV / 2;
				hv4 = hv3 = hv2 = hv1;
				
				dp.push("user1", BiometricType.HEARTRATE, hr1);
	    		dp.push("user1", BiometricType.RESPIRATION, rr1);
	    		dp.push("user2", BiometricType.HEARTRATE, hr2);
	    		dp.push("user2", BiometricType.RESPIRATION, rr2);
	    		dp.push("user3", BiometricType.HEARTRATE, hr3);
	    		dp.push("user3", BiometricType.RESPIRATION, rr3);
	    		dp.push("user4", BiometricType.HEARTRATE, hr4);
	    		dp.push("user4", BiometricType.RESPIRATION, rr4);
	    		if(axis){
		    		dp.push("user1", BiometricType.HRV, hv1);
		    		dp.push("user2", BiometricType.HRV, hv2);
		    		dp.push("user3", BiometricType.HRV, hv3);
		    		dp.push("user4", BiometricType.HRV, hv4);
	    		}
	    		
	    		Thread.sleep(interval);
			}
			
			Thread.sleep(2 * interval);
			
			for(int i = 0; i <= steps; i++){
				hr1 = 0 * dp.MAX_HR / 3;
				hr2 = 1 * dp.MAX_HR / 3;
				hr3 = 2 * dp.MAX_HR / 3;
				hr4 = 3 * dp.MAX_HR / 3;
				rr1 = i * dp.MAX_RESP / steps;
				rr4 = rr3 = rr2 = rr1;
				hv1 = dp.MAX_HRV / 2;
				hv4 = hv3 = hv2 = hv1;
				
				dp.push("user1", BiometricType.HEARTRATE, hr1);
	    		dp.push("user1", BiometricType.RESPIRATION, rr1);
	    		dp.push("user2", BiometricType.HEARTRATE, hr2);
	    		dp.push("user2", BiometricType.RESPIRATION, rr2);
	    		dp.push("user3", BiometricType.HEARTRATE, hr3);
	    		dp.push("user3", BiometricType.RESPIRATION, rr3);
	    		dp.push("user4", BiometricType.HEARTRATE, hr4);
	    		dp.push("user4", BiometricType.RESPIRATION, rr4);
	    		if(axis){
		    		dp.push("user1", BiometricType.HRV, hv1);
		    		dp.push("user2", BiometricType.HRV, hv2);
		    		dp.push("user3", BiometricType.HRV, hv3);
		    		dp.push("user4", BiometricType.HRV, hv4);
	    		}
	    		
	    		Thread.sleep(interval);
			}
			
			Thread.sleep(2 * interval);
			
			for(int i = 0; i <= steps; i++){
				hv1 = i * dp.MAX_HR / steps;
				hv4 = hv3 = hv2 = hv1;
				rr1 = 0 * dp.MAX_RESP / 3;
				rr2 = 1 * dp.MAX_RESP / 3;
				rr3 = 2 * dp.MAX_RESP / 3;
				rr4 = 3 * dp.MAX_RESP / 3;
				hr1 = dp.MAX_HRV / 2;
				hr4 = hr3 = hr2 = hr1;
				
				dp.push("user1", BiometricType.HEARTRATE, hr1);
	    		dp.push("user1", BiometricType.RESPIRATION, rr1);
	    		dp.push("user2", BiometricType.HEARTRATE, hr2);
	    		dp.push("user2", BiometricType.RESPIRATION, rr2);
	    		dp.push("user3", BiometricType.HEARTRATE, hr3);
	    		dp.push("user3", BiometricType.RESPIRATION, rr3);
	    		dp.push("user4", BiometricType.HEARTRATE, hr4);
	    		dp.push("user4", BiometricType.RESPIRATION, rr4);
	    		if(axis){
		    		dp.push("user1", BiometricType.HRV, hv1);
		    		dp.push("user2", BiometricType.HRV, hv2);
		    		dp.push("user3", BiometricType.HRV, hv3);
		    		dp.push("user4", BiometricType.HRV, hv4);
	    		}
	    		
	    		Thread.sleep(interval);
			}
		}
	}
}
