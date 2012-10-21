package com.vitaltech.bioink;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import rajawali.RajawaliActivity;

public class RajActivity extends RajawaliActivity {
	private static final String TAG=RajActivity.class.getSimpleName();
	public static final Boolean DEBUG = MainActivity.DEBUG;

	private Scene scene;
	private OnClickListener onBack;
	
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
		
		onBack = new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		};
    }
}
