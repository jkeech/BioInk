package com.vitaltech.bioink;

/*
 * This class models a single user within the visualization
 */
public class User {
	// biological data retrieved from sensors
	public double heartrate = 0;
	public double respiration = 0;
	public double temp = 0;
	public double conductivity = 0;
	
	// each user also has a corresponding blob visualization
	public Blob ink;	
}
