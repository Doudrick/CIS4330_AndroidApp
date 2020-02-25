package com.example.knockknockalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ActivityAccelStats extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private Button testButton;
    private TextView lblXAccel;
    private TextView lblYAccel;
    private TextView lblZAccel;

    private TextView lblXMax;
    private TextView lblYMax;
    private TextView lblZMax;

    private TextView lblXFilt;
    private TextView lblYFilt;
    private TextView lblZFilt;

    private TextView lblXDiff;
    private TextView lblYDiff;
    private TextView lblZDiff;

    private TextView lblXGyro;
    private TextView lblYGyro;
    private TextView lblZGyro;

    private TextView lblGForce;

    private TextView lblRecordedKnocks;

    private TextView lblNumKnocks;

    private float xMax = 0;
    private float yMax = 0;
    private float zMax = 0;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private float xGyro = 0;
    private float yGyro = 0;
    private float zGyro = 0;

    private float lastXGyro = 0;
    private float lastYGyro = 0;
    private float lastZGyro = 0;

    private int numKnocks = 0;
    private int increment = 0;
    private SensorManager sensorManager;

    private long triggerTime = 0;
    private long currentTime = 0;

    private Sensor accel;
    private Sensor gyro;

    private boolean trigger = false;

    private static DecimalFormat df2 = new DecimalFormat("#.###");
    private static DecimalFormat df4 = new DecimalFormat("#.#####");

    static final float ALPHA = 0.75f; // if ALPHA = 1 OR 0, no filter applies.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accel_stats);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyro, sensorManager.SENSOR_DELAY_FASTEST);

        lblXAccel = findViewById(R.id.lblXAxis);
        lblYAccel = findViewById(R.id.lblYAxis);
        lblZAccel = findViewById(R.id.lblZAxis);

        lblXMax = findViewById(R.id.lblXMAX);
        lblYMax = findViewById(R.id.lblYMAX);
        lblZMax = findViewById(R.id.lblZMAX);

        lblXFilt = findViewById(R.id.lblXFilt);
        lblYFilt = findViewById(R.id.lblYFilt);
        lblZFilt = findViewById(R.id.lblZFilt);

        lblXDiff = findViewById(R.id.lblXDiff);
        lblYDiff = findViewById(R.id.lblYDiff);
        lblZDiff = findViewById(R.id.lblZDiff);

        lblXGyro = findViewById(R.id.lblXGyro);
        lblYGyro = findViewById(R.id.lblYGyro);
        lblZGyro = findViewById(R.id.lblZGyro);

        lblGForce = findViewById(R.id.lblGForce);

        lblRecordedKnocks = findViewById(R.id.lblRecordedKnocks);

        lblNumKnocks = findViewById(R.id.lblNumKnocks);


        testButton = findViewById(R.id.btnClear);
        testButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.btnClear){
            xMax = 0;
            yMax = 0;
            zMax = 0;
            lblXMax.setText(String.valueOf(0));
            lblYMax.setText(String.valueOf(0));
            lblZMax.setText(String.valueOf(0));
            numKnocks = 0;
            lblNumKnocks.setText(String.valueOf(0));
            lblRecordedKnocks.setText("Recorded Knocks:");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] oldVals = {lastX, lastY, lastZ};

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float[] currentVals = {x, y, z};
            if (trigger) {
                float[] filtered = lowPass(currentVals, oldVals);
                lblXFilt.setText(df2.format(filtered[0]));
                lblYFilt.setText(df2.format(filtered[1]));
                lblZFilt.setText(df4.format(filtered[2]));

                lblXDiff.setText(df2.format(filtered[0] - x));
                lblYDiff.setText(df2.format(filtered[1] - y));
                lblZDiff.setText(df2.format(filtered[2] - z));
            }

            if (x > xMax) {
                lblXMax.setText(df2.format(x));
                xMax = x;
            }
            if (y > yMax) {
                lblYMax.setText(df2.format(y));
                yMax = y;
            }
            if (z > zMax) {
                lblZMax.setText(df4.format(z));
                zMax = z;
            }
            if (z > 10) {
                if (xGyro < 0.001 && yGyro < 0.001 && zGyro < 0.001) {
                    //It's a knock, (probably)
                    currentTime = System.nanoTime();
                    if(triggerTime == 0){
                        triggerTime = currentTime;
                        numKnocks++;
                        lblNumKnocks.setText(String.valueOf(numKnocks));
                        lblRecordedKnocks.append("\n" + String.valueOf(z));
                    }else if((currentTime-triggerTime)/1000000 > 100){
                        numKnocks++;
                        lblNumKnocks.setText(String.valueOf(numKnocks));
                        lblRecordedKnocks.append("\n" + String.valueOf(z));
                        triggerTime = currentTime;
                    }
                }
            }
            lblXAccel.setText(df2.format(x));
            lblYAccel.setText(df2.format(y));
            lblZAccel.setText(df4.format(z));
            double gX = x/9.8;
            double gY = y/9.8;
            double gZ = z/9.8;
            double gCalc = (Math.sqrt(gX * gX + gY * gY + gZ * gZ));

            lblGForce.setText(df2.format(gCalc));

            lastX = x;
            lastY = y;
            lastZ = z;

            trigger = true;

        }else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lblXGyro.setText(df2.format(x));
            lblYGyro.setText(df2.format(y));
            lblZGyro.setText(df2.format(z));

            xGyro = x;
            yGyro = y;
            zGyro = z;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    protected float[] lowPass( float[] input, float[] output )
    {
        if ( output == null )
            return input;
        for ( int ix=0; ix<input.length; ix++ )
        {
            output[ix] = output[ix] + ALPHA * (input[ix] - output[ix]);
        }
        return output;
    }
}
