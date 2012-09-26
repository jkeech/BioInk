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

	public static final Boolean DEBUG=MainActivity.DEBUG;
	
	private BluetoothAdapter btAdapter;
	private Context context;
	private Activity activity;

	public Discovery(Context _context,Activity _activity,BluetoothAdapter _btAdapter) {
		if(DEBUG) Log.d(TAG,"Bluetooth discovery");

		context=_context;
		activity=_activity;
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
        // setupChat() will then be called during onActivityResult
        if (!btAdapter.isEnabled()) {
            Intent enableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if(enableIntent==null){
            	Log.e(TAG, "enable intent is null");
            	return;
            }
			Log.d(TAG, "start bluetooth intent");
            ((Activity)context).startActivityForResult(enableIntent, 1); // 3==REQUEST_ENABLE_BT
//            while(btAdapter.getState()!=BluetoothAdapter.STATE_ON || btAdapter.getState()!=BluetoothAdapter.STATE_TURNING_ON){
//    			Log.e(TAG, "turning on bluetooth <= "+btAdapter.getState());
//            	try {
//            		Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					Log.e(TAG, e.toString());
//				}
//            }
 //           activity=((Activity)context).getParent();
//            if(activity!=null){
//            	activity.startActivityForResult(enableIntent, 1);
//            }else{
//    			Toast.makeText(context.getApplicationContext(),"activity == null",Toast.LENGTH_LONG).show();
//            }
        }
		
		Log.d(TAG, "bluetooth adapter: "+btAdapter.getName()+", state: "+btAdapter.getState()+", scanmode: "+btAdapter.getScanMode());

		Set<BluetoothDevice> pairedDevices=btAdapter.getBondedDevices();
		Log.d(TAG, "bluetooth set size: "+pairedDevices.size());

	}

}
