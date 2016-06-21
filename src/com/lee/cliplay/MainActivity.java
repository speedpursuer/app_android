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
import android.os.Bundle;

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
//        setupPushService(mMyApp.apiKey);
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

    @Override
    public void onPause() {
        super.onPause();
    }

//    private void setupPushService(String apiKey) {
//        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, apiKey);
//        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(0, 0, 0, 0);
//        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//        cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
//        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//        cBuilder.setNotificationSound("android.resource://" + getPackageName() + "/" + R.raw.sound);
//        PushManager.setNotificationBuilder(this, 1, cBuilder);
//    }
}
