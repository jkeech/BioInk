package com.vitaltech.bioink;

public class Blob {
	// current data
	private float _x,_y,_z; // position
	private float _r,_g,_b; // color
	private float _radius; // size
	private float _energy; // amount of energy on the surface of the blob
	
	// target data to animate towards
	public float x,y,z;
	public float r,g,b;
	public float radius;
	public float energy;
	
	// smooth out the animations with exponential decay towards the target
	private float DECAY = 2.0f; // 1 = instantaneous, >1 for slower decay
	
	public Blob(){
		_x = _y = _z = x = y = z = 0;
		_r = _g = _b = r = g = b = 0;
		_radius = 0;
		radius = 0.2f;
		_energy = energy = 0;
	}
	
	public void draw(){
		animate();
		
		// TODO: add drawing code here, probably with an external 3D library
	}
	
	// update current data by moving towards the target
	private void animate(){
		_x = _x + (x-_x)/DECAY;
		_y = _y + (y-_y)/DECAY;
		_z = _z + (z-_z)/DECAY;
		_r = _r + (r-_r)/DECAY;
		_g = _g + (g-_g)/DECAY;
		_b = _b + (b-_b)/DECAY;
		_radius = _radius + (radius-_radius)/DECAY;
		_energy = _energy + (energy-_energy)/DECAY;
	}
}
