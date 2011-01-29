package com.bb.bbdroid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.bb.bbdroid.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class BBClientHelper {

    private static String HOST = "http://bb.isasaka.net/api/";
    private static String sUserAgent = null;
    private static final String TAG = "BBSampleClient";
    private static byte[] sBuffer = new byte[512];
    private DefaultHttpClient client;
    private String host;
    private String port;
    private String nick;
    private String password;
    private String sid;
    private String channel;

    public static class ApiException extends Exception {
        public ApiException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ApiException(String detailMessage) {
            super(detailMessage);
        }
    }
    
    BBClientHelper() {
        client = new DefaultHttpClient();
        this.channel = null;
    }

    BBClientHelper(String nick, String sid) {
        client = new DefaultHttpClient();
        this.nick = nick;
        this.sid = sid;
        this.channel = null;
    }

    BBClientHelper(String host, String port, String nick, String password, String sid) {
        client = new DefaultHttpClient();
        this.host = host;
        this.port = port;
        this.nick = nick;
        this.password = password;
        this.sid = sid;
        this.channel = null;
    }

    public String getSid() {
        return sid;
    }

    public String getUserAgent() {
        return sUserAgent;
    }

    public CookieStore getCookie() {
        return client.getCookieStore();
    }

    public void setCookie(CookieStore cookieStore) {
        client.setCookieStore(cookieStore);
    }
    
    public String getNick() {
        return nick;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public void setNick(String nick) {
    	this.nick = nick;
    }
    
    public void setHost(String host) {
    	this.host = host;
    }
    
    public void setPort(String port) {
    	this.port = port;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }


    public void prepareUserAgent(Context context) {
        try {
            // Read package name and version number from manifest
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            sUserAgent = String.format(context.getString(R.string.template_user_agent), info.packageName,
                    info.versionName);

        } catch (NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package information in PackageManager", e);
        }
    }

    public synchronized String connect() throws ApiException {
        String url = HOST + "CONNECT";
        List<NameValuePair> objValuePairs = getPostParameters();
        try {
            HttpResponse response = sendRequest(url, objValuePairs);
            String cookie = response.getFirstHeader("Set-Cookie").getValue();
            if (cookie.length() != 0) {
                setNewSessionID(cookie);
            }
            ByteArrayOutputStream content = readResponse(response);
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with CONNECT API", e);
        }
    }

    private HttpResponse sendRequest(String url, List<NameValuePair> params) throws ApiException {
        HttpPost request = new HttpPost(url);
        request.setHeader("User-Agent", sUserAgent);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("Encoding Exception at Connect API", e);
        }
        HttpResponse response;
        try {
            response = client.execute(request);
            // check http response code
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            	throw new ApiException("BB API Status code was not 200");	
            }
        } catch (IOException e) {
            throw new ApiException("Problem communicating with client.execute", e);
        }
        return response;
    }

    private ByteArrayOutputStream readResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        int readBytes = 0;
        while ((readBytes = inputStream.read(sBuffer)) != -1) {
            content.write(sBuffer, 0, readBytes);
        }
        return content;
    }

    private List<NameValuePair> getPostParameters() {
        List<NameValuePair> objValuePairs = new ArrayList<NameValuePair>(2);
        objValuePairs.add(new BasicNameValuePair("nick", nick));
        objValuePairs.add(new BasicNameValuePair("pass", password));
        objValuePairs.add(new BasicNameValuePair("server", host));
        objValuePairs.add(new BasicNameValuePair("port", port));
        if (sid != null) {
            objValuePairs.add(new BasicNameValuePair("sid", sid));
        }
        return objValuePairs;
    }

    private void setNewSessionID(String cookie) {
        String[] cookies = cookie.split(";", -1);
        String[] newsid = cookies[0].split("=", -1);
        sid = newsid[1];
        // save pref here
    }

    public synchronized String join(String channel) throws ApiException {
        if (sid == null) {
            throw new ApiException("session id is null");
        }
        String url = HOST + "JOIN";
        List<NameValuePair> objValuePairs = getPostParameters();
        objValuePairs.add(new BasicNameValuePair("channel", channel));
        try {
            HttpResponse response = sendRequest(url, objValuePairs);
            ByteArrayOutputStream content = readResponse(response);
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with CONNECT API", e);
        }
    }

    public synchronized String say(String message) throws ApiException {
        if (sid == null) {
            throw new ApiException("session id is null");
        } else if (channel == null) {
            throw new ApiException("channel is null");        	
        }
        String url = HOST + "SPEAK";
        List<NameValuePair> objValuePairs = getPostParameters();
        objValuePairs.add(new BasicNameValuePair("message", message));
        objValuePairs.add(new BasicNameValuePair("channel", channel));
        try {
            HttpResponse response = sendRequest(url, objValuePairs);
            ByteArrayOutputStream content = readResponse(response);
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with CONNECT API", e);
        }
    }

    public synchronized String read() throws ApiException {
        if (sid == null) {
            throw new ApiException("session id is null");
        }
        String url = HOST + "READ";
        List<NameValuePair> objValuePairs = getPostParameters();
        try {
            HttpResponse response = sendRequest(url, objValuePairs);
            ByteArrayOutputStream content = readResponse(response);
            return new String(content.toByteArray());
        } catch (IOException e) {
            throw new ApiException("Problem communicating with CONNECT API", e);
        }
    }
}
