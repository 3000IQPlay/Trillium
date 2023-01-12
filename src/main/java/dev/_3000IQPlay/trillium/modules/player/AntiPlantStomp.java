package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;

public class AntiPlantStomp
        extends Module {
    public AntiPlantStomp() {
        super("AntiPlantStomp", "Stops you from stomping on plants.", Module.Category.PLAYER, true, false, false);
    }
}
