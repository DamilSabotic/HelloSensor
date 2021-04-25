package com.example.hellosensor;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.hardware.SensorEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class compass extends AppCompatActivity implements SensorEventListener {

    // device sensor manager
    private SensorManager SensorManage;

    private SoundPool soundPool;
    private int sonar;

    // define the compass picture that will be use
    private ImageView compassimage;

    // record the angle turned of the compass picture
    private float DegreeStart = 0f;
    private float targetDegree = 0f;
    private final int play_button_nmbr = 1;
    private final int findNorth_button_nmbr = 2;
    private int selectedbutton = 0;
    Button play_button;
    Button north_button;
    TextView DegreeTV;
    TextView target;
    float currentDegree = 0f;
    ConstraintLayout Layout;
    static final float ALPHA = 0.25f;
    protected float[] magSensorVals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        //
        compassimage = (ImageView) findViewById(R.id.compass_image);

        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);
        target = (TextView) findViewById(R.id.target);
        play_button = (Button) findViewById(R.id.play);
        north_button = (Button) findViewById(R.id.findNorth);
        Layout = (ConstraintLayout) findViewById(R.id.ConstraintLayout_compass);
        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        sonar = soundPool.load(this, R.raw.sonar, 1);

    }

    /**
     * vibration
     */
    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.EFFECT_TICK));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

    /**
     * Called when the user taps play button
     */
    public void play(View view) {
        targetDegree = Math.round(Math.random() * 350f);
        if (selectedbutton == play_button_nmbr) {
            play_button.setBackgroundColor(Color.LTGRAY);
            target.setText("");
            selectedbutton = 0;
        } else {
            selectedbutton = play_button_nmbr;
            play_button.setBackgroundColor(0xFF29B6F6);
            north_button.setBackgroundColor(Color.LTGRAY);
            target.setText("Rotate phone to find the Degree");
        }

    }

    public void north(View view) {
        if (selectedbutton == findNorth_button_nmbr) {
            north_button.setBackgroundColor(Color.LTGRAY);
            target.setText("");
            selectedbutton = 0;
        } else {
            selectedbutton = findNorth_button_nmbr;
            north_button.setBackgroundColor(0xFF29B6F6);
            play_button.setBackgroundColor(Color.LTGRAY);
            target.setText("Rotate phone to find North");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get angle around the z-axis rotated
       magSensorVals = lowPass(event.values.clone(), magSensorVals);
       float degree = Math.round(magSensorVals[0]);


        DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");

        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);

        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
        buttonPressed(degree);
    }


    /**
     *     @Override
     *     public void onSensorChanged(SensorEvent event) {
     *
     *         // get angle around the z-axis rotated
     *         float degree = Math.round(event.values[0]);
     *
     *
     *         DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
     *
     *         // rotation animation - reverse turn degree degrees
     *         RotateAnimation ra = new RotateAnimation(
     *                 DegreeStart,
     *                 -degree,
     *                 Animation.RELATIVE_TO_SELF, 0.5f,
     *                 Animation.RELATIVE_TO_SELF, 0.5f);
     *
     *         // set the compass animation after the end of the reservation status
     *         ra.setFillAfter(true);
     *
     *         // set how long the animation for the compass image will take place
     *         ra.setDuration(210);
     *
     *         // Start animation of compass image
     *         compassimage.startAnimation(ra);
     *         DegreeStart = -degree;
     *         buttonPressed(degree);
     *     }
     **/

    public void findDegree(float degree) {

        if (targetDegree == degree) {
            soundPool.play(sonar, 1, 1, 0, 0, 1);
            target.setText("Congratulations, Your target was : " + Float.toString(targetDegree) + " degrees");
            Layout.setBackgroundColor(0xFF9CCC65);
            vibrate();
        } else {
            Layout.setBackgroundColor(0xFFFFFFFF);
        }

    }

    public void findNorth(float degree) {
        if (15f > degree || degree > 345f) {
            soundPool.play(sonar, 1, 1, 0, 0, 1);
            target.setText("Congratulations, you found the North");
            Layout.setBackgroundColor(0xFF9CCC65);
            vibrate();
        } else {
            Layout.setBackgroundColor(0xFFFFFFFF);
        }
    }

    public void goHome(View view){
        onPause();
        Intent intent = new Intent(this, main.class);
        startActivity(intent);
    }

    public void buttonPressed(float degree) {
        switch (selectedbutton) {
            case play_button_nmbr:
                findDegree(degree);
                break;
            case findNorth_button_nmbr:
                findNorth(degree);
                break;
        }
    }
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        if(Math.abs(output[0] - input[0]) > 180) { //Necessary to prevent a flips when going from 360 to 0 degrees.
            return input;
        }
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}