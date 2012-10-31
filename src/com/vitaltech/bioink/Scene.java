package com.vitaltech.bioink;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.FloatMath;
import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DQueue;
import rajawali.animation.RotateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
import rajawali.math.Number3D.Axis;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.bounds.BoundingSphere;

public class Scene extends RajawaliRenderer {
	public ConcurrentHashMap<String,Blob> users = new ConcurrentHashMap<String,Blob>(7);
	
	private BaseObject3D container;
	private DirectionalLight mLight;
	
	public Scene(Context context){
		super(context);
		setFrameRate(60);
		initScene();
	}
	
	@Override
	public void initScene(){
		mLight = new DirectionalLight(0.3f, -0.3f, 1.0f); // set the direction
		mLight.setPower(0.8f);

		mCamera.setNearPlane(0.01f);
		mCamera.setLookAt(0, 0, 0);
		mCamera.setPosition(0, 0, -2.5f);
		
		setBackgroundColor(Color.WHITE);
		
		container = new BaseObject3D();
		container.isContainer(true);
		
		Animation3DQueue queue = new Animation3DQueue();
		
		
		/*RotateAnimation3D mCamAnim = new RotateAnimation3D(Axis.X,360);
	    mCamAnim.setDuration(5000);
	    mCamAnim.setRepeatCount(Animation3D.INFINITE);
	    mCamAnim.setTransformable3D(container);
	    queue.addAnimation(mCamAnim);	    
	    */
		
	    RotateAnimation3D mCamAnim2 = new RotateAnimation3D(Axis.Y,360);
	    mCamAnim2.setDuration(20000);
	    mCamAnim2.setRepeatCount(Animation3D.INFINITE);
	    mCamAnim2.setTransformable3D(container);
	    queue.addAnimation(mCamAnim2);	
	    
	    /*
	    RotateAnimation3D mCamAnim3 = new RotateAnimation3D(Axis.Z,360);
	    mCamAnim3.setDuration(3000);
	    mCamAnim3.setRepeatCount(Animation3D.INFINITE);
	    mCamAnim3.setTransformable3D(container);
	    queue.addAnimation(mCamAnim3);
	    */
	    queue.start();
	    
	    addChild(container);
	    
	    Sphere bounds = new Sphere(1,20,20);
	    bounds.setPosition(0, 0, 0);
	    SimpleMaterial material = new SimpleMaterial();
	    material.setUseColor(true);
	    bounds.setColor(Color.LTGRAY);
	    bounds.setMaterial(material);
	    bounds.setTransparent(true);
	    bounds.setDrawingMode(GLES20.GL_LINES);
	    container.addChild(bounds);
	    
	}
	
	@Override public void onDrawFrame(GL10 glUnused) {
		mCamera.setLookAt(0, 0, 0);
		synchronized(users){
			super.onDrawFrame(glUnused);
			for (Blob blob : users.values()){
				blob.draw();
			}
			zoomCamera();
		}
	}
	
	// zooms the camera in and out to fill the screen with the blobs in the scene
	private void zoomCamera(){
		float maxDistFromOrigin = 0;
		for (Blob blob : users.values()){
			BoundingSphere sphere = blob.getGeometry().getBoundingSphere();
			sphere.transform(blob.getModelMatrix());
			maxDistFromOrigin = Math.max(maxDistFromOrigin, distanceFromOrigin(sphere));
		}
		mCamera.setZ(-2.5f*maxDistFromOrigin);
	}
	
	// calculates the distance of the farthest point in a sphere from the origin
	private float distanceFromOrigin(BoundingSphere s){
		Number3D pos = s.getPosition();
		return FloatMath.sqrt(pos.x*pos.x + pos.y*pos.y + pos.z*pos.z) + s.getRadius();
	}
	
	
	/*
	 * This method updates a certain DataType value for one user. The user object
	 * will be created if it does not exist. This method should be called by the
	 * data processing module.
	 */
	public void update(String id, DataType type, float val){
		synchronized(users){
			if(!users.containsKey(id)){
				Blob tmp = new Blob();
				users.put(id,tmp); // insert into the dictionary if it does not exist
				tmp.addLight(mLight);			
				container.addChild(tmp);
			}
		}
		
		// update the user in the scene
		switch(type){
			case COLOR: users.get(id).adjustColor(val); break;
			case ENERGY: users.get(id).energy = val; break;
			case X: users.get(id).x = val; break;
			case Y: users.get(id).y = val; break;	
			case Z: users.get(id).z = val; break;
			case VOLUME: users.get(id).setVolume(val); break;
		}
	}
}