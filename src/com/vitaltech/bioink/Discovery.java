package com.vitaltech.bioink;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
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

	public Discovery(Context _context, BluetoothAdapter _btAdapter) {
		if (DEBUG) Log.d(TAG, "Bluetooth discovery");
		context = _context;
		btAdapter = _btAdapter;
		// findDevices(btAdapter);
	}

	public void findDevices(BluetoothAdapter _btAdapter) {
		if (DEBUG) Log.d(TAG, "find devices");
//		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			Toast.makeText(context.getApplicationContext(),
					"Bluetooth not available on this device", Toast.LENGTH_LONG)
					.show();
			Log.e(TAG, "Bluetooth not available on this device");
			return;
		}

		// If BT is not on, request that it be enabled.
		if (! btAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			if (DEBUG) Log.d(TAG, "start bluetooth intent");
			((Activity) context).startActivityForResult(enableIntent, 1);
		}
		if (! btAdapter.isEnabled()) {
			if (DEBUG) Log.d(TAG, "bluetooth intent did not turn on bluetooth");
			return;
		}

		if (DEBUG) Log.d(TAG, "bluetooth adapter: " + btAdapter.getName() + ", state: "
			+ btAdapter.getState() + ", scanmode: " + btAdapter.getScanMode());

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (DEBUG) Log.d(TAG, "bluetooth set size: " + pairedDevices.size());
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (DEBUG) Log.d(TAG, "device: " + device.getName() + ", "
					+ device.getAddress() + ", " + device.getBondState());
			}
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

	public void showDevices(){
		Activity activity = (Activity) context;
		if(btAdapter == null){
			Log.e(TAG, "No Bluetooth");
			((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio does not exist");
			((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.INVISIBLE);
			((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.INVISIBLE);
		}else if(btAdapter.isEnabled()){
			((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio is on");
			((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.VISIBLE);
			((TextView)activity.findViewById(R.id.pairedTextView)).setText("add paired here");
			((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.VISIBLE);
			((TextView)activity.findViewById(R.id.audibleTextView)).setText("add audible here");
		}else{
			Log.d(TAG, "Bluetooth disabled");
			((TextView)activity.findViewById(R.id.radioTextView)).setText("Radio is off");
			((TextView)activity.findViewById(R.id.pairedTextView)).setVisibility(View.INVISIBLE);
			((TextView)activity.findViewById(R.id.audibleTextView)).setVisibility(View.INVISIBLE);
		}
	}

}

