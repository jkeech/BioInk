package com.vitaltech.bioink;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// user interface

public class MainActivity extends Activity {
	private static final String TAG=MainActivity.class.getSimpleName();

	public static final Boolean DEBUG=true;

	private Button vizButton;
	private Discovery discovery;

	// **** Start Lifecycle ****
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUG) Log.d(TAG, "__onCreate()__");

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

		BluetoothAdapter btAdapter=BluetoothAdapter.getDefaultAdapter();
		if(btAdapter==null){
			Toast.makeText(getApplicationContext(),"Bluetooth not available on this device",Toast.LENGTH_LONG).show();
			Log.e(TAG, "Bluetooth not available on this device");
			finish();
		}

        discovery=new Discovery(getApplicationContext(), getParent(), btAdapter);
		
		this.vizButton=(Button)this.findViewById(R.id.vizButton);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View v) {
			        if(DEBUG) Log.d(TAG, "Viz Button pressed");
			        // TODO Start viz engine here.
				}
			}
		);
    }

    @Override
    public void onStart() {
    	super.onStart();
    	if(DEBUG) Log.d(TAG,"__onStart()__");
    }

    @Override
    public void onRestart() {
    	super.onRestart();
    	if(DEBUG) Log.d(TAG,"__onRestart()__");
    }

    @Override
    protected void onResume() {
    	super.onResume();
        if(DEBUG) Log.d(TAG, "__onResume()__");
    	// resume screen state
    	// resume bluetooth traffic
    	// resume data analysis
    }

    // **** Activity is running at this point ****
    
    @Override
    public void onPause(){
    	super.onPause();
        if(DEBUG) Log.d(TAG, "__onPause()__");
    	// save screen state
    	// pause bluetooth traffic
    	// pause data analysis
    }
    
    public void onStop() {
    	super.onStop();
    	if(DEBUG) Log.d(TAG, "__onStop()__");
    }

    public void onDestroy() {
    	super.onStop();
    	if(DEBUG) Log.d(TAG, "__onDestroy()__");
    }
    // **** End Lifecycle ****    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void changeRadioStatus(Boolean power){
    	String radio="off.";
    	if(power)
    		radio="on.";
    	((TextView)this.findViewById(R.id.radioTextView)).setText("Radio is "+radio);
    }

    public void changePairedStatus(Integer paired){
    	((TextView)this.findViewById(R.id.pairedTextView)).setText("Devices paired: "+paired.toString());
    }

    public void changeAudibleStatus(Integer audible){
    	((TextView)this.findViewById(R.id.audibleTextView)).setText("Devices audible: "+audible.toString());
    }
}
