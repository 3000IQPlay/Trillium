package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.gui.mainmenu.GLSLShader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@Mixin( GuiMainMenu.class )
public class MixinGuiMainMenu extends GuiScreen {
	public GLSLShader shader;
    public long initTime;

    @Inject(method = { "drawPanorama" }, at = { @At("TAIL") })
    public void drawPanoramaTailHook(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo info) {
        try {
            this.shader = new GLSLShader("/assets/minecraft/mainmenu/shader.fsh");
        }
        catch (final IOException ex) {}
        GlStateManager.disableCull();
        this.shader.useShader(this.width * 2, this.height * 2, (float)(mouseX * 2), (float)(mouseY * 2), (System.currentTimeMillis() - this.initTime) / 1000.0f);
        GL11.glBegin(7);
        GL11.glVertex2f(-1.0f, -1.0f);
        GL11.glVertex2f(-1.0f, 1.0f);
        GL11.glVertex2f(1.0f, 1.0f);
        GL11.glVertex2f(1.0f, -1.0f);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }

    @Inject(method = { "initGui" }, at = { @At("HEAD") })
    private void initHook(final CallbackInfo info) {
        this.initTime = System.currentTimeMillis();
    }
}