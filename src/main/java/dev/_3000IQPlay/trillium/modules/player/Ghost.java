package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ghost extends Module {
    public Ghost() {
        super("Ghost", "You can fly anywhere when you die", Category.PLAYER, true, false, false);
    }

    private boolean bypass = false;

    @Override
    public void onEnable() {
        bypass = false;
    }

    @Override
    public void onDisable() {
        if(mc.player != null) mc.player.respawnPlayer();
        bypass = false;
    }

    @Override
    public void onUpdate() {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() == 0.0f) {
            mc.player.setHealth(20.0f);
            mc.player.isDead = false;
            bypass = true;
            mc.displayGuiScreen(null);
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event){
        if(bypass && event.getPacket() instanceof CPacketPlayer) event.setCanceled(true);
    }
}
