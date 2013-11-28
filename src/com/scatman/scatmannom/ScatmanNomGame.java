package com.scatman.scatmannom;

import android.os.Bundle;

import com.scatman.framework.Screen;
import com.scatman.framework.impl.AndroidGame;


/**
 * We might consider adding android:hardwareAccelerated="true" under application, but it's an
 * android 3.0 thing.
 *
 * AndroidGame is an activity
 */
public class ScatmanNomGame extends AndroidGame {	
	// The loading screen will load all the assets of our game
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Screen getStartScreen() {
		return new LoadingScreen(this);		
	}    
	
	
}
