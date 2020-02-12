package com.example.knockknockalarm;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SensorLogger {
    private ArrayList<Double> SensorLog;
    private String sensorType;
    public SensorLogger(String type){
        sensorType = type;
        SensorLog = new ArrayList<Double>();
    }

    public int logSensor(Double reading){
        SensorLog.add(reading);
        return SensorLog.size();
    }
}
