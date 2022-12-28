package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;

public class Ambience extends Module {
    public Ambience() {
        super("Ambience", "Changes the color of the environment", Category.RENDER, true, false, false);
    }

    public final Setting<ColorSetting> colorLight = this.register(new Setting<>("Color Light", new ColorSetting(0x8800FF00)));
}