package com.vitaltech.bioink;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
        scene = new Scene(this,1000);
		scene.initScene();
		scene.setSurfaceView(mSurfaceView);
		super.setRenderer(scene);
		// END VIZ SCENE

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



