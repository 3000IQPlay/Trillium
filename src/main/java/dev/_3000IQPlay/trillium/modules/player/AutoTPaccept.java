package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.TrilliumUtils;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class AutoTPaccept extends Module {

    public AutoTPaccept() {
        super("AutoTPAccept", "Accepts tp automatically", Category.PLAYER, true, false, false);
    }
    public Setting<Boolean> onlyFriends = register(new Setting("OnlyFriends", Boolean.TRUE));


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketChat) {
            final SPacketChat packet = (SPacketChat)event.getPacket();
            if (packet.getType() != ChatType.GAME_INFO && this.tryProcessChat(packet.getChatComponent().getFormattedText(), packet.getChatComponent().getUnformattedText())) {
               // event.setCanceled(true);
            }
        }
    }




    private boolean tryProcessChat(String message, final String unformatted) {
        String out = message;
        out = message;
            if (Util.mc.player == null) {
                return false;
            }
            if(out.contains("teleport")){
                if(onlyFriends.getValue()) {
                    if (Trillium.friendManager.isFriend(TrilliumUtils.solvename(out))) {
                        mc.player.sendChatMessage("/tpaccept");
                    }
                } else {
                    mc.player.sendChatMessage("/tpaccept");
                }
            }
        return true;
    }
}
