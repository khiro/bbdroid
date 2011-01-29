package com.bb.bbdroid;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BBChannelStatus implements Serializable {
	
	public static final String TABLE_NAME = "bb_channel";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CHANNEL_NAME = "channel_name";
	public static final String COLUMN_JOIN_STATUS = "channel_status";
	
	private Long rowid = null;
	private String channelName = null;
	private String joinStatus = null;
	
	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}
	
	public Long getRowid() {
		return rowid;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public String getChannelName() {
		return channelName;
	}
	
	public void setJoinStatus(String channelStatus) {
		this.joinStatus = channelStatus;
	}
	
	public String getJoinStatus() {
		return joinStatus;
	}

}
