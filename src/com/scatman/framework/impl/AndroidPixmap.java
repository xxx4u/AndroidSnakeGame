package com.scatman.framework.impl;

import android.graphics.Bitmap;
import com.scatman.framework.Graphics.PixmapFormat;
import com.scatman.framework.Pixmap;

/**
 * This class is just a wrapper for a Bitmap and it's desired format.
 * It lets us create, use, and find out information about our pixmaps.
 * You could argue this is not necessary, but then again the first time I took
 * 241 I wrote the entire game in one file, and look how that turned out... :(
 */
public class AndroidPixmap implements Pixmap{
	Bitmap bitmap;
	PixmapFormat format;	
	
	public AndroidPixmap(Bitmap bitmap, PixmapFormat format) {
		this.bitmap = bitmap;
		this.format = format;
	}
		
	@Override
	public int getWidth() {
		return bitmap.getWidth();
	}
		
	@Override
	public int getHeight() {
		return bitmap.getHeight();
	}
	
	@Override
	public PixmapFormat getFormat() {
		return format;
	}
		
	@Override
	public void dispose() {
		bitmap.recycle();
	}
	
}
