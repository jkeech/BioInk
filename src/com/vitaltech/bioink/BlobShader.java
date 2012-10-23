package com.vitaltech.bioink;

import rajawali.materials.DiffuseMaterial;
import android.opengl.GLES20;

public class BlobShader extends DiffuseMaterial {	
	protected static final String mVShader = 
			"precision mediump float;\n" +
			"uniform mat4 uMVPMatrix;\n" +
			"uniform mat3 uNMatrix;\n" +
			"uniform mat4 uMMatrix;\n" +
			"uniform mat4 uVMatrix;\n" +
			
			//-- normalized axis vectors. we'll use these to
			//    get the angles
			"const vec3 cXaxis = vec3(1.0, 0.0, 0.0);\n" +
			"const vec3 cYaxis = vec3(0.0, 1.0, 0.0);\n" +
			"const vec3 cZaxis = vec3(0.0, 0.0, 1.0);\n" +
			// -- the amplitude of the 'wave' effect
			"uniform float cStrength;\n" +
			// the time in which to propagate the waves
			"uniform float uTime;\n" +

			"attribute vec4 aPosition;\n" +
			"attribute vec3 aNormal;\n" +
			"attribute vec2 aTextureCoord;\n" +
			"attribute vec4 aColor;\n" +

			"varying vec2 vTextureCoord;\n" +
			"varying vec3 N;\n" +
			"varying vec4 V;\n" +
			"varying vec4 vColor;\n" +

			M_FOG_VERTEX_VARS +
			"%LIGHT_VARS%" +

			"void main() {\n" +
			"	vec4 position = aPosition;\n" +
			"	float dist = 0.0;\n" +
			"	vec3 normal = aNormal;\n" +
			
						// MY UPDATES TO THE REGULAR DIFFUSE SHADER
						"	float strength = cStrength * 0.1;\n" +
						// -- normalized direction from the origin (0,0,0)
						"	vec3 directionVec = normalize(vec3(aPosition));\n" +
						// -- the angle between this vertex and the x, y, z angles
						"	float xangle = dot(cXaxis, directionVec) * 5.0;\n" +
						"	float yangle = dot(cYaxis, directionVec) * 6.0;\n" +
						"	float zangle = dot(cZaxis, directionVec) * 4.5;\n" +
						"	vec4 timeVec = aPosition;\n" +
						"	float time = uTime * .001;\n" +
						// -- cos & sin calculations for each of the angles
						//    change some numbers here & there to get the 
						//    desired effect.
						"	float cosx = cos(time + xangle);\n" +
						"	float sinx = sin(time + xangle);\n" +
						"	float cosy = cos(time + yangle);\n" +
						"	float siny = sin(time + yangle);\n" +
						"	float cosz = cos(time + zangle);\n" +
						"	float sinz = sin(time + zangle);\n" +
						// -- multiply all the parameters to get the final
						//    vertex position
						"	timeVec.x += directionVec.x * cosx * siny * cosz * strength;\n" +
						"	timeVec.y += directionVec.y * sinx * cosy * sinz * strength;\n" +
						"	timeVec.z += directionVec.z * sinx * cosy * cosz * strength;\n" +
						"	gl_Position = uMVPMatrix * timeVec;\n" +

			"	vTextureCoord = aTextureCoord;\n" +
			"	N = normalize(uNMatrix * normal);\n" +
			"	V = uMMatrix * position;\n" +
			"	vColor = aColor;\n" +

			"%LIGHT_CODE%" +
			"}";
	
	protected int muTimeHandle;
	protected int mcStrengthHandle;
	protected float mTime;
	protected float mStrength;
	
	public BlobShader() {
		super(mVShader, DiffuseMaterial.mFShader, false);
	}
	
	public BlobShader(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
	}
	
	public void setShaders(String vertexShader, String fragmentShader)
	{
		super.setShaders(vertexShader, fragmentShader);
		muTimeHandle = getUniformLocation("uTime");
		mcStrengthHandle = getUniformLocation("cStrength");
	}
	
	public void useProgram() {
		super.useProgram();
		// -- make sure that time updates every frame
		GLES20.glUniform1f(muTimeHandle, mTime);
		GLES20.glUniform1f(mcStrengthHandle, mStrength);
	}
	
	public void setTime(float time) {
		mTime = time;
	}
	
	public void setStrength(float strength){
		mStrength = strength;
	}
}