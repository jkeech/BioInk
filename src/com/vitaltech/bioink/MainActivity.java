package com.vitaltech.bioink;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import rajawali.RajawaliActivity;

// user interface with master branch

public class MainActivity extends RajawaliActivity {
	private static final String TAG=MainActivity.class.getSimpleName();

	public static final Boolean DEBUG=true;

	private Button vizButton;
	private Discovery discovery;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;
	private BluetoothAdapter btAdapter;
	
	private Scene scene;

	// **** Start Lifecycle ****
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        if(DEBUG) Log.d(TAG, "__onCreate()__");
        
        // START VIZ SCENE
        scene = new Scene(this,1000);
		scene.initScene();
		scene.setSurfaceView(mSurfaceView);
		super.setRenderer(scene);
		// END VIZ SCENE

		btAdapter=BluetoothAdapter.getDefaultAdapter();
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

		discovery=new Discovery(this, btAdapter);
		
		this.vizButton=(Button)this.findViewById(R.id.vizButton);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
			        if(DEBUG) Log.d(TAG, "Viz Button pressed");
			        if(vizButton.getText()=="Enable Bluetooth"){
			            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
			        }else{
			            new Thread(new Runnable() { 
			                public void run(){
			                	if(DEBUG) Log.d(TAG,"start Bluetooth thread");
					            // TODO Start bluetooth thread here.
			                }
			            }).start();
			            new Thread(new Runnable() { 
			                public void run(){
			                	if(DEBUG) Log.d(TAG,"start data thread");
						        // TODO Start data processing thread here.
			                }
			            }).start();
			            if (DEBUG) Log.d(TAG,"start viz");
			            setContentView(mLayout);
			            
			            // start data feeding thread for testing
			            new Thread(new Runnable() { public void run() { generateData(); }}).start();
			            
			        }
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
    public void onStart() { // Make application visible
    	super.onStart();
    	if(DEBUG) Log.d(TAG,"__onStart()__");
    }

    @Override
    protected void onResume() { // Activity was partially visible
        registerReceiver(broadcastReceiver, intentFilter);
    	super.onResume();
        if(DEBUG) Log.d(TAG, "__onResume()__");
        if(btAdapter.isEnabled()){
			changeRadioStatus("on");
			vizButton.setText("Start Visualization");
        }else{
			changeRadioStatus("off");
			vizButton.setText("Enable Bluetooth");
		}
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

    public void onDestroy() { // Activity was hidden but is now being stopped altogether
    	super.onStop();
    	if(DEBUG) Log.d(TAG, "__onDestroy()__");
    }
    // **** End Lifecycle ****    

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, (android.view.Menu) menu);
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

    	
    public void generateData(){
    	try {
    		scene.update("user1", DataType.HEARTRATE, 50);
        	scene.update("user1", DataType.TEMP, 97);
			Thread.sleep(6000);
			scene.update("user1", DataType.TEMP, 105);
	    	scene.update("user1", DataType.HEARTRATE,120);
	    	Thread.sleep(4000);
	    	scene.update("user1", DataType.HEARTRATE, 0);
	    	Thread.sleep(1000);
	    	scene.update("user1", DataType.TEMP, 95);
	    	//Thread.sleep(4000);
	    	//scene.update("user2", DataType.HEARTRATE, 70);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
