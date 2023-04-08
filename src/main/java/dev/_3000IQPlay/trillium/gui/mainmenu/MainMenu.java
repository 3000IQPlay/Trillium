package dev._3000IQPlay.trillium.gui.mainmenu;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.MainSettings;
import dev._3000IQPlay.trillium.gui.mainmenu.*;
import dev._3000IQPlay.trillium.gui.widgets.TGuiButton;
import dev._3000IQPlay.trillium.util.RoundedShader;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Random;

public class MainMenu extends GuiScreen {
	private String text = "Trillium b1.8.5 - By _3000IQPlay#8278";
	public GLSLShader shader;
    public long initTime;
	
	@Override
    public void initGui() {
		this.buttonList.clear();
		this.initTime = System.currentTimeMillis();
        try {
            if (MainSettings.getInstance().mainMenu.getValue().booleanValue() && MainSettings.getInstance().randomShader.getValue().booleanValue()) {
                Random random = new Random();
                GLSLShaderList[] shaders = GLSLShaderList.cloneList();
                this.shader = new GLSLShader(shaders[random.nextInt(shaders.length)].getShader());
            } else if (MainSettings.getInstance().mainMenu.getValue().booleanValue() && !MainSettings.getInstance().randomShader.getValue().booleanValue()){
                this.shader = new GLSLShader(MainSettings.getInstance().menuShader.getValue().getShader());
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load background shader", e);
        }
		ScaledResolution sr = new ScaledResolution(this.mc);
        this.width = sr.getScaledWidth();
        this.height = sr.getScaledHeight();
		this.buttonList.add(new TGuiButton(1, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 65, 106, 30, true, true, 7.0f, false, "Singleplayer"));
	    this.buttonList.add(new TGuiButton(2, sr.getScaledWidth() / 2 + 4, sr.getScaledHeight() / 2 - 65, 106, 30, true, true, 7.0f, false, "Multiplayer"));
		this.buttonList.add(new TGuiButton(0, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 - 24, 220, 30, true, true, 7.0f, false, "Settings"));
		this.buttonList.add(new TGuiButton(3, sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 17, 220, 30, true, true, 7.0f, false, "Exit"));
    }
	
	@Override
    protected void actionPerformed(final GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (button.id == 3) {
            Trillium.unload(false);
            this.mc.shutdown();
        }
    }
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (MainSettings.getInstance().mainMenu.getValue().booleanValue()) {
            GlStateManager.disableCull();
            this.shader.useShader(this.width * 2, this.height * 2, mouseX * 2, mouseY * 2, (float)(System.currentTimeMillis() - this.initTime) / 1000.0f);
            GL11.glBegin((int)7);
            GL11.glVertex2f((float)-1.0f, (float)-1.0f);
            GL11.glVertex2f((float)-1.0f, (float)1.0f);
            GL11.glVertex2f((float)1.0f, (float)1.0f);
            GL11.glVertex2f((float)1.0f, (float)-1.0f);
            GL11.glEnd();
            GL20.glUseProgram((int)0);
        }
		ScaledResolution sr = new ScaledResolution(this.mc);
        GlStateManager.disableCull();
        GL11.glBegin(7);
        GL11.glVertex2f(-1.0F, -1F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float half_w = sr.getScaledWidth() / 2f;
        float halh_h = sr.getScaledHeight() / 2f;
		Color color = new Color(0x86000000, true);
        RoundedShader.drawGradientRound(half_w - 120, halh_h - 80, 240, 140, 15f, color, color, color, color);
		FontRender.drawString3(text, FontRender.getStringWidth3(text) / 10.0f - 14, FontRender.getFontHeight3() - 2, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}