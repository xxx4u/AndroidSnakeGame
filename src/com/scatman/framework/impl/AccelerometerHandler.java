package com.scatman.framework.impl;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * A COMPASS HANDLER ALTERNATIVE IS ON PAGE 204
 */
public class AccelerometerHandler implements SensorEventListener {
	// The three axes
	float accelX;
	float accelY;
	float accelZ;
	
	
	/**
	 * PRIMARY CONSTRUCTOR
	 */
	public AccelerometerHandler(Context context) {
		// A SensorManager allows us to get a reference to the Accelerometer. So we create one here
		// A SensorManager can also tell us whether or not accelerometer is installed
		// A SensorManger is a so-called "system service" provided by the Android system
		SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		// We poll the manager for all the installed sensors that have the type accelerometer
		// If an accelerometer is installed...
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) { 
			// We create a reference to the accelerometer
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			// We register "this" SensorEventListener implementation as a listener for the "accelerometer", with an 
			// event reporting frequency of "SENSOR_DELAY_GAME" which is a number optimized for games
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	
	/**
	 * WE WILL NOT NEED TO USE THIS METHOD
	 */
	@Override
	public void onAccuracyChanged(Sensor sesnor, int accuracy) {
		// nothing to do here
	}
	
	
	/**
	 * ON SENSOR CHANGED LISTENER
	 * This method is called when a new accelerometer event arrives
	 * A sensor event has a public float array memeber called values[] that holds the current acceleration values
	 * of each of the three axes
	 * 		>> .values[0] holds the value of the x-axis
	 * 		>> .values[1] holds the value of the y-axis
	 * 		>> .values[2] holds the value of the z-axis
	 * We WOULD need to synchronize these values if we were doing something with the values, but since we are not doing anything
	 * complicated, we can rely on the Java Memory Model (this was on page 204)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		accelX = event.values[0];
		accelY = event.values[1];
		accelZ = event.values[2];
	}
	
	
	public float getAccelX() {
		return accelX;
	}
	
	
	
	public float getAccelY() {
		return accelY;
	}
	
	
	
	public float getAccelZ() {
		return accelZ;
	}
	
	
	
	
}
