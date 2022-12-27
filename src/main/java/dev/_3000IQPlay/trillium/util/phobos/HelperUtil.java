package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.combat.AutoCrystal;
import dev._3000IQPlay.trillium.util.MathUtil;
import dev._3000IQPlay.trillium.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static dev._3000IQPlay.trillium.util.Util.mc;
import static dev._3000IQPlay.trillium.util.phobos.CalculationMotion.isLegit;

public class HelperUtil
{
    public static AutoCrystal.BreakValidity isValid(AutoCrystal module, Entity crystal)
    {
        return isValid(module, crystal, false);
    }

    // TODO: make sure that we use the correct lastPos everywhere!
    public static AutoCrystal.BreakValidity isValid(AutoCrystal module, Entity crystal, boolean lastPos)
    {
        if (module.existed.getValue() != 0
                && System.currentTimeMillis()
                - ((IEntity) crystal).getTimeStamp()
                + (module.pingExisted.getValue()
                ? Trillium.serverManager.getPing() / 2.0
                : 0)
                < module.existed.getValue())
        {
            return AutoCrystal.BreakValidity.INVALID;
        }

        if (lastPos && !module.rangeHelper.isCrystalInRangeOfLastPosition(crystal)
                || !lastPos && !module.rangeHelper.isCrystalInRange(crystal))
        {
            return AutoCrystal.BreakValidity.INVALID;
        }

        if (lastPos && Trillium.positionManager.getDistanceSq(crystal)
                >= MathUtil.square(module.breakTrace.getValue())
                || !lastPos && mc.player.getDistanceSq(crystal)
                >= MathUtil.square(module.breakTrace.getValue()))
        {
            if (lastPos && !Trillium.positionManager.canEntityBeSeen(crystal)
                    || !lastPos && !RayTraceUtil.canBeSeen(
                    new Vec3d(crystal.posX,
                            crystal.posY + 1.7,
                            crystal.posZ),
                    mc.player))
            {
                return AutoCrystal.BreakValidity.INVALID;
            }
        }

        // TODO: lastPos and then check isLegit???????? not sure if this is ok
        if (module.noRotateNigga(AutoCrystal.ACRotate.Break)
                || module.isNotCheckingRotations()
                || (isLegit(crystal, crystal)
                && AutoCrystal.POSITION_HISTORY
                .arePreviousRotationsLegit(crystal,
                        module.rotationTicks
                                .getValue(),
                        true)))
        {
            return AutoCrystal.BreakValidity.VALID;
        }

        return AutoCrystal.BreakValidity.ROTATIONS;
    }

    public static void simulateExplosion(AutoCrystal module, double x, double y, double z)
    {
        List<Entity> entities = mc.world.loadedEntityList;
        if (entities == null)
        {
            return;
        }

        for (Entity entity : entities)
        {
            if (entity instanceof EntityEnderCrystal
                    && entity.getDistanceSq(x, y, z) < 144)
            {
                if (module.pseudoSetDead.getValue())
                {
                    ((IEntity) entity).setPseudoDead(true);
                }
                else
                {
                    Trillium.setDeadManager.setDead(entity);
                }
            }
        }
    }

    /**
     * Checks if a change in blockChange at the given position
     * would be valid, that means that a player close to it
     * would have his feet exposed.
     *
     * @param pos the changed position.
     * @return <tt>true</tt> if the position exposes a player.
     */
    public static boolean validChange(BlockPos pos, List<EntityPlayer> players)
    {
        for (EntityPlayer player : players)
        {
            if (player == null
                    || player.equals(mc.player)
                    || (player.isDead)
                    || Trillium.friendManager.isFriend(player))
            {
                continue;
            }

            if (player.getDistanceSqToCenter(pos) <= 4
                    && player.posY >= pos.getY())
            {
                return true;
            }
        }

        return false;
    }

    public static boolean valid(Entity entity, double range, double trace)
    {
        EntityPlayer player = mc.player;
        double d = entity.getDistanceSq(player);
        if (d >= MathUtil.square(range))
        {
            return false;
        }

        if (d >= trace)
        {
            return RayTraceUtil.canBeSeen(entity, player);
        }

        return true;
    }

}