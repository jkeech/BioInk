package com.vitaltech.bioink;

public class DataSimulator {
	private Scene scene;
	
	public DataSimulator(Scene scene){
		this.scene = scene;
	}
	
	public void run(){
    	try {
    		scene.update("user1", DataType.COLOR, 1);
        	scene.update("user1", DataType.ENERGY, 1);
	    	Thread.sleep(4000);
	    	scene.update("user1", DataType.X, 2);
	    	scene.update("user1", DataType.Y, 2);
	    	scene.update("user1", DataType.Z, 2);
	    	Thread.sleep(1000);
	    	scene.update("user2", DataType.COLOR, 0.5f);
	    	scene.update("user2", DataType.ENERGY, 0.5f);
	    	scene.update("user2", DataType.X, -2);
	    	scene.update("user2", DataType.Y, -2);
	    	scene.update("user2", DataType.Z, -2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
