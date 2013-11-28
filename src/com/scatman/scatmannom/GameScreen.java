package com.scatman.scatmannom;

import java.util.List;

import android.graphics.Color;
import android.util.Log;

import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Input.TouchEvent;
import com.scatman.framework.Pixmap;
import com.scatman.framework.Screen;




/**
 * The game screen can be in one of four states:
 * 
 * 	1) Waiting for the user to confirm that he or she is ready
 * 		>> in the ready state, we simply ask the user to touch the screen to start the game
 * 	2) Running the game
 * 		>> in the running state, we update the world, render it, and also tell the snake to
 * 		   to turn left or right when the player presses one of the buttons at the bottom of the screen
 * 	3) Waiting in a paused state
 * 		>> in the paused state, we simply show two options: one to resume the game, and one to quit it
 * 	4) Waiting for the user to click a button in the game-over state
 * 		>> in the game-over state, we tell the user that the game is over and provide them with a button
 * 		   to touch so that the user can get back to the main menu
 */
public class GameScreen extends Screen {
	// These will be the four possible states of the game (listed above in the comments)
	enum GameState {
		Ready,
		Running,
		Paused,
		GameOver
	}
	// The default state is obviously going to be Ready
	GameState state = GameState.Ready;
	// Many reasons to have this, including the game's score, which we will save in the two methods below
	World world;
	// The currently displayed score in the forms of an integer and a String. The reason we have both of these is
	// because we don't want to create new strings constantly from the World.score member each time we draw the score.
	// Instead we cache the string and only create a new one when the score changes
	int oldScore = 0;
	String score = "0";
	
	
	
	/**
	 * Primary Constructor
	 * The game screen will be in the ready state after the constructor returns to the caller
	 */
	public GameScreen(Game game) {
		super(game);
		world = new World();
	}
	
	
	
