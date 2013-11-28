package com.scatman.framework.impl;

import java.io.IOException;

import com.scatman.framework.Music;


import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;



/**
 * AndroidMusic is responsible for streaming bigger music files from the disk to the audio card * 
 * 	 >> OnCompletionListener is an interface that provides us with a means of informing ourselves
 * 		about when a MediaPlayer has stopped playing back a music file. If this happens, the MediaPlayer
 * 		needs to be prepared again before we can invoke any of the other methods.
 */
public class AndroidMusic implements Music, OnCompletionListener {
	MediaPlayer mediaPlayer;
	// We can only called MediaPlayer.start()/stop()/pause() when the MdiaPlayer is prepared
	boolean isPrepared = false;
	
	
	/**
	 * Primary Constructor
	 */
	public AndroidMusic(AssetFileDescriptor assetDescriptor) {
		mediaPlayer = new MediaPlayer();
		
		try {
			// Tell the mediaPlayer what file to play back. The AssetFileDescriptor is passed in as an argument in this case.
			// 1) The file Descriptor
			// 2) In reality, assets are all stored in a single file, for the mediaplayer to get to the start of the file, we give it an offset
			// 3) The length of the audioFile
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(), assetDescriptor.getLength());			
			// This will actually open the file and check whether it can be read and played back by the MediaPlayer instance
			// After prepare(), we can freely play, pause, stop, loop and change the volume
			mediaPlayer.prepare();
			// Set our boolean to true, since we just called prepare()
			isPrepared = true;
			// Register an onCompletionListener
			mediaPlayer.setOnCompletionListener(this);
		} catch(Exception e) {
			throw new RuntimeException("Couldn't load music");
		}
		
	}
	
	

	@Override
	public void dispose() {
		// The later call to .release() will throw a runtime exception if the mediaPlayer is playing when you call it.
		// So check if it is playing...
		if( mediaPlayer.isPlaying() ) {
			// ... and if it is then stop it
			mediaPlayer.stop();
		}
		// When we are done with the MediaPlayer instance, we make sure that all the resources it takes up are released.
		mediaPlayer.release();
	}
	
	
	/**
	 * Very straightforward. Check if this mediaPlayer instance is looping
	 */
	@Override
	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}
	
	
	/**
	 * Note that this will return false if the MediaPlayer is paused but not stopped
	 */
	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}
	
	
	@Override
	public boolean isStopped() {
		return !isPrepared;
	}
	
	
	@Override
	public void pause() {
		if( mediaPlayer.isPlaying() ) {
			mediaPlayer.pause();
		}
	}
	
	
	@Override
	public void play() {
		// If the MediaPlayer is already playing, we simply return from the function
		if ( mediaPlayer.isPlaying() ) {
			return;
		}
		
		try {
			synchronized (this) {
				// We check to see if the mediaPlayer is already prepared based on our flag
				if (!isPrepared) {
					// And we prepare it if needed
					mediaPlayer.prepare();
				}
				// Then we start the playback
				mediaPlayer.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * setLooping can be called in any state of the media player
	 */
	@Override
	public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
    }

	/**
	 * setVolume can be called in any state of the media player
	 */
	@Override
    public void setVolume(float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    
    /**
     * stop() stops the MediaPlayer and sets the isPrepared flag in a synchronized block
     */
	@Override
    public void stop() {
        mediaPlayer.stop();
        
        synchronized (this) {
            isPrepared = false;
        }
    }

    
    /**
     * method sets the isPrepared flag in a synchronized block so that the other methods don't start throwing 
     * exceptions out of the blue.
     */
	@Override
    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            isPrepared = false;
        }
    }
	
	
	
}
