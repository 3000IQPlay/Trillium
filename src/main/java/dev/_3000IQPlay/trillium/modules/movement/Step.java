package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.modules.Module;

public class Step 
        extends Module { // I will add smthing later since the normal one from Experium doesnt work in dis base
    private static Step instance;
	
    public Step() {
        super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
        instance = this;
    }

    public static Step getInstance() {
        if (instance == null) {
            instance = new Step();
        }
        return instance;
    }
}