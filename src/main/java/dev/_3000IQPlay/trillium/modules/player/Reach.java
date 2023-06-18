package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Random;

public class Reach extends Module {
    private final Random rand = new Random();
    public final Setting<Float> MinReach = this.register(new Setting<>("MinReach", 3.0f, -2.0f, 6.0f));
    public final Setting<Float> MaxReach = this.register(new Setting<>("MaxReach", 3.35f, -2.0f, 6.0f));
	public final Setting<Float> reachChance = this.register(new Setting<>("ReachChance", 75.0f, 0.0f, 100.0f));
    public final Setting<Boolean> weaponOnly = this.register(new Setting<>("WeaponOnly", false));
    public final Setting<Boolean> movingOnly = this.register(new Setting<>("MovingOnly", false));
    public final Setting<Boolean> sprintOnly = this.register(new Setting<>("SprintOnly", false));
    public final Setting<Boolean> hitThroughBlocks = this.register(new Setting<>("HitThroughBlocks", false));

    public Reach() {
        super("Reach", "Make you can attack far target", Category.PLAYER, true, false, false);
    }

    @SubscribeEvent
    public void onMove(final MouseEvent ev) {
        if (true) {
            if (this.weaponOnly.getValue()) {
                if (mc.player.getHeldItemMainhand().isEmpty()) {
                    return;
                }
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)) {
                    return;
                }
            }
            if (this.movingOnly.getValue() && mc.player.moveForward == 0.0 && mc.player.moveStrafing == 0.0) {
                return;
            }
            if (this.sprintOnly.getValue() && !mc.player.isSprinting()) {
                return;
            }
			if (this.reachChance.getValue() < 100.0f && this.rand.nextDouble() > this.reachChance.getValue() / 100.0f) {
                return;
            }
            if (!this.hitThroughBlocks.getValue() && mc.objectMouseOver != null) {
                final BlockPos blocksReach = mc.objectMouseOver.getBlockPos();
                if (blocksReach != null && mc.world.getBlockState(blocksReach).getBlock() != Blocks.AIR) {
                    return;
                }
            }
            double reachValues = this.MinReach.getValue() + this.rand.nextDouble() * (this.MaxReach.getValue() - this.MinReach.getValue());
            final Object[] target = doReach(reachValues, 0.0, 0.0f);
            if (target == null) {
                return;
            }
            mc.objectMouseOver = new RayTraceResult((Entity) target[0], (Vec3d) target[1]);
            mc.pointedEntity = (Entity) target[0];
        }
    }

    public static Object[] doReach(final double reachValue, final double AABB, final float cwc) {
        final Entity target = mc.getRenderViewEntity();
        Entity entity = null;
        if (target == null || mc.world == null) {
            return null;
        }
        final Vec3d targetEyes = target.getPositionEyes(0.0f);
        final Vec3d targetLook = target.getLook(0.0f);
        final Vec3d targetVector = targetEyes.add(targetLook.x * reachValue, targetLook.y * reachValue, targetLook.z * reachValue);
        Vec3d targetVec = null;
        final List<Entity> targetHitbox = mc.world.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox().grow(targetLook.x * reachValue, targetLook.y * reachValue, targetLook.z * reachValue).grow(1.0, 1.0, 1.0));
        double reaching = reachValue;
        for (int i = 0; i < targetHitbox.size(); ++i) {
            final Entity targetEntity = targetHitbox.get(i);
            if (targetEntity.canBeCollidedWith()) {
                final float targetCollisionBorderSize = targetEntity.getCollisionBorderSize();
                AxisAlignedBB targetAABB = targetEntity.getEntityBoundingBox().grow(targetCollisionBorderSize, targetCollisionBorderSize, targetCollisionBorderSize);
                targetAABB = targetAABB.grow(AABB, AABB, AABB);
                final RayTraceResult tagetPosition = targetAABB.calculateIntercept(targetEyes, targetVector);
                if (targetAABB.contains(targetEyes)) {
                    if (0.0 < reaching || reaching == 0.0) {
                        entity = targetEntity;
                        targetVec = (tagetPosition == null ? targetEyes : tagetPosition.hitVec);
                        reaching = 0.0;
                    }
                } else if (tagetPosition != null) {
                    final double targetHitVec = targetEyes.distanceTo(tagetPosition.hitVec);
                    if (targetHitVec < reaching || reaching == 0.0) {
                        final boolean canRiderInteract = false;
                        if (targetEntity == target.getRidingEntity()) {
                            if (reaching == 0.0) {
                                entity = targetEntity;
                                targetVec = tagetPosition.hitVec;
                            }
                        } else {
                            entity = targetEntity;
                            targetVec = tagetPosition.hitVec;
                            reaching = targetHitVec;
                        }
                    }
                }
            }
        }
        if (reaching < reachValue && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }
        if (entity == null || targetVec == null) {
            return null;
        }
        return new Object[]{entity, targetVec};
    }
}