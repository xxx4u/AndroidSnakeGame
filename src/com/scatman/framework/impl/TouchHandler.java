package com.scatman.framework.impl;

import java.util.List;
import com.scatman.framework.Input.TouchEvent;
import android.view.View.OnTouchListener;


/**
 * All TouchHandlers must implement the OnTouchListener interface which is used to register the handler with a view.
 * 
 * The SingleTouchHandler and the MultiTouchHandler will both implement this interface
 */
public interface TouchHandler extends OnTouchListener {

	public boolean isTouchDown(int pointer);
	
	public int getTouchX(int pointer);
	
	public int getTouchY(int pointer);
	
	public List<TouchEvent> getTouchEvents();
	
}
