package com.scatman.scatmannom;

import java.util.ArrayList;
import java.util.List;



/**
 * model of the mvc design pattern 
 */
public class Snake {
	// These constants encode the direction of Scatman Nom
	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;
	
	// This List will hold all of the SnakeParts. The first item in this list will be the head.
	public List<SnakePart> parts = new ArrayList<SnakePart>();
	// What direction Scatman Nom is heading
	public int direction;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 * Initially creates Scatman Nom's head and two addition tail parts in
	 * more or less, the center of the screen.
	 */
	public Snake() {
		// Initial direction to move will be set to Snake.UP
		direction = UP;
		parts.add(new SnakePart(5, 6));
		parts.add(new SnakePart(5, 7));
		parts.add(new SnakePart(5, 8));
	}
	
	
	
	/**
	 * This will be called typically if the user presses the left button.
	 * We just increment the direction value by one.
	 * The range of acceptable constants is 0,1,2,3, so if incrementing this direction value causes it to 
	 * become greater than the largest one (3), then we wrap it around and make it 0
	 */
	public void turnLeft() {
		direction += 1;
		if(direction > RIGHT) {
			direction = UP;
		}
		
	}
	
	/**
	 * This will be called typically if the user presses the right button.
	 * We just decrement the direction value by one.
	 * The range of acceptable constants is 0,1,2,3, so if decrementing this direction value causes it to 
	 * become less than the lowest one (0), then we wrap it around and make it 3
	 */
	public void turnRight() {
		direction -= 1;
		if (direction < UP) {
			direction = RIGHT;
		}
	
	}
	
	
	/**
	 * We just add a new snakepart to the end of the list. This new part will
	 * have the same position as the current end part. The next time
	 * Scatman Nom advances, these two parts will move apart.
	 */
	public void eat() {
		SnakePart end = parts.get(parts.size()-1);
		parts.add(new SnakePart(end.x, end.y));
	}
	
	
	/**
	 * This method moves Scatman Nom, including his head and all of tail parts.
	 * Note that this method does not move him in terms of where he is in pixels,
	 * it moves him based on his location on the grid. The top left of the grid
	 * is 0,0 and the bottom right of the grid is 9,12
	 */
	public void advance() {
		
		
		// We get a handle on the head snake part
		SnakePart head = parts.get(0);
		
		// We get the number of snake parts from the list minus the head, and store it in len
		int len = parts.size() - 1;
		
		// We move each part to the position of the part before it (in the list), starting with the last part
		// We also exclude the head from this process
		for( int i = len; i > 0; i-- ) {
			SnakePart before = parts.get(i-1);
			SnakePart part = parts.get(i);
			part.x = before.x;
			part.y = before.y;
		}
		
		// Then we move the head according to Scatman Nom's current direction
		if(direction == UP){
			head.y -= 1;
		}
		if(direction == LEFT){
			head.x -= 1;
		}
		if(direction == DOWN){
			head.y += 1;
		}
		if(direction == RIGHT){
			head.x += 1;
		}
		
		// Finally we perform some checks to make sure Scatman Nom doesn't go outside the world.
		// If he does, we just make him come out on the other side of his world.
		if (head.x < 0) {
			head.x = 9;
		}		
		if (head.x > 9) {
			head.x = 0;
		}		
		if (head.y < 0) {
			head.y = 12;
		}		
		if (head.y > 12) {
			head.y = 0;
		}
		
	}
	
	
	/**
	 * This method just checks if Scatman Nom has bitten any of the parts of his tail.
	 * All it does it check that no tail part is at the same position as the head.
	 */
	public boolean checkBitten() {
		// Get the number of parts in the snake parts list
		int len = parts.size();
		// get a handle on Scatman nom's head (first item in the list)
		SnakePart head = parts.get(0);
		
		// compare every single tail part's location, with scatman nom's head position
		// If we find that the head and any tail part occupy the same position, return true
		for(int i = 1; i < len; i++) {			
			SnakePart part = parts.get(i);
			if(part.x == head.x && part.y == head.y){ return true; }			
		}
		
		// if we get here, the head was not in the same position as any of the tail parts
		return false;
	}
	
	
}
