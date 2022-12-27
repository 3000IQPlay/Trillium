package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ClickMiddleEvent extends EventStage
{
    private boolean moduleCancelled;

    public void setModuleCancelled(boolean cancelled)
    {
        this.moduleCancelled = cancelled;
    }

    public boolean isModuleCancelled()
    {
        return moduleCancelled;
    }

}