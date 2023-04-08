package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class EventSync extends EventStage {

    float yaw;
    float pitch;
    boolean onGround;

    public EventSync(float yaw, float pitch, boolean onGround) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {return onGround;}
}