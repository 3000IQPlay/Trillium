package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.gui.hud.HudEditorGui;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.util.Util;

public class HudEditor extends Module{
    private static HudEditor INSTANCE = new HudEditor();

    public HudEditor() {
        super("HudEditor", "Hud change yes", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static HudEditor getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditor();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }


    @Override
    public void onEnable(){
        Util.mc.displayGuiScreen(HudEditorGui.getHudGui());
        toggle();
    }
	
    @Override
    public void onDisable(){
    }
}