package twilight.of.the.devs.cordova;

import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.*;
import android.hardware.*;
import android.util.*;
import android.telephony.*;
import android.telephony.CellInfoGsm;
import android.telephony.gsm.*;
import java.util.*;

public class CellCollector extends CordovaPlugin {

    private static String ACTION_START = "start";
    private static String ACTION_STOP = "stop";
    private static String ACTION_CONFIGURE = "configure";
    Intent serivceIntent;
    CallbackContext callbackContext;
    private TelephonyManager mTelephonyManager;

    /// configuration options

    private CellListener listener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        // your init code here
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if(ACTION_START.equals(action)){
            PluginResult pgRes = new PluginResult(PluginResult.Status.OK, "");
            pgRes.setKeepCallback(true);
            listener = new CellListener(callbackContext);
            mTelephonyManager = (TelephonyManager) this.cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_CELL_INFO | PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            callbackContext.sendPluginResult(pgRes);

            return true;
        } else if (ACTION_STOP.equals(action)) {
            mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
            callbackContext.success("Stopped listening");
            return true;
        } else if (ACTION_CONFIGURE.equals(action)){

            return true;
        }
        return false;
    }

    class CellListener extends PhoneStateListener {

        private CallbackContext callback;

        public CellListener(CallbackContext callback){
            this.callback = callback;
        }

        @Override
        public void onCellInfoChanged (List<CellInfo> cellInfo) {
            if(cellInfo == null){
                return;
            }
            List<List<Integer>> result = new LinkedList<List<Integer>>();
            for (CellInfo info : cellInfo) {
                CellInfoGsm gsmInfo = (CellInfoGsm) info;
                List<Integer> intList = new LinkedList<Integer>();
                intList.add(gsmInfo.getCellIdentity().getCid());
                intList.add(gsmInfo.getCellIdentity().getLac());
                intList.add(gsmInfo.getCellIdentity().getMcc());
                intList.add(gsmInfo.getCellIdentity().getMnc());
                intList.add(gsmInfo.getCellIdentity().getPsc());
                intList.add(gsmInfo.getCellSignalStrength().getDbm());
                intList.add(gsmInfo.getCellSignalStrength().getLevel());
                intList.add(gsmInfo.getCellSignalStrength().getAsuLevel());
                result.add(intList);
            }
            try {
                JSONObject object = new JSONObject();
                object.put("cell_info", result.toArray());
                PluginResult pgRes = new PluginResult(PluginResult.Status.OK, object);
                pgRes.setKeepCallback(true);
                this.callback.sendPluginResult(pgRes);
            } catch (JSONException e){
                Log.d("CellDetailsPlugin", e.getMessage());
            }
        }

        @Override
        public void onCellLocationChanged (CellLocation location){
            if(location == null) return;
            // TODO - Handle all cell types
            GsmCellLocation loc = (GsmCellLocation)location;
            List<Integer> info = new LinkedList<Integer>();
            info.add(loc.getCid());
            info.add(loc.getLac());
            info.add(loc.getPsc());
            try {
                JSONObject object = new JSONObject();
                object.put("cell_location", info.toArray());
                PluginResult pgRes = new PluginResult(PluginResult.Status.OK, object);
                pgRes.setKeepCallback(true);
                this.callback.sendPluginResult(pgRes);
            } catch (JSONException e){
                Log.d("CellDetailsPlugin", e.getMessage());
            }
        }

        @Override
        public void onSignalStrengthsChanged (SignalStrength signalStrength){
            if(signalStrength == null) return;
            try {
                JSONObject object = new JSONObject();
                object.put("signal_strength", signalStrength.getGsmSignalStrength());
                PluginResult pgRes = new PluginResult(PluginResult.Status.OK, object);
                pgRes.setKeepCallback(true);
                this.callback.sendPluginResult(pgRes);
            } catch (JSONException e){
                Log.d("CellDetailsPlugin", e.getMessage());
            }
        }
    }
}
