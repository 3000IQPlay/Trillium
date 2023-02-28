package dev._3000IQPlay.trillium.util.protect.keyauth;

import dev._3000IQPlay.trillium.util.protect.keyauth.api.KeyAuth;

/**
 * @author SprayD
 */
public class KeyAuthApp {
	private static final String url = "https://keyauth.win/api/1.1/";
	
	private static final String ownerid = "DmMvZJqp39"; // You can find out the owner id in the profile settings keyauth.com
	private static final String appname = "TrilliumVerify"; // Application name
	private static final String version = "1.0"; // Application version

	public static KeyAuth keyAuth = new KeyAuth(appname, ownerid, version, url);
}
