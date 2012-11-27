package com.vitaltech.bioink;

import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import android.widget.TextView;
import rajawali.RajawaliActivity;

@SuppressWarnings("deprecation")
public class StatsDisplay {
	private TextView label;
	private RajawaliActivity activity;
	private DataProcess dp;
	private Timer statsTimer = new Timer();
	private StatsTask statsTask;
	
	public StatsDisplay(RajawaliActivity activity, TextView label, DataProcess dp){
		this.label = label;
		this.activity = activity;
		this.dp = dp;
		
		statsTask = new StatsTask();
		statsTimer.schedule(statsTask, 0, 1000); // run once a second
	}

	private void updateStats(final Map<String,User> users) {
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		    	SortedMap<String,User> sorted = new TreeMap<String,User>(users);
		    	String text = "";
		    	int i = 1;
		    	for (User user : sorted.values()){
		    		text += String.format("User %d: HR: %03.0f RESP: %02.0f\n",i,user.heartrate,user.respiration);
		    		i++;
		    	}
		    	label.setText(text);
		    }
		});
	}
	
	private class StatsTask extends TimerTask {
		public void run(){
			updateStats(dp.users);
		}
	}

}
