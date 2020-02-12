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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private Button testButton;
    private TextView xAxis;
    private TextView yAxis;
    private TextView zAxis;

    private TextView lblXMax;
    private TextView lblYMax;
    private TextView lblZMax;

    private TextView lblXFilt;
    private TextView lblYFilt;
    private TextView lblZFilt;

    private TextView lblXDiff;
    private TextView lblYDiff;
    private TextView lblZDiff;

    private TextView lblRecordedKnocks;

    private TextView lblNumKnocks;

    private float xMax = 0;
    private float yMax = 0;
    private float zMax = 0;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private int numKnocks = 0;
    private int increment = 0;
    private SensorManager sensorManager;
    private Sensor sensor;

    private boolean trigger = false;

    static final float ALPHA = 0.75f; // if ALPHA = 1 OR 0, no filter applies.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        xAxis = findViewById(R.id.lblXAxis);
        yAxis = findViewById(R.id.lblYAxis);
        zAxis = findViewById(R.id.lblZAxis);

        lblXMax = findViewById(R.id.lblXMAX);
        lblYMax = findViewById(R.id.lblYMAX);
        lblZMax = findViewById(R.id.lblZMAX);

        lblXFilt = findViewById(R.id.lblXFilt);
        lblYFilt = findViewById(R.id.lblYFilt);
        lblZFilt = findViewById(R.id.lblZFilt);

        lblXDiff = findViewById(R.id.lblXDiff);
        lblYDiff = findViewById(R.id.lblYDiff);
        lblZDiff = findViewById(R.id.lblZDiff);

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
            }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] oldVals = {lastX, lastY, lastZ};

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float[] currentVals = {x, y, z};
        if(trigger){
            float[] filtered = lowPass(currentVals, oldVals);
            lblXFilt.setText(String.valueOf(filtered[0]));
            lblYFilt.setText(String.valueOf(filtered[1]));
            lblZFilt.setText(String.valueOf(filtered[2]));

            lblXDiff.setText(String.valueOf(filtered[0] - x));
            lblYDiff.setText(String.valueOf(filtered[1] - y));
            lblZDiff.setText(String.valueOf(filtered[2] - z));
        }

        if(x > xMax){
            lblXMax.setText(String.valueOf(x));
            xMax = x;
        }
        if(y > yMax){
            lblYMax.setText(String.valueOf(y));
            yMax = y;
        }
        if(z > zMax){
            lblZMax.setText(String.valueOf(z));
            zMax = z;
        }
        if(z > 0){
            numKnocks++;
            lblNumKnocks.setText(String.valueOf(numKnocks));
            lblRecordedKnocks.append("\n" + String.valueOf(z));
        }
        xAxis.setText(String.valueOf(x));
        yAxis.setText(String.valueOf(y));
        zAxis.setText(String.valueOf(z));


        lastX = x;
        lastY = y;
        lastZ = z;

        trigger = true;

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
