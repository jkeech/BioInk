package com.vitaltech.bioink;


import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import rajawali.animation.Animation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.renderer.RajawaliRenderer;

public class Scene extends RajawaliRenderer {
	public ConcurrentHashMap<String,Blob> users = new ConcurrentHashMap<String,Blob>(7);
	
	private DirectionalLight mLight;
	private Number3D axis;
	
	public Scene(Context context){
		super(context);
		setFrameRate(60);
		initScene();
	}
	
	@Override
	public void initScene(){
		mLight = new DirectionalLight(0.3f, -0.3f, 1.0f); // set the direction
		mLight.setPower(0.8f);

		axis = new Number3D(2, 4, 1);
		axis.normalize();

		mCamera.setPosition(0, 0, -10);
		mCamera.setLookAt(0,0,0);
		
		setBackgroundColor(Color.WHITE);
	}
	
	@Override public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		for (Blob blob : users.values()){
			blob.draw();
		}
	}
	
	/*
	 * This method updates a certain DataType value for one user. The user object
	 * will be created if it does not exist. This method should be called by the
	 * data processing module.
	 */
	public void update(String id, DataType type, float val){
		if(!users.containsKey(id)){
			Blob tmp = new Blob();
			users.put(id,tmp); // insert into the dictionary if it does not exist
			
			Animation3D mAnim;
			mAnim = new RotateAnimation3D(axis, 360);
			mAnim.setRepeatCount(Animation3D.INFINITE);
			mAnim.setDuration(500);
			mAnim.setTransformable3D(tmp);
			
			tmp.addLight(mLight);
			
			addChild(tmp);
		}
		
		// update the user in the scene
		switch(type){
			case COLOR: users.get(id).adjustColor(val); break;
			case ENERGY: users.get(id).energy = val; break;
			case X: users.get(id).x = val; break;
			case Y: users.get(id).y = val; break;	
			case Z: users.get(id).z = val; break;
		}
	}
}