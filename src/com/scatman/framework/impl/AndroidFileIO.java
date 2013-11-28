package com.scatman.framework.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.scatman.framework.FileIO;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AndroidFileIO implements FileIO {
	Context context;
	AssetManager assets;
	String externalStoragePath;

	public AndroidFileIO(Context context, String folderName) {
		// Store the context, which is the gateway to everything in android
		this.context = context;
		// Create the AssetManager and load all of our assets into it
		this.assets = context.getAssets();
		// Note that we don't ever check if external storage is available. An exception will be thrown if not.
		//this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + folderName;
		File dir = new File(this.externalStoragePath);
        if(!dir.mkdirs()) { Log.d("Problems", "could not create directory: " + externalStoragePath); }
	}
	
	
	/**
	 * Method to get an InputStream for an asset
	 */
	@Override
	public InputStream readAsset(String fileName) throws IOException {
		return assets.open(fileName);
	}
	
	/**
	 * Method to get an InputStream for a file in the external storage
	 */
	@Override
	public InputStream readFile(String fileName) throws IOException {
		Log.d("Problems", "loadPath" + externalStoragePath + fileName);
		return new FileInputStream(externalStoragePath + fileName);
	}
	
	/**
	 * Method to get an OutputStream for a file in external storage
	 */
	@Override
	public OutputStream writeFile(String fileName) throws IOException {
		Log.d("Problems", "savePath" + externalStoragePath + fileName);
		return new FileOutputStream(externalStoragePath + fileName);
		
	}
	
	/**
	 * Methods that gets the SharedPreferences for the game
	 */
	public SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
