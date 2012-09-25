package com.vitaltech.bioink;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Discovery {
	private static final String TAG = Discovery.class.getSimpleName();
	private BluetoothAdapter btAdapter;
	private Context context;
	
	public static final int STATE_ON=12;
	public static final int STATE_TURNING_OFF=13;
	public static final int STATE_OFF=10;
	public static final int STATE_TURNING_ON=11;
	public static final int SCAN_MODE_NONE=20;
	public static final int SCAN_MODE_CONNECTABLE=21;
	public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE=23;

	public Discovery(Context _context) {
		context=_context;
		Toast.makeText(context.getApplicationContext(),"Bluetooth discovery",Toast.LENGTH_SHORT).show();
		btAdapter=BluetoothAdapter.getDefaultAdapter();
		Log.d(TAG, "bluetooth adapter: "+btAdapter.getName()+", "+btAdapter.getState()+", "+btAdapter.getScanMode());
		Set<BluetoothDevice> pairedDevices=btAdapter.getBondedDevices();
		Log.d(TAG, "bluetooth set size: "+pairedDevices.size());
		
	}

}