	/**
	 * all update() does is fetch the touch events and keyevents from the input module, 
	 * then delegate the update to one of the four update methods implemented down further
	 */
	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		if (state == GameState.Ready) {
			updateReady(touchEvents);
		}
		if (state == GameState.Running) {
			updateRunning(touchEvents, deltaTime);
		}
		if (state == GameState.Paused) {
			updatePaused(touchEvents);
		}
		if (state == GameState.GameOver) {
			updateGameOver(touchEvents);
		}
	}
	
	
	/**
	 * This will be called when the screen is in the ready state. All it does is check if the screen
	 * was touched. If that's the case, it changes the state to running
	 */
	private void updateReady(List<TouchEvent> touchEvents) {
		if(touchEvents.size() > 0) {
			state = GameState.Running;
			// This fixes the bug where the tick speed wouldn't reset each game
			World.tick = World.TICK_INITIAL;			
		}
	}
	
	
	/**
	 * 
	 */
	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
		// We make an integer to hold the number of touchEvents we have to loop through 
		int len = touchEvents.size();
		
		// We loop through each of the touch events until we find one with the type
		// TouchEvent.TOUCH_UP and TouchEvent.TOUCH_DOWN
		for (int i = 0; i < len; i++) {
			// Get a reference to each event so we can compare it and see if it's of type TOUCH_UP and TOUCH_DOWN
			TouchEvent event = touchEvents.get(i);
			
			// See if the event is of type TOUCH_UP
			if(event.type == TouchEvent.TOUCH_UP) {
				// and if it is of type TOUCH_UP, then see if it took place in the top left corner of the screen
				// which would mean the "Paused" button was pressed
				if(event.x < 64 && event.y < 64) {
					// if the "Paused" button was pressed, then play and click sound effect
					if(Settings.soundEnabled) { Assets.click.play(1); }
					// and set the GameState to paused
					state = GameState.Paused;
					// we are done here to exit the updateRunning() method
					return;
				}
			}
			
			// See if the event is of type TOUCH_DOWN
			if(event.type == TouchEvent.TOUCH_DOWN) {
				// and if a finger was pressed on the screen, then see if...
				// see if the turn left button was pressed at the bottom left of the screen, and call the corresponding method
				if(event.x < 64 && event.y > 416) { world.snake.turnLeft(); }
				// see if the turn right button was pressed at the bottom right of the screen, and call the corresponding method
				if(event.x > 256 && event.y > 416) { world.snake.turnRight(); }
			}			
			
		}// end for loop
		
		
		// tell the world to update itself. This could signal that the game is over by setting the gameOver flag to true
		// If this happens, the following if statement is going to catch that and end the game
		world.update(deltaTime);
		
		// This if statement will check to see if the world.gameOver flag is set to true. This will indicate that the 
		// snake has bitten itself, and the gameOver screen should be displayed
		if(world.gameOver) {
			// since the gameOver flag was true, play the sound effect for the snake biting itself
			if(Settings.soundEnabled) { Assets.bitten.play(1); }
			// then set the current state to be GameOver
			state = GameState.GameOver;
		}
		
		// The world.score variable will be the most accurate. See if the local oldScore is up to date. If it is not
		// up to date, this indicates that the snake has eaten something. The score should be updated, and a sound effect played
		if(oldScore != world.score) {// and if it's not...
			// then make ours up to date by copying the value
			oldScore = world.score;
			// score is the string representation of oldscore/world.score, so just make it up to date as well
			score = "" + oldScore;
			// play the sound effect for the snake eating it's food
			if(Settings.soundEnabled){ Assets.eat.play(1); }
		}
	}
	
	
	
	/**
	 * updatePaused() just checks whether one of the menu options was touched and changes
	 * the state accordingly
	 */
	private void updatePaused(List<TouchEvent> touchEvents) {
		// We make an integer to hold the number of touchEvents we have to loop through 
		int len = touchEvents.size();
		
		// We loop through each of the touch events until we find one with the type TOUCH_UP
		for(int i=0; i<len; i++) {
			// Get a handle on the current TouchEvent
			TouchEvent event = touchEvents.get(i);
			
			// See if the event is of type TOUCH_UP
			if(event.type == TouchEvent.TOUCH_UP) {
				
				if(event.x > 80 && event.x <= 240) {
					// if the TOUCH_UP event occurred at this location then "Resume" was pressed
					if(event.y > 100 && event.y <= 148) {
						if(Settings.soundEnabled) { Assets.click.play(1); }
						state = GameState.Running;
						return;
					}
					// if the TOUCH_UP event occurred at this location then "Quit" was pressed
					if(event.y > 148 && event.y < 196) {
						if(Settings.soundEnabled) { Assets.click.play(1); }
						game.setScreen(new MainMenuScreen(game));
						return;
					}
					
				}// end if 80
			}// end if TOUCH_UP			
		}// end for loop
	}// end updatePaused()
		
		
		
		
		/**
		 * updateGameOver() checks if the button in the middle of the screen was pressed
		 * if it has been pressed, then we initiate a screen transition back to the main menu screen
		 */
	private void updateGameOver(List<TouchEvent> touchEvents) {
		// We make an integer to hold the number of touchEvents we have to loop through
		int len = touchEvents.size();
		
		// We loop through each of the touch events until we find one with the type TOUCH_UP
		for(int i = 0; i < len; i++) {
			
			// Get a handle on the current TouchEvent
			TouchEvent event = touchEvents.get(i);
			
			// See if the event is of type TOUCH_UP
			if(event.type == TouchEvent.TOUCH_UP){
				// if the TOUCH_UP event occurred at the following location then the button was pressed to return
				// to the main menu screen
				if(event.x >= 128 && event.x <= 192 && event.y >= 200 && event.y <= 264) {
					if(Settings.soundEnabled) { Assets.click.play(1); }
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}// end for
	}
	
	
	
	/**
	 * THE FIRST RENDERING METHOD
	 * It draws the background, renders the world, and calls the other drawing method based on the current state
	 * Finally it updates the score on the screen
	 */
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		
		// Draw the background image, because that's obviously needed in all states
		g.drawPixmap(Assets.background, 0, 0);
		
		// render the world
		drawWorld(world);
		
		// Draw the respective drawing method for the state we are in
		if (state == GameState.Ready)   { drawReadyUI();    }
		if (state == GameState.Running) { drawRunningUI();  }
		if (state == GameState.Paused)  { drawPausedUI();   }
		if (state == GameState.GameOver){ drawGameOverUI(); }
		
		// Finally update the score's text at the bottom of the screen
		drawText(g, score, g.getWidth() / 2 - score.length()*20 / 2, g.getHeight() - 42 );
	}
	
	
	
	/**
	 * This drawing method actually puts the snake, and the stain on the screen
	 */
	public void drawWorld(World world) {
		Graphics g = game.getGraphics();
		
		Snake snake = world.snake;
		SnakePart head = snake.parts.get(0);
		Stain stain = world.stain;
		Powerup powerup = world.powerup;
		
		// **** THE STAIN ****
		// Create a Pixmap for the stain, and then depending on the stains's type, assign the pixmap to an asset
		Pixmap stainPixmap = null;
		if(stain.type == Stain.TYPE_1){ stainPixmap = Assets.stain1; } 
		if(stain.type == Stain.TYPE_2){ stainPixmap = Assets.stain2; }
		if(stain.type == Stain.TYPE_3){ stainPixmap = Assets.stain3; }		
		// Determine the x and y pixel coordinates to draw the stain pixmap, depending on it's x and y cell position
		int x = stain.x * 32;
		int y = stain.y * 32;
		// then actually draw the pixmap
		g.drawPixmap(stainPixmap, x, y);
		
		
		// **** THE POWERUP ****
		// Draw a powerup if there should be one
		if(world.isThereAPowerup == true) {
			Log.d("Problems", "isThereAPowerup was true, so we are inside the if()");
			Pixmap powerupPixmap = null;
			x = powerup.x * 32;
			y = powerup.y * 32;
			powerupPixmap = Assets.clearstain;
			g.drawPixmap(powerupPixmap, x, y);
		}
		
		
		// **** THE SNAKE'S TAIL ****
		// Get the number of parts the snake is made of
		int len = snake.parts.size();
		// modify the orientation of each body part, set the types of the parts
		for (int i = 1; i<len; i++) {
			if(snake.parts.get(i-1).y > snake.parts.get(i).y || snake.parts.get(i-1).y < snake.parts.get(i).y) {
				snake.parts.get(i).setType(SnakePart.BODY_TYPE_UPDOWN);
			}
			else {
				snake.parts.get(i).setType(SnakePart.BODY_TYPE_LEFTRIGHT);
			}
		}
		
		// for each of the tail parts (excluding the head)
		for(int i = 1; i < len; i++) {
			SnakePart part = snake.parts.get(i); // get a handle on each of the parts
			x = part.x * 32; // determine it's x pixel coordinate
			y = part.y * 32; // determine it's y pixel coordinate
			g.drawPixmap(snake.parts.get(i).getAssetType(), x, y); // now draw the tail part
		}
		
		
		// **** THE SNAKE'S HEAD ****
		// Create a pixmap to represent the snake's head
		Pixmap headPixmap = null;
		// Depending on what direction the snake is facing, assign the head pixmap to an image asset
		if(snake.direction == Snake.UP){ headPixmap = Assets.headUp; }
		if(snake.direction == Snake.LEFT){ headPixmap = Assets.headLeft; }
		if(snake.direction == Snake.DOWN){ headPixmap = Assets.headDown; }
		if(snake.direction == Snake.RIGHT){ headPixmap = Assets.headRight; }
		// Determine the x and y pixel coordinates to draw the head pixmap, depending on it's x and y cell position
		x = head.x * 32 + 16;
		y = head.y * 32 + 16;
		// then actually draw the snake head pixmap
		g.drawPixmap(headPixmap, x - headPixmap.getWidth() / 2, y - headPixmap.getHeight() / 2);
	}
	
	
	
	
	/**
	 * This method just draws the "ready? touch screen to start" image
	 * It also draws the black line that separates the playing field from the control buttons
	 */
	private void drawReadyUI() {
		Graphics g = game.getGraphics();
		
		g.drawPixmap(Assets.ready, 47, 100); // draw the ready image
		g.drawLine(0, 416, 480, 416, Color.BLACK); // draw the black separator line
	}
	
	
	
	
	/**
	 * drawRunningUI() draws the paused button, and the left and right turn buttons
	 * It also draws the black line that separates the playing field from the control buttons
	 */
	private void drawRunningUI() {
		Graphics g = game.getGraphics();
		
		g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64);
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}
	
	
	
	/**
	 * drawPausedUI() draws the paused image
	 * It also draws the black line that separates the playing field from the control buttons
	 */
	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		
		g.drawPixmap(Assets.pause, 80, 100);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
	}
	
	
	/**
	 * drawGameOverUI() draws the game over image as well as the button that will return the user to the main menu
	 * It also draws the black line that separates the playing field from the control buttons
	 */
	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		
		g.drawPixmap(Assets.gameOver, 62, 100);
		g.drawPixmap(Assets.buttons, 128, 200, 0, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
	}
	
	
	/**
	 * drawText is the same as the one in the HighScoreScreen
	 */
	public void drawText(Graphics g, String line, int x, int y) {
		// Get the number of characters in the string
		int len = line.length();
		
		// For every letter/digit in the string
		for(int i = 0; i < len; i++) {
			// get a handle on that character
			char character = line.charAt(i);
			
			// Now see if that character is a space and if it is...
			if(character == ' ') {
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
			if(character == '.') { // if it's a "."
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
			
		} // end the "for every character in the string" loop
	}// end drawText()
	
	
	
	/**
	 * This get's called when the activity is paused or the game screen is replaced by another screen.
	 * This is a great place to save our settings
	 */
	@Override
	public void pause() {
		// First set the state of the game to paused. If pause() got called because the activity got paused
		// this will ensure that the user is asked if they want to resume the game when they return
		if(state == GameState.Running) { state = GameState.Paused; }
		// If the game screen happens to be in a gameOver state...)
		if(world.gameOver) {
			// we attempt to add the score to the high scores ( if it's high enough )
			Settings.addScore(world.score);
			// then we save the settings to the external storage
			Settings.save(game.getFileIO());
		}		
	}// end pause()
	

	
	
	@Override
	public void resume() {
		
	}
	
	
	
	@Override
	public void dispose() {
		
	}
	
	
	
	
}
