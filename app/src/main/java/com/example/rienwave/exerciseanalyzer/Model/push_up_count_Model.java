package com.example.rienwave.exerciseanalyzer.Model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.rienwave.exerciseanalyzer.Events.PushUpCounterChangedEvent;
import com.example.rienwave.exerciseanalyzer.Events.SitUpCounterChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class push_up_count_Model implements SensorEventListener {
    public List<Double> AxVal;
    public List<Double> AyVal;
    public List<Double> AzVal;

    public List<Double> GxVal;
    public List<Double> GyVal;
    public List<Double> GzVal;

    ArrayList<ArrayList<Double>> SensorInfo;

    private SensorManager asensorM;
    private SensorManager gsensorM;
    private Sensor aSensor;
    private Sensor gSensor;

    public int counter;
    public Boolean hasStarted;
    private double avZ;
    private boolean isflat;
    private boolean isLastFlat;

    public push_up_count_Model(Context context){
        init(context);
    }


    private void init(Context context){
        AxVal = new ArrayList<Double>();
        AyVal = new ArrayList<Double>();
        AzVal = new ArrayList<Double>();

        GxVal = new ArrayList<Double>();
        GyVal = new ArrayList<Double>();
        GzVal = new ArrayList<Double>();

        SensorInfo = new ArrayList<ArrayList<Double>>(6);

        hasStarted = false;
        isLastFlat = false;

        asensorM = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        aSensor = asensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        gsensorM = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        gSensor = gsensorM.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        avZ = 0;
        isflat = false;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
        PushUpCounterChangedEvent event = new PushUpCounterChangedEvent();
        event.setValue("" + this.counter);
        EventBus.getDefault().post(event);
    }

    public void IncrementCounter() {
        setCounter(counter + 1);
    }

    public Boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(Boolean hasStarted) {
        this.hasStarted = hasStarted;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            AxVal.add((double)Math.round(event.values[0] * 100d) / 100d);
            AyVal.add((double)Math.round(event.values[1] * 100d) / 100d);
            AzVal.add((double)Math.round(event.values[2] * 100d) / 100d);
            Analyze();
        }
        else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            GxVal.add((double)Math.round(event.values[0] * 100d) / 100d);
            GyVal.add((double)Math.round(event.values[1] * 100d) / 100d);
            GzVal.add((double)Math.round(event.values[2] * 100d) / 100d);
            Analyze();
        }
    }

    public void ClearCount(){
        setCounter(0);
    }

    private void Analyze (){

        // ToDO determine if text_Counter needs to be incremented
        // TODO clear the ArrayLIst with every new sit-up

        //IncrementCounter();

        //boolean hasFlat = false;

        if (AzVal.size() > 200) {

            for (int i = 0; i < AzVal.size(); i++) {
                avZ += AzVal.get(i);
                //if (AzVal.get(i) > 9.5 && Math.abs(AzVal.get(i))< 10)
                    //hasFlat = true;
            }

            avZ /= AzVal.size();

            if (avZ < 9.8 /*&& hasFlat*/) {
                IncrementCounter();
            }

            /*if (Math.abs(AzVal.get(AzVal.size()-1)) > 8.5 && Math.abs(AzVal.get(AzVal.size()-1)) < 10.5) {
                isflat = true;
                isLastFlat = isflat;
            }
            else {
                if (isflat && isflat == isLastFlat) {
                    IncrementCounter();
                    isflat = false;
                }else {
                    isLastFlat = isflat;
                }
            }*/
            avZ = 0;

            AzVal.clear();
            AxVal.clear();
            AyVal.clear();
        }
    }

        // testing purposes only, should be bounded by if statements


    public void onStartStopClick(){
        if (hasStarted){
            asensorM.unregisterListener(this);
            gsensorM.unregisterListener(this);
        }
        else {
            // SENSOR_DELAY_FASTEST (0 microsecond delay).
            asensorM.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_FASTEST);
            gsensorM.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        hasStarted = !hasStarted;
    }

    public void onStopClick(){
        counter = 0;
    }

    // not applicable

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {    }
}
