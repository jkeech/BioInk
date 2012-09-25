package com.vitaltech.bioink;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

// user interface

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button vizButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Main Activity created");
        setContentView(R.layout.activity_main);
        this.vizButton=(Button)this.findViewById(R.id.button1);
        this.vizButton.setOnClickListener(
        	new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), "start viz pressed", Toast.LENGTH_SHORT).show();
				}
			}
		);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	// save screen state
    	// pause bluetooth traffic
    	// pause data analysis
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	// resume screen state
    	// resume bluetooth traffic
    	// resume data analysis
    }
    
}
