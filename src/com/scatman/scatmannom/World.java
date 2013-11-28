package com.scatman.scatmannom;

import java.util.Random;

import android.util.Log;


/**
 * Methods: Constructor / PlaceStain / update
 * Responsibilities:
 * 		1) Keeps track of Scatman Nom ( in the form of snake instances ), as well as the Stains. There will only
 * 		   ever be a single stain in the world at a given time.
 *		2) Providing a method that will update Scatman Nom in a time-based manner. For example he should advance
 *		   by one cell every 0.5 seconds. This method will also check if Scatman Nom has eaten a stain or has bitten
 *		   himself.
 *		3) Keeping track of the score; this is basically just the number of stains eaten so far times 10
 *		4) Increasing the speed of Scatman Nom after every ten stains he's eaten.
 *		5) Keeping track of whether Scatman Nom is still alive. We'll us this to determine whether the game is over later on
 *		6) Creating a new stain after Scatman Nom eats the current one
 */
public class World {
	static final int WORLD_WIDTH = 10; // World's width (in cells)
	static final int WORLD_HEIGHT = 13; // World's height (in cells)
	static final int SCORE_INCREMENT = 10; // value we use to increment the score eat stain eaten
	static final float TICK_INITIAL = 0.5f; // the initial time interval used to advance Scatman Nom (tick)
	static final float TICK_DECREMENT = 0.05f; // the value we decrement the tick each 10 stains eaten
	
	public Snake snake;
	public Stain stain;
	public Powerup powerup;
	boolean isThereAPowerup = false;
	public boolean gameOver = false;
	public int score = 0; // current score
	
	
	boolean fields[][] = new boolean[WORLD_WIDTH][WORLD_HEIGHT];
	// produces random numbers where we will place the stain, and generate it's type
	Random random = new Random(); 
	// time accumulator variable to which we'll add the frame delta time 
	float tickTime = 0;
	// the current duration of a tick, which defines how often we advance Scatman Nom
	static float tick = TICK_INITIAL;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public World() {
		// Here we create a new Scatman Nom instance
		snake = new Snake();
		// and we place the very first random stain
		placeStain();
	}
	
	
	
