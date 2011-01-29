/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.bb.bbdroid;

import org.apache.http.client.CookieStore;

import com.bb.bbdroid.BBClientHelper.ApiException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;

public class BBClientService extends Service {
    
    private BBClientHelper client;
    
    public class LocalBinder extends Binder {
        BBClientService getService() {
            return BBClientService.this;
        }
    }
    
    public String getSid() {
        return client.getSid();
    }
    
    public CookieStore getCookie() {
        return client.getCookie();
    }
    
    public String connect() throws ApiException {
        return client.connect();
    }
    
    public String join(String channel) throws ApiException {
        String ret = client.join(channel);
        client.setChannel(channel);
        return ret;
    }
    
    public String say(String message) throws ApiException {
        return client.say(message);
    }
    
    public String getNick() {
        return client.getNick();
    }
    
    public String getChannel() {
        return client.getChannel();
    }
    
    public void setChannel(String channel) {
        client.setChannel(channel);
    }
    
    public void setNick(String nick) {
        client.setNick(nick);
    }
    
    public void setHost(String host) {
        client.setHost(host);
    }
    
    public void setPort(String port) {
        client.setPort(port);
    }
    
    public void setPassword(String password) {
        client.setPassword(password);
    }

    
    @Override
    public void onCreate() {
//        client = new BBClientHelper("bb.isasaka.net", "6667", "bbcli", "guestdegues", null);
    	client = new BBClientHelper();
        client.prepareUserAgent(this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
    
    private final IBinder mBinder = new LocalBinder();

}
