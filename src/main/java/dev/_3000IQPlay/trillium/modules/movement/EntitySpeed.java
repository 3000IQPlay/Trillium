package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.PlayerUpdateEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntitySpeed
        extends Module {
    public Setting<Float> speed = this.register(new Setting<Float>("Speed", 1.5f, 0.1f, 10.0f));
    public Setting<Boolean> bypass = this.register(new Setting<Boolean>("Bypass", false));
    double currSpeed = 0;
	
	public EntitySpeed() {
        super("EntitySpeed", "Go fast on entity brrrr", Module.Category.MOVEMENT, false, false, false);
    }
	
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
		if (EntitySpeed.fullNullCheck()) return;
        if (mc.player.ridingEntity != null) {
            Entity riding = mc.player.ridingEntity;
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            riding.rotationYaw = yaw;
            final boolean movingForward = forward != 0.0;
            final boolean movingStrafe = strafe != 0.0;
            if (this.bypass.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.connection.sendPacket(new CPacketUseEntity(riding, EnumHand.MAIN_HAND));
            }
            if (!movingForward && !movingStrafe) {
                riding.motionX = 0.0;
                riding.motionZ = 0.0;
                currSpeed = 40;
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    forward = ((forward > 0.0) ? 1.0 : -1.0);
                }
                if (currSpeed != 80)
                    currSpeed++;
                double speed = this.speed.getValue() * (currSpeed/80);
                double cos = Math.cos(Math.toRadians(yaw + 90.0f));
                double sin = Math.sin(Math.toRadians(yaw + 90.0f));
                riding.motionX = forward * speed * cos + strafe * speed * sin;
                riding.motionZ = forward * speed * sin - strafe * speed * cos;
            }
        } else {
            currSpeed = 40;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        Packet packet = event.getPacket();
        if (!this.bypass.getValue() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.currentScreen instanceof GuiDownloadTerrain) return;
        if (packet instanceof SPacketPlayerPosLook) {
            event.setCanceled(true);
        } else if (packet instanceof SPacketSetPassengers && mc.player.ridingEntity != null) event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        currSpeed = 40;
    }

    @Override
    public void onDisable() {
        if (mc.player.ridingEntity != null) mc.player.ridingEntity.noClip = false;
    }
}