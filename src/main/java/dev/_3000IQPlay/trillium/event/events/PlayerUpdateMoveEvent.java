package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.util.MovementInput;

public class PlayerUpdateMoveEvent
        extends EventStage {
    public MovementInput movementInput;

    public PlayerUpdateMoveEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }
}