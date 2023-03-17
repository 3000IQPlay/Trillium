package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RotationUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class AntiFireBall extends Module {
    private final Setting<Mode> swing = this.register(new Setting<>("Swing", Mode.Silent));
	private final Setting<Float> delay = this.register(new Setting<>("HitDelay", 50.0f, 0.0f, 500.0f));
	private final Setting<Float> hitRange = this.register(new Setting<>("HitRange", 5.5f, 1.0f, 6.0f));
    private final Setting<Boolean> rotation = this.register(new Setting<>("Rotation", false));
	private final Setting<Float> rotateRange = this.register(new Setting<>("RotateRange", 7.5f, 1.0f, 10.0f));
	private final Setting<Boolean> interpolate = this.register(new Setting<>("Interpolate", false, v -> this.rotation.getValue()));
	private final Timer timer = new Timer();
	
	public AntiFireBall() {
        super("AntiFireBall", "Automatically punches fireballs away from you", Module.Category.PLAYER, true, false, false);
    }
	
	@Override
	public void onUpdate() {
        for (Object entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityFireball && mc.player.getDistanceSq((EntityFireball) entity) < this.rotateRange.getValue() && this.rotation.getValue()) {
                RotationUtil.getNCPRotations((Entity) entity, this.interpolate.getValue());
            }
            if (entity instanceof EntityFireball && mc.player.getDistanceSq((EntityFireball) entity) < this.hitRange.getValue() && timer.passedMs((double) this.delay.getValue())) {
                if (this.swing.getValue() == Mode.Normal) {
                    mc.playerController.attackEntity(mc.player, (Entity) entity);
                } else if (this.swing.getValue() == Mode.Silent) {
					mc.player.connection.sendPacket(new CPacketUseEntity((Entity) entity));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }

                timer.reset();
                break;
            }
        }
    }
	
	public static enum Mode {
	    Normal,
		Silent;
	}
}