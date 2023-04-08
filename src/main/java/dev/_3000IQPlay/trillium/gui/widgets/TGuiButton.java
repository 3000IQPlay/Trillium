package dev._3000IQPlay.trillium.gui.widgets;

import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static dev._3000IQPlay.trillium.util.Util.mc;

public class TGuiButton extends GuiButton {

    static ScaledResolution sr;
	boolean outline;
	boolean coolWave;
	float rectRound;
	boolean fontTwo;
    String name;

    public TGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, boolean outline, boolean coolWave, float rectRound, boolean fontTwo, String name) {
        super(buttonId, x, y, name);
        sr = new ScaledResolution(mc);
		this.width = widthIn;
		this.height = heightIn;
		this.outline = outline;
		this.coolWave = coolWave;
		this.rectRound = rectRound;
		this.fontTwo = fontTwo;
        this.name = name;
    }


    public static int getMouseX() {
        return Mouse.getX() * sr.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY() {
        return sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float mouseButton) {
        if (this.visible) {
            this.hovered = (mouseX >= this.x && mouseY >= (this.y) && mouseX < this.x + this.width && mouseY < (this.y) + this.height);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            Color color = new Color(24, 24, 27, 185);
			Color color1 = new Color(5, 5, 5, 255);

            GlStateManager.pushMatrix();
			if (outline) {
				if (coolWave) {
					RoundedShader.drawGradientHorizontal(hovered ? x : x - 1, hovered ? y: y - 1, hovered ? this.width : this.width + 2, hovered ? this.height : this.height + 2, rectRound, ColorUtil.applyOpacity(ClickGui.getInstance().getColor(200), 0.9f).getRGB(), ColorUtil.applyOpacity(ClickGui.getInstance().getColor(0), 0.9f).getRGB());
			    } else {
					RoundedShader.drawGradientRound(hovered ? x : x - 1, hovered ? y + 1 : y, hovered ? this.width : this.width + 2, hovered ? this.height : this.height + 2, rectRound, color1, color1, color1, color1);
				}
			}
            RoundedShader.drawGradientRound(hovered ? x + 1 : x, hovered ? y + 1 : y, hovered ? this.width - 2 : this.width, hovered ? this.height - 2 : this.height, rectRound, color, color, color, color);
			
			if (fontTwo) {
                FontRender.drawCentString3(name, x + this.width / 2, y + 5f + this.height / 4, hovered ? new Color(0x7A7A7A).getRGB() : -1);
			} else {
				FontRender.drawCentString6(name, x + this.width / 2, y + 5f + this.height / 4, hovered ? new Color(0x7A7A7A).getRGB() : -1);
			}
			
            GlStateManager.popMatrix();

            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {

    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height + 10);
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {

    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}