	/**
	 * 
	 */
	private void placeStain() {				
		// We start by clearing the cell array, by setting all of the cells to false
		for ( int x = 0; x < WORLD_WIDTH; x++) {
			for ( int y = 0; y < WORLD_WIDTH; y++) {
				fields[x][y] = false;
			}
		}		
		
		// Here we set all of the cells occupied by the parts of the snake to true
		int len = snake.parts.size(); // Get the number of snake parts
		for ( int i = 0; i < len; i++) { // for each part
			SnakePart part = snake.parts.get(i);// get a handle on that part
			// and take it's actual x,y, and set the fields array to true at that same x, y
			fields[part.x][part.y] = true;
		}		
		
		// We are about to try to find an x and y to place the stain
		// Get a random x and random y value
		int stainX = random.nextInt(WORLD_WIDTH);
		int stainY = random.nextInt(WORLD_HEIGHT);
		// Here we scan the 
		while (true) {
			// If the random x and y we generated happens to be a cell set to false, we're done
			if (fields[stainX][stainY] == false) {
				break; // end the while loop
			}
			// Otherwise we increment the x coordinate by 1
			stainX += 1;
			// But  what if that new x is larger than the screen's width?
			if (stainX >= WORLD_WIDTH) {// in that case we...
				// Reset the x back to 0 ( left side of the screen )
				stainX = 0;
				// and just increment the y by one
				stainY += 1;
				// ACK! But what if the new y is larger than the screen's height?
				if (stainY >= WORLD_HEIGHT) {// in that case we...
					// Reset the y back to 0
					stainY = 0;
				}
			}
		}// end while (true)
		
		// That loop just ran until we had an open cell ( a "false" x and y in the fields array)
		// Since we're here, we must have found an open x and y. We use that x and y, in addition to 
		// a random integer (resulting in either 0, 1 or 2, *See the Stain class) to create a new
		// stain with those coordinates.
		stain = new Stain(stainX, stainY, random.nextInt(3));		
		
		
		int shouldMakePowerup = random.nextInt(7);
		if (shouldMakePowerup == 1) {
			Log.d("Problems", "should make powerup was apparently 1.");
		// There is a chance that the Powerup will be placed each time a stain is replaced
		int powerupX = random.nextInt(WORLD_WIDTH);
		int powerupY = random.nextInt(WORLD_HEIGHT);
		
		while (true) {
			// If the random x and y we generated happens to be a cell set to false, we're done
			if (fields[powerupX][powerupY] == false && ((stainX != powerupX) && (stainY != powerupY)) ) {
				break; // end the while loop
			}
			// Otherwise we increment the x coordinate by 1
			powerupX += 1;
			// But  what if that new x is larger than the screen's width?
			if (powerupX >= WORLD_WIDTH) {// in that case we...
				// Reset the x back to 0 ( left side of the screen )
				powerupX = 0;
				// and just increment the y by one
				powerupY += 1;
				// ACK! But what if the new y is larger than the screen's height?
				if (powerupY >= WORLD_HEIGHT) {// in that case we...
					// Reset the y back to 0
					powerupY = 0;
				}
			}
		}// end while (true)		
		
		powerup = new Powerup(powerupX, powerupY);
		Log.d("Problems", "a powerup was made");
		isThereAPowerup = true;
		} // end if should make powerup
	}
	
	
	/**
	 * update() is responsible for updating the world and all the objects in it based on the delta time we pass it
	 * This method will call each frame in the game screen so that the World is updated constantly.
	 */
	public void update(float deltaTime) {
		// Start by checking whether the game is over
		if (gameOver) {
			return; // if it is, then we don't need to update anything
		}
		
		// add the delta time to our accumulator
		tickTime += deltaTime;
		
		// this while loop will use up all the ticks that have accumulated 
		// (EX. if accumulated ticktime is 1.2, and one tick should take 0.5 seconds, we update the world twice, leaving 0.2 seconds 
		// in the accumulator
		while (tickTime > tick ) {
			// First subtract the tick interval from the accumulator
			tickTime -= tick;
			// Then we tell Scatman Nom to advance
			snake.advance();
			
			// Then we check if he has bitten himself
			if (snake.checkBitten()) { // and if he has...
				gameOver = true; // set the boolean gameOver to true
				return; // get out of this method
			}
			
			// We get a handle on the snake's head
			SnakePart head = snake.parts.get(0);
			
			// Now we check if the snake's head is in the same cell as the stain ( remember there is only 1 of each )
			if (head.x == stain.x && head.y == stain.y) {// if his head is in the same cell as a stain...
				// update the score by adding the current score to the score_increment variable
				score += SCORE_INCREMENT;
				// call the snakes eat() method which will simply add a new snake tail part to the end of the snake
				snake.eat();
				// Now we check if the snake is composed of as many parts as there are cells on the screen
				if(snake.parts.size() == WORLD_WIDTH * WORLD_HEIGHT) {// and if there is no more room...
					gameOver = true;// the game is over
					return;// return from the method
				}
				else {
					// if there IS still room; however, then just randomly place another stain by calling placeStain()
					placeStain();
				}
				
				// Now we check if the snake has eaten 10 more stains, which would constitute an "increase in game speed"
				// Actually it will just decrement the tick speed by TICK_DECREMENT, which should be 0.05f seconds
				if (score % 100 == 0 && tick - TICK_DECREMENT > 0) {
					tick -= TICK_DECREMENT;
				}
			}
			
			if ( isThereAPowerup == true) {
				if (head.x == powerup.x && head.y == powerup.y){
					int len = snake.parts.size();
					if(len > 2) {
						for (int i = len-1; i > 2; i--) {
							snake.parts.remove(i);
						}
					}
					this.isThereAPowerup = false;
					powerup = null;
				}
			}
			
			
		} // end while
				
		
	}
		
	
}
