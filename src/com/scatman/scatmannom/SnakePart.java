package com.scatman.scatmannom;

import com.scatman.framework.Pixmap;


/**
 * This is a single class that represents both the head and the tail parts
 * Each part occupies a single cell in Scatman Nom's World
 * 
 * model of the mvc design pattern
 */
public class SnakePart {
	public int x, y;
	
	// These were to help me make the body parts follow suit
	public int type = 0;
	public static final int BODY_TYPE_LEFTRIGHT = 0; // left right
	public static final int BODY_TYPE_UPDOWN = 1; // up down
	
	public SnakePart(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	
	/**
	 * type must be either BODY_TYPE_LEFTRIGHT or BODY_TYPE_UPDOWN
	 * This method helped me make the body parts follow suit
	 */
	public void setType(int type) {
		this.type = type;		
	}
	
	
	
	/**
	 * This will be called when GameScreen tries to draw a body part.
	 * This method was necessary for the body parts to follow suit
	 */
	public Pixmap getAssetType() {
		
		if(this.type == BODY_TYPE_LEFTRIGHT) {
			return Assets.tail; // left right
		}
		else {
			return Assets.tail2; // up down
		}
		
	}
	
	
}
