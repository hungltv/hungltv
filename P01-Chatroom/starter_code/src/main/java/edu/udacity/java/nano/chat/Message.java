package edu.udacity.java.nano.chat;

import org.json.JSONObject;

/**
 * WebSocket message model
 */
public class Message {
    // TODO: add message model.
	private String msg;
    private String user;
    private String type;
    private int onlineCount;
	
    public Message(JSONObject data) {
		super();
		this.msg = data.optString("msg");
		this.user = data.optString("user");
		this.type = data.optString("type");
		this.onlineCount = data.optInt("onlineCount");
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getOnlineCount() {
		return onlineCount;
	}
	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

    
	
}
