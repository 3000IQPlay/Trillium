package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.event.events.UpdateWalkingPlayerEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoBreed extends Module {
    public Setting<Float> distance = this.register(new Setting<>("Range", 5.0f, 1.0f, 10.0f));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
	EntityAnimal toFeed;
	
	public AutoBreed() {
        super("AutoBreed", "Automatically forces animals to make babies", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        super.onTick();
        for (Entity e : AutoBreed.mc.world.loadedEntityList) {
            if (e instanceof EntityAnimal) {
                final EntityAnimal animal = (EntityAnimal) e;
                if (animal.getHealth() > 0) {
                    if (!animal.isChild() && !animal.isInLove() && AutoBreed.mc.player.getDistanceSq(animal) <= 4.5f && animal.isBreedingItem(AutoBreed.mc.player.inventory.getCurrentItem())) {
                        toFeed = animal;
                        doFeedStuff(this.rotate.getValue());
                    }
                }
            }
        }
    }

    public void doFeedStuff(Boolean rotate) {
        if (rotate) {
            AutoBreed.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(toFeed.prevRotationYaw, toFeed.prevRotationPitch, true));
            AutoBreed.mc.playerController.interactWithEntity(AutoBreed.mc.player, toFeed, EnumHand.MAIN_HAND);
        } else {
            AutoBreed.mc.playerController.interactWithEntity(AutoBreed.mc.player, toFeed, EnumHand.MAIN_HAND);
        }
    }
}