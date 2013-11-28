package com.scatman.scatmannom;

import java.util.List;

import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Input.TouchEvent;
import com.scatman.framework.Screen;


/**
 * HighscoreScreen name is self descriptive.
 * 		Each digit image is 20x32
 * 		The dot image is 10x32
 * 		numbers.png looks like this [0123456789.]
 * Say we want to draw "1. 100" at position 20, 100
 * 		It is done like so: 
 * 		game.getGraphics().drawPixmap(Assets.numbers, 20, 100, 20, 0, 20, 32);
 * 		(where to put the x, where to put y, the image to draw's x, y, width, and height)
 * Digit 1 has a width of 20 pixels. The next character of our string would have to be
 * rendered at (20+20, 100). In this case, the next character is the dot. Done like so:
 * 		game.getGraphics().drawPixmap(Assets.numbers, 40, 100, 200, 0, 10, 32);
 * Next we need to draw the space which should be at (20+20+10, 100). We will just assume the space's
 * width is the same as a character, and advance the x axis by 20 pixels. So the next character,
 * which will be the "1" in the "100" will be rendered at (20+20+10+20, 100)
 */
public class HighscoreScreen extends Screen {
	// This will store five lines, each containing a high score such as:
	// 1. 100
	// 3. 75
	String lines[] = new String[5];
	
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public HighscoreScreen(Game game) {
		super(game);
		
		// We store the strings of the five high-score lines in this string array member
		// This array doesn't just contain the high scores, it contains the entire line "2. 65"
		for(int i = 0; i < 5; i++) {
			lines[i] = "" + (i + 1) + ". " + Settings.highscores[i];
		}
		
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
		for(int i = 0; i < len; i++) {
			// Get a reference to each event so we can compare it and see if it's of type TOUCH_UP
			TouchEvent event = touchEvents.get(i);
			// See if the event is of type TOUCH_UP
			if (event.type == TouchEvent.TOUCH_UP) {
				
				// No need for inBounds() this time. The button is at the bottom left
				// of the screen. So we only care about the x and y of the button.
				// See if the event's x is less than 64 and if the y is greater than 416
				if(event.x < 64 && event.y > 416) {
					// If the sound is enabled, play the click button
					if(Settings.soundEnabled) { Assets.click.play(1); }
					// Then, set the current screen to be the MainMenuScreen again
					game.setScreen(new MainMenuScreen(game));
					// We're all done, the screen is about to be transitioned, so return
					return;
				}
			} // end if TOUCH_UP
		} // end for
		
	}
	
	
	
	
	/**
	 * present() draws our images to the screen. In this case, it draws the screen
	 * header "HIGHSCORES" to the screen, draws the back button, and finally
	 * calls drawText 5 times to draw each of the lines containing the player's 
	 * high scores, 1. 100, 2. 60, 3. 45 etc.
	 */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		
		// Draw the background image to fill the screen
		g.drawPixmap(Assets.background, 0, 0);
		// Draw the HIGHSCORES portion of the Assets.mainMenu image
		g.drawPixmap(Assets.mainMenu, 64, 20, 0, 42, 196, 42);
		
		// The starting high score line will have a y coordinate/position of 100
		int y = 100;
		
		// Now using our array of high score lines, we loop through all 5 high scores and 
		// call drawText to print them out. Each time the y coordinate increases by 50
		// The first line starts at 20, 100, next line is 20, 150
		for (int i = 0; i < 5; i++) {
			drawText(g, lines[i], 20, y); // draw the line
			y += 50; // draw the next line 50 pixels lower
		}
		
		// Finally we draw the back arrow button at 0, 416
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64);
	}



	/**
	 *
	 */
	private void drawText(Graphics g, String line, int x, int y) {
		// Get the number of characters in the string
		int len = line.length();
		
		// For every letter/digit in the string
		for (int i = 0; i < len; i++) {
			// get a handle on that character
			char character = line.charAt(i);
			
			// Now see if that character is a space and if it is...
			if (character == ' ') {
				// ... then just move the "cursor" right 20 pixels
				x += 20;
				// and get the next character
				continue;
			}
			
			// All of the letters and digits are in a single line in the .png image like "0123456789."
			// So we only need the x coordinate of the digit's location inside the .png
			// and we also need the width of that character as well. So here we create two variables
			// that will hold the x-coordinate and the width and give them default values of 0
			int srcX = 0;
			int srcWidth = 0;
			
			// To calculate the x and width, we determine if the character is a number or a "."
			if (character == '.') { // if it's a "."
				srcX = 200; // we know exactly where the "." is in the file
				srcWidth = 10; // the width of the dot is only 10 pixels
			} else { // if it's a number 
				// this formula will give us the x coordinate of the number inside the .png file, for
				// a .png file who's digits are exactly 20 pixels apart
				srcX = (character - '0') * 20; 
				srcWidth = 20; // the width of a number in this file is expected to be 20 pixels
			}
			
			// Draw that number using Graphics.drawPixmap()
			// (which asset to draw, x and y where to draw it, what x and y of the .png to start the extraction,
			// and the width and height of the chunk of the image to extract
			g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
			// After the character is drawn, we move the cursor right in the amount of the width of the character just drawn
			x += srcWidth;
			
		}// end the "for every character in the string" loop
		
	}// end drawText()
	
	
		
	/**
	 * This method is of no use to us in this screen, but we have to implement
	 * all of the interface's methods
	 */
	@Override
	public void pause() {		
	}

	/**
	 * This method is of no use to us in this screen, but we have to implement
	 * all of the interface's methods
	 */
	@Override
	public void resume() {		
	}

	/**
	 * This method is of no use to us in this screen, but we have to implement
	 * all of the interface's methods
	 */
	@Override
	public void dispose() {		
	}	
	
	
}
