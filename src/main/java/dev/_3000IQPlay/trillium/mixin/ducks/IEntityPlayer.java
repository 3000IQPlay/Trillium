package dev._3000IQPlay.trillium.mixin.ducks;


import dev._3000IQPlay.trillium.util.phobos.MotionTracker;

public interface IEntityPlayer {
    void setMotionTracker(MotionTracker motionTracker);

    MotionTracker getMotionTracker();

    void setBreakMotionTracker(MotionTracker motionTracker);

    MotionTracker getBreakMotionTracker();

    void setBlockMotionTracker(MotionTracker motionTracker);

    MotionTracker getBlockMotionTracker();

    int getTicksWithoutMotionUpdate();

    void setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate);

}