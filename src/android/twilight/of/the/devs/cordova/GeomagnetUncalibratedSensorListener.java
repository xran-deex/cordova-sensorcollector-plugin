package twilight.of.the.devs.cordova;

import android.content.*;
import android.hardware.*;
import android.util.*;
import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class GeomagnetUncalibratedSensorListener implements SensorEventListener {

    private CallbackContext callback;

    public GeomagnetUncalibratedSensorListener(CallbackContext callback){
        this.callback = callback;
    }

    ////// SensorEvent interface
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            JSONArray object = new JSONArray(event.values);
            PluginResult pgRes = new PluginResult(PluginResult.Status.OK, object);
            pgRes.setKeepCallback(true);
            this.callback.sendPluginResult(pgRes);
        } catch (JSONException e){
            Log.d("SENSOR", e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
