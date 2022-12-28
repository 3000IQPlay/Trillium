package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.modules.Module;

public class AntiDisconnect extends Module {

    public AntiDisconnect() {
        super("AntiDisconnect", "Protection against accidental exit", Category.CLIENT, true, false, false);
    }
}
