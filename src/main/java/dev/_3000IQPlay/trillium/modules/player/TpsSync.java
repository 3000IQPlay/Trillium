package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;

public class TpsSync
        extends Module {
    public TpsSync() {
        super("TpsSync", "синхронизирует игру-с тпс", Module.Category.PLAYER, true, false, false);
    }


    @Override
    public void onUpdate(){
        if(Trillium.serverManager.getTPS() > 1) {
            Trillium.TICK_TIMER = Trillium.serverManager.getTPS() / 20f;
        } else {
            Trillium.TICK_TIMER = 1f;
        }
    }
}

