package dev._3000IQPlay.trillium.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PreRenderEvent extends Event {

    private final float partialTicks;

    public PreRenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}