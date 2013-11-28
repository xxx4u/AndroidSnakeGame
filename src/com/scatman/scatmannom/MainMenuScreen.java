package com.scatman.scatmannom;

import java.util.List;
import android.util.Log;
import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Input.TouchEvent;
import com.scatman.framework.Screen;
import com.scatman.framework.impl.AndroidAudio;


/**
 * MainMenuScreen class renders the logo, the main menu options, and the sound
 * setting in the form of a toggle button. All it does it react to touches on either
 * the main menu options or the sound setting toggle button.
 * 
 * To do this, we need to know two things:
 *     1) Where on the screen we render the images
 *     2) where the touch areas are that will either trigger 
 *        a screen transition or toggle the sound setting
 */
public class MainMenuScreen extends Screen {
	public static AndroidAudio bgSong;
	
	public MainMenuScreen(Game game) {
		super(game);
		if(Settings.soundEnabled == true){
			Assets.bgMusic.play();		
		}
	}

	
	/**
	 * update() is where we will do all of our touch events checking
	 */
	@Override
	public void update(float deltaTime) {
		if(Assets.bgMusic.isPlaying() == false ) {
			if(Settings.soundEnabled == true){
				Assets.bgMusic.play();
			}
		}
		
		if(Assets.bgMusic.isPlaying() == true ) {
			if(Settings.soundEnabled == false){
				Assets.bgMusic.stop();
			}
		}
		
		// Necessary to get the height of the screen later
		Graphics g = game.getGraphics();
		// We get the touch events from the Input instance the Game provides us
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		// we get these, but we don't use them. It's just to clear the internal buffer
		// page 249 said that this was a necessary step
		game.getInput().getKeyEvents();
		
		// We make an integer to hold the number of touchEvents we have to loop through
		int len = touchEvents.size();
		
		// We loop through each of the touch events until we find one with the type
		// TouchEvent.TOUCH_UP
		for(int i = 0; i < len; i++) {
			// Get a reference to each event so we can compare it and see if it's of type TOUCH_UP
			TouchEvent event = touchEvents.get(i);
			
			// See if the event is of type TOUCH_UP
			if(event.type == TouchEvent.TOUCH_UP){
				
				// Check if touch event was on sound toggle button
				if(inBounds(event, 0, g.getHeight() - 64, 64, 64)) {
					// if so, then simply invert Settings.soundEnabled boolean
					Settings.soundEnabled = !Settings.soundEnabled;
					if(Settings.soundEnabled){
						Assets.click.play(1); // play the click sound
					}
				}
				
				// Check if touch event was on Play button
				if(inBounds(event, 64, 220, 192, 42)) {
					// transition the screen to the Game screen
					game.setScreen(new GameScreen(game));
					try {
					if(Settings.soundEnabled){
						Assets.click.play(1); // play the click sound
					} } catch(Exception e) {Log.d("Problem", "in main menu, could not play click for game screen in update");}
					return;
				}				
				
				// Check if touch event was on Highscores button
				if(inBounds(event, 64, 220 + 42, 192, 42)) {
					// transition the screen to the HighscoreScreen
					game.setScreen(new HighscoreScreen(game));
					if(Settings.soundEnabled){
						Assets.click.play(1); // play the click sound
					}
					return;
				}				
				
				// Check if touch event was on Help button
				if(inBounds(event, 64, 220 + 84, 192, 42)) {
					// transition the screen to the HelpScreen
					game.setScreen(new HelpScreen(game));
					if(Settings.soundEnabled){
						Assets.click.play(1); // play the click sound
					}
					return;
				}				
				
			}// end if TOUCH_UP
		}// end for		
	}// end update()

	
	/**
	 * inBounds() determines if a TouchEvent is within a particular rectangle.
	 * This rectangle is defined by the parameters x, y, width and height
	 * If the x and y coords of the TouchEvent are within these bounds, return = true
	 */
	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if ( (event.x > x) && (event.x < x + width - 1) && 
			 (event.y > y) && (event.y < y + height - 1) ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	@Override
	public void present(float deltaTime) {
		
		Graphics g = game.getGraphics();
		
		// Draw the background to 0,0 . Since this will clear the framebuffer, no clear() is necessary
		g.drawPixmap(Assets.background, 0, 0);
		// Draw the Logo at 32, 20
		g.drawPixmap(Assets.logo, 32, 20);
		// Draw the Play/Highscores/Help menu at 64, 220
		g.drawPixmap(Assets.mainMenu, 64, 220);
		
		// Recall that both sound buttons are in the same buttons file
		// Determine if sounds are enabled or disabled
		if (Settings.soundEnabled) {
			// if they are enabled, draw the top left portion of the buttons Pixmap
			g.drawPixmap(Assets.buttons, 0, 416, 0, 0, 64, 64);
		}
		else {
			// if they are not enabled, draw the top right portion of the buttons Pixmap
			g.drawPixmap(Assets.buttons, 0, 416, 64, 0, 64, 64);
		}
	}

	
	/**
	 * Since we can change the sound settings on this Screen, we want to make sure that
	 * the settings are persisted
	 */
	@Override
	public void pause() {
		// Write the current settings to the file in external storages		
		Settings.save(game.getFileIO());		
	}

	
	/**
	 * No purpose in MainMenuScreen
	 */
	@Override
	public void resume() {
	}

	/**
	 * No purpose in MainMenuScreen
	 */
	@Override
	public void dispose() {
	}
	
	
}
