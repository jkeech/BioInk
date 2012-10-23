package com.vitaltech.bioink;

import android.widget.TextView;
import rajawali.RajawaliActivity;
import rajawali.util.FPSUpdateListener;

@SuppressWarnings("deprecation")
public class FPSDisplay implements FPSUpdateListener {
	private TextView label;
	private RajawaliActivity activity;
	
	public FPSDisplay(RajawaliActivity activity, TextView label){
		this.label = label;
		this.activity = activity;
	}

	public void onFPSUpdate(final double fps) {
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		    	label.setText("FPS: "+ (int)fps);
		    }
		});
	}

}
