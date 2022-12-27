package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;

public class KeyEventTwo
        extends EventStage {
    public boolean info;
    public boolean pressed;

    public KeyEventTwo(int stage, boolean info, boolean pressed) {
        super(stage);
        this.info = info;
        this.pressed = pressed;
    }
}

