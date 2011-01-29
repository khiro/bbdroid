package com.bb.bbdroid;

import java.util.ArrayList;

import com.bb.bbdroid.R;
import com.bb.bbdroid.BBClientHelper.ApiException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BBClientUI extends Activity implements OnItemClickListener {

    private ArrayList<BBMessageStatus> listitems;    
    private BBMessageAdapter arrayAdapter;    
    private ListView listView;
    private String channel;
    private String nick;

    private boolean mIsBound;
    private BBClientService mBoundService;

    
    // read handler
    final android.os.Handler handler = new android.os.Handler();
    final Runnable mRunnable = new Runnable() {
        public void run() {
            BBClientReadTask task = new BBClientReadTask(BBClientUI.this, mBoundService.getNick(), mBoundService.getSid(), mBoundService.getChannel(), listitems, arrayAdapter);
            task.execute(mBoundService.getCookie());
            handler.postDelayed(this, 10000);
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((BBClientService.LocalBinder) service).getService();
            
            // set host, port, nick, pass
            SharedPreferences pref = getSharedPreferences("pref",MODE_WORLD_READABLE);
            mBoundService.setHost(pref.getString(getString(R.string.host), ""));
            mBoundService.setPort(pref.getString(getString(R.string.port), ""));
            mBoundService.setPassword(pref.getString(getString(R.string.password), ""));
            mBoundService.setNick(nick);
            try {
                mBoundService.connect();
            } catch (ApiException e) {
				BBMessageStatus status = new BBMessageStatus("system", "connect error");
                listitems.add(status);
                arrayAdapter.notifyDataSetChanged();
            }

            channel = mBoundService.getChannel();
            if (channel == null) {
        		BBMessageStatus status = new BBMessageStatus("system", "you need to join channel");            	
        		listitems.add(status);
        		arrayAdapter.notifyDataSetChanged();
            } else {
            	try {
            		// check joined channel
            		mBoundService.join(channel);
            	} catch (ApiException e) {
            		BBMessageStatus status = new BBMessageStatus("system", "join error");            	
            		listitems.add(status);
            		arrayAdapter.notifyDataSetChanged();
            	}
            }
        }
    };

    void doStartService() {
        startService(new Intent(BBClientUI.this, BBClientService.class));
    }

    void doStopService() {
        stopService(new Intent(BBClientUI.this, BBClientService.class));
    }

    void doBindService() {
        bindService(new Intent(BBClientUI.this, BBClientService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        // doStopService();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		// need asynctask
//		String title = mBoundService.getNick() + " " + mBoundService.getChannel() + " Topic";
//        setTitle(title);
        handler.removeCallbacks(mRunnable);
        handler.postDelayed(mRunnable, 1000);
	}

    private OnClickListener mSayListener = new OnClickListener() {
        public void onClick(View v) {
            TextView messageEditText = (TextView) findViewById(R.id.MessageEditText);
            CharSequence message = messageEditText.getText();
            if (message.length() >= 1) {
                // send message to server
                // add message and update listview
                try {
                    mBoundService.say(message.toString());
                    // get user name
    				BBMessageStatus status = new BBMessageStatus(nick, message.toString());
                    listitems.add(status);
                    arrayAdapter.notifyDataSetChanged();
                } catch (ApiException e) {
                	BBMessageStatus status = new BBMessageStatus("system", e.toString());                	
                    listitems.add(status);
                    arrayAdapter.notifyDataSetChanged();
                }
                // record message?
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbchannel);
        setTitle("nick channel and Topic");

        Button button = (Button) findViewById(R.id.SayButton);
        button.setOnClickListener(mSayListener);

        listView = (ListView) findViewById(R.id.MessageView);
        listitems = new ArrayList<BBMessageStatus>();
        arrayAdapter = new BBMessageAdapter(BBClientUI.this, android.R.layout.simple_expandable_list_item_1,
                listitems);
        listView.setAdapter(arrayAdapter);
        
        SharedPreferences pref = getSharedPreferences("pref",MODE_WORLD_READABLE);
        nick = pref.getString(getString(R.string.nickname), "");
        // start service
        // bind?
        doStartService();
        doBindService();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channelmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.channel_list:
            Intent showIntent = new Intent(this, BBClientUIChannelList.class);
            startActivity(showIntent);
            break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    };
}
