package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Called when a player steps up a block
 * @author Doogie13
 * @since 12/27/2021
 * from https://github.com/momentumdevelopment/cosmos/
 */
@Cancelable
public class StepEvent extends EventStage {

    // info
    private final AxisAlignedBB axisAlignedBB;
    private float height;

    public StepEvent(AxisAlignedBB axisAlignedBB, float height) {
        this.axisAlignedBB = axisAlignedBB;
        this.height = height;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setHeight(float in) {
        height = in;
    }

    public float getHeight() {
        return height;
    }
}