package dev._3000IQPlay.trillium.util.protect.keyauth.user;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserData {

	private final String username;
	private final String subscription;
	private final String expiry;

	public UserData(JSONObject json) {
		JSONObject info = json.getJSONObject("info");

		JSONArray subArray = info.getJSONArray("subscriptions");
		JSONObject subObject = subArray.getJSONObject(0);
		
		this.username = info.getString("username");
		this.subscription = subObject.getString("subscription");
		this.expiry = subObject.getString("expiry");
	}

	public String getUsername() {
		return username;
	}

	public String getSubscription() {
		return subscription;
	}

	public String getExpiry() {
		return expiry;
	}
}