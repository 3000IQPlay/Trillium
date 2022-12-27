package dev._3000IQPlay.trillium.gui.classic.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;

import dev._3000IQPlay.trillium.gui.classic.ClassicGui;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.setting.Bind;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton
        extends Button {
    private final Setting setting;
    public boolean isListening;

    public BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x11555555 : -2007673515) : (!this.isHovering(mouseX, mouseY) ? Trillium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue() : Trillium.moduleManager.getModuleByClass(ClickGui.class).mainColor.getValue().getAlpha()));
        if (this.isListening) {
            FontRender.drawString5("...", this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1);
        } else {
            FontRender.drawString5("Bind " + ChatFormatting.GRAY + this.setting.getValue().toString().toUpperCase() + " " + (((Bind)this.setting.getValue()).isHold() ? "hold" : "toggle"), this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }

    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton != 0 && this.isHovering(mouseX, mouseY)){
            ((Bind)this.setting.getValue()).setHold(!((Bind)this.setting.getValue()).isHold());
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.isHovering(mouseX, mouseY)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            this.onMouseClick();
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }

    @Override
    public boolean getState() {
        return !this.isListening;
    }
}

