package com.bb.bbdroid;

import java.util.ArrayList;

import org.apache.http.client.CookieStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bb.bbdroid.BBClientHelper.ApiException;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BBClientReadTask extends AsyncTask<CookieStore, Void, String> {

		private BBClientHelper client;
		private Activity activity;
		private String channel;
		
	    private ArrayList<BBMessageStatus> listitems;
	    private BBMessageAdapter arrayAdapter;
	    
	    private static String READ_ERROR = "READ_ERROR";

		public BBClientReadTask(Activity activity, String nick, String sid, String channel, ArrayList<BBMessageStatus> listitems, BBMessageAdapter arrayAdapter) {
			this.activity = activity;
			this.channel = channel;
			client = new BBClientHelper(nick, sid);
			this.listitems = listitems;
			this.arrayAdapter = arrayAdapter;
		}

		@Override
		protected String doInBackground(CookieStore... params) {
			if (channel == null) {
				return READ_ERROR;
			}
			client.setCookie(params[0]);				
			String result;
			try {
				result = client.read();
			} catch (ApiException e) {
				result = READ_ERROR;
			}
			return result;
		}
		
		protected void onPostExecute(String result) {
			// parse json
			if (!result.endsWith(READ_ERROR)) {
			    try {
                    JSONObject jsons = new JSONObject(result);
                    // need joined channel name
                    JSONArray jsonArray = jsons.getJSONArray(channel);
                    for (int i = 0; i < jsons.length(); i += 2) {
                        String nick = jsonArray.getString(i);
                        String message = jsonArray.getString(i+1);
                        BBMessageStatus status = new BBMessageStatus(nick, message);
                        listitems.add(status);
                        arrayAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    BBMessageStatus status = new BBMessageStatus("system", "json parse error");
                    listitems.add(status);
                    arrayAdapter.notifyDataSetChanged();    
                }
			}
		}
}
