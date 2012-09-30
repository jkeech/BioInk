package com.vitaltech.bioink;

/*
 * This class models a single user within the visualization
 */
public class User {
	// biological data retrieved from sensors
	public float heartrate = 0;
	public float respiration = 0;
	public float temp = 0;
	public float conductivity = 0;
	public float stress = 0; // 0 <= stress <= 1
	
	// each user also has a corresponding blob visualization
	public Blob ink = new Blob();	
}
