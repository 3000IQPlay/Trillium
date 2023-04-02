package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.modules.client.MainSettings;
import dev._3000IQPlay.trillium.gui.mainmenu.*;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.Random;

@Mixin( GuiMainMenu.class )
public abstract class MixinGuiMainMenu extends GuiScreen {
	@Shadow
    protected abstract void renderSkybox(int var1, int var2, float var3);
	
	public GLSLShader shader;
    public long initTime;
	
	@Inject(method={"initGui"}, at={@At(value="RETURN")}, cancellable=true)
    public void initGui(CallbackInfo info) {
        try {
            if (MainSettings.getInstance().mainMenuShader.getValue().booleanValue() && MainSettings.getInstance().randomShader.getValue().booleanValue()) {
                Random random = new Random();
                GLSLShaderList[] shaders = GLSLShaderList.cloneList();
                this.shader = new GLSLShader(shaders[random.nextInt(shaders.length)].getShader());
            } else if (MainSettings.getInstance().mainMenuShader.getValue().booleanValue() && !MainSettings.getInstance().randomShader.getValue().booleanValue()){
                this.shader = new GLSLShader(MainSettings.getInstance().menuShader.getValue().getShader());
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load background shader", e);
        }
    }
	
	@Inject(method={"drawScreen"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawScreenShader(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (MainSettings.getInstance().mainMenuShader.getValue().booleanValue()) {
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
    }
	
	@Redirect(method={"drawScreen"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiMainMenu;renderSkybox(IIF)V"))
    private void voided(GuiMainMenu guiMainMenu, int mouseX, int mouseY, float partialTicks) {
        if (!MainSettings.getInstance().mainMenuShader.getValue().booleanValue()) {
            this.renderSkybox(mouseX, mouseY, partialTicks);
        }
    }

    @Redirect(method={"drawScreen"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal=0))
    private void noRect1(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
        if (!MainSettings.getInstance().mainMenuShader.getValue().booleanValue()) {
            this.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }
    }

    @Redirect(method={"drawScreen"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiMainMenu;drawGradientRect(IIIIII)V", ordinal=1))
    private void noRect2(GuiMainMenu guiMainMenu, int left, int top, int right, int bottom, int startColor, int endColor) {
        if (!MainSettings.getInstance().mainMenuShader.getValue().booleanValue()) {
            this.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }
    }

    @Inject(method = { "initGui" }, at = { @At("HEAD") })
    private void initHook(final CallbackInfo info) {
        this.initTime = System.currentTimeMillis();
    }
}