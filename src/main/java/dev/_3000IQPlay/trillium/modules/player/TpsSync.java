package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;

public class TpsSync
        extends Module {
    public TpsSync() {
        super("TpsSync", "Best module", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate(){
        if (Trillium.serverManager.getTPS() > 1) {
            TpsSync.mc.timer.tickLength = 50.0f / (Trillium.serverManager.getTPS() / 20f);
        } else {
            TpsSync.mc.timer.tickLength = 50.0f / 1f;
        }
    }
}

