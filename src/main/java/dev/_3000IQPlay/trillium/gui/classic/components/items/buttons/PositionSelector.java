package dev._3000IQPlay.trillium.gui.classic.components.items.buttons;

import dev._3000IQPlay.trillium.setting.Setting;

public class PositionSelector extends Button{
    public Setting setting;

    public PositionSelector(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }




}
