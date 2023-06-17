package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public final class NoJumpDelay
        extends Module {
    private static NoJumpDelay INSTANCE = new NoJumpDelay();

    public NoJumpDelay() {
        super("NoJumpDelay", "Removes delay from jumping", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }
	
	public static NoJumpDelay getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoJumpDelay();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

