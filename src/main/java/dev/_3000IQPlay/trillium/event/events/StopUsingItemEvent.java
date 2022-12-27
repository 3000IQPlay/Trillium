package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class StopUsingItemEvent extends EventStage {
    private boolean packet = false;

    public boolean isPacket() {
        return packet;
    }

    public void setPacket(boolean packet) {
        this.packet = packet;
    }
}