package com.vitaltech.bioink;

import zephyr.android.BioHarnessBT.BTClient;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public class Bioharness {
	private BluetoothDevice BtDevice;
	private BTClient _bt;
	private NewConnectedListener NConnListener;
	private Handler msgHandler;
	
	public Bioharness(BluetoothAdapter _adapter, BluetoothDevice _BtDevice, Handler _msgHandler){
		BtDevice = _BtDevice;
		msgHandler = _msgHandler;
		
		_bt = new BTClient(_adapter, BtDevice.getAddress());
		NConnListener = new NewConnectedListener(msgHandler,msgHandler);
		_bt.addConnectedEventListener(NConnListener);
	}

}
