package twilight.of.the.devs.cordova;

import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.*;
import android.hardware.*;
import android.util.*;
import android.net.wifi.*;
import java.util.*;

public class WifiCollector extends CordovaPlugin {

    private static String ACTION_SCAN = "scan";
    private static String ACTION_START = "start";
    private static String ACTION_STOP = "stop";

    Intent serivceIntent;
    CallbackContext callbackContext;
    WifiManager mWifiManager;
    List<ScanResult> scanResults;
    BroadcastReceiver reciever;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Log.d("WIFI", action);
        if(ACTION_START.equals(action)){
            PluginResult pgRes = new PluginResult(PluginResult.Status.OK, "Registered");
            pgRes.setKeepCallback(true);
            mWifiManager = (WifiManager) this.cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
            this.reciever = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context c, Intent intent)
                {
                   List<ScanResult> scanResults = mWifiManager.getScanResults();
                   handleResults(scanResults);
                }
            };
            this.cordova.getActivity().registerReceiver(this.reciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            callbackContext.sendPluginResult(pgRes);
            Log.d("WIFI", "inside " + action);
            return true;
        } else if (ACTION_SCAN.equals(action)){
            PluginResult pgRes = new PluginResult(PluginResult.Status.OK, "");
            pgRes.setKeepCallback(true);
            mWifiManager = (WifiManager) this.cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
            mWifiManager.startScan();
            callbackContext.sendPluginResult(pgRes);
            Log.d("WIFI", "inside " + action);
            return true;
        } else if (ACTION_STOP.equals(action)){
            if(this.reciever != null) {
                this.cordova.getActivity().unregisterReceiver(this.reciever);
                this.reciever = null;
            }
            return true;
        }
        return false;
    }

    private void handleResults(List<ScanResult> results){
        JSONArray arr = new JSONArray();
        try {
            for (ScanResult result : results) {
                JSONObject obj = new JSONObject();
                obj.put("BSSID", result.BSSID);
                obj.put("SSID", result.SSID);
                obj.put("Frequency", result.frequency);
                obj.put("Level", result.level);
                arr.put(obj);
            }
        } catch (JSONException e){}
        PluginResult pgRes = new PluginResult(PluginResult.Status.OK, arr);
        pgRes.setKeepCallback(true);
        this.callbackContext.sendPluginResult(pgRes);
    }
}
