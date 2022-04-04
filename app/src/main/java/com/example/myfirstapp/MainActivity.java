package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when user taps the accelerometer button **/
    public void enterAccelerometer(View view){
        // Enter code to enter accelerometer activity
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }

    /** Called when user taps the Compass button **/
    public void enterCompass(View view){
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

}