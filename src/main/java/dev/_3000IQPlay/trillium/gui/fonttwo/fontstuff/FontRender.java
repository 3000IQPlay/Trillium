package dev._3000IQPlay.trillium.gui.fonttwo.fontstuff;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class FontRender{
    public static boolean isCustomFontEnabled() {
            return true;
    }
	
	public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static float drawStringWithShadow(String text, float x, float y, int color) {
        return drawStringWithShadow(text, (int) x, (int) y, color);
    }

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask((boolean) false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
    }

    public static float drawString(String text, float x, float y, int color) {
        return drawString(text, (int) x, (int) y, color);
    }

    // ints
    public static float drawStringWithShadow(String text, int x, int y, int color) {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer.drawStringWithShadow(text, x, y, color);

        return Util.mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static float drawString(String text, int x, int y, int color) {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer.drawString(text, x, y, color);

        return Util.mc.fontRenderer.drawString(text, x, y, color);
    }

    public static float drawString2(String text, int x, int y, int color) {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer2.drawString(text, x, y, color);

        return Util.mc.fontRenderer.drawString(text, x, y, color);
    }

    public static float drawString3(String text, float x, float y, int color) {
        return Trillium.fontRenderer3.drawString(text, x, y, color);
    }
    public static float drawCentString3(String text, float x, float y, int color) {
        return Trillium.fontRenderer3.drawString(text, x - getStringWidth3(text) / 2f, y - getFontHeight3() / 2f , color);
    }




    public static float drawString4(String text, int x, int y, int color) {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer4.drawString(text, x, y, color);

        return Util.mc.fontRenderer.drawString(text, x, y, color);
    }
    public static float drawString5(String text, float x, float y, int color) {
            return Trillium.fontRenderer5.drawString(text, x, y, color);
    }


    public static float drawString6(String text, float x, float y, int color,boolean shadow) {
        if (shadow) {
            return Trillium.fontRenderer6.drawString(text, x, y, color);

            // Trillium.fontRenderer6.drawString(text, x - 0.6F, y - 0.6F,new Color(0x66000001, true).getRGB(), false);
          //  Trillium.fontRenderer6.drawString(text, x, y, color, false);
        } else {
            return Trillium.fontRenderer6.drawString(text, x, y, color);
        }
    }


    public static float drawCentString6(String text, float x, float y, int color) {
        return Trillium.fontRenderer6.drawString(text, x - getStringWidth6(text) / 2f, y, color);

    }

    public static float drawCentString5(String text, float x, float y, int color) {
        return Trillium.fontRenderer5.drawString(text, x - getStringWidth5(text) / 2f, y, color);

    }


    public static int getStringWidth(String str) {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer.getStringWidth(str);

        return Util.mc.fontRenderer.getStringWidth(str);
    }

    public static int getStringWidth6(String str) {
        return Trillium.fontRenderer6.getStringWidth(str);
    }

    public static int getStringWidth5(String str) {
        return Trillium.fontRenderer5.getStringWidth(str);
    }

    public static int getStringWidth3(String str) {
        return Trillium.fontRenderer3.getStringWidth(str);
    }


    public static int getStringWidth4(String str) {
            return Trillium.fontRenderer4.getStringWidth(str);
    }

    public static int getFontHeight() {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer.getHeight() + 2;

        return Util.mc.fontRenderer.FONT_HEIGHT;
    }

    public static int getFontHeight2() {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer4.getHeight() + 2;

        return Util.mc.fontRenderer.FONT_HEIGHT;
    }

    public static int getFontHeight3() {
        if (isCustomFontEnabled())
            return Trillium.fontRenderer3.getHeight();

        return Util.mc.fontRenderer.FONT_HEIGHT;
    }

    public static int getFontHeight6() {
            return Trillium.fontRenderer6.getHeight();
    }
    public static int getFontHeight5() {
        return Trillium.fontRenderer5.getHeight() + 2;
    }

}