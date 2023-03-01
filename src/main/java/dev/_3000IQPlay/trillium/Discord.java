package dev._3000IQPlay.trillium;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import dev._3000IQPlay.trillium.modules.client.RPC;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.gui.GuiScreenServerList;

import java.io.*;
import java.util.Objects;

import static dev._3000IQPlay.trillium.util.ItemUtil.mc;

public class Discord {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    private static int index;
    public static boolean started;
    static {
        index = 1;
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence ( );
    }

    public static void start ( ) {
        started = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers ( );
        rpc.Discord_Initialize("1079687534864515092", handlers, true, "");
            Discord.presence.startTimestamp = (System.currentTimeMillis() / 1000L);
            Discord.presence.details = Util.mc.currentScreen instanceof GuiMainMenu ? "In the main menu" : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue() ? Minecraft.getMinecraft().currentServerData.serverIP.equals("localhost") ? "on " + "2bt2.org via 2bored2wait" : "on " + Minecraft.getMinecraft().currentServerData.serverIP : " Multiplayer") : " Singleplayer");
            Discord.presence.state = RPC.INSTANCE.state.getValue();
            Discord.presence.largeImageText = "Trillium INC.";
            rpc.Discord_UpdatePresence(presence);
            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    rpc.Discord_RunCallbacks();

                    if (!RPC.inQ) {
                        Discord.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu || Minecraft.getMinecraft().currentScreen instanceof GuiScreenServerList || Minecraft.getMinecraft().currentScreen instanceof GuiScreenAddServer ? "Gaming on " : (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP : " Multiplayer") : " Singleplayer");
                    }
                    if (RPC.inQ) {
                        Discord.presence.state = "In queue: " + RPC.position;
                    } else {
                        Discord.presence.state = RPC.INSTANCE.state.getValue();
                    }


                    if (RPC.INSTANCE.nickname.getValue()) {
                        Discord.presence.smallImageText = "User: " + mc.session.getUsername();
                        Discord.presence.smallImageKey = "https://minotar.net/helm/" + mc.session.getUsername() + "/100.png";
                    }
                    Discord.presence.largeImageKey = "https://cdn.discordapp.com/attachments/873253458315214849/1080518441557049416/TrilliumOnTop.png";
                    rpc.Discord_UpdatePresence(presence);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException ignored) {
                    }
                }
            }, "RPC-Callback-Handler");
            thread.start();
    }

    static String String1 = "none";

    public static void stop ( ) {
        started = false;
        if ( thread != null && ! thread.isInterrupted ( ) ) {
            thread.interrupt ( );
        }
        rpc.Discord_Shutdown ( );
    }
}