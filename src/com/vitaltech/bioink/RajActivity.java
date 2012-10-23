package com.vitaltech.bioink;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import rajawali.RajawaliActivity;

@SuppressWarnings("deprecation")
public class RajActivity extends RajawaliActivity {
	private static final String TAG = RajActivity.class.getSimpleName();
	public static final Boolean DEBUG = MainActivity.DEBUG;

	private DataProcess dp;
	private Scene scene;
	private BluetoothManager BTMan;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUG) Log.d(TAG, "__onCreate()__");
        
        // INSTANTIATE VIZ SCENE
        scene = new Scene(this);
		scene.initScene();
		scene.setSurfaceView(mSurfaceView);
		super.setRenderer(scene);
		// END VIZ SCENE

		// START DATA PROCESSING 
		dp = new DataProcess(1000);
		dp.addScene(scene);
		// END DATA PROCESSING

		// INSTANTIATE BLUETOOTH
		if(BTMan == null){
			if(DEBUG) Log.d(TAG,"start Bluetooth DISABLED");
//			BTMan = new BluetoothManager(btAdapter, dp); // FIXME
		}else{
			if(DEBUG) Log.d(TAG,"Bluetooth already started");
		}
		// END BLUETOOTH
		// DISPLAY FPS
		if(DEBUG){
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView label = new TextView(this);
	        label.setTextSize(20);
	        ll.addView(label);
	        mLayout.addView(ll);
	        
	        FPSDisplay fps = new FPSDisplay(this,label);
	        scene.setFPSUpdateListener(fps);
		}
		// END FPS DISPLAY

		// start data feeding thread for testing
		new Thread(new Runnable() {
			public void run() { 
				DataSimulator ds = new DataSimulator(dp);
				ds.run();
			}
		}).start();// debug data

		setContentView(mLayout);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(DEBUG) Log.d(TAG, "keycode received " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(DEBUG) Log.d(TAG, "back keycode received, ending raj viz activity");
            finish();
            return true;
        }
        return false;
    }
}



