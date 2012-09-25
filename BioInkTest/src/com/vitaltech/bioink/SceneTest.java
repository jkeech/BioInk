package com.vitaltech.bioink;

import static org.junit.Assert.*;

import org.junit.Test;

public class SceneTest {

	@Test
	public void testUpdate() {
		Scene scene = new Scene(1000);
		
		// add some dummy items
		scene.update("user1",DataType.HEARTRATE,50);
		scene.update("user2",DataType.TEMP,98.6);
		scene.update("user1",DataType.TEMP,99.78);
		
		assertEquals(50,scene.users.get("user1").heartrate,0);
		assertEquals(99.78,scene.users.get("user1").temp,0);
		assertEquals(98.6,scene.users.get("user2").temp,0 );
		
		// update existing item
		scene.update("user1",DataType.HEARTRATE,60.3);
		assertEquals(60.3,scene.users.get("user1").heartrate,0);
	}

}
