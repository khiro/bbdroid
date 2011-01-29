package com.bb.bbdroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BBChannelDBOpenHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "BB_CLIENT";

	public BBChannelDBOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		
		try{
			StringBuilder createSql = new StringBuilder();
			createSql.append("create table " + BBChannelStatus.TABLE_NAME + " (");
			createSql.append(BBChannelStatus.COLUMN_ID + " integer primary key autoincrement not null,");
			createSql.append(BBChannelStatus.COLUMN_CHANNEL_NAME + " text not null,");
			createSql.append(BBChannelStatus.COLUMN_JOIN_STATUS + " text");
			createSql.append(")");

			db.execSQL( createSql.toString());	
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
