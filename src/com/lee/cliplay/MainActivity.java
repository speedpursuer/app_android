/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.lee.cliplay;

import android.app.Activity;
import android.app.Notification;
import android.content.DialogInterface;
import android.os.Bundle;

import com.alertdialogpro.AlertDialogPro;
import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lee.cliplay.Push.CustomApplication;

import org.apache.cordova.CordovaActivity;

public class MainActivity extends CordovaActivity
{
    protected CustomApplication mMyApp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mMyApp = (CustomApplication)this.getApplicationContext();
        setupPushService();
        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity currActivity = mMyApp.getCurrentActivity();
        if (this.equals(currActivity))
            mMyApp.setCurrentActivity(null);
    }

    private void setupPushService() {
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "Sk05U14kHEFgG6hEpiEDczkE");
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(0, 0, 0, 0);
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
        cBuilder.setNotificationSound("android.resource://" + getPackageName() + "/" + R.raw.sound);
        PushManager.setNotificationBuilder(this, 1, cBuilder);
    }

    public void showDialog(String title, String desc, boolean clean) {

//        if(dialog!= null) dialog = null;

//        TextView view = new TextView(this);
//        view.setText(title);
//        view.setGravity(Gravity.CENTER);
//        view.setTextColor(Color.WHITE);
//        view.setTextSize(20);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        if(!clean) {
//            builder.setCustomTitle(view)
//                .setMessage(desc)
//                .setNegativeButton("好", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//        }else{
//            builder.setCustomTitle(view)
//                .setMessage(desc)
//                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                        imagePipeline.clearCaches();
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//        }
//
//        builder.create().show();


        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
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
