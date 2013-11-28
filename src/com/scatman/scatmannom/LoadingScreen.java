package com.scatman.scatmannom;


import android.util.Log;

import com.scatman.framework.Game;
import com.scatman.framework.Graphics;
import com.scatman.framework.Graphics.PixmapFormat;
import com.scatman.framework.Screen;


/**
 * The purpose of a LoadingScren is to load all of the assets into their static members.
 * Then, the LoadingScreen will load the settings.
 * Finally, the LoadingScreen will pass control to the main menu screen
 */
public class LoadingScreen extends Screen {

	public LoadingScreen(Game game) {
		super(game);
	}
	
	
	@Override
	public void update(float deltaTime) {
		
		Graphics g = game.getGraphics();
		try {
		// Load the images and background
   //!!     Assets.background = g.newPixmap("background.png", PixmapFormat.RGB565);
        Assets.background = g.newPixmap("brickbackground.png", PixmapFormat.RGB565);
   //!!    Assets.logo = g.newPixmap("logo.png", PixmapFormat.ARGB4444);
        Assets.logo = g.newPixmap("logo1.png", PixmapFormat.ARGB4444);
        Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
        Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
        Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
        Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
        Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
        Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
        Assets.ready = g.newPixmap("ready.png", PixmapFormat.ARGB4444);
        Assets.pause = g.newPixmap("pausemenu.png", PixmapFormat.ARGB4444);
        Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
        // The originals
//        Assets.headUp = g.newPixmap("headup.png", PixmapFormat.ARGB4444);
//        Assets.headLeft = g.newPixmap("headleft.png", PixmapFormat.ARGB4444);
//        Assets.headDown = g.newPixmap("headdown.png", PixmapFormat.ARGB4444);
//        Assets.headRight = g.newPixmap("headright.png", PixmapFormat.ARGB4444);
        Assets.headUp = g.newPixmap("headup1.png", PixmapFormat.ARGB4444);
        Assets.headLeft = g.newPixmap("headleft1.png", PixmapFormat.ARGB4444);
        Assets.headDown = g.newPixmap("headdown1.png", PixmapFormat.ARGB4444);
        Assets.headRight = g.newPixmap("headright1.png", PixmapFormat.ARGB4444);
        //Assets.tail = g.newPixmap("tail.png", PixmapFormat.ARGB4444);
        Assets.tail = g.newPixmap("body1leftright.png", PixmapFormat.ARGB4444);
        Assets.tail2 = g.newPixmap("body1updown.png", PixmapFormat.ARGB4444);
        Assets.stain1 = g.newPixmap("stain1.png", PixmapFormat.ARGB4444);
        Assets.stain2 = g.newPixmap("stain2.png", PixmapFormat.ARGB4444);
        Assets.stain3 = g.newPixmap("stain3.png", PixmapFormat.ARGB4444);
        Assets.clearstain = g.newPixmap("clearstain.png", PixmapFormat.ARGB4444);
        
        // Load the three sound effects
        Assets.click = game.getAudio().newSound("click.ogg");
        Assets.eat = game.getAudio().newSound("eat.ogg");
        Assets.bitten = game.getAudio().newSound("bitten.ogg");
        
        Assets.bgMusic = game.getAudio().newMusic("ScatmanGoodEightBit.mp3");
        
		}
		catch(Exception e) {
			Log.d("Problems", "In the assets file, couldn't load something");
		}
		
        // We load the settings
        Settings.load(game.getFileIO());
				
        // We initiate a screen transition to a screen called MainMenuScreen which will take over 
        // execution from this point on
        game.setScreen(new MainMenuScreen(game));
	}


	@Override
	public void present(float deltaTime) {
		
	}


	@Override
	public void pause() {
		
	}


	@Override
	public void resume() {
		
	}


	@Override
	public void dispose() {
		
	}
}
