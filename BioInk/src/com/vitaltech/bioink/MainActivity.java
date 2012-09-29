package com.vitaltech.bioink;

import rajawali.RajawaliActivity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends RajawaliActivity {
	private Scene scene;
    
    @Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scene = new Scene(this,1000);
		scene.setSurfaceView(mSurfaceView);
		super.setRenderer(scene);
	}	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
