package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.PlayerUpdateEvent;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Predicate;

public class AutoFish
        extends Module {
    private final Setting<Integer> castingDelay = this.register(new Setting<>("CastingDelay", 20, 0, 40));
    private final Setting<Float> maxSoundDistance = this.register(new Setting<>("MaxSoundDistance", Float.valueOf(2.0f), Float.valueOf(0.0f), Float.valueOf(12.0f)));
    private final Setting<Integer> failSafeTime = this.register(new Setting<>("FailSafeTime", 600, 0, 3000));
    private int cDelay = 0;
    private int Field1602 = 0;
    private boolean Field1603 = false;
	
	public AutoFish() {
        super("AutoFish", "Auto fishes for you bro", Module.Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.cDelay = 0;
        this.Field1602 = 0;
        this.Field1603 = false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean getSoundEffect(SPacketSoundEffect sPacketSoundEffect) {
        EntityPlayerSP entityPlayerSP = AutoFish.mc.player;
        if (!sPacketSoundEffect.getSound().equals(SoundEvents.ENTITY_BOBBER_SPLASH)) return false;
        if (entityPlayerSP == null) return false;
        if (entityPlayerSP.fishEntity == null) return false;
        if (((Float)this.maxSoundDistance.getValue()).floatValue() == 0.0f) return true;
        Vec3d vec3d = new Vec3d(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ());
        if (!(entityPlayerSP.fishEntity.getPositionVector().distanceTo(vec3d) <= (double)((Float)this.maxSoundDistance.getValue()).floatValue())) return false;
        return true;
    }

    public void rightClick() {
        if (this.cDelay <= 0) {
            AutoFish.mc.rightClickMouse();
            this.cDelay = (Integer)this.castingDelay.getValue();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
		if (AutoFish.fullNullCheck()) return;
        SPacketSoundEffect sPacketSoundEffect;
        if (event.getPacket() instanceof SPacketSoundEffect && this.getSoundEffect(sPacketSoundEffect = (SPacketSoundEffect)event.getPacket())) {
            this.rightClick();
        }
    }

    @SubscribeEvent
    public void onMouseInputEvent(InputEvent.MouseInputEvent event) {
		if (AutoFish.fullNullCheck()) return;
        if (AutoFish.mc.gameSettings.keyBindUseItem.isKeyDown() && this.Field1602 > 0) {
            this.cDelay = (Integer)this.castingDelay.getValue();
        }
    }

    @SubscribeEvent
    public void onPlayerUpdateEvent(PlayerUpdateEvent event) {
		if (AutoFish.fullNullCheck()) return;
        EntityPlayerSP entityPlayerSP = AutoFish.mc.player;
        ItemStack itemStack = entityPlayerSP.getHeldItemMainhand();
        if (this.cDelay > (Integer)this.castingDelay.getValue()) {
            this.cDelay = (Integer)this.castingDelay.getValue();
        } else if (this.cDelay > 0) {
            --this.cDelay;
        }
        if (itemStack != null && itemStack.getItem() instanceof ItemFishingRod) {
            if (!this.Field1603) {
                this.cDelay = (Integer)this.castingDelay.getValue();
                this.Field1603 = true;
            } else if (entityPlayerSP.fishEntity == null) {
                this.rightClick();
            } else {
                ++this.Field1602;
                if ((Integer)this.failSafeTime.getValue() != 0 && this.Field1602 > (Integer)this.failSafeTime.getValue()) {
                    this.rightClick();
                    this.cDelay = 0;
                    this.Field1602 = 0;
                    this.Field1603 = false;
                }
            }
        } else {
            this.rightClick();
            this.cDelay = 0;
            this.Field1602 = 0;
            this.Field1603 = false;
        }
    }
}