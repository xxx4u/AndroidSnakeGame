package com.scatman.framework;

import com.scatman.framework.Graphics.PixmapFormat;

public interface Pixmap {

	public int getWidth();
	
	public int getHeight();
	
	public PixmapFormat getFormat();
	
	public void dispose();
	
}
