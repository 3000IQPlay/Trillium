package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.util.ItemUtil;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFish extends Module{
    public AutoFish() {
        super("AutoFish", "Auto fishes for u ._.", Category.MISC, true, false, false);
    }


    private int rodSlot = -1;

    @Override
    public void onEnable() {
        if (nullCheck()) {
            toggle();
            return;
        }
        rodSlot = ItemUtil.findItem(ItemFishingRod.class);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event){
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.NEUTRAL && packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
                if( rodSlot == -1 )
                    rodSlot = ItemUtil.findItem(ItemFishingRod.class);
                if( rodSlot != -1 )
                {
                    int startSlot = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(rodSlot));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (startSlot != -1)
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));
                }
            }
        }
    }
}

