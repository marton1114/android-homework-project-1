package com.example.lightcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    // radio Buttons
    RadioGroup radioGroup;
    RadioButton radioButton;

    // sub and add Buttons
    Button shutterLeftButton;
    Button shutterRightButton;
    Button isoLeftButton;
    Button isoRightButton;
    Button apertureLeftButton;
    Button apertureRightButton;

    // TextViews
    TextView shutterTextView;
    TextView isoTextView;
    TextView apertureTextView;
    TextView textViewToCalculate; // the actual TextView with the disabled Buttons

    // measure Button
    Button measureButton;

    // light sensor tools
    private SensorManager sensorManager;
    private LightSensorEventListener sel = new LightSensorEventListener();
    private Sensor mLight;

    // Value Lists
    static List<String> shutters = Arrays.asList("1/8000", "1/4000", "1/2000",
            "1/1000", "1/500", "1/250", "1/125", "1/60", "1/30", "1/15",
            "1/8", "1/4", "1/2", "1", "2", "4", "8", "15", "30", "60");
    int shutteri = 8;
    List<String> isos = Arrays.asList("100", "200", "400", "800", "1600");
    int isoi = 0;
    List<String> apertures = Arrays.asList("1", "1.4", "2", "2.8", "4",
            "5.6", "8", "11", "16", "22");
    int aperturei = 4;


    public static List<String> getShutters() {
        return shutters;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // finding id of the RadioGroup
        radioGroup = findViewById(R.id.radioGroup);

        // finding id of the add and sub buttons
        shutterLeftButton = findViewById(R.id.shutterLeftButton);
        shutterRightButton = findViewById(R.id.shutterRightButton);
        isoLeftButton = findViewById(R.id.isoLeftButton);
        isoRightButton = findViewById(R.id.isoRightButton);
        apertureLeftButton = findViewById(R.id.apertureLeftButton);
        apertureRightButton = findViewById(R.id.apertureRightButton);

        // finding id of the TextViews
        shutterTextView = findViewById(R.id.shutterTextView);
        isoTextView = findViewById(R.id.isoTextView);
        apertureTextView = findViewById(R.id.apertureTextView);
        textViewToCalculate = shutterTextView;

        // finding id of the measure button
        measureButton = findViewById(R.id.measureButton);

        // initializing the SensorManager
        sensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        measureButton.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sensorManager.registerListener(sel, mLight, SensorManager.SENSOR_DELAY_NORMAL);
                sel.setTv(textViewToCalculate);
                sel.setShutter(shutterStringToFloat(shutters.get(shutteri)));
                sel.setIso(Integer.valueOf(isos.get(isoi)));
                sel.setAperture(Float.valueOf(apertures.get(aperturei)));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sensorManager.unregisterListener(sel);
            }
            return false;
        });

        setStateOfTextViews(shutteri, isoi, aperturei);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void checkButton(View view) {
        int checkedId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(checkedId);

        if (checkedId == R.id.shutterRadioButton) {
            setStateOfButtons(false, true, true);
            textViewToCalculate = shutterTextView;
        } else if (checkedId == R.id.isoRadioButton) {
            setStateOfButtons(true, false, true);
            textViewToCalculate = isoTextView;
        } else if (checkedId == R.id.apertureRadioButton) {
            setStateOfButtons(true, true, false);
            textViewToCalculate = apertureTextView;
        }

        setStateOfTextViews(shutteri, isoi, aperturei);
    }

    public void nextButtonClicked(View view) {
        if (view.getId() == shutterRightButton.getId()) {
            setStateOfTextViews(shutteri + 1, isoi, aperturei);
        } else if (view.getId() == isoRightButton.getId()) {
            setStateOfTextViews(shutteri, isoi + 1, aperturei);
        } else {
            setStateOfTextViews(shutteri, isoi, aperturei + 1);
        }
    }

    public void beforeButtonClicked(View view) {
        if (view.getId() == shutterLeftButton.getId()) {
            setStateOfTextViews(shutteri - 1, isoi, aperturei);
        } else if (view.getId() == isoLeftButton.getId()) {
            setStateOfTextViews(shutteri, isoi - 1, aperturei);
        } else {
            setStateOfTextViews(shutteri, isoi, aperturei - 1);
        }
    }

    private void setStateOfButtons(Boolean bp1, Boolean bp2, Boolean bp3) {
        shutterLeftButton.setEnabled(bp1);
        shutterRightButton.setEnabled(bp1);
        isoLeftButton.setEnabled(bp2);
        isoRightButton.setEnabled(bp2);
        apertureLeftButton.setEnabled(bp3);
        apertureRightButton.setEnabled(bp3);
    }

    private void setStateOfTextViews(int newShutteri, int newIsoi, int newAperturei) {
        if (newShutteri <= shutters.size() - 1 && newShutteri >= 0)
            this.shutteri = newShutteri;
        if (newIsoi <= isos.size() - 1 && newIsoi >= 0)
            this.isoi = newIsoi;
        if (newAperturei <= apertures.size() - 1 && newAperturei >= 0)
            this.aperturei = newAperturei;

        shutterTextView.setText(shutters.get(shutteri));
        isoTextView.setText(isos.get(isoi));
        apertureTextView.setText(apertures.get(aperturei));
    }

    public static float shutterStringToFloat(String shutterValue) {
        float result = 0;
        if (shutterValue.contains("/")) {
            result = 1 / Float.valueOf(shutterValue.substring(2, shutterValue.length()));
        } else {
            result = Float.valueOf(shutterValue);
        }

        return result;
    }
}