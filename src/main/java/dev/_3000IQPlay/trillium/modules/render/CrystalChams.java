package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.setting.ColorSetting;

public class CrystalChams
        extends Module {
    public static CrystalChams INSTANCE;
    public Setting<modes> mode = this.register(new Setting<modes>("Mode", modes.FILL));
    public Setting<outlineModes> outlineMode = this.register(new Setting<outlineModes>("Outline Mode", outlineModes.WIRE));
    public Setting<Float> size = this.register(new Setting<Float>("Size", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    public Setting<Float> crystalSpeed = this.register(new Setting<Float>("Speed", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    public Setting<Float> crystalBounce = this.register(new Setting<Float>("Bounce", Float.valueOf(0.2f), Float.valueOf(0.1f), Float.valueOf(1.0f)));
    public Setting<BlendModes> blendModes = this.register(new Setting<BlendModes>("Blend", BlendModes.Default));
    public Setting<Boolean> enchanted = this.register(new Setting<Boolean>("Glint", false));
	public Setting<ColorSetting> glintC = this.register(new Setting<ColorSetting>("GlintColor", new ColorSetting(0x141414d9), v -> this.enchanted.getValue()));
    public Setting<Boolean> texture = this.register(new Setting<Boolean>("Texture", false));
	public Setting<ColorSetting> colorC = this.register(new Setting<ColorSetting>("Color", new ColorSetting(0x00000000)));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", false));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));
	public Setting<ColorSetting> outlineC = this.register(new Setting<ColorSetting>("OutlineColor", new ColorSetting(0x141414d9), v -> this.outline.getValue()));
    public Setting<Boolean> hiddenSync = this.register(new Setting<Boolean>("Hidden Sync", true));
	public Setting<ColorSetting> hiddenC = this.register(new Setting<ColorSetting>("HiddenColor", new ColorSetting(0x000000), v -> this.hiddenSync.getValue() == false));

    public CrystalChams() {
        super("CrystalChams", "Modifies crystal rendering in different ways", Module.Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public static enum modes {
        FILL,
        WIREFRAME;
    }

    public static enum outlineModes {
        WIRE,
        FLAT;
    }
	
    public static enum BlendModes {
        Default,
        Brighter;
    }
}