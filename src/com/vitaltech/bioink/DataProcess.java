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
	public void push(String userID, DataType dtype, float value){
		Iterator<UserDP> it = users.iterator();
		
		while(it.hasNext()){
			UserDP user = it.next();
			if(user.id.equals(userID)){
				switch(dtype){
				case HEARTRATE: 
					user.heartrate = value; 
					break;
				case RESPIRATION: 
					user.respiration = value;
					break;
				case TEMP: 
					user.temp = value; 
					break;
				case CONDUCTIVITY: 
					user.conductivity = value;
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
			UserDP user = it.next();
			if(user.id.equals(userID)){
				return user;
			}
		}
		return null;
	}
	
	public float userDistance(String id_x, String id_y){
		float diff = 0;
		float total = 0;
		
		UserDP x = getUser(id_x);
		UserDP y = getUser(id_y);
		
		if(x.heartrate > y.heartrate){
			diff = x.heartrate - y.heartrate;
			total += diff;
		}else{
			diff = y.heartrate - x.heartrate;
			total += diff;
		}
		
		if(x.respiration > y.respiration){
			diff = x.respiration - y.respiration;
			total += diff;
		}else{
			diff = y.respiration - x.respiration;
			total += diff;
		}
		
		if(x.temp > y.temp){
			diff = x.temp - y.temp;
			total += diff;
		}else{
			diff = y.temp - x.temp;
			total += diff;
		}
		
		if(x.conductivity > y.conductivity){
			diff = x.conductivity - y.conductivity;
			total += diff;
		}else{
			diff = y.conductivity - x.conductivity;
			total += diff;
		}
		
		return total;
	}
}
