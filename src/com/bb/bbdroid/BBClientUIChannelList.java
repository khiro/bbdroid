package com.bb.bbdroid;

import java.util.ArrayList;
import java.util.List;

import com.bb.bbdroid.R;
import com.bb.bbdroid.BBClientHelper.ApiException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BBClientUIChannelList extends Activity implements OnItemClickListener {

    private ArrayList<String> listitems;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    
    // bind service
    private boolean mIsBound;
    private BBClientService mBoundService;
    
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((BBClientService.LocalBinder) service).getService();
        }
    };
    
    void doBindService() {
        bindService(new Intent(BBClientUIChannelList.this, BBClientService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.bbchannelinputdialog, null);
        return new AlertDialog.Builder(BBClientUIChannelList.this).setIcon(R.drawable.alert_dialog_icon)
                .setTitle(R.string.channel_dialog_two_buttons_title).setView(textEntryView)
                .setPositiveButton(R.string.channel_dialog_ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // get channel name from edit text
                        // join channel
                        // add storage ?
                        // update list view
                        TextView channelEditText = (TextView) textEntryView.findViewById(R.id.ChannelEditText);
                        CharSequence channel = channelEditText.getText();
                        if (channel.length() >= 1) {
	                        	// check channel string
//								mBoundService.join(channel.toString());
	                            listitems.add(channel.toString());                            
	                            arrayAdapter.notifyDataSetChanged();
	                            BBChannelStatus status = new BBChannelStatus();
	                            status.setChannelName(channel.toString());
	                            BBChannelDao dao = new BBChannelDao(BBClientUIChannelList.this);
	                            dao.save(status);
                        }
                    }

                }).setNegativeButton(R.string.channel_dialog_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // nothing to do
                    }

                }).create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbchannellist);
        setTitle("Channel List");
        listView = (ListView) findViewById(R.id.ListView02);
        listitems = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(BBClientUIChannelList.this,
                android.R.layout.simple_expandable_list_item_1, listitems);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
        
        // view list ?
		DataLoadTask task = new DataLoadTask();
		task.execute();
        
        doBindService();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channellistmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {          
        case R.id.new_channel:
            showDialog(0);
            break;
        }
        return true;
    }

	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		String channel = (String) listView.getItemAtPosition(position);
		try {
			mBoundService.join(channel.toString());
			mBoundService.setChannel(channel);
//			showIntent.putExtra(BizCard.TABLE_NAME, bizCard);
			setResult(RESULT_OK);
			finish();	
		} catch (ApiException e) {
		}
	}
	
	public class DataLoadTask extends AsyncTask<Object, Integer, List<BBChannelStatus>> {
		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected List<BBChannelStatus> doInBackground(Object... params) {
			BBChannelDao dao = new BBChannelDao(BBClientUIChannelList.this);
			return dao.list();
		}
		
		@Override
		protected void onPostExecute(List<BBChannelStatus> result) {
        	for (BBChannelStatus status : result) {
        		String channel = status.getChannelName();
        		listitems.add(channel);                            
        		arrayAdapter.notifyDataSetChanged();
        	}
		}
	}
}
