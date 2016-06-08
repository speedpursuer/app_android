package com.lee.cliplay.Push;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.alertdialogpro.AlertDialogPro;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lee.cliplay.ClipActivity;
import com.lee.cliplay.HelloJNI;

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
    public String uid = "";
    public String pwd = "";

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
    }

    public void showPush() {

        if(pushID.equals("")) return;

        OkHttpClient client = new OkHttpClient();

        String credential = Credentials.basic(uid, pwd);

        Request request = new Request.Builder()
                .header("Authorization", credential)
                .url(dbString + "cliplay_prod/" + pushID)
                .build();

        final Application app = CustomApplication.this;

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                if(!webViewLoaded) return;

                if(mCurrentActivity != null) {
                    mCurrentActivity.finish();
                }

                String responseBody = response.body().string();

                Intent intent = new Intent();
                intent.setClass(app.getApplicationContext(), ClipActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("urls", responseBody);
                intent.putExtra("pushHeader", pushHeader);
                app.getApplicationContext().startActivity(intent);

                pushID = "";
            }
        });
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
