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
        super("AutoTPaccept", "Принимает тп автоматом", Category.PLAYER, true, false, false);
    }
    public Setting<Boolean> onlyFriends = register(new Setting("onlyFriends", Boolean.TRUE));


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

            if(out.contains("телепортироваться")){
                if(onlyFriends.getValue()) {
                    if (Trillium.friendManager.isFriend(TrilliumUtils.solvename(out))) {
                        mc.player.sendChatMessage("/tpaccept");
                    }
                } else {
                        mc.player.sendChatMessage("/tpaccept");
                }

            }

            /*
    [20:39:09] [Client thread/INFO]: [CHAT] MrZak34 просит телепортироваться к Вам.
    [20:39:09] [Client thread/INFO]: [CHAT] Для принятия запроса, введите /tpaccept.
    [20:39:10] [Client thread/INFO]: [CHAT] Для отказа от запроса введите /tpdeny.
     */
        return true;
    }
}
