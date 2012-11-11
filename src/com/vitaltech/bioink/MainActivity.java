package com.vitaltech.bioink;

import com.vitaltech.bioink.RangeSeekBar.OnRangeSeekBarChangeListener;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG=MainActivity.class.getSimpleName();
	public static final Boolean DEBUG=true;

	private Button vizButton;
	private Button settingsButton;
	private Discovery discovery;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter intentFilter;
	private BluetoothAdapter btAdapter;
	private Boolean vizActive;// = null;
	
	private boolean settingsVisible = false;
	
	// Settings
	private float minHR = DataProcess.MIN_HR;
	private float maxHR = DataProcess.MAX_HR;
	private float minResp = DataProcess.MIN_RESP;
	private float maxResp = DataProcess.MAX_RESP;
	private BiometricType colorType = BiometricType.RESPIRATION;
	private BiometricType energyType = BiometricType.HEARTRATE;
	private LinearLayout settingsLayout;
	private LinearLayout.LayoutParams settingsParams;

	// **** Start Lifecycle ****
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "test message");
		setContentView(R.layout.activity_main);
		if(DEBUG) Log.d(TAG, "__onCreate()__");

		if(vizActive == null){
			vizActive = false;
		}

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

		// Catch Bluetooth radio events
		intentFilter=new IntentFilter();
		intentFilter.addAction(android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED);
		broadcastReceiver=new BroadcastReceiver() {
			@Override
			public void onReceive(Context contxt, Intent intent) {
				if(DEBUG) Log.d(TAG, "broadcast received");
				int code = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
				switch(code){
				case BluetoothAdapter.STATE_ON:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => on");
					changeRadioStatus("on");
					vizButton.setText("Start Visualization");
//					BTMan.bt_enabled();
					break;
				case BluetoothAdapter.STATE_OFF:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => off");
					changeRadioStatus("off");
					vizButton.setText("Enable Bluetooth");
//					BTMan.bt_disabled();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
				case BluetoothAdapter.STATE_TURNING_ON:
					if(DEBUG) Log.d(TAG, "bluetooth broadcast receiver => changing");
					changeRadioStatus("changing");
					break;
				default:
					Log.e(TAG, "bluetooth broadcast receiver => undefined: " + code);
					break;
				}
			}
		};
		connectButton();
		setupSettingsMenu();
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
							if (DEBUG) Log.d(TAG,"start viz");
							Intent myIntent = new Intent(v.getContext(), RajActivity.class);
							
							// transfer settings to RajActivity
							myIntent.putExtra("minHR", minHR);
							myIntent.putExtra("maxHR", maxHR);
							myIntent.putExtra("minResp", minResp);
							myIntent.putExtra("maxResp", maxResp);
							myIntent.putExtra("colorType", colorType);
							myIntent.putExtra("energyType", energyType);
							
							startActivityForResult(myIntent, 0);
							vizActive = true;
						}
					}
				}
				);
		
		this.settingsButton=(Button)this.findViewById(R.id.settingsButton);
		this.settingsButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (DEBUG) Log.d(TAG,"start settings");
						toggleMenu();
						/*
						Intent myIntent = new Intent(v.getContext(), SettingsMenu.class);
						startActivityForResult(myIntent, 0);
						*/
					}
				}
				);
		
	}
	
	private void setupSettingsMenu(){	         
	        // Grabbing the Application context
	        final Context context = getApplication();
	         
	        // Creating a new LinearLayout
	        settingsLayout = new LinearLayout(this);
	         
	        // Setting the orientation to vertical
	        settingsLayout.setOrientation(LinearLayout.VERTICAL);
	         
	        // Defining the LinearLayout layout parameters to fill the parent.
	        settingsParams = new LinearLayout.LayoutParams(
	            LinearLayout.LayoutParams.FILL_PARENT,
	            LinearLayout.LayoutParams.FILL_PARENT);
	        
	        // create RangeSeekBar as Float for Heartrate
	        RangeSeekBar<Float> seekBarHR = new RangeSeekBar<Float>(DataProcess.MIN_HR, DataProcess.MAX_HR, context);
	        seekBarHR.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
	                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
	                        minHR = minValue;
	                        maxHR = maxValue;
	                        if(DEBUG) Log.d("menu","minHR: "+minHR+", maxHR: "+maxHR);
	                }
	        });
	        
	        // create RangeSeekBar as Float for Respiration
	        RangeSeekBar<Float> seekBarResp = new RangeSeekBar<Float>(DataProcess.MIN_RESP, DataProcess.MAX_RESP, context);
	        seekBarResp.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Float>() {
	                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
	                        minResp = minValue;
	                        maxResp = maxValue;
	                        if(DEBUG) Log.d("menu","minResp: "+minResp+", maxResp: "+maxResp);
	                }
	        });
	        
	        Button saveBtn = new Button(this);
	        saveBtn.setText(R.string.save_button);
	        saveBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	        saveBtn.setOnClickListener(new Button.OnClickListener() {
	            public void onClick(View v) {
	              try {
	                toggleMenu();
	              } catch (Exception e) {	  
	              }
	            }
	          });
	        
	        final String[] biometricTypes = { "Heartrate", "Respiration" };
	        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,biometricTypes);
	        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	        
	        
	        Spinner colorSpinner = new Spinner(this);
	        colorSpinner.setAdapter(aa);
	        colorSpinner.setSelection(1); // start this one with Respiration selected instead of heartrate
	        colorSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	        	public void onItemSelected(AdapterView<?> parent, View v, int position,
	        			long id) {
	        		if(biometricTypes[position].equals("Heartrate")){
	        			colorType = BiometricType.HEARTRATE;
	        		}
	        		if(biometricTypes[position].equals("Respiration")){
	        			colorType = BiometricType.RESPIRATION;
	        		}
	        		if(DEBUG) Log.d("menu","colorType: "+colorType);
	        	}

	        	public void onNothingSelected(AdapterView<?> parent) {}
	        });
	        
	        Spinner energySpinner = new Spinner(this);
	        energySpinner.setAdapter(aa);
	        energySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	        	public void onItemSelected(AdapterView<?> parent, View v, int position,
	        			long id) {
	        		if(biometricTypes[position].equals("Heartrate")){
	        			energyType = BiometricType.HEARTRATE;
	        		}
	        		if(biometricTypes[position].equals("Respiration")){
	        			energyType = BiometricType.RESPIRATION;
	        		}
	        		if(DEBUG) Log.d("menu","energyType: "+energyType);
	        	}

	        	public void onNothingSelected(AdapterView<?> parent) {}
	        });

	        settingsLayout.addView(seekBarHR);
	        settingsLayout.addView(seekBarResp);
	        settingsLayout.addView(colorSpinner);
	        settingsLayout.addView(energySpinner);
	        settingsLayout.addView(saveBtn);
	}
	
	private void toggleMenu(){
		if(settingsVisible){
			if(DEBUG) Log.d("menu","switching to Main Menu");
			setContentView(R.layout.activity_main);
			connectButton();
			settingsVisible = false;
		} else {
			if(DEBUG) Log.d("menu","switching to Settings Menu");
			setContentView(settingsLayout,settingsParams);
			settingsVisible = true;
		}
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
		}
	}

	// **** Activity is running at this point ****

	@Override
	public void onPause(){ // Activity was visible but now is now partially visible
		if(DEBUG) Log.d(TAG, "__onPause()__");
		unregisterReceiver(broadcastReceiver);
		super.onPause();
	}
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

