package com.vitaltech.bioink;

import rajawali.RajawaliActivity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends RajawaliActivity {
	private Scene scene;
    
    @Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scene = new Scene(this,1000);
		scene.initScene();
		scene.setSurfaceView(mSurfaceView);
		super.setRenderer(scene);
		
		new Thread(new Runnable() { public void run() { generateData(); }}).start();
	}	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void generateData(){
    	scene.update("user1", DataType.HEARTRATE, 50);
    	scene.update("user1", DataType.TEMP, 97);
    	try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	scene.update("user1", DataType.TEMP, 105);
    	scene.update("user1", DataType.HEARTRATE,120);
    }
}
