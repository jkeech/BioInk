package com.vitaltech.bioink;

import java.util.Set;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;


public class BluetoothManager {
    /** Called when the activity is first created. */
	
	BluetoothAdapter adapter = null;
	Set<BluetoothDevice> BHDevices = null;
	BluetoothDevice BHdevice = null;
	//DataProcessing dataProcessing;
	BTClient _bt;
	ZephyrProtocol _protocol;
	NewConnectedListener _NConnListener;
	private final int HEART_RATE = 0x100;
	private final int RESPIRATION_RATE = 0x101;
	private final int SKIN_TEMPERATURE = 0x102;
	private final int POSTURE = 0x103;
	private final int PEAK_ACCLERATION = 0x104;
	
	public BluetoothManager(BluetoothAdapter _adapter /*, DataProcessing _dataProcessing*/){
		//Set our BT adapter object
		adapter = _adapter;
		
		//Get our pairedDevices
		Set<BluetoothDevice> AllDevices = adapter.getBondedDevices();
		for(BluetoothDevice device : AllDevices){
			if(device.getName().startsWith("BH")){
				BHDevices.add(device);
				//Quick hack for single device
				BHdevice = device;
				
			}
		}
		
		_bt = new BTClient(adapter, BHdevice.getAddress());
		_NConnListener = new NewConnectedListener(msgHandler,msgHandler);
		_bt.addConnectedEventListener(_NConnListener);
		if(_bt.IsConnected()){
			_bt.start();
		}
		
	}
	
	
        	
    final Handler msgHandler = new Handler(){
    	public void handleMessage(Message msg)
    	{
    		switch (msg.what)
    		{
    		case HEART_RATE:
    			String HeartRatetext = msg.getData().getString("HeartRate");
    			//dataProcessing.push
    			//public void push(String userID, DataType dtype, float value){
    			
    		break;
    		
    		case RESPIRATION_RATE:
    			String RespirationRatetext = msg.getData().getString("RespirationRate");    		
    		break;
    		
    		case SKIN_TEMPERATURE:
    			String SkinTemperaturetext = msg.getData().getString("SkinTemperature");
    		break;
    		
    		case POSTURE:
    			String PostureText = msg.getData().getString("Posture");
    		break;
    		
    		case PEAK_ACCLERATION:
    			String PeakAccText = msg.getData().getString("PeakAcceleration");
    		break;	
    		
    		
    		}
    	}

    };
    
}
