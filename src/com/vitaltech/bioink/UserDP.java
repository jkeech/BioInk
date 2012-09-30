package com.vitaltech.bioink;

public class UserDP {
	//User identifying data
	public int id;
	
	//User Biometric data
	public float heartrate = 0;
	public float respiration = 0;
	public float temp = 0;
	public float conductivity = 0;
	
	public UserDP(int userID){
		id = userID;
	}
}
