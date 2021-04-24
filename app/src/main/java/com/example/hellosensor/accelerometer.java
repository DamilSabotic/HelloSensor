package com.example.hellosensor;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class accelerometer extends Activity implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView xAcc;
    private TextView yAcc;
    private TextView zAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        sensorManager = (android.hardware.SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        xAcc = (TextView) findViewById(R.id.x_acc);
        yAcc = (TextView) findViewById(R.id.y_acc);
        zAcc = (TextView) findViewById(R.id.z_acc);
    }

    public void goHome(View view){
        onPause();
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
    }



        @Override
        public void onSensorChanged(SensorEvent event){
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final float alpha = (float) 0.8;

            // Isolate the force of gravity with the low-pass filter.
            double[] gravity = new double[3];
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            double[] linear_acceleration = new double[3];
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            String x="X;";
            String y="Y;";
            String z="Z;";

            xAcc.setText(x + Math.round(linear_acceleration[0]));
            yAcc.setText(y +  Math.round(linear_acceleration[1]));
            xAcc.setText(z +  Math.round(linear_acceleration[2]));

        }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }




}