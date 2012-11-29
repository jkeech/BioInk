package com.vitaltech.bioink;


import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.FloatMath;
import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.lights.ALight;
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
	private Stack<ALight> lights = new Stack<ALight>();
	
	private Number3D cameraLookAt = new Number3D(0,0,0);
	private float cameraZPos = -2.5f;
	private float cameraYPos = 0;
	
	private boolean DEBUG = MainActivity.DEBUG;
	
	public Scene(Context context){
		super(context);
		setFrameRate(60);
		initScene();
	}
	
	@Override
	public void initScene(){
		//set the background color
		setBackgroundColor(Color.WHITE);
		
		// add lights to the scene
		DirectionalLight light = new DirectionalLight();
		light.setPosition(-1, 1, -1);
		light.setLookAt(0, 0, 0);
		light.setPower(0.2f);
		lights.add(light);
		
		light = new DirectionalLight();
		light.setPosition(1, -1, 1);
		light.setLookAt(0, 0, 0);
		light.setPower(0.2f);
		lights.add(light);
		
		light = new DirectionalLight();
		light.setPosition(0, 0, -1);
		light.setLookAt(0, 0, 0);
		light.setPower(0.1f);
		lights.add(light);
		
		
		// add the container that will hold all of the blobs
	    container = new BaseObject3D();
		container.isContainer(true);
	    addChild(container);
	    
	    // setup the camera
		mCamera.setNearPlane(0.01f);
		mCamera.setLookAt(cameraLookAt);
		mCamera.setPosition(0,0,cameraZPos);
		
		// Start rotating the camera/scene
	    RotateAnimation3D cameraAnimation = new RotateAnimation3D(Axis.Y,360);
	    cameraAnimation.setDuration(60000);
	    cameraAnimation.setRepeatCount(Animation3D.INFINITE);
	    cameraAnimation.setTransformable3D(container);	
	    cameraAnimation.start();
	    
	    if(DEBUG){
	    	// show bounding sphere of the entire coordinate space
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
	    
	}
	
	@Override public void onDrawFrame(GL10 glUnused) {
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
		// compute the centroid of all blobs
		int numUsers = 0;
		Number3D avg = new Number3D();
		for (Blob blob : users.values()){
			BoundingSphere sphere = blob.getGeometry().getBoundingSphere();
			sphere.transform(blob.getModelMatrix());
			numUsers++;
			avg.add(sphere.getPosition());
		}
		if(numUsers > 0)
			avg.multiply(1.0f/numUsers);
		
		// compute the distance that the camera should be from the centroid by using
		// a multiple of the max distance from any blob to the centroid
		float maxDist = 0;
		for (Blob blob : users.values()){
			BoundingSphere sphere = blob.getGeometry().getBoundingSphere();
			sphere.transform(blob.getModelMatrix());
			maxDist = Math.max(maxDist, sphere.getPosition().distanceTo(avg) + blob.getRadius());
		}
		
		// use the Pythagorean Theorem plus the y- and z-values of the centroid to calculate
		// the position of the camera along the y- and z-axis so that the distance from
		// the camera to the centroid is correct		
		float distFromCamera = Math.max(2.5f*maxDist,avg.x);
		float z = avg.z - FloatMath.sqrt(distFromCamera*distFromCamera - avg.x*avg.x);
		float y = avg.y;
		
		// finally, set the position of the camera and the location for the camera to look at
		animateCamera(avg,y,z);
	}
	
	private void animateCamera(Number3D lookAt,float yPos, float zPos){
		// animate from the current position towards the target position
		// by linearly interpolating the position to look at as well as
		// the Z position of the camera
		
		final float DECAY = 10.0f;
		cameraLookAt.add(lookAt.add(cameraLookAt.clone().multiply(-1.0f)).multiply(1.0f/DECAY));
		cameraZPos += (zPos-cameraZPos)/DECAY;
		cameraYPos += (yPos-cameraYPos)/DECAY;
		
		if(Float.isNaN(cameraZPos)){
			cameraZPos = 0;
			if(DEBUG) Log.w("viz","Camera Z Position NaN; Resetting to 0");
		}
		if(Float.isNaN(cameraYPos)){
			cameraYPos = 0;
			if(DEBUG) Log.w("viz","Camera Y Position NaN; Resetting to 0");
		}
		
		mCamera.setLookAt(cameraLookAt);
		mCamera.setZ(cameraZPos);
		mCamera.setY(cameraYPos);
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
				tmp.setLights(lights);
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