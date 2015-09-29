package twilight.of.the.devs.cordova;

import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import android.content.*;
import android.hardware.*;
import android.util.*;

public class GeomagneticPlugin extends CordovaPlugin {

    private static String ACTION_START = "start";
    private static String ACTION_STOP = "stop";
    private static String ACTION_CONFIGURE = "configure";
    Intent serivceIntent;
    CallbackContext callbackContext;
    HashMap<String, SensorEventListener> callbackMap = new HashMap<String, SensorEventListener>();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    /// configuration options
    private String sensor_delay = "normal"; // default to SENSOR_DELAY_NORMAL
    private String sensor_type = "magnetic_field_uncalibrated";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("SENSOR", action);
        String sensorType = args.getString(0);

        if(ACTION_START.equals(action)){
            PluginResult pgRes = new PluginResult(PluginResult.Status.OK, "Now listening to sensor");
            pgRes.setKeepCallback(true);
            callbackContext.sendPluginResult(pgRes);
            if(mSensorManager == null)
                mSensorManager = (SensorManager) this.cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);

            saveCallback(sensorType, callbackContext);
            return true;
        } else if (ACTION_STOP.equals(action)) {
            SensorEventListener listener = this.callbackMap.get(sensorType);
            mSensorManager.unregisterListener(listener);
            callbackContext.success("Stopped listening to " + sensorType + " sensor");
            return true;
        } else if (ACTION_CONFIGURE.equals(action)){
            // [0: interval, 1: type, ...]
            this.sensor_delay = args.getString(0);
            this.sensor_type = args.getString(1);
            return true;
        }
        return false;
    }

    private void saveCallback(String sensorType, CallbackContext callback){
        int type = getSensorType(sensorType);
        Sensor sensor = mSensorManager.getDefaultSensor(type);
        SensorEventListener listener;
        switch (type) {
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                listener = new GeomagnetUncalibratedSensorListener(callback);
                this.callbackMap.put(sensorType, listener);
                mSensorManager.registerListener(listener, sensor, getSensorDelay(this.sensor_delay));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                listener = new GeomagnetSensorListener(callback);
                this.callbackMap.put(sensorType, listener);
                mSensorManager.registerListener(listener, sensor, getSensorDelay(this.sensor_delay));
                break;
        }
    }

    private int getSensorDelay(String delay){
        if(delay.equals("normal")){
            return SensorManager.SENSOR_DELAY_NORMAL;
        } else if(delay.equals("ui")){
            return SensorManager.SENSOR_DELAY_UI;
        }
        return SensorManager.SENSOR_DELAY_NORMAL;
    }

    private int getSensorType(String type){
        if(type.equals("magnetic_field_uncalibrated")){
            return Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
        } else if (type.equals("magnetic_field")){
            return Sensor.TYPE_MAGNETIC_FIELD;
        }
        return Sensor.TYPE_MAGNETIC_FIELD;
    }

}
