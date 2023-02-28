package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;

public class Models extends Module {
    public Models() {
        super("Models", "Models", Category.RENDER, true, false, false);
    }


    public Setting<Boolean> onlySelf = register(new Setting<>("onlySelf", false));
    public Setting<Boolean> friends = register(new Setting<>("friends", false));
    public Setting<Boolean> friendHighlight = register(new Setting<>("friendHighLight", false));
    public Setting<mode> Mode = register(new Setting("Mode", mode.Freddy));
    public enum mode {
        Amogus, Rabbit, Freddy
    }

    public  Setting<ColorSetting> eyeColor = this.register(new Setting<>("eyeColor", new ColorSetting(-2009289807)));
    public  Setting<ColorSetting> bodyColor = this.register(new Setting<>("bodyColor", new ColorSetting(-2009289807)));
    public  Setting<ColorSetting> legsColor = this.register(new Setting<>("legsColor", new ColorSetting(-2009289807)));


}


