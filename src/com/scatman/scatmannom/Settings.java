package com.scatman.scatmannom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.util.Log;

import com.scatman.framework.FileIO;


/**
 * Settings is a class that stores the player's top 5 highest scores,
 * and whether or not they prefer to play with sound on or off.
 * Settings are loaded in from a file in external storage called "mrnom"
 * Settings are also saved to this file.
 * When adding a high score to the highscores static array, they are sorted in descending order
 */
public class Settings {
	// Default Values for sound and highscores
	public static boolean soundEnabled = true;
	public static int[] highscores = new int[] { 100, 80, 50, 30, 10 };
	
	
	
	/**
	 * load() tries to load the settings from a file called ".mrnom" from external storage
	 * Very first line should be a boolean string for "true" or "false" if sound is enabled
	 * Subsequent 5 lines should be scores
	 */
	public static void load(FileIO files) {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile("settings.txt")));
			
			// Read in the first line which should be a 0 or 1, for soundEnabled
			soundEnabled = Boolean.parseBoolean(in.readLine());
			
			// Fill the array of high scores with the five scores listed in the file
			for (int i = 0; i < 5; i++) {
				highscores[i] = Integer.parseInt(in.readLine());
			}
		} catch (IOException e) {
			// :(It's ok we have defaults
			Log.d("Problems", "Settings.load.catch(IOException)");
		} catch (NumberFormatException e) {
			// :/ It's ok, defaults save our day
			Log.d("Problems", "Settings.load.catch(NumberFormatException)");
		} finally {
			try {				
				// Close the Buffered reader
				if (in != null) { in.close(); } 				
			} catch (IOException e) {
				Log.d("Problems", "Settings.load.finally.catch(IOException)");
			}
		} // end finally
	}// end load()
	
	
	
	/**
	 * save() saves the current settings to mnt/sdcard/.mrnom
	 */
	public static void save(FileIO files) {
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new OutputStreamWriter(files.writeFile("settings.txt")));			
			out.write(Boolean.toString(soundEnabled) + "\n");
			
			for ( int i = 0; i < 5; i++ ) {
				out.write(Integer.toString(highscores[i]) + "\n");
			}
			
		} catch (IOException e) {
			Log.d("Problems", "Settings.save.catch(IOException)");
		}
		finally {
			try {
				if (out != null) { out.close(); }
			} catch (IOException e) {
				Log.d("Problems", "Settings.save.finally.catch(IOException)");
			}
		}// end finally
	}// end save()
	
	
	
	/**
	 * Use this method to add a new score to the high scores, automatically re-sorting
	 * them depending on the value we want to insert
	 */
	public static void addScore(int score) {
		for (int i = 0; i < 5; i++) {
			
			if (highscores[i] < score) {
				
				for (int j = 4; j > i; j--) {
					highscores[j] = highscores[j-1];
				}
					highscores[i] = score;
					break;							
			} // end if
		}// end for i
	}// end addScore
	
}
