package com.vitaltech.bioink;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG=MainActivity.class.getSimpleName();
	public static final Boolean DEBUG=true;

	private Button vizButton;
	private Discovery discovery;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;
	private BluetoothAdapter btAdapter;
	private Boolean vizActive;
	
//	private kailean bluetooth		// FIXME
	private DataProcess dp;

	// **** Start Lifecycle ****
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "test message");
        setContentView(R.layout.activity_main);
        if(DEBUG) Log.d(TAG, "__onCreate()__");
        
		btAdapter=BluetoothAdapter.getDefaultAdapter();
		if(btAdapter==null){
			Toast.makeText(getApplicationContext(),"Bluetooth not available on this device",Toast.LENGTH_LONG).show();
			Log.e(TAG, "Bluetooth not available on this device");
			finish();
		}

		discovery=new Discovery(this, btAdapter);
        new Thread(new Runnable() { 
            public void run(){
            	if(DEBUG) Log.d(TAG,"start finding devices");
            	discovery.findDevices(btAdapter);
            }
        }).start();

		vizActive = false;

		// START DATA PROCESSING 
		dp = new DataProcess(1000);
		dp.addScene(scene);
		// END DATA PROCESSING

		// TODO INSTANTIATE BLUETOOTH
		// FIXME bluetooth(dataprocessing)
		// END BLUETOOTH

		// Catch Bluetooth radio events
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
		connectButton();
    }
    
    private void connectButton(){
		// Configure single Button system
		this.vizButton=(Button)this.findViewById(R.id.vizButton);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
			        if(DEBUG) Log.d(TAG, "Viz Button pressed");
			        if(vizButton.getText()=="Enable Bluetooth"){
			            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
			        }else{
			        	// bluetooth
			        	
			        	// data processing

			            if (DEBUG) Log.d(TAG,"start viz");
			            Intent myIntent = new Intent(v.getContext(), RajActivity.class);
			            startActivityForResult(myIntent, 0);
			            vizActive = true;

			            // start data feeding thread for testing
			            new Thread(new Runnable() {
			            	public void run() { 
		    	        		DataSimulator ds = new DataSimulator(dp);
		            			ds.run();
			            	}
			            }).start();// debug data
			        }
				}
			}
		);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(DEBUG) Log.d(TAG, "back keycode received");
            if(vizActive){
                if(DEBUG) Log.d(TAG, "turning off visualization");
                setContentView(R.layout.activity_main);
                vizActive = false;
                connectButton();
                return true;
            }else{
                if(DEBUG) Log.d(TAG, "ending app");
                finish();
            }
        }else if (keyCode == KeyEvent.KEYCODE_MENU){
                if(DEBUG) Log.d(TAG, "onCreateOptionsMenu()");
// FIXME              getMenuInflater().inflate(R.menu.activity_main, (android.view.Menu) menu);
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() { // Activity was partially visible
        if(DEBUG) Log.d(TAG, "__onResume()__");
        registerReceiver(broadcastReceiver, intentFilter);
    	super.onResume();
    	if(vizActive){
            if(DEBUG) Log.d(TAG, "return to visualization");
// FIXME            setContentView(mLayout);
        }else{
            if(DEBUG) Log.d(TAG, "return to menu");
            setContentView(R.layout.activity_main);
            connectButton();
            if(btAdapter.isEnabled()){
				changeRadioStatus("on");
				vizButton.setText("Start Visualization");
				changeAudibleStatus(0);
				changePairedStatus(0);
	        }else{
				changeRadioStatus("off");
				vizButton.setText("Enable Bluetooth");
				changeAudibleStatus(-1);
				changePairedStatus(-1);
			}
    	}
    }

    // **** Activity is running at this point ****
    
    @Override
    public void onPause(){ // Activity was visible but now is now partially visible
        if(DEBUG) Log.d(TAG, "__onPause()__");
        unregisterReceiver(broadcastReceiver);
    	super.onPause();
    	// pause bluetooth traffic
    	// stop data analysis
    	// stop screen visualization
    }
    
//    public void onDestroy() { // Activity was hidden but is now being stopped altogether
//    	if(DEBUG) Log.d(TAG, "__onDestroy()__");
//    	super.onStop();
//    }
    // **** End Lifecycle ****    

    private void changeRadioStatus(String stat){
    	if(vizActive){
    		Log.e(TAG, "Cannot update radio status while in visualization");
    	}else{
    		((TextView)this.findViewById(R.id.radioTextView)).setText("Radio is "+stat);
    	}
    }

    private void changePairedStatus(Integer paired){
    	if(vizActive){
    		Log.e(TAG, "Cannot update paired status while in visualization");
    	}else{
    		((TextView)this.findViewById(R.id.pairedTextView)).setText("Devices paired: "+paired.toString());
    	}
    }

    private void changeAudibleStatus(Integer audible){
    	if(vizActive){
    		Log.e(TAG, "Cannot update audible status while in visualization");
    	}else{
    		((TextView)this.findViewById(R.id.audibleTextView)).setText("Devices audible: "+audible.toString());
    	}
    }
}

