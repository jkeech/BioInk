package com.vitaltech.bioink;

import java.util.ArrayList;

import android.util.Log;

public class DataSimulatorPlusPlus{
	private static final String TAG = DataSimulatorPlus.class.getSimpleName();
	private static final Boolean DEBUG = MainActivity.DEBUG;
	
	private DataProcess dp;
	private ArrayList<SimSensor> Sensors;
	private int pauseTime = 1000;
	
	public DataSimulatorPlusPlus(DataProcess _dp, int numUsers) {
		dp = _dp;
		Sensors = new ArrayList<SimSensor>();
		
		for(int i = 0; i < numUsers; i++){
			Sensors.add(new SimSensor());
		}
	}
	
	public void run(){
		Boolean stop = false;
		int loop = 0;
		
		while (!stop) {
			if(DEBUG) Log.d(TAG, "simulator loop " + loop++);
			int i = 0;
			for (SimSensor Sensor : Sensors) {
				dp.push(String.valueOf(i), BiometricType.HEARTRATE,
						Sensor.getHeartRate());
				dp.push(String.valueOf(i), BiometricType.RESPIRATION,
						Sensor.getRespRate());
				dp.push(String.valueOf(i), BiometricType.RR, 
						Sensor.getRtoR());
				i++;
			}
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				stop = true;
				Log.e(TAG, "caught interrupt exception : " + e.toString());
			}
		}
	}
}
