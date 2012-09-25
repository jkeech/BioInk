package com.vitaltech.bioink;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// user interface

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button vizButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Main Activity created");
        setContentView(R.layout.activity_main);
        this.vizButton=(Button)this.findViewById(R.id.vizButton);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), "start viz pressed", Toast.LENGTH_SHORT).show();
			        new Discovery(getApplicationContext());
				}
			}
		);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	// save screen state
    	// pause bluetooth traffic
    	// pause data analysis
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// resume screen state
    	// resume bluetooth traffic
    	// resume data analysis
    }
    
    public void changeRadioStatus(Boolean power){
    	String radio="off.";
    	if(power)
    		radio="on.";
    	((TextView)this.findViewById(R.id.radioTextView)).setText("Radio is "+radio);
    }

    public void changeDevicesPaired(Integer paired){
    	((TextView)this.findViewById(R.id.pairedTextView)).setText("Devices paired: "+paired.toString());
    }

    public void changeDevicesAudible(Integer audible){
    	((TextView)this.findViewById(R.id.audibleTextView)).setText("Devices audible: "+audible.toString());
    }
}
