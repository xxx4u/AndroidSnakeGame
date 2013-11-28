package com.scatman.scatmannom;


/**
 * model of the MVC design pattern
 *
 */
public class Stain {
	// public static final constants that encode the type of stain
	public static final int TYPE_1 = 0;
	public static final int TYPE_2 = 1;
	public static final int TYPE_3 = 2;
	// The x and y coordinates in Mr. Nom's world (remember it is divided into cells)
	public int x, y;
	// this type will be one of the constants previously defined
	public int type;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public Stain(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
}
