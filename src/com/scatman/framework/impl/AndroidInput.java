package com.scatman.framework.impl;

import java.util.List;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

import com.scatman.framework.Input;



public class AndroidInput implements Input {
	AccelerometerHandler accelHandler;
	KeyboardHandler keyHandler;
	TouchHandler touchHandler;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public AndroidInput(Context context, View view, float scaleX, float scaleY) {
		accelHandler = new AccelerometerHandler(context);
		keyHandler = new KeyboardHandler(view);
		
		// We check the Android Version that the application uses to run
		if (Integer.parseInt(VERSION.SDK) < 5) { // Android version 2.0  = SDK version 5
			// If the Device is not compatible with multitouch we use the SingleTouchHandler
			touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
		}
		else {
			// If the device is compatible with multitouch we use the MultiTouchhandler
			touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
		}
		
	}


	@Override
	public boolean isKeyPressed(int keyCode) {
		return keyHandler.isKeyPressed(keyCode);
	}


	@Override
	public boolean isTouchDown(int pointer) {
		return touchHandler.isTouchDown(pointer);
	}


	@Override
	public int getTouchX(int pointer) {
		return touchHandler.getTouchX(pointer);
	}


	@Override
	public int getTouchY(int pointer) {
		return touchHandler.getTouchY(pointer);
	}


	@Override
	public float getAccelX() {
		return accelHandler.getAccelX();
	}


	@Override
	public float getAccely() {
		return accelHandler.getAccelY();
	}


	@Override
	public float getAccelZ() {
		return accelHandler.getAccelZ();
	}


	@Override
	public List<KeyEvent> getKeyEvents() {
		return keyHandler.getKeyEvents();
	}


	@Override
	public List<TouchEvent> getTouchEvents() {
		return touchHandler.getTouchEvents();
	}
	
	
	
	
	
	
}
