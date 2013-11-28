package com.scatman.framework.impl;

import java.util.ArrayList;
import java.util.List;

import com.scatman.framework.Pool;
import com.scatman.framework.Input.KeyEvent;
import com.scatman.framework.Pool.PoolObjectFactory;

import android.view.View;
import android.view.View.OnKeyListener;


/**
 * KeyboardHandler implements the OnKeyListener interface so that it can receive
 * key events from a View
 */
public class KeyboardHandler implements OnKeyListener {
	// An array holding 128 booleans. We store the current state of each key (if it's pressed or not) in this array
	// It is indexed by the key's key code. .KEYCODE_XXX are all between 0 and 127
    boolean[] pressedKeys = new boolean[128];
    // A Pool that holds the instances of our KeyEvent class. We recycle all the KeyEvent objects we create
    Pool<KeyEvent> keyEventPool;
    // ArrayList that stores the KeyEvents that have not yet been consumed by our game. Each time we get a new
    // KeyEvent on the UI thread, we add it to the this list.
    List<KeyEvent> keyEventsBuffer = new ArrayList<KeyEvent>();    
    // ArrayList that stores the KeyEvents that we return by calling KeyboardHandler.getKeyEvents()
    List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();

    
    /**
     * PRIMARY CONSTRUCTOR
     * The View is the view from which we want to receive key events
     */
    public KeyboardHandler(View view) {
    	// We need a PoolObjectFactory instance in order to create a Pool object. This PoolObjectFactory will handle KeyEvents
        PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
        	@Override
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        };
        // Here we create a new Pool object passing it the PoolObjectFactory we just created, and setting the size of the Pool array to 100
        keyEventPool = new Pool<KeyEvent>(factory, 100);
        // When a keyEvent is fired by the view, the onKey method in this class will be called and will receive the event
        view.setOnKeyListener(this);
        // We want to make sure that the view will receive key events by making it the focused View
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    
    /**
     * PRIMARY KEY LISTENER
     * onKey is called each time the view receives a new key event
     */
    @Override
    public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
    	// We want to ignore and do nothing for any ACTION_MULTIPLE key events
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) {
            return false;
        }

        // We synchronize here because our key events are received on the UI thread and read on
        // the main loop thread, so we have to make sure that none of our members are accessed in parallel
        synchronized (this) {
        	// We just create a new KeyEvent (from our pool) and store it locally. At this point it is = to just saying new KeyEvent
            KeyEvent keyEvent = keyEventPool.newObject();
            // The current Android KeyEvent produced and passed in a keyCode. We store that keyCode in our keyEvent
            keyEvent.keyCode = keyCode;
            // We store the unicode character inside of our KeyEvent
            keyEvent.keyChar = (char) event.getUnicodeChar();
            
            // If the type of the current Android KeyEvent is ACTION_DOWN
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
            	// We set our keyEvent's type to be our predefined KEY_DOWN value
                keyEvent.type = KeyEvent.KEY_DOWN;
                if(keyCode >= 0 && keyCode < 128)
                    pressedKeys[keyCode] = true;
            }
            
            // If the type of the current Android KeyEvent is ACTION_UP
            if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
            	// We set our keyEvent's type to be our predefined KEY_UP value
                keyEvent.type = KeyEvent.KEY_UP;
                if(keyCode >= 0 && keyCode < 128)
                    pressedKeys[keyCode] = false;
            }
            // We add the keyEvent that we just created to the ArrayList of KeyEvents
            keyEventsBuffer.add(keyEvent);
        }
        
        return false;
    }

    
    /**
     * This method will determine if a particular key is being pressed down or not based on it's keyCode
     * Since we are working with primitive types only, there is no need for synchronization
     */
    public boolean isKeyPressed(int keyCode) {
    	// If the passed in keyCode is not within the acceptable range of 128 keycodes...
        if (keyCode < 0 || keyCode > 127) {
        	// ... then break out of this method
            return false;
        }
        // But if it IS within the acceptable range, return whether or not that key is being pressed down or not
        return pressedKeys[keyCode];
    }

    
    /**
     * Page 210
     */
    public List<KeyEvent> getKeyEvents() {
    	// This method will be called from a different thread, so it requires synchronization
        synchronized (this) {
        	// We find out how many keyEvents are in the keyEvents ArrayList
            int len = keyEvents.size();
            // Then we loop through each item in the keyEvents ArrayList
            for (int i = 0; i < len; i++) {
            	// and we add each of these items to our Pool's ArrayList of recyclable KeyEvents
                keyEventPool.free(keyEvents.get(i));
            }
            // Then we empty the keyEvents ArrayList
            keyEvents.clear();
            // Basically just copy all of the items in keyEventsBuffer ArrayList into keyEvents ArrayList
            keyEvents.addAll(keyEventsBuffer);
            // Then we clear the keyEventsBuffer ArrayList
            keyEventsBuffer.clear();
            
            return keyEvents;
        }
    }
    
    
    
}