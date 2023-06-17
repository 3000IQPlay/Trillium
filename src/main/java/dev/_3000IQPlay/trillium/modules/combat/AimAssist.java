package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class AimAssist extends Module {
	
	public Setting<Float> fov = this.register(new Setting<>("FOVAngle", 80.0f, 0.0f, 360.0f));
	public Setting<Float> speed = this.register(new Setting<>("Speed", 8.0f, 0.0f, 15.0f));
	public Setting<Float> range = this.register(new Setting<>("Range", 3.8f, 3.0f, 6.0f));
	public Setting<Boolean> pitch = this.register(new Setting<>("Pitch", true));
	
	public AimAssist() {
        super("AimAssist", "Automatically aim at your target", Category.COMBAT, true, false, false);
    }

	@Override
	public void onUpdate() {
		final EntityPlayer target = this.getClosestPlayerToCursor((float) this.fov.getValue());
		if (Objects.nonNull(target)) {
			mc.player.rotationYaw = mc.player.rotationYaw + (AimAssist.getYawChange(target) / (float) this.speed.getValue());
			if (this.pitch.getValue()) {
				mc.player.rotationPitch = mc.player.rotationPitch + (AimAssist.getPitchChange(target) / (float) this.speed.getValue());
			}
		}
	}

	private EntityPlayer getClosestPlayerToCursor(final float angle) {
		float distance = angle;
		EntityPlayer tempPlayer = null;
		for (final EntityPlayer player : mc.world.playerEntities) {
			if (isValidEntity(player)) {
				final float yaw = AimAssist.getYawChange(player);
				final float pitch = AimAssist.getPitchChange(player);
				if (yaw > angle || pitch > angle) {
					continue;
				}
				final float currentDistance = (yaw + pitch) / 2F;
				if (currentDistance <= distance) {
					distance = currentDistance;
					tempPlayer = player;
				}
			}
		}
		return tempPlayer;
	}

	private boolean isValidEntity(final EntityPlayer player) {
		return Objects.nonNull(player) && player.isEntityAlive() && player.getDistance(mc.player) <= this.range.getValue() && player.ticksExisted > 20 && !player.isInvisibleToPlayer(mc.player) && !Trillium.friendManager.isFriend(mc.player);
	}
	
	public static float getPitchChange(EntityLivingBase entity) {
		final double deltaX = entity.posX - mc.player.posX;
		final double deltaZ = entity.posZ - mc.player.posZ;
		final double deltaY = entity.posY - 2.2D + entity.getEyeHeight() - mc.player.posY;
		final double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
		return -MathHelper.wrapDegrees(mc.player.rotationPitch - (float) pitchToEntity);
	}

	public static float getYawChange(EntityLivingBase entity) {
		final double deltaX = entity.posX - mc.player.posX;
		final double deltaZ = entity.posZ - mc.player.posZ;
		double yawToEntity;

		if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
			yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else {
			if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
				yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
			} else {
				yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
			}
		}
		return MathHelper.wrapDegrees(-(mc.player.rotationYaw - (float) yawToEntity));
	}
}