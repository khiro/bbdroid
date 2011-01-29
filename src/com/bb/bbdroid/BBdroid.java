package com.bb.bbdroid;

import com.bb.bbdroid.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BBdroid extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbdroid);
		
		SharedPreferences pref = getSharedPreferences("pref",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
		final Editor e = pref.edit();
		setTextString(R.id.EditText01, pref.getString(getTextViewString(R.id.TextView01), ""));
		setTextString(R.id.EditText02, pref.getString(getTextViewString(R.id.TextView02), ""));
		setTextString(R.id.EditText03, pref.getString(getTextViewString(R.id.TextView03), ""));
		setTextString(R.id.EditText04, pref.getString(getTextViewString(R.id.TextView04), ""));

		// on button
		Button addButton = (Button) findViewById(R.id.Button01);
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				e.putString(getTextViewString(R.id.TextView01), getEditTextString(R.id.EditText01));
				e.putString(getTextViewString(R.id.TextView02), getEditTextString(R.id.EditText02));
				e.putString(getTextViewString(R.id.TextView03), getEditTextString(R.id.EditText03));
				e.putString(getTextViewString(R.id.TextView04), getEditTextString(R.id.EditText04));
				e.commit();
				
				// start bbclient ui
				Intent showIntent;
				showIntent = new Intent(BBdroid.this, BBClientUI.class);
				startActivity(showIntent);
			}
		});
    }
	
	private void setTextString(int id, String value){
		TextView mText = (TextView) findViewById(id);
		mText.setText(value);
	}

	private String getTextViewString(int id) {
		TextView mText = (TextView) findViewById(id);
		return (String) mText.getText();
	}
	
	private String getEditTextString(int id) {
		EditText mText = (EditText) findViewById(id);
		SpannableStringBuilder sb = (SpannableStringBuilder) mText.getText();
		return sb.toString();
	}
}