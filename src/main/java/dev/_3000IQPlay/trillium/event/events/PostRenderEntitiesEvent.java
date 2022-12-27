package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;

public class PostRenderEntitiesEvent extends EventStage
{
    private final float partialTicks;
    private final int pass;

    public PostRenderEntitiesEvent(float partialTicks, int pass)
    {
        this.partialTicks = partialTicks;
        this.pass = pass;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public int getPass()
    {
        return pass;
    }
}