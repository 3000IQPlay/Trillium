package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiFriendHit extends Module {
    public AntiFriendHit() {
        super("AntiFriendHit", "Prevents you from hitting your friends", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event){
        if (AntiFriendHit.nullCheck()){return;}
        if (event.getPacket() instanceof CPacketUseEntity){
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if(packet.getAction().equals(CPacketUseEntity.Action.ATTACK)){
                Entity entity = packet.getEntityFromWorld(mc.world);
                if(entity instanceof EntityPlayer) {
                    if (Trillium.friendManager.isFriend((EntityPlayer) entity)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}