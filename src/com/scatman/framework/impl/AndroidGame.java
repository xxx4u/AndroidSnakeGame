package com.scatman.framework.impl;


import com.scatman.framework.Audio;
import com.scatman.framework.FileIO;
import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Input;
import com.scatman.framework.Screen;
import com.scatman.scatmannom.Assets;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;



/**
 * AndroidGame responsibilities:
 * 1) Perform window management. In our context, this means setting up an activity
 * 	  and an AndroidFastRenderView, and handling the activity life cycle in a clean way
 * 
 * 2) Use and manage a WakeLock so that the screen does not dim
 * 
 * 3) Instantiate and hand out references to Graphics, Audio, FileIO, and Input
 * 	  to interested parties
 * 
 * 4) Manage Screens and integrate them with the activity life cycle
 * 
 * 5) Our general goal is to have a single class called AndroidGame from which we
 *    can derive. We want to implement the Game.getStartScreen() method later on to
 *    start our game in the following way:
 *    
 *    	public class MrNomGame extends AndroidGame {
 *                  
 *         public Screen getStartScreen() {
 *            return new MainMenu(this);
 *         }
 *    
 *      }
 * 
 */
public abstract class AndroidGame extends Activity implements Game {
	// We will draw to renderView, and renderView will also manage our main loop thread
	AndroidFastRenderView renderView;
	Graphics graphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	// This will be the currently active screen
	Screen currentScreen;
	// This will keep our screen from dimming
	WakeLock wakeLock;
	
	
	/**
	 * onCreate()
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Here we make the activity Full Screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// We either want to use a 320x480 frame buffer (portrait), or a 480x320 (Landscape)
		// So we get the orientation of the Activity's Screen (basically just the device)		
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		
		// We set the framebuffer size
		int frameBufferWidth = isLandscape ? 480 : 320;
		int frameBufferHeight = isLandscape ? 320 : 480;
		
		// We instantiate a bitmap which we'll hand to the AndroidFastRenderView and AndroidGraphics
		// We make the bitmap use an RGB565 color format so we don't waste memory andso drawing is faster
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
		
		// We calculate the scaleX and scaleY values that the SingleTouchHandler and the MultiTouchhandler
		// classes will use to transform the touch event coordinates in our fixed-coordinate system
		float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
		float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();
		
		
		renderView = new AndroidFastRenderView( this, frameBuffer );
		graphics   = new AndroidGraphics( getAssets(), frameBuffer );
		//fileIO     = new AndroidFileIO( this );
		fileIO = new AndroidFileIO(this, "/MrNom/");
		audio      = new AndroidAudio( this );
		input      = new AndroidInput( this, renderView, scaleX, scaleY );
		currentScreen = getStartScreen();
		
		// Fill the View with the AndroidFastRenderView
		setContentView( renderView );
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");		
	}
	
	
	/**
	 * onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		// Essentially we are "turning on" the WakeLock
        wakeLock.acquire();
        // We make sure that the currentScreen is informed that the 
        // game (and therefore the Activity) has been resumed
        currentScreen.resume();
        // We tell the AndroidFastRenderView to resume the rendering thread, which will also kick off our
        // game's main loop where we tell the current Screen to update and present itself in each iteration
        renderView.resume();
	}
	
	
	/**
	 * onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		// Dispose of the WakeLock
		wakeLock.release();
		// Make sure that the rendering thread is terminated. We must terminate the rendering thread before
		// calling the current Screen's onPause()
		renderView.pause();
		// We tell the current Screen that it should pause itself
		currentScreen.pause();
		
		// In case the ACtivity is going to be destroyed, we also inform the Screen so that it 
		// can do any necessary cleanup work.
		if (isFinishing()) {
			currentScreen.dispose();
			if(Assets.bgMusic.isPlaying() == true) {
				Assets.bgMusic.stop();		
			}
			
		}
	}
	
	
	/**
	 * Simply return the respective instance to the caller. Which will alsways be one
	 * of our Screen implementations of our game
	 */
	@Override
    public Input getInput() {
        return input;
    }

	
	
    /**
	 * Simply return the respective instance to the caller. Which will alsways be one
	 * of our Screen implementations of our game
	 */
    @Override
    public FileIO getFileIO() {
        return fileIO;
    }
    
    

    /**
	 * Simply return the respective instance to the caller. Which will alsways be one
	 * of our Screen implementations of our game
	 */
    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    
    
    /**
	 * Simply return the respective instance to the caller. Which will alsways be one
	 * of our Screen implementations of our game
	 */
    @Override
    public Audio getAudio() {
        return audio;
    }
	
	
	/**
	 * We'll usually call the setScreen method in the update() method in a Screen instance.
	 * 
	 * 	Example: Assume we have a main menu Screen where we check to see if the Play button is pressed in
	 * 	the update() method. If that is the case, we will transition to the next Screen by calling 
	 * 	the AndroidGame.setScreen() method from within the MainMenu.update() method with a 
	 * 	brand-new instance of that next Screen.
	 */
    @Override
	public void setScreen(Screen screen) {
		if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
		}
			
			// Tell the current screen to pause and dispose of itself so that it can make room for the new screen
			this.currentScreen.pause();
			this.currentScreen.dispose();
			
			// Ask the new screen to resume itself and update itself once with a delta time of zero
			screen.resume();
			screen.update(0);
			
			// Set the screen member to the new screen
			this.currentScreen = screen;
	}	
	
		
	public Screen getCurrentScreen() {
		return currentScreen;
	}
	
}
