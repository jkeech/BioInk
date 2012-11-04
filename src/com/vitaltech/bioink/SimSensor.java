package com.vitaltech.bioink;

import java.util.Random;

public class SimSensor {
	public float RtoR;
	public float HeartRate;
	public float RespRate;
	public Random Randomizer;
	
	public SimSensor(){
		Randomizer = new Random();
		
		RtoR = 600;
		HeartRate = 70;
		RespRate = 15;
	}
	
	public float getRtoR(){
		RtoR = DataSimulatorDP.generateRR(RtoR);
		return RtoR;
	}
	public float getHeartRate(){
		HeartRate += (5 * Randomizer.nextGaussian());
		return HeartRate;
	}
	public float getRespRate(){
		RespRate += (5 * Randomizer.nextGaussian());
		return RespRate;
	}
}
