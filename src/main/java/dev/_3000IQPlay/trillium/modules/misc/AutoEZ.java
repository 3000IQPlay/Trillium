package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.TrilliumUtils;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class AutoEZ extends Module {
    public AutoEZ() {
        super("AutoEZ", "Writes nice words when you kill smone C:", Category.MISC, true, false, false);
        loadEZ();
    }

    private Setting<ModeEn> Mode = register(new Setting("Mode", ModeEn.Basic));

    public enum ModeEn {
        Custom,
        Basic
    }

    public Setting<Boolean> global = this.register ( new Setting <> ( "global", true));
    String a = "";
    String b = "";
    String c = "";


    @Override
    public void onEnable(){
        loadEZ();
    }


    String[] EZ = new String[]{
            "%player% Get Owned Faggot HAHAHA",
            "%player% You are such a New Gen LMFAO",
            "%player% EZZZZZ LMAOO",
            "%player% Kiddo EZZZZZZZZZ",
            "%player% Go Cry abt it LEL",
			"%player% LOL GG EZ",
    };

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketReceive(PacketEvent.Receive e){
        if(fullNullCheck()) return;
        if (e.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = e.getPacket();
            if (packet.getType() != ChatType.GAME_INFO) {
                a = packet.getChatComponent().getFormattedText();
                if(a.contains("You killed a player")){
                    b = TrilliumUtils.solvename(a);

                    if(Mode.getValue() == ModeEn.Basic) {
                        int n;
                        n = (int) Math.floor(Math.random() * EZ.length);
                        c = EZ[n].replace("%player%", b);
                    } else {
                        if(EZWORDS.isEmpty()){
                            Command.sendMessage("The file with AutoEZ is empty!");
                            return;
                        }
                        c = EZWORDS.get(new Random().nextInt(EZWORDS.size()));
                        c = c.replaceAll("%player%", b);
                    }

                    mc.player.sendChatMessage(global.getValue() ? "!" + c : c);
                }
            }
        }
    }


    public static ArrayList<String> EZWORDS = new ArrayList<>();




    public static void loadEZ() {
        try {
            File file = new File("Trillium/AutoEZ.txt");
            if (!file.exists()) file.createNewFile();;

            new Thread(() -> {
                try {

                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr);



                    ArrayList<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    boolean newline = false;

                    for (String l : lines) {
                        if (l.equals("")) {
                            newline = true;
                            break;
                        }
                    }

                    EZWORDS.clear();
                    ArrayList<String> spamList = new ArrayList<>();

                    if (newline) {
                        StringBuilder spamChunk = new StringBuilder();

                        for (String l : lines) {
                            if (l.equals("")) {
                                if (spamChunk.length() > 0) {
                                    spamList.add(spamChunk.toString());
                                    spamChunk = new StringBuilder();
                                }
                            } else {
                                spamChunk.append(l).append(" ");
                            }
                        }
                        spamList.add(spamChunk.toString());
                    } else {
                        spamList.addAll(lines);
                    }

                    EZWORDS = spamList;
                } catch (Exception e) {
                    System.err.println("Could not load file ");
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Could not load file ");
        }

    }


}
