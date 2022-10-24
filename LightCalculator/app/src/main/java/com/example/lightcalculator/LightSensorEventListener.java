package com.example.lightcalculator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import java.util.List;

public class LightSensorEventListener implements SensorEventListener {
    private TextView tv;
    private float shutter;
    private float aperture;
    private int iso;

    public void setShutter(float shutter) {
        this.shutter = shutter;
    }

    public void setAperture(float aperture) {
        this.aperture = aperture;
    }

    public void setIso(int iso) {
        this.iso = iso;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float lux = sensorEvent.values[0];

        if (tv.getId() == R.id.shutterTextView)
            // tv.setText(String.valueOf(calculateShutter(lux, aperture, iso)));
            tv.setText(closestShutter(calculateShutter(lux, aperture, iso)));
        else if (tv.getId() == R.id.isoTextView) {
            tv.setText(String.valueOf(calculateIso(lux, shutter, aperture)));
        } else {
            tv.setText(String.valueOf(calculateAperture(lux, shutter, iso)));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //TODO
    }

    private int luxToEv(float lux) {
        return (int) ((lux / 1428.57) + 5);
    }

    private float calculateShutter(float lux, float aperture, int iso) {
        int ev = luxToEv(lux);

        return (float) ((Math.pow(aperture, 2) * 100) / (Math.pow(2, ev) * iso));
    }

    private int calculateIso(float lux, float shutter, float aperture) {
        int ev = luxToEv(lux);

        return (int) ((Math.pow(aperture, 2) * 100) / (Math.pow(2, ev) * shutter));
    }

    private float calculateAperture(float lux, float shutter, float iso) {
        int ev = luxToEv(lux);

        return (float) Math.sqrt((Math.pow(2, ev) * iso * shutter) / 100);
    }

    private String closestShutter(float shutter) {
        List<String> shutters = MainActivity.getShutters();

        int mini = shutters.size() - 1;   // index of the smallest difference
        for (int i = 0; i < shutters.size(); i++) {
            float difference = Math.abs(MainActivity.shutterStringToFloat(shutters.get(i)) - shutter);
            if (difference < Math.abs(MainActivity.shutterStringToFloat(shutters.get(mini)) - shutter)) {
                mini = i;
            }
        }

        return shutters.get(mini);
    }
}
