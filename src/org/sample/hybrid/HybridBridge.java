package org.sample.hybrid;

import android.content.Context;
import android.content.Intent;

import com.lee.cliplay.ClipActivity;
import com.lee.cliplay.Encrypt.Encryption;
import com.lee.cliplay.Push.CustomApplication;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by hschinsk on 6/18/15.
 */
public class HybridBridge extends CordovaPlugin {

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            CustomApplication app = ((CustomApplication)cordova.getActivity().getApplicationContext());
            if (action.equals("showList")) {
                String urls = args.getString(0);
                boolean showTip = args.getBoolean(1);

                if(cordova.getActivity().getLocalClassName().equals("ClipActivity")) return false;

                Context context = cordova.getActivity().getApplicationContext();
                Intent intent = new Intent(context, ClipActivity.class);
                intent.putExtra("urls", urls);
                intent.putExtra("showTip", showTip);
                cordova.startActivityForResult(this,intent,1);
                callbackContext.success();
                return true;
            }else if (action.equals("showAlert")){
                String title = args.getString(0);
                String desc = args.getString(1);
                boolean clean = args.getBoolean(2);
//                ((MainActivity)cordova.getActivity()).showDialog(title, desc, clean);
                app.showDialog(title, desc, clean, cordova.getActivity());
                callbackContext.success();
                return true;
            }else if (action.equals("checkPush")) {
                app.webViewLoaded = true;
                app.showPush();
                return true;
            }else if (action.equals("getDBString")) {
                Encryption e = new Encryption();
//                e.encrypt(cordova.getActivity(), app.dbKey, app.dbFile);
                e.decrypt(cordova.getActivity(), app.dbKey, app.dbFile);
                callbackContext.success(app.dbString + "," + app.dbFile + "," + app.dbName);
                return true;
            }else if (action.equals("moveToBack")) {
                cordova.getActivity().moveTaskToBack(true);
            }
            callbackContext.error("Invalid action");
            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        itemsList = data.getStringArrayListExtra("items");
    }
}
