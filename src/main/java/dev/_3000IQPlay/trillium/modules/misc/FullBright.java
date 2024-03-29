package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

public class FullBright
        extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.GAMMA));
    public Setting<Boolean> effects = this.register(new Setting<Boolean>("Effects", false));
    private float previousSetting = 1.0f;

    public FullBright() {
        super("Fullbright", "Makes you see everything brighter", Module.Category.RENDER, true, false, false);
    }


    @Override
    public void onEnable() {
        this.previousSetting = FullBright.mc.gameSettings.gammaSetting;
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.GAMMA) {
            FullBright.mc.gameSettings.gammaSetting = 1000.0f;
        }
        if (this.mode.getValue() == Mode.POTION) {
            FullBright.mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210));
        }
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() == Mode.POTION) {
            FullBright.mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
        FullBright.mc.gameSettings.gammaSetting = this.previousSetting;
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketEntityEffect && this.effects.getValue()) {
            final SPacketEntityEffect packet = event.getPacket();
            if (FullBright.mc.player != null && packet.getEntityId() == FullBright.mc.player.getEntityId() && (packet.getEffectId() == 9 || packet.getEffectId() == 15)) {
                event.setCanceled(true);
            }
        }
    }

    public enum Mode
    {
        GAMMA,
        POTION;
    }
}