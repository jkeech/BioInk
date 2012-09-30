package com.vitaltech.bioink;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class DataProcess {
	//List of current users
	public List<UserDP> users;
	
	public DataProcess(){
		users = New ArrayList<UserDP>;
	}
	
	//Add user to the user list
	public void addUser(int userID){
		users.add(New UserDP(userID));	
	}
	
	//Push user data into data processing module, user is specified by its ID
	public void push(int userID, DataType dtype, float value){
		Iterator<UserDP> it = users.iterator();
		
		while(it.hasNext()){
			if(it.next().id == userID){
				switch(dtype){
				case HEARTRATE: 
					it.next().heartrate = value; 
					break;
				case RESPIRATION: 
					it.next().respiration = value;
					break;
				case TEMP: 
					it.next().temp = value; 
					break;
				case CONDUCTIVITY: 
					it.next().conductivity = value;
					break;
				}
			}
		}
	}
	
	public UserDP getUser(int userID){
		Iterator<UserDP> it = users.iterator();
		
		while(it.hasNext()){
			if(it.next().id == userID){
				return it.next();
			}
		}
		return null;
	}
}
