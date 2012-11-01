package com.vitaltech.bioink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergedUser {
	//average biological data from the merged users
	//public String id;
	public float heartrate = 0;
	public float respiration = 0;
	public float hrv = 0;
	//public boolean hrv_active = false;
	
	public List<String> members;
	
	public MergedUser(String u1, String u2){
		members = Collections.synchronizedList(new ArrayList<String>());
		
		members.add(u1);
		members.add(u2);
	}
}
