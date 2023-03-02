package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class UpdateWalkingPlayerEvent
        extends EventStage {
	protected boolean onGround;
	
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
	
	public UpdateWalkingPlayerEvent(boolean onGround) {
        this.onGround = onGround;
    }
	
	public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
	
	public boolean isOnGround() {
        return this.onGround;
    }
}