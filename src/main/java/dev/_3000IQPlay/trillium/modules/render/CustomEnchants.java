package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;

public class CustomEnchants extends Module{
    public CustomEnchants() {
        super("RainbowEnchants", "Makes enchant glint more colorful", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }
    public static CustomEnchants getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomEnchants();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
    private static CustomEnchants INSTANCE = new CustomEnchants();
}