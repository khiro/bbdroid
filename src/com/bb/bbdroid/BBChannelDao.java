package com.bb.bbdroid;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BBChannelDao {
	
	private BBChannelDBOpenHelper helper = null;
	
	public BBChannelDao(Context context) {
		helper = new BBChannelDBOpenHelper(context);
	}

	public BBChannelStatus save(BBChannelStatus status) {
		SQLiteDatabase db = helper.getWritableDatabase();
		BBChannelStatus result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(BBChannelStatus.COLUMN_CHANNEL_NAME, status.getChannelName());
			values.put(BBChannelStatus.COLUMN_JOIN_STATUS, status.getJoinStatus());
			Long rowId = status.getRowid();
			if(rowId == null) {
				rowId = db.insert(BBChannelStatus.TABLE_NAME, null, values);
			} else {
				db.update(BBChannelStatus.TABLE_NAME, values, BBChannelStatus.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});
			}
			result = load(rowId);
		} finally {
			db.close();
		}
		return result;
	}
	
	public void delete(BBChannelStatus status) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete(BBChannelStatus.TABLE_NAME, BBChannelStatus.COLUMN_ID + "=?", new String[]{ String.valueOf( status.getRowid())});
		} finally {
			db.close();
		}
	}
	
	public BBChannelStatus load(Long rowId) {
		SQLiteDatabase db = helper.getReadableDatabase();
		BBChannelStatus status = null;
		try {
			Cursor cursor = db.query( BBChannelStatus.TABLE_NAME, null, BBChannelStatus.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
			cursor.moveToFirst();
			status= getBBChannelStatus(cursor);
		} finally {
			db.close();
		}
		return status;
	}
	
	public List<BBChannelStatus> list() {
		List<BBChannelStatus> bbChannelStatusList = new ArrayList<BBChannelStatus>();
		SQLiteDatabase db = helper.getReadableDatabase();
		try {
			Cursor cursor = db.query( BBChannelStatus.TABLE_NAME, null, null, null, null, null, BBChannelStatus.COLUMN_ID);
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				bbChannelStatusList.add(getBBChannelStatus(cursor));
				cursor.moveToNext();
			}
		} finally {
			db.close();
		}
		return bbChannelStatusList;
	}
	
	private BBChannelStatus getBBChannelStatus(Cursor cursor) {
		BBChannelStatus status = new BBChannelStatus();
		status.setRowid(cursor.getLong(0));
		status.setChannelName(cursor.getString(1));
		status.setJoinStatus(cursor.getString(2));
		return status;
	}

}
