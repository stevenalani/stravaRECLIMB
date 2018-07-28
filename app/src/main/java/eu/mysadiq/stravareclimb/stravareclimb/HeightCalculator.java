package eu.mysadiq.stravareclimb.stravareclimb;

import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

public class HeightCalculator implements Observer {
    private static int VALUE_LIMIT;
    private static final int VALUE_DEFAULT = 999999999;
    private boolean isInitialized;

    private float resultASC = 0;
    private float resultDESC = 0;

    private double startPressure = VALUE_DEFAULT;
    private double endPressure = VALUE_DEFAULT;
    private double defaultVarity = 0;

    private Queue<Double> eventsQueue;


    public HeightCalculator(int triggercount) {
        this.eventsQueue = new LinkedList<Double>();
        VALUE_LIMIT = triggercount;
        isInitialized = false;
    }

    private double CalculateVarity(double values[]) {
        double average = CalculateAverage(values);
        double varity = 0.0;

        for (int i = 0;i< values.length;i++)
            varity += (values[i] - average)*(values[i] - average);
        varity /= values.length;
        return Math.sqrt(varity);
    }

    private double CalculateAverage(double values[]){
        double average = 0.0;
        for (int i = 0;i < values.length;i++)
            average += values[i];
        average /= values.length;
        return average;
    }

    private double[] pollToArray(int size){
        double[] values = new double[size];
        for (int i = 0; i < size;i++){
            values[i] = eventsQueue.poll();
        }
        return values;
    }
    @Override
    public void update(Observable observable, Object o) {
        this.eventsQueue.add((Double) o);
        int initSwitch = VALUE_LIMIT > 100?VALUE_LIMIT*2:100;
        if (!isInitialized)
            Log.i("HeightCalculator","_______________________Buffering____________________ ITEMS:"+ eventsQueue.size());
        if (!isInitialized && eventsQueue.size() == initSwitch){
            Log.i("HeightCalculator","_______________________Init Start____________________");
            double[] values = pollToArray(initSwitch);
            double average = CalculateAverage(values);
            defaultVarity = CalculateVarity(values);
            this.startPressure = average;
            this.endPressure = VALUE_DEFAULT;
            isInitialized = true;

        }else if(isInitialized && eventsQueue.size() == VALUE_LIMIT){
            double[] values = pollToArray(VALUE_LIMIT );
            double avgValue = CalculateAverage(values);
            double varity = defaultVarity;
            setStartEnd(avgValue);
            if(startPressure != VALUE_DEFAULT && endPressure != VALUE_DEFAULT){
                //Log.i("HeightCalculator", "ASC: " + resultASC);
                Log.i("HeightCalculator", "Pressure diff"+ (endPressure - startPressure) );
                if(endPressure - startPressure > varity) {
                    float resulting = SensorManager.getAltitude((float) startPressure, (float) endPressure);
                    resultASC += resulting > 0 ? resulting : 0;
                    resultDESC += resulting < 0 ? resulting : 0;
                    startPressure = endPressure = VALUE_DEFAULT;

                    Log.i("HeightCalculator", "ASC: " + resultASC);

                }

            }
        }
    }


    private void setStartEnd(double average){
        if(startPressure ==  VALUE_DEFAULT)
            startPressure = average;
        else if (endPressure == VALUE_DEFAULT)
            endPressure = average;

    }
}
