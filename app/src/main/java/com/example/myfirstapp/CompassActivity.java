package com.example.myfirstapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mMagnetic;
    private Sensor mAccelerometer;
    private TextView dTV;
    private ImageView cIV;
    private boolean entering = false;
    private float[] accReadings = new float[3];
    private float[] magReadings = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private static final float ALPHA = 0.1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        dTV = (TextView) findViewById(R.id.tvDegrees);
        cIV = (ImageView) findViewById(R.id.ivCompass);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accReadings = applyLowPassFilter(event.values, accReadings);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magReadings = applyLowPassFilter(event.values, magReadings);
        }
        updateOrientationAngles();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mMagnetic,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    private String getDirection(double angle) {
        StringBuilder direction = new StringBuilder();

        if (angle >= 350 || angle <= 10){
            direction.append("N");
        }
        if (angle < 350 && angle > 280) {
            direction.append("NW");
        }
        if (angle <= 280 && angle > 260) {
            direction.append("W");
        }
        if (angle <= 260 && angle > 190) {
            direction.append("SW");
        }
        if (angle <= 190 && angle > 170) {
            direction.append("N");
        }
        if (angle <= 170 && angle > 100) {
            direction.append("SE");
        }
        if (angle <= 100 && angle > 80) {
            direction.append("E");
        }
        if (angle <= 80 && angle > 10) {
            direction.append("NE");
        }
        return direction.toString();
    }

    private void updateOrientationAngles() {
        mSensorManager.getRotationMatrix(rotationMatrix, null, accReadings, magReadings);
        float[] orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles);
        double degrees = (Math.toDegrees((double) orientation[0]) + 360.0) % 360.0;
        double angle = Math.round(degrees * 100) / 100;
        String direction = getDirection(degrees);
        dTV.setText(angle + " degrees " + direction);
        if (direction.equals("N")) {
            if (entering) {
                vibrate();
                entering = false;
            }
        } else {
            entering = true;
        }
        cIV.setRotation((float) angle * -1);
    }

    // https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    private float[] applyLowPassFilter(float[] input, float[] output) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    // https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate-with-different-frequency
    // Vibrate for 150 milliseconds
    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }
}
