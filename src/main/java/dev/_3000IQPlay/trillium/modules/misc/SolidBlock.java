package dev._3000IQPlay.trillium.modules.misc;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;

public class SolidBlock
        extends Module {
	private static SolidBlock INSTANCE = new SolidBlock();
	public Setting<Boolean> web = this.register(new Setting<Boolean>("Web", true));
	public Setting<Boolean> vine = this.register(new Setting<Boolean>("Vine", false));
	
    public SolidBlock() {
        super("SolidBlock", "No more running into webs/vines :)", Module.Category.MISC, true, false, false);
		this.setInstance();
    }
	
	public static SolidBlock getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SolidBlock();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
