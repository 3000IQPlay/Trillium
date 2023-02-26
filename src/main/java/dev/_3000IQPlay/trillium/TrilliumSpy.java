package dev._3000IQPlay.trillium;

import dev._3000IQPlay.trillium.gui.auth.AuthGui;
import dev._3000IQPlay.trillium.util.protect.keyauth.api.KeyAuth;
import dev._3000IQPlay.trillium.util.protect.keyauth.util.HWID;
import dev._3000IQPlay.trillium.util.protect.WebhookUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrilliumSpy {
	public static String enteredKey = "";
	
    public static void sendLogginSuccess() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " successfully logged in!");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			embed.addField("Key", "" + "||" + enteredKey + "||", false);
			embed.addField("HWID", "" + HWID.getHWID(), false);
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendLogginFail() {
        try {
			if (enteredKey.isEmpty()) {
				enteredKey = "null";
			}
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " failed to log in! (Possible attacker)");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
			embed.addField("Key", "" + "||" + enteredKey + "||", false);
			embed.addField("PC-Name", "" + "||" + System.getProperty("user.name") + "||", false);
			embed.addField("OS-Name", "" + System.getProperty("os.name"), false);
			embed.addField("HWID", "" + HWID.getHWID(), false);
            embed.setColor(Color.RED);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendLaunch() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " ran Trillium");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
            embed.setColor(Color.GREEN);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void sendExit() {
        try {
            WebhookUtil webhook = new WebhookUtil("https://discord.com/api/webhooks/1079113808141885490/k_VCH-9WM4KMcr6wxHj9Vp_ti6w79-5l_EY8fQ0I47aCnKK0f-17-HtmnDfJoOUURgx5");
            WebhookUtil.EmbedObject embed = new WebhookUtil.EmbedObject();
            embed.setTitle(Minecraft.getMinecraft().getSession().getUsername() + " exited Minecraft");
            embed.setThumbnail("https://crafatar.com/avatars/" + Minecraft.getMinecraft().getSession().getProfile().getId() + "?size=128&overlay");
            embed.setColor(Color.GRAY);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);
            webhook.execute();
        } catch (Exception e) {
            // ignore
        }
    }
	
	public static void getKey(String key) {
		enteredKey = key;
	}

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }
}