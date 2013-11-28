package com.scatman.framework.impl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class AndroidFastRenderView extends SurfaceView implements Runnable {
	
	// A reference to the game instance from which we can get the active screen
	AndroidGame game;
	// The artificial framebuffer
	Bitmap framebuffer;
	// renderThread is simply a reference to the thread instance that will be responsible for 
	// executing our rendering thread logic.
	Thread renderThread = null;
	// holder is a reference to the surfaceholder instance that we get from the SurfaceView superclass
	SurfaceHolder holder;
	// A boolean flag we will use to signal the rendering thread that it should stop
	volatile boolean running = false;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer) {
		// Call the super's constructor, which is an Activity
		super(game);
		// Store parameters in their respective members
		this.game = game;
		this.framebuffer = framebuffer;
		// In order to render to a SurfaceView from a different thread than the UI thread,
		// We need to aquire an instance of the SurfaceHolder class
		// The SurfaceHolder is a wrapper around the surface and provides us with two methods:
		// Canvas SurfaceHolder.lockCanvas() & SurfaceHolder.unlockAndPost(Canvas canvas)
		// We will use these two to methods in our rendering thread to aquire the canvas, render with it,
		// and finally make the image we just rendered visible on the screen.
		this.holder = getHolder();
	}

	
	
	/**
	 * Called when the activity is onPause()
	 * A primary purpose for this method is to start the thread
	 * In short, resume() makes sure that our thread interacts nicely with the activity life cycle
	 */
	public void resume() {
		// Creates a new thread, notes it's running with a boolean, and starts it
		// Note that we create a thread here, each time resume() is called. This means
		// that somewhere else we will need to stop the thread or we will have a bunch of them running
		// rampant. We stop this thread in pause()
		running = true;
		// Notice that we set the fastRenderView instance itself as the runnable of the thread
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	
	

	/**
	 * run() tracks the delta time between each frame.
	 */
	public void run() {
		
		Rect dstRect = new Rect();
		// System.nanoTime() returns the current time in nanoseconds as a long
		long startTime = System.nanoTime();
		
		while(running) {
			// If we don't have a surface holder, get out of this method.
			// We check whether or not the surface has been created, because we cannot aquire the
			// canvas from the surfaceHolder as long as the surface is not yet valid.
			// If this method returns true, we can safely lock the surface and draw to it via the 
			// canvas we receive.
			if(!holder.getSurface().isValid()) {
				continue;
			}
			
			// A nanosecond is one-billionth of a second
			// WE START by taking the difference between the last loop iteration's start time and the current time
			// To make it easier to work with this deltatime, we convert it into seconds
			float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
			// Then we save the current time stamp, which we'll use in the next iteration to calculate delta time
			startTime = System.nanoTime();
			
			// Call the current screen's update() and present() which will update the game logic and render
			// things to the artificial framebuffer
			game.getCurrentScreen().update(deltaTime);
			game.getCurrentScreen().present(deltaTime);
			
			// Set the surface holder for drawing
			// Here we get a hold of the Canvas for the SurfaceView and draw the artificial framebuffer
			// Scaling is performed automatically
			Canvas canvas = holder.lockCanvas();
			// sets the top and left members of dstRect to 0 and 0, sets the bottom and right members 
			// to the actual screen dimensions
			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(framebuffer, null, dstRect, null);
			// release the surface holder and update the display
			// This method unlocks the surface again and makes sure that what we've drawn via the canvas
			// gets displayed to the screen. We must pass the canvas we created with the lockCanvas()
			// MAKE SURE TO UNLOCK THE SURFACE, OTHERWISE THIS ACTIVITY WILL LOCK UP THE PHONE!!!!!!
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	
	
	/**
	 * pause() terminates the rendering/main loop thread and waits for it to die completely before returning
	 */
	public void pause() {
		running = false;
		
		while(true) {
			try {
				renderThread.join();
				break;
			} catch(InterruptedException e) {
				// retry
			}
		}
		
	} // end pause
	
	
	
	
}
