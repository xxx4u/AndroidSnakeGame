package com.scatman.framework.impl;

import java.io.IOException;

import com.scatman.framework.Audio;
import com.scatman.framework.Music;
import com.scatman.framework.Sound;



import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Audio is responsible for creating sound and music instances from asset files
 */
public class AndroidAudio implements Audio {
	// The AssetManager is necessary for loading sound effects from asset files into the SoundPool
	// on a call to AndroidAudio.newSound()
	AssetManager assets;
	SoundPool soundPool;
	
	
	/**
	 * We pass the game's Activity to the constructor for two reasons:
	 * 1) it allows us to set the volume control of the media stream
	 * 2) It gives us an AssetManager instance, which we store in the corresponding class member
	 */
	public AndroidAudio(Activity activity) {
		// Make the volume controls modify the music stream channel
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load all of our assets into the assets variable
		this.assets = activity.getAssets();
		// soundPool can play back 20 sound effects in parallel to the music stream
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}
	
	/**
	 * Method that creates a new AndroidMusic instance
	 */
	@Override
	public Music newMusic(String filename) {
		try {
			// Open the sound effect from assets and store it in a variable
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			// The AndroidMusic() constructor takes an AssetFileDescriptor, 
			// which it uses to create an internal MediaPlayer. We just created the AFD above
			return new AndroidMusic(assetDescriptor);
		} catch (IOException e) {
			// We should only get here if we forget to add the music fiel to the assets/ directory
			// or if our music file contains false bytes.
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}
	
	/**
	 * Method that loads a sound effect from an asset into the SoundPool and returns
	 * an AndroidSound instance.
	 * 
	 * To load a sound effect, use the SoundPoo.load() method. We store our files in assets/, so we use the load()
	 * that takes an AssetFileDescriptor. We get an assetFileDescriptor from an AssetManager.
	 */
	@Override
	public Sound newSound(String filename) {
		try {
			// soundPool.load() method takes an assetFileDescriptor, so we create one now containing the sound effect file
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			// AndroidSound() constructor takes a sound ID, so we create one now for the sound effect file
			int soundId = soundPool.load(assetDescriptor,  0);
			// Create a new AndroidSound instance
			return new AndroidSound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}

}
