package com.vitaltech.bioink;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Discovery {
	private static final String TAG=Discovery.class.getSimpleName();

	// Get debug setting from MainActivity
	public static final Boolean DEBUG=MainActivity.DEBUG;
	
	private BluetoothAdapter btAdapter;
	private Context context;

	public Discovery(Context _context, BluetoothAdapter _btAdapter) {
		if(DEBUG) Log.d(TAG,"Bluetooth discovery");

		context=_context;
		findDevices(btAdapter);
	}
	
	public void findDevices(BluetoothAdapter _btAdapter){
		
		btAdapter=BluetoothAdapter.getDefaultAdapter();
		if(btAdapter==null){
			Toast.makeText(context.getApplicationContext(),"Bluetooth not available on this device",Toast.LENGTH_LONG).show();
			Log.e(TAG, "Bluetooth not available on this device");
			return;
		}

        // If BT is not on, request that it be enabled.
        if (!btAdapter.isEnabled()) {
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			Log.d(TAG, "start bluetooth intent");
            ((Activity)context).startActivityForResult(enableIntent, 1); // 3==REQUEST_ENABLE_BT
        }
		
		Log.d(TAG, "bluetooth adapter: "+btAdapter.getName()+", state: "+btAdapter.getState()+", scanmode: "+btAdapter.getScanMode());

		Set<BluetoothDevice> pairedDevices=btAdapter.getBondedDevices();
		Log.d(TAG, "bluetooth set size: "+pairedDevices.size());

	}

}
