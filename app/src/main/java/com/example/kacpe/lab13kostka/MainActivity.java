package com.example.kacpe.lab13kostka;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private Button buttonStart;
    private ImageView imageView;
    private TextView textViewStatus;

    private Sensor sensorAccelerometer;
    private SensorManager sensorManager;
    private Random random = new Random();

    private boolean isNextUpdate = false;
    private long checkSensorTime = 100;
    private long lastUpdate = 0;
    private long lastUpdateStop = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD_MIN = 10;
    private static final int SHAKE_THRESHOLD_AVG = 100;
    private static final int SHAKE_THRESHOLD_MAX = 300;
    private static final int MIN = 1;
    private static final int MAX = 6;

    private int getRandom(int min, int max){
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return random.nextInt(max) + min;

    }

    private void changePicture(int i){
        switch (i) {
            case 1:
                imageView.setImageResource(R.drawable.k1);
                break;
            case 2:
                imageView.setImageResource(R.drawable.k2);
                break;
            case 3:
                imageView.setImageResource(R.drawable.k3);
                break;
            case 4:
                imageView.setImageResource(R.drawable.k4);
                break;
            case 5:
                imageView.setImageResource(R.drawable.k5);
                break;
            case 6:
                imageView.setImageResource(R.drawable.k6);
                break;
            default:
                throw new IllegalArgumentException("changePicture went wrong");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById((R.id.buttonStart));
        imageView = (ImageView) findViewById(R.id.imageViewDice);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartAction();
            }
        });
    }

    private void buttonStartAction(){
        isNextUpdate = false;
        sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > checkSensorTime) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;


                if (speed > SHAKE_THRESHOLD_MAX) {
                    changePicture(getRandom(MIN,MAX));
                    textViewStatus.setText("Shaking MAX");
                    lastUpdateStop = lastUpdate;
                    isNextUpdate = true;
                    checkSensorTime = 10;
                }
                else if(speed > SHAKE_THRESHOLD_AVG){
                    changePicture(getRandom(MIN,MAX));
                    textViewStatus.setText("Shaking AVG");
                    lastUpdateStop = lastUpdate;
                    isNextUpdate = true;
                    checkSensorTime = 100;
                }
                else if(speed > SHAKE_THRESHOLD_MIN){
                    changePicture(getRandom(MIN,MAX));
                    textViewStatus.setText("Shaking MIN");
                    lastUpdateStop = lastUpdate;
                    isNextUpdate = true;
                    checkSensorTime = 200;
                }
                else {
                    curTime = System.currentTimeMillis();
                    textViewStatus.setText("Is Stopping " + (curTime - lastUpdateStop));
                    if (((curTime - lastUpdateStop) > 2000) && isNextUpdate == true){
                        textViewStatus.setText("Stopped");
                        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
