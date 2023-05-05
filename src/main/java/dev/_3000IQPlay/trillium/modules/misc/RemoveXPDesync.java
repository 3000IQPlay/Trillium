package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class RemoveXPDesync
        extends Module {
	public final Setting<Float> timer = this.register(new Setting<Float>("Timer", 1.0f, 0.1f, 10.0f));
	public final Setting<Float> force = this.register(new Setting<Float>("Force", 111.0f, 0.1f, 120.0f));
	public final Setting<Float> attempts = this.register(new Setting<Float>("Attempts", 1.0f, 1.0f, 20.0f));
    protected List<Entity> activeEntities = new ArrayList<>();
    protected boolean started;
    protected int index;
	
	public RemoveXPDesync() {
        super("RemoveXPDesync", "Attempts to remove exp desync", Module.Category.MISC, false, false, false);
    }
	
	@Override
    public void onUpdate(){
        if (this.started) {
            if (this.index < this.attempts.getValue()) {
                mc.player.setPosition(mc.player.posX, mc.player.posY - this.force.getValue(), mc.player.posZ);
                this.index++;
				Trillium.TIMER = this.timer.getValue();
            } else {
                Trillium.TIMER = 1.0f;
                this.index = 0;
                this.started = false;
            }
        }
        this.activeEntities = mc.world.loadedEntityList;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            final CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem) event.getPacket();
            if (this.isEXP(packet.getHand())) {
                this.started = true;
            }
        }
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (this.isEXP(packet.getHand())) {
                this.started = true;
            }
        }
    }
	
	protected boolean isEXP(final EnumHand enumHand){
        return mc.player.getHeldItem(enumHand).getItem().equals(Items.EXPERIENCE_BOTTLE);
    }
}