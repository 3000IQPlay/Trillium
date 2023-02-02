package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.util.MathUtil;
import dev._3000IQPlay.trillium.util.ColorUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Objects;

public class WaterMark
        extends Module {
	public Setting<Double> barPosY = this.register(new Setting<Double>("BarPosY", 0.0, -2.0, 15.0));
	public Setting<Double> textPosY = this.register(new Setting<Double>("TextPosY", 0.0, -6.0, 3.0));
	public Setting<ColorSetting> bgC = register(new Setting<ColorSetting>("BackgroundColor", new ColorSetting(-15461356)));
	public Setting<ColorSetting> bC = register(new Setting<ColorSetting>("BorderColor", new ColorSetting(-519435766)));
	public Setting<ColorSetting> fC = this.register(new Setting<ColorSetting>("FirstLineColor", new ColorSetting(-8453889)));
    public Setting<ColorSetting> sC = this.register(new Setting<ColorSetting>("SecondLineColor", new ColorSetting(-16711681)));
	public Setting<ColorSetting> tC = this.register(new Setting<ColorSetting>("ThirdLineColor", new ColorSetting(-16711808)));
    public Setting<ColorSetting> foC = this.register(new Setting<ColorSetting>("FourthLineColor", new ColorSetting(-14024449)));
	
    public WaterMark() {
        super("CSGOWaterMark", "Hot csgo watermark", Module.Category.HUD, true, false, false);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (WaterMark.nullCheck()) {
            return;
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			String ping = this.getPing((EntityPlayer)WaterMark.mc.player) + "ms";
            String fpsText = Minecraft.debugFPS + "fps ";
			String name = mc.player.getDisplayNameString();
			String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toLowerCase() : WaterMark.mc.getCurrentServerData().serverIP.toLowerCase();
			String Trillium = "Trillium b1.3.7";
			String text = Trillium + " | " + server + " | " + ping + " | " + fpsText;
            float width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 6;
            int height = 20;
            int posX = 2;
            int posY = 2;
			double barPosY = this.barPosY.getValue();
			double textPosY = this.textPosY.getValue();
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 10.0f), height + 6, ColorUtil.toRGBA(this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha()));
            RenderUtil.drawRectangleCorrectly(posX - 4, posY - 4, (int)(width + 11.0f), height + 7, ColorUtil.toRGBA(this.bC.getValue().getRed(), this.bC.getValue().getGreen(), this.bC.getValue().getBlue(), this.bC.getValue().getAlpha()));
            WaterMark.drawRect(posX, posY, (float)posX + width + 2.0f, posY + height, new Color(this.bgC.getValue().getRed(), this.bgC.getValue().getGreen(), this.bgC.getValue().getBlue(), this.bgC.getValue().getAlpha()).getRGB());
            WaterMark.drawRect((double)posX + 2.5, (double)posY + 2.5, (double)((float)posX + width) - 0.5, (double)posY + 4.5, new Color(this.bgC.getValue().getRed(), this.bgC.getValue().getGreen(), this.bgC.getValue().getBlue(), this.bgC.getValue().getAlpha()).getRGB());
            WaterMark.drawGradientSideways(4.0, (posY + barPosY) + 3, 4.0f + width / 3.0f, (posY + barPosY) + 4, new Color(this.fC.getValue().getRed(), this.fC.getValue().getGreen(), this.fC.getValue().getBlue(), this.fC.getValue().getAlpha()).getRGB(), new Color(this.sC.getValue().getRed(), this.sC.getValue().getGreen(), this.sC.getValue().getBlue(), this.sC.getValue().getAlpha()).getRGB());
            WaterMark.drawGradientSideways(4.0f + width / 3.0f, (posY + barPosY) + 3, 4.0f + width / 3.0f * 2.0f, (posY + barPosY) + 4, new Color(this.sC.getValue().getRed(), this.sC.getValue().getGreen(), this.sC.getValue().getBlue(), this.sC.getValue().getAlpha()).getRGB(), new Color(this.tC.getValue().getRed(), this.tC.getValue().getGreen(), this.tC.getValue().getBlue(), this.tC.getValue().getAlpha()).getRGB());
            WaterMark.drawGradientSideways(4.0f + width / 3.0f * 2.0f, (posY + barPosY) + 3, width / 3.0f * 3.0f + 1.0f, (posY + barPosY) + 4, new Color(this.tC.getValue().getRed(), this.tC.getValue().getGreen(), this.tC.getValue().getBlue(), this.tC.getValue().getAlpha()).getRGB(), new Color(this.foC.getValue().getRed(), this.foC.getValue().getGreen(), this.foC.getValue().getBlue(), this.foC.getValue().getAlpha()).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)(4 + posX), (float)(8 + (posY + textPosY)), -1);
        }
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)f, (float)f1, (float)f2, (float)f3);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(left, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, top, 0.0).endVertex();
        bufferBuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float)(col1 >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(col1 >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(col1 >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(col1 & 0xFF) / 255.0f;
        float f4 = (float)(col2 >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(col2 >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(col2 >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(col2 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
		GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int)2848);
        GL11.glShadeModel((int)7425);
        GL11.glPushMatrix();
        GL11.glBegin((int)7);
        GL11.glColor4f((float)f1, (float)f2, (float)f3, (float)f);
        GL11.glVertex2d((double)left, (double)top);
        GL11.glVertex2d((double)left, (double)bottom);
        GL11.glColor4f((float)f5, (float)f6, (float)f7, (float)f4);
        GL11.glVertex2d((double)right, (double)bottom);
        GL11.glVertex2d((double)right, (double)top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
    }

    private int getPing(EntityPlayer player) {
        int ping = 0;
        try {
            ping = (int)MathUtil.clamp((float)Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1.0f, 300.0f);
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        return ping;
    }
}