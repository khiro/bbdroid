package com.bb.bbdroid;

import java.util.ArrayList;

import com.bb.bbdroid.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BBMessageAdapter extends ArrayAdapter {
	
	private ArrayList items;
	private LayoutInflater inflater;

	public BBMessageAdapter(Context context, int textViewResourceId, ArrayList items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.bbmessagerow, null);
		}
		BBMessageStatus item = (BBMessageStatus) items.get(position);
		if (item != null) {
			TextView screenName = (TextView)view.findViewById(R.id.toptext);  
			screenName.setTypeface(Typeface.DEFAULT_BOLD);  
			TextView text = (TextView)view.findViewById(R.id.bottomtext);  
			if (screenName != null) {  
				screenName.setText(item.getNick());
			}
			if (text != null) {
				text.setText(item.getMessage());
			}
		}
		return view;
	}
}
