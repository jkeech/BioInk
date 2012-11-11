package com.vitaltech.bioink;

import com.vitaltech.bioink.RangeSeekBar;
import com.vitaltech.bioink.RangeSeekBar.OnRangeSeekBarChangeListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SettingsMenu extends Activity {
	private float minHR = DataProcess.MIN_HR;
	private float maxHR = DataProcess.MAX_HR;
	private float minResp = DataProcess.MIN_RESP;
	private float maxResp = DataProcess.MAX_RESP;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        // Grabbing the Application context
        final Context context = getApplication();
         
        // Creating a new LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
         
        // Setting the orientation to vertical
        linearLayout.setOrientation(LinearLayout.VERTICAL);
         
        // Defining the LinearLayout layout parameters to fill the parent.
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.FILL_PARENT);
        
        // create RangeSeekBar as Float for Heartrate
        RangeSeekBar<Float> seekBarHR = new RangeSeekBar<Float>(DataProcess.MIN_HR, DataProcess.MAX_HR, context);
        seekBarHR.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
                        minHR = minValue;
                        maxHR = maxValue;
                }
        });
        
        // create RangeSeekBar as Float for Respiration
        RangeSeekBar<Float> seekBarResp = new RangeSeekBar<Float>(DataProcess.MIN_RESP, DataProcess.MAX_RESP, context);
        seekBarResp.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
                        minResp = minValue;
                        maxResp = maxValue;
                }
        });

        linearLayout.addView(seekBarHR);
        linearLayout.addView(seekBarResp);         
         
        // Setting the LinearLayout as our content view
        setContentView(linearLayout, llp);
    }
}
