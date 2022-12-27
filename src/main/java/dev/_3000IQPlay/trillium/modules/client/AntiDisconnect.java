package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.modules.Module;

public class AntiDisconnect extends Module {

    public AntiDisconnect() {
        super("AntiDisconnect", "Защита от случайного выхода", Category.CLIENT, true, false, false);
    }
}
