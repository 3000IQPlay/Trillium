package dev._3000IQPlay.trillium.modules;

import dev._3000IQPlay.trillium.gui.hud.HudEditorGui;
import dev._3000IQPlay.trillium.gui.classic.ClassicGui;
import dev._3000IQPlay.trillium.setting.Setting;
import java.util.ArrayList;
import java.util.List;

import dev._3000IQPlay.trillium.util.Util;

public class Feature implements Util{

    public List<Setting> settings = new ArrayList<>();
    private String name;

    public Feature() {
    }

    public Feature(String name) {
        this.name = name;
    }

    public static boolean nullCheck() {
        return mc.player == null;
    }

    public static boolean fullNullCheck() {
        return mc.player == null || mc.world == null;
    }

    public String getName() {
        return this.name;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public boolean isEnabled() {
        if (this instanceof Module) {
            return ((Module) this).isOn();
        }
        return false;
    }

    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public Setting register(Setting setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        if (this instanceof Module && mc.currentScreen instanceof ClassicGui) {
            ClassicGui.getInstance().updateModule((Module) this);
        }
        if (this instanceof Module && mc.currentScreen instanceof HudEditorGui) {
            HudEditorGui.getInstance().updateModule((Module) this);
        }
        return setting;
    }

    public Setting getSettingByName(String name) {
        for (Setting setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public void reset() {
        for (Setting setting : this.settings) {
            setting.setValue(setting.getDefaultValue());
        }
    }

    public void clearSettings() {
        this.settings = new ArrayList<>();
    }
}

