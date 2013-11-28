package com.scatman.framework.impl;

import java.util.ArrayList;
import java.util.List;

import com.scatman.framework.Pool;
import com.scatman.framework.Input.TouchEvent;
import com.scatman.framework.Pool.PoolObjectFactory;


import android.view.MotionEvent;
import android.view.View;




/**
 * This is for all versions of Android before Android 2.0.
 * Multitouch is only supported on Android Version 2.0 and above
 */
public class SingleTouchHandler implements TouchHandler{
	boolean isTouched;
	int touchX;
	int touchY;
	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public SingleTouchHandler(View view, float scaleX, float scaleY) {
		// Create a PoolObjectFactory object that will allow our Pool to return TouchEvents
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			@Override
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		
		// Create a Pool that we will use to recycle TouchEvents
		touchEventPool = new Pool<TouchEvent>(factory, 100);
		// When TouchEvents are fired from the view, handle them in this classes's onTouch method
		view.setOnTouchListener(this);
		// Just store the two scales in our class members for now
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	
	/**
	 * Called when touch events are fired
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		synchronized(this) {
			// Create a blank TouchEvent object
			TouchEvent touchEvent = touchEventPool.newObject();
			
			// Fill the TouchEvent object's type variable and flag if the screen is being touched or not
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				touchEvent.type = TouchEvent.TOUCH_DOWN;
				isTouched = true;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				touchEvent.type = TouchEvent.TOUCH_DRAGGED;
				isTouched = true;
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				touchEvent.type = TouchEvent.TOUCH_UP;
				isTouched = false;
				break;
			}			
			}// end switch
			
			
			touchEvent.x = touchX = (int)(event.getX() * scaleX);
			touchEvent.y = touchY = (int)(event.getY() * scaleY);
			// Add the TouchEvent to the touchEventsBuffer ArrayList
			touchEventsBuffer.add(touchEvent);
			
			// Return true because we handled the TouchEvent ourselves
			return true;			
		}
	}

	
	
	/**
	 * pointerID must be 0 since this class only support single touch
	 */
	@Override
	public boolean isTouchDown(int pointer) {
		synchronized(this) {
			if (pointer == 0) {
				return isTouched;
			}
			else {
				return false;
			}
		}
	}

	
	/**
	 * pointerID must be 0 since this class only support single touch
	 */
	@Override
	public int getTouchX(int pointer) {
		synchronized(this) {
			return touchX;
		}
	}
	
	

	/**
	 * pointerID must be 0 since this class only support single touch
	 */
	@Override
	public int getTouchY(int pointer) {
		synchronized(this) {
			return touchY;
		}
	}

	

	@Override
	public List<TouchEvent> getTouchEvents() {
		synchronized(this) {
			// We find out how many events are in the touchEvents arrayList
			int len = touchEvents.size();
			// Then we iterate through each item and add it to the Pool's recyclable Events ArrayList
			for(int i = 0; i < len; i++ ) {
				touchEventPool.free(touchEvents.get(i));
			}
			
			// Then we empty the touchEvents ArrayList
			touchEvents.clear();
			// Basically just copy all of the items in touchEventsBuffer ArrayList into touchEvents ArrayList
			touchEvents.addAll(touchEventsBuffer);
			// Then we clear the touchEventsBuffer ArrayList
			touchEventsBuffer.clear();
			
			return touchEvents;
		}
		
	}

	
	
	
	
	
	
}
