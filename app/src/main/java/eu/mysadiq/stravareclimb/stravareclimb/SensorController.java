package eu.mysadiq.stravareclimb.stravareclimb;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorController extends Observable implements SensorEventListener {

    private SensorManager sensorManager;

    private Sensor mSensor;
    float oldVal = 0.0f;
    float height = 0.0f;
    public SensorController(SensorManager manager) {
        sensorManager = manager;
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        setChanged();
        Log.i("Sensorcontroller",""+(double)sensorEvent.values[0]);
        float altitude = 0f;
        if(oldVal != 0.0f)
            altitude = SensorManager.getAltitude(oldVal,sensorEvent.values[0]);
        oldVal = sensorEvent.values[0];
        if(altitude > 0.1)
        height += altitude;
        Log.i("SensorcontrollerOH","Height: "+height);
       // this.notifyObservers((double)sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
