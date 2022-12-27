package dev._3000IQPlay.trillium.util.phobos;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.UpdateEntitiesEvent;
import dev._3000IQPlay.trillium.mixin.ducks.IEntityPlayer;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.modules.combat.AutoCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ExtrapolationHelper extends Feature {
    private final AutoCrystal module;

    public ExtrapolationHelper(AutoCrystal module) {
        this.module = module;
    }

    public static void onUpdateEntity(UpdateEntitiesEvent e){

        //TODO SUBSCRIBEp
        for (EntityPlayer player : mc.world.playerEntities) {
            MotionTracker tracker = ((IEntityPlayer) player).getMotionTracker();
            MotionTracker breakTracker = ((IEntityPlayer) player).getBreakMotionTracker();
            MotionTracker blockTracker = ((IEntityPlayer) player).getBlockMotionTracker();
            if (player.getHealth() <= 0 || mc.player.getDistanceSq(player) > 400
                    || !Trillium.moduleManager.getModuleByClass(AutoCrystal.class).selfExtrapolation.getValue()
                    && player.equals(mc.player)) {
                if (tracker != null) {
                    tracker.active = false;
                }

                if (breakTracker != null) {
                    breakTracker.active = false;
                }

                if (blockTracker != null) {
                    blockTracker.active = false;
                }

                continue;
            }

            if (tracker == null && Trillium.moduleManager.getModuleByClass(AutoCrystal.class).extrapol.getValue() != 0) {
                tracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setMotionTracker(tracker);
            }

            if (breakTracker == null && Trillium.moduleManager.getModuleByClass(AutoCrystal.class).bExtrapol.getValue() != 0) {
                breakTracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setBreakMotionTracker(breakTracker);
            }

            if (blockTracker == null && Trillium.moduleManager.getModuleByClass(AutoCrystal.class).blockExtrapol.getValue() != 0) {
                blockTracker = new MotionTracker(mc.world, player);
                ((IEntityPlayer) player).setBlockMotionTracker(blockTracker);
            }

            updateTracker(tracker, Trillium.moduleManager.getModuleByClass(AutoCrystal.class).extrapol.getValue());
            updateTracker(breakTracker, Trillium.moduleManager.getModuleByClass(AutoCrystal.class).bExtrapol.getValue());
            updateTracker(blockTracker, Trillium.moduleManager.getModuleByClass(AutoCrystal.class).blockExtrapol.getValue());
        }
    }

    private static void updateTracker(MotionTracker tracker, int ticks) {
        if (tracker == null) {
            return;
        }

        tracker.active = false;
        tracker.copyLocationAndAnglesFrom(tracker.tracked);
        tracker.gravity = Trillium.moduleManager.getModuleByClass(AutoCrystal.class).gravityExtrapolation.getValue();
        tracker.gravityFactor = Trillium.moduleManager.getModuleByClass(AutoCrystal.class).gravityFactor.getValue();
        tracker.yPlusFactor = Trillium.moduleManager.getModuleByClass(AutoCrystal.class).yPlusFactor.getValue();
        tracker.yMinusFactor = Trillium.moduleManager.getModuleByClass(AutoCrystal.class).yMinusFactor.getValue();
        for (tracker.ticks = 0; tracker.ticks < ticks; tracker.ticks++) {
            tracker.updateFromTrackedEntity();
        }

        tracker.active = true;
    }

    public MotionTracker getTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getMotionTracker();
    }

    public MotionTracker getBreakTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getBreakMotionTracker();
    }

    public MotionTracker getBlockTracker(Entity player) {
        return ((IEntityPlayer) player).getBlockMotionTracker();
    }

}