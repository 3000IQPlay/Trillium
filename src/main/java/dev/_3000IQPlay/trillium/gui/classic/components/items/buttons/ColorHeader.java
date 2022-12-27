package dev._3000IQPlay.trillium.gui.classic.components.items.buttons;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.classic.ClassicGui;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.setting.ColorSettingHeader;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;

public class ColorHeader extends Button {
    private final Setting<ColorSettingHeader> header;


    public ColorHeader(Setting setting) {
        super(setting.getName());
        this.header = setting;
        this.width = 15;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, Trillium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
        FontRender.drawString5(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1 );
    }

    @Override
    public void update() {
        this.setHidden(!this.header.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            getParentSetting().getValue().setOpenedCSH(!getParentSetting().getValue().getStateCSH());
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    public Setting<ColorSettingHeader> getParentSetting() {
        return header;
    }
}
