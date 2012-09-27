package com.vitaltech.bioink;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;

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

		intentFilter=new IntentFilter();
		intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED);
		
		broadcastReceiver=new BroadcastReceiver() {
			@Override
			public void onReceive(Context contxt, Intent intent) {
				if(DEBUG) Log.d(TAG, "broadcast received");
				switch(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)){
				case BluetoothAdapter.STATE_ON:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => on");
					changeRadioStatus("on");
					vizButton.setText("Start Visualization");
					break;
				case BluetoothAdapter.STATE_OFF:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => off");
					changeRadioStatus("off");
					vizButton.setText("Enable Bluetooth");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
				case BluetoothAdapter.STATE_TURNING_ON:
				default:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => changing");
					changeRadioStatus("changing");
					break;
				}
			}
		};
		registerReceiver(broadcastReceiver, intentFilter);

		discovery=new Discovery(this, btAdapter);
		
		this.vizButton=(Button)this.findViewById(R.id.vizButton);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View v) {
			        if(DEBUG) Log.d(TAG, "Viz Button pressed");
			        if(vizButton.getText()=="Enable Bluetooth"){
			            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
			        }
			        // TODO Start viz engine here.
				}
			}
		);
    }

    @Override
    public void onRestart() { // Activity was stopped; step to onStart()
    	super.onRestart();
    	if(DEBUG) Log.d(TAG,"__onRestart()__");
    }

    @Override
    public void onStart() { // Make Activity visible
    	super.onStart();
    	if(DEBUG) Log.d(TAG,"__onStart()__");
    	// start bluetooth module
    }

    @Override
    protected void onResume() { // Activity was partially visible
        registerReceiver(broadcastReceiver, intentFilter);
    	super.onResume();
        if(DEBUG) Log.d(TAG, "__onResume()__");
    	// resume bluetooth traffic
    	// start data analysis
    	// start screen visualization
    }

    // **** Activity is running at this point ****
    
    @Override
    public void onPause(){ // Activity was visible but now is now partially visible
        unregisterReceiver(broadcastReceiver);
    	super.onPause();
        if(DEBUG) Log.d(TAG, "__onPause()__");
    	// pause bluetooth traffic
    	// stop data analysis
    	// stop screen visualization
    }
    
    public void onStop() { // Activity was partially visible but is now hidden
    	super.onStop();
    	if(DEBUG) Log.d(TAG, "__onStop()__");
    }

    public void onDestroy() { // Activity was hidden but is now being stopped alltogether
    	super.onStop();
    	if(DEBUG) Log.d(TAG, "__onDestroy()__");
    }
    // **** End Lifecycle ****    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void changeRadioStatus(String stat){
    	((TextView)this.findViewById(R.id.radioTextView)).setText("Radio is "+stat);
    }

    public void changePairedStatus(Integer paired){
    	((TextView)this.findViewById(R.id.pairedTextView)).setText("Devices paired: "+paired.toString());
    }

    public void changeAudibleStatus(Integer audible){
    	((TextView)this.findViewById(R.id.audibleTextView)).setText("Devices audible: "+audible.toString());
    }
}
