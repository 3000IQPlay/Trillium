package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;

public class EventPreMotion extends EventStage {

    public float getYaw() {
        return yaw;
    }

    float yaw;

    public float getPitch() {
        return pitch;
    }

    float pitch;
    public EventPreMotion(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }


}