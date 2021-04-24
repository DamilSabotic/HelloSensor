package com.example.hellosensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void switchToCompass(View view){
       Intent  compass = new Intent(this, compass.class);
        startActivity(compass);
    }

    public void switchToAccelerometer(View view){
      Intent  acc = new Intent(getApplicationContext(), accelerometer.class);
        startActivity(acc);
    }
}