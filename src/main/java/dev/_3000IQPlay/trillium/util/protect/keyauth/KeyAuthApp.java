package dev._3000IQPlay.trillium.util.protect.keyauth;

import dev._3000IQPlay.trillium.util.protect.keyauth.api.KeyAuth;

/**
 * @author SprayD
 */
public class KeyAuthApp {
	private static String url = "https://keyauth.win/api/1.1/";
	
	private static String ownerid = "DmMvZJqp39"; // You can find out the owner id in the profile settings keyauth.com
	private static String appname = "kismanccplus"; // Application name
	private static String version = "1.0"; // Application version

	public static KeyAuth keyAuth = new KeyAuth(appname, ownerid, version, url);
}
