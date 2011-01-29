package com.bb.bbdroid;

public class BBMessageStatus {
	private String nick;
	private String message;
	
	BBMessageStatus() {
	}
	
	BBMessageStatus(String nick, String message) {
		this.nick = nick;
		this.message = message;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getNick() {
		return nick;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	
	
}
