package com.vitaltech.bioink;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import rajawali.RajawaliActivity;

public class RajActivity extends RajawaliActivity {
	private static final String TAG=RajActivity.class.getSimpleName();
	public static final Boolean DEBUG = MainActivity.DEBUG;

	private Scene scene;
	
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
		new Thread(new Runnable() { 
            public void run(){
            	if(DEBUG) Log.d(TAG,"start data simulation");
            	new DataSimulator(scene).run();
            }
        }).start(); // data processing
        
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



