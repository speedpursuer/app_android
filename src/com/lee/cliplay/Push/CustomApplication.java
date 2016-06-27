package com.lee.cliplay.Push;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.alertdialogpro.AlertDialogPro;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lee.cliplay.ClipActivity;
import com.lee.cliplay.HelloJNI;
import com.lee.cliplay.R;
import com.lee.cliplay.configs.LocalDataMgr;
import com.lee.cliplay.configs.imagepipeline.ImagePipelineConfigFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xl on 16/5/31.
 */
public class CustomApplication extends Application {
    public boolean webViewLoaded = false;
    public String pushID = "";
    public String pushHeader = "";
    private Activity mCurrentActivity = null;
    public String dbString = "";
    public String dbFile = "";
    public String dbKey = "";
    public String uid = "";
    public String pwd = "";
    public String dbName = "";
    public String apiKey = "";

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbString = HelloJNI.dbStringFromJNI();
        dbFile = HelloJNI.dbFileFromJNI();
        uid = HelloJNI.uidFromJNI();
        pwd = HelloJNI.pwdFromJNI();
        dbKey = HelloJNI.keyFromJNI();
        dbName = HelloJNI.dbNameFromJNI();
        apiKey = HelloJNI.apiKeyFromJNI();
        setupPushService(apiKey);
        Fresco.initialize(this, ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this));
        LocalDataMgr.init(this, dbKey);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Fresco.shutDown();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    public void showPush() {

        if(pushID.equals("")) return;

        OkHttpClient client = new OkHttpClient();

        String credential = Credentials.basic(uid, pwd);

        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(dbString + dbName + "/" + pushID)
                .build();

        final CustomApplication app = CustomApplication.this;

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                if(!app.webViewLoaded) return;

                String responseBody = response.body().string();

                try{
                    JSONObject dataJson = new JSONObject(responseBody);
                    String url = dataJson.getString("image");
                    if(url == null) return;
                }catch (JSONException e){
                    e.printStackTrace();
                    return;
                }

                Intent intent = new Intent();

                intent.putExtra("urls", responseBody);
                intent.putExtra("pushHeader", app.pushHeader);

                Context context = app.getCurrentActivity();

                if(app.mCurrentActivity != null && !app.mCurrentActivity.getLocalClassName().equals("MainActivity")) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                intent.setClass(context, ClipActivity.class);
                context.startActivity(intent);

                app.pushID = "";
            }
        });
    }

    private void setupPushService(String apiKey) {
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, apiKey);
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(0, 0, 0, 0);
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setNotificationSound("android.resource://" + getPackageName() + "/" + R.raw.sound);
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }

    public void showDialog(String title, String desc, boolean clean, Context context) {

        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(context);
        builder.setTitle(title).setMessage(desc);

        if(!clean) {
            builder.setNegativeButton("好", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }else{
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    imagePipeline.clearCaches();
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }

        builder.show();
    }
}
