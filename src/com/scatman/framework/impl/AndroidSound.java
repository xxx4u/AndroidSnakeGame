package com.scatman.framework.impl;

import com.scatman.framework.Sound;

import android.media.SoundPool;

/**
 * AndroidSound lets us playback sound effects that are stored in RAM
 */
public class AndroidSound implements Sound {
	int soundId;
	SoundPool soundPool;
	
		
	public AndroidSound(SoundPool soundPool, int soundId) {
		this.soundId = soundId;
		this.soundPool = soundPool;
	}
	
	
	@Override
	public void play(float volume) {
		// Play the sound effect, arguments:
		// 1) argument is the handle we received from the SoundPool.load() method
		// 2&3) two parameters specify the volume to be used for the left and right channels, between 0 and 1
		// 4) is the priority, we just set to 0
		// 5) is looping, which is not reccomended so we just use 0
		// 6) is the playback rate, anyting higher than 1 will allow the sound effect to be played back faster, under 1 is slower
		soundPool.play(soundId,  volume, volume, 0, 0, 1);
	}
	
	
	@Override
	public void dispose() {
		// Do this when we no longer need a sound effect and want to free some memory
		soundPool.unload(soundId);
	}
	
}
