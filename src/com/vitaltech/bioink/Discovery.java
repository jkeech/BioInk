package com.vitaltech.bioink;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ConnectListenerImpl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Discovery {
	private static final String TAG = Discovery.class.getSimpleName();

	// Get debug setting from MainActivity
	public static final Boolean DEBUG = MainActivity.DEBUG;

	private BluetoothAdapter btAdapter;
	private Context context;
	private AtomicBoolean running;

	public Discovery(Context _context, BluetoothAdapter _btAdapter) {
		if (DEBUG) Log.d(TAG, "Bluetooth discovery instantiated");
		context = _context;
		btAdapter = _btAdapter;
		running = new AtomicBoolean(false);
	}

	public void findDevices() {
		if(running.getAndSet(true)){
			Log.w(TAG, "findDevices() already running");
			return;
		}
		if(Looper.myLooper() == Looper.getMainLooper()){
			Log.e(TAG, "findDevices() must be run in background");
			return;
		}
		if (btAdapter == null) {
			Toast.makeText(context.getApplicationContext(),
					"Bluetooth not available on this device", Toast.LENGTH_LONG)
					.show();
			Log.e(TAG, "findDevices() Bluetooth not available on this device");
			return;
		}

//		// If BT is not on, request that it be enabled.
//		if (! btAdapter.isEnabled()) {
//			Intent enableIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			if (DEBUG) Log.d(TAG, "findDevices() start bluetooth intent");
//			((Activity) context).startActivityForResult(enableIntent, 1);
//		}
		if (! btAdapter.isEnabled()) {
			if (DEBUG) Log.d(TAG, "findDevices() bluetooth is off");
			return;
		}
		if (DEBUG) Log.d(TAG, "findDevices() started");

		if (DEBUG) Log.d(TAG, "findDevices() bluetooth adapter: " + btAdapter.getName() + ", state: "
			+ btAdapter.getState() + ", scanmode: " + btAdapter.getScanMode());

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (DEBUG) Log.d(TAG, "findDevices() bluetooth set size: " + pairedDevices.size());
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (DEBUG) Log.d(TAG, "findDevices(): " + device.getName() + ", "
					+ device.getAddress() + ", " + device.getBondState());

		//		ConnectListenerImpl c = new ConnectListenerImpl(msgHandler, null);
//				Bioharness bh = new Bioharness(btAdapter, device, msgHandler);
				
				BTClient btclient = new BTClient(btAdapter, device.getAddress());

//				bt = new BTClient(_adapter, BtDevice.getAddress());
//				if(DEBUG) Log.d(TAG,"Connected? " + bt.IsConnected());
//				NConnListener = new NewConnectedListener(msgHandler,msgHandler);
//				bt.addConnectedEventListener(NConnListener);


				if(btclient == null){
					Log.e(TAG, device.getName() + " null connection 1");
					continue;
				}
				if(btclient.IsConnected()){
					Log.e(TAG, device.getName() + " connected 2");
					btclient.addConnectedEventListener(new NewConnectedListener(new Handler(Looper.getMainLooper()), new Handler(Looper.getMainLooper())));
					try{
						btclient.Close();
					} catch (NullPointerException npe){
						Log.e(TAG, "npe on btclient.close()");
					}
					continue;
				}else if(! btclient.IsConnected()){
					Log.e(TAG, device.getName() + " not connected 3");
					continue;
				}




//				private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				BluetoothSocket btSocket = null;
//BluetoothDevice remoteDevice = myAdapter.getRemoteDevice("00:00:00:00:00:00");
				BluetoothDevice remote = btAdapter.getRemoteDevice(device.getAddress());
				try {
//					btSocket = device.createRfcommSocketToServiceRecord(
					btSocket = remote.createRfcommSocketToServiceRecord(
							//UUID.fromString("BioInk Discovery")
							//UUID.randomUUID()
							MY_UUID
					);
				} catch (IOException e) {
					Log.e(TAG, "could not socket to " + device.getName());
				}
				btAdapter.cancelDiscovery();
				if( btSocket == null){
					Log.d(TAG, "null device " + device.getName());
				}else{
					Log.d(TAG, "good socket to device " + device.getName());
					try {
						Log.d(TAG, "try connect to socket to device " + device.getName());
						btSocket.connect();
						Log.d(TAG, "finished connect to socket to device " + device.getName());
					} catch (IOException e) {
//						e.printStackTrace();
						Log.e(TAG, "connect to socket failed " + device.getName());
//						continue;
					}
					try {
						Log.d(TAG, "try closing socket to device " + device.getName());
						btSocket.close();
						Log.d(TAG, "finished closing socket to device " + device.getName());
					} catch (IOException e) {
//						e.printStackTrace();
						Log.e(TAG, "close device failed " + device.getName());
//						continue;
					}
					Log.w(TAG, "connected and closed device " + device.getName());
				}
			}
		}

		if(running.getAndSet(false)){
			if(DEBUG) Log.d(TAG, "findDevices() finished clean");
		}else{
			Log.w(TAG, "findDevices() finished dirty");
		}
	}

	public void showDevices(){
		if(context == null){
			Log.e(TAG, "context is null");
			return;
		}
		Activity activity = (Activity) context;
		if(activity == null){
			Log.e(TAG, "cast to activity has failed");
			return;
		}
		try{
			if(btAdapter == null){
				Log.e(TAG, "No Bluetooth");
				((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio does not exist");
				((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.INVISIBLE);
				((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.INVISIBLE);
			}else if(btAdapter.isEnabled()){
				if(DEBUG) Log.d(TAG, "Bluetooth enabled");
				((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio is on");
				((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.VISIBLE);
				((TextView)activity.findViewById(R.id.pairedTextView)).setText("add \npaired\n here");
				((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.VISIBLE);
				((TextView)activity.findViewById(R.id.audibleTextView)).setText("add audible here");
			}else{
				if(DEBUG) Log.d(TAG, "Bluetooth disabled");
				((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio is off");
				((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.INVISIBLE);
				((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.INVISIBLE);
			}
		} catch (NullPointerException nullExc){
			Log.w(TAG, "null pointer exception: layout not set up?");
		} finally{
			if(DEBUG) Log.d(TAG, "finished showing devices");
		}
	}

	// Turn a Set of 8-bit Bytes into a List of 10-bit Longs
	public static List<Long> zephyParse(Set<Byte> samples) {
		List<Long> decoded = new ArrayList<Long>();
		Byte[] array = (Byte[])samples.toArray(new Byte[samples.size()]);

		int shifter = 1;
		for(int i=1; i<samples.size(); ++i){
			if(shifter != 5){
				long rightMask = ((long)255 << ((shifter-1) * 2)) & 255;
				long right = (((long)array[i-1]) & rightMask) >> ((shifter-1)*2);
				long leftMask = ((long)1 << (shifter * 2)) - 1;
				long left = ((((long)array[i]) & leftMask) << (8-((shifter-1)*2)));
				
				decoded.add(
					(left | right) & 1023
				);
			}

			shifter++;
			if(shifter > 5){
				shifter = 1;
			}
		}
		
		return decoded;
	}

}

