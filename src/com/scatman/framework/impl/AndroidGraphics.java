package com.scatman.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

import com.scatman.framework.Graphics;
import com.scatman.framework.Pixmap;


/**
 * 
 * Bitmaps are on 178
 */
public class AndroidGraphics implements Graphics {
	// The assetManager will be used to load bitmap instances
	AssetManager assetManager;
	// This will represent our artificial framebuffer. BITMAPS ARE 178
	Bitmap frameBuffer;
	// The Canvas will be used to draw to the artificial framebuffer
	Canvas canvas;
	// We need a Paint member for some of the drawing methods
	Paint paint;
	// We need these two rectangles for implementing the AndroidGraphics.drawPixmap() methods
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
		this.assetManager = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
	}
	
	
	/**
	 * newPixmap() method tries to load a Bitmap from an asset file, using the specified format
	 */
	@Override
	public Pixmap newPixmap(String fileName, PixmapFormat format) {
		// Config holds the constants for picture formats
		Config config = null;
		
		// We translate the PixmapFormat into one of the Config constants
		if(format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if (format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else 
			config = Config.ARGB_8888;
		
		// We create a new Options instance and set our preferred color format
		// I personally think this is wrong. I might use the 179 example instead with BitmapFactory.Options
		Options options = new Options();
		options.inPreferredConfig = config;
				
		InputStream inputStream = null;
		Bitmap bitmap = null;
		
		try {
			// Open the file with an inputstream, then ust BitmapFactory to interpret the stream as a bitmap
			inputStream = assetManager.open(fileName);
			bitmap = BitmapFactory.decodeStream(inputStream);
			// If nothing was loaded then there must be a problem, throw an exception
			if (bitmap == null) {
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
			}
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} finally {
			// Then just close the inputstream, we no longer need it
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
		
		// Here we try to check to see what PixmapFormat the BitmapFactory chose to use to decode the image
		// *Remember that the BitmapFacotry might decide to ignore our desired color format
		if (bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if (bitmap.getConfig() == Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;
		
		// Then we construct a new AndroidPixmap instance using the bitmap we decoded and the chosen PixmapFormat
		return new AndroidPixmap(bitmap, format);
	} // end newPixmap
	
	
	/**
	 * clear() method extracts the red, green and blue components of the 32-bit ARGB color parameter/argument
	 * and calls the Canvas.drawRGB() method, which clear our artificial framebuffer with that color.
	 * It ignores any alpha value of the specified color, so we don't have to extract it
	 */
	@Override
	public void clear(int color) {
		canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
	}
	
	/**
	 * drawPixel() draws a pixel of our artificial framebuffer via the drawPoint method.
	 * We set the color of our paint member variable to the parameter's, then pass the color,
	 * and the two coordinates to the drawing method.
	 */
	@Override
	public void drawPixel(int x, int y, int color) {
		paint.setColor(color);
		canvas.drawPoint(x, y, paint);
	}
	
	/**
	 * The drawLine() methods draws the given line of the artificial framebuffer, using the paint
	 * member to specify the color when calling the drawLine method
	 */
	@Override
	public void drawLine(int x, int y, int x2, int y2, int color) {
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
	}
	
	
	/**
	 * I modified this based on a correction on his website
	 * drawRect() draws a filled and colored rectangle.
	 */
	@Override
	public void drawRect(int x, int y, int width, int height, int color) {
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		// (top-left x,  top-left y,  bottom-right x,  bottom-right y,  color and style)
		// We must subtract 1 because the x/y coordinates of the top left actually count as 1
		// he gave us this: canvas.drawRect(x, y, x + width - 1, y + width - 1, paint);
		// I think it's supposed to be this:
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
	}
	
	
	/**
	 * FIRST DRAWPIXMAP
	 * This drawPixmap() allows us to draw a portion of a Pixmap
	 * the first x and y are the top left coordinates of where this pixmap will actually be drawn in the framebuffer
	 * the srcX and srcY integers are the top left coordinates of the original pixmap. What we are doing it creating
	 * a rectangle inside of the original pixmap, to outline what portion of it should be drawn.
	 * the srcWidth and srcHeight are how wide and tall the source Pixmaps rectangle
	 */
	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
		
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth - 1;
		srcRect.bottom = srcY + srcHeight - 1;
		
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y + srcHeight - 1;
		
		// This method will automatically do blending if pixmap is PixmapFormat.ARGB4444 or 8888 color depth
		// drawBitmap(Bitmap, subset of the bitmap to be drawn, rectangle the bitmap will be scaled/translated to fit into, paint)
		canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
	}
	
	/**
	 * SECOND DRAWPIXMAP
	 * draws the complete pixmap to the artificial framebuffer at the given coordinates
	 */
	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y) {
		canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, null);
	}
	
	
	/**
	 * Returns the width of the artificial framebuffer stored 
	 * by the AndroidGraphics to swhich it renders internally
	 */
	@Override
	public int getWidth() {
		return frameBuffer.getWidth();
	}
	
	
	/**
	 * Returns the height of the artificial framebuffer
	 * by the AndroidGraphics to swhich it renders internally
	 */
	@Override
	public int getHeight() {
		return frameBuffer.getHeight();
	}
		
	
}
