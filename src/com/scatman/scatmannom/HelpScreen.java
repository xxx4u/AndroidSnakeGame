package com.scatman.scatmannom;

import java.util.List;

import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Screen;
import com.scatman.framework.Input.TouchEvent;


/**
 * For our help screens:
 * 		The help image is at 64, 100
 * 		The next button is at 256, 416
 */
public class HelpScreen extends Screen {
	
	public HelpScreen(Game game) {
		super(game);
	}
	
	
	@Override
	public void update(float deltaTime) {
		// We get the touch events from the Input instance the Game provides us
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		// we get these, but we don't use them. It's just to clear the internal buffer
		// page 249 said that this was a necessary step
		game.getInput().getKeyEvents();
		
		// We make an integer to hold the number of touchEvents we have to loop through
		int len = touchEvents.size();
		
		// We loop through each of the touch events until we find one with the type
		// TouchEvent.TOUCH_UP
		for (int i = 0; i < len; i++) {
			// Get a reference to each event so we can compare it and see if it's of type TOUCH_UP
			TouchEvent event = touchEvents.get(i);
			
			// See if the event is of type TOUCH_UP
			if(event.type == TouchEvent.TOUCH_UP) {
				
				// No need for inBounds() this time. The button is at the bottom right
				// of the screen. So we only care about the x and y of the button.
				// See if the event's x is greater than 256 and if the y is greater than 416
				if(event.x > 256 && event.y > 416) {
					// Set the current screen to be a HelpScreen2
					game.setScreen(new HelpScreen2(game));
					
					// If the sound is enabled, play the click button
					if( Settings.soundEnabled ) { Assets.click.play(1); }
					
					// We're all done, the screen is about to be transitioned, so return
					return;
				}
			}// end if TOUCH_UP
		}// end for
	}// end update()
	
	
	/**
	 * present() is essentially the draw method.
	 */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics(); 
		// We'll need this to draw stuff
		// Draw these three assets in their appropriate locations (Asset, x, y)
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.help1, 64, 100);
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}


	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
