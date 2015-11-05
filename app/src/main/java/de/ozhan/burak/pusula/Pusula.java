package de.ozhan.burak.pusula;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Pusula extends AppCompatActivity implements SensorEventListener{

	private SensorManager sensorManager;
	private Sensor magnetometer,accelerometer,orientation;

	@Bind(R.id.heading_magnet)
	TextView heading_magnet;

	@Bind(R.id.heading_ori)
	TextView heading_ori;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pusula);
		ButterKnife.bind(this);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	float[] mGravity;
	float[] mGeomagnetic;
	float[] mOrientation;

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			mGravity = event.values;
		}


		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			mGeomagnetic = event.values;
			
			if (mGravity != null) {
				float R[] = new float[9];
				float I[] = new float[9];

				if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {

					// orientation contains azimut, pitch and roll
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);

					float rotation = orientation[0] * 360f / (2f * 3.14159f);
					rotation += 360f;
					rotation = (int) rotation % 360;
					rotation = ((int) rotation / 10) * 10;
					heading_magnet.setText(String.format("%.0f", rotation));
				}
			}
		}

		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			mOrientation = event.values;

			float rotation = mOrientation[0];// * 360f / (2f * 3.14159f);
			rotation += 360f;
			rotation = (int) rotation % 360;
			rotation = ((int) rotation / 10) * 10;
			heading_ori.setText(String.format("%.0f", rotation));
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
