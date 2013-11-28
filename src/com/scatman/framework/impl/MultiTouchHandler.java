package com.scatman.framework.impl;

import java.util.ArrayList;
import java.util.List;
import com.scatman.framework.Input.TouchEvent;
import com.scatman.framework.Pool;
import com.scatman.framework.Pool.PoolObjectFactory;
import android.view.MotionEvent;
import android.view.View;


/**
 * Multitouch is only supported on Android Versions 2.0 and above
 */
public class MultiTouchHandler implements TouchHandler {
	// Our multitouch supports up to 10 fingers
	private static final int MAX_TOUCHPOINTS = 10;
	
	boolean[] isTouched = new boolean[MAX_TOUCHPOINTS]; // Stores whether the finger with that pointer ID is down or not
	int[] touchX = new int[MAX_TOUCHPOINTS]; // x coordinates for each pointer ID
	int[] touchY = new int[MAX_TOUCHPOINTS]; // y coordinates for each pointer ID
	int[] id = new int[MAX_TOUCHPOINTS];
	// A Pool that holds the instances of our TouchEvent class. We recycle all the TouchEvent objects we create
	Pool<TouchEvent> touchEventPool;
	// ArrayList that stores the TouchEvents that we return
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	// ArrayList that stores the TouchEvents that have not yet been consumed by our game. Each time we get a new
    // TouchEvent on the UI thread, we add it to the this list.
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;

	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public MultiTouchHandler(View view, float scaleX, float scaleY) {
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
		synchronized (this) {
			// First we want the event type. We mask the getAction() integer.
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			// Then, we extract the pointer index and fetch the corresponding pointer identifier from the MotionEvent (p.139)
			int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			// Find out the number of fingers that have coordinates in the motion event. getPointerCount() returns the number of active pointers
			int pointerCount = event.getPointerCount();
			
			TouchEvent touchEvent;
			
			for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
				
				// Reset the data for fingers that are not/no longer touched down.
				if (i >= pointerCount) {
					isTouched[i] = false;
					id[i] = -1;
					continue;
				}
				
				// getPointerId takes a pointer index, and returns the pointer identifier based on a pointer index
				int pointerId = event.getPointerId(i);
				
				if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
					// if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch point
					continue;
				}
				
				
				switch (action) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN: {
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DOWN;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
						touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;
					}
	
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL: {
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_UP;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
						touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
						isTouched[i] = false;
						id[i] = -1;
						touchEventsBuffer.add(touchEvent);
						break;
					}
	
					case MotionEvent.ACTION_MOVE: {
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DRAGGED;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) (event.getX(i) * scaleX);
						touchEvent.y = touchY[i] = (int) (event.getY(i) * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;
					}
				} // end switch
			}// end for loop
			
			// Indicate that we processed the touch event ourselves
			return true;
		}
	}


	/**
	 * 
	 */
	@Override
	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return false;
			else
				return isTouched[index];
		}
	}

	
	/**
	 * 
	 */
	@Override
	public int getTouchX(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return 0;
			else
				return touchX[index];
		}
	}


	/**
	 * 
	 */
	@Override
	public int getTouchY(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS)
				return 0;
			else
				return touchY[index];
		}
	}


	/**
	 * 
	 */
	@Override
	public List<TouchEvent> getTouchEvents() {
		synchronized (this) {
			// We find out how many events are in the touchEvents arrayList
			int len = touchEvents.size();
			// Then we iterate through each item and add it to the Pool's recyclable Events ArrayList
			for (int i = 0; i < len; i++) {
				touchEventPool.free(touchEvents.get(i));
			}
			
			// Then we empty the touchEvents ArrayList
			touchEvents.clear();
			// Basically just copy all of the items in touchEventsBuffer
			// ArrayList into touchEvents ArrayList
			touchEvents.addAll(touchEventsBuffer);
			// Then we clear the touchEventsBuffer ArrayList
			touchEventsBuffer.clear();

			return touchEvents;
		}
	}
	
	// returns the index for a given pointerId or -1 if no index.
	private int getIndex(int pointerId) {
		for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
			if (id[i] == pointerId) {
				return i;
			}
		}
		return -1;
	}
}