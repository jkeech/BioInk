package com.vitaltech.bioink;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class DataProcess {
	//List of current users
	public List<UserDP> users;
	
	public DataProcess(){
		users = new ArrayList<UserDP>();
	}
	
	//Add user to the user list
	public void addUser(String userID){
		users.add(new UserDP(userID));	
	}
	
	//Push user data into data processing module, user is specified by its ID
	public void push(int userID, DataType dtype, float value){
		Iterator<UserDP> it = users.iterator();
		
		while(it.hasNext()){
			if(it.next().id.equals(userID)){
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
				default:
					break;
				}
			}
		}
	}
	
	public UserDP getUser(String userID){
		Iterator<UserDP> it = users.iterator();
		
		while(it.hasNext()){
			if(it.next().id.equals(userID)){
				return it.next();
			}
		}
		return null;
	}
	
	public float userDistance(String id_x, String id_y){
		float diff = 0;
		
		UserDP x = getUser(id_x);
		UserDP y = getUser(id_y);
		
		if(x.heartrate > y.heartrate){
			diff = x.heartrate - y.heartrate;
		}else{
			diff = y.heartrate - x.heartrate;
		}
		
		if(x.respiration > y.respiration){
			diff = diff + x.respiration - y.respiration;
		}else{
			diff = diff + y.respiration - x.respiration;
		}
		
		if(x.temp > y.temp){
			diff = diff + x.temp - y.temp;
		}else{
			diff = diff + y.temp - x.temp;
		}
		
		if(x.conductivity > y.conductivity){
			diff = diff + x.conductivity - y.conductivity;
		}else{
			diff = diff + y.conductivity - x.conductivity;
		}
		
		return diff;
	}
}
