package dev._3000IQPlay.trillium.gui.classic.components.items.buttons;

import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;

import java.awt.*;

public class ColorShit extends Button{

    private final Setting setting;
    public ColorShit(Setting setting) {
        super(setting.getName());
        this.width = 15;
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f,new Color(1,1,1,0).getRGB());
        this.setHidden(!this.setting.isVisible());
    }



}
