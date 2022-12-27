package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MouseEvent extends EventStage {
    private final int button;
    private final boolean state;

    public MouseEvent(int button, boolean state) {
        this.button = button;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public int getButton() {
        return button;
    }

}