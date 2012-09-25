package com.vitaltech.bioink;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

// user interface

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
}
