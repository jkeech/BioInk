package com.vitaltech.bioink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergedUser extends User {
	
	public List<String> members;
	
	public MergedUser(String u1, String u2){
		members = new ArrayList<String>();
		//Collections.synchronizedList(new ArrayList<String>());
		
		members.add(u1);
		members.add(u2);
	}
}
