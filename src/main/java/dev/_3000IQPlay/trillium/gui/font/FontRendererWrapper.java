package dev._3000IQPlay.trillium.gui.font;

import dev._3000IQPlay.trillium.util.DefaultFontRenderer;
import dev._3000IQPlay.trillium.util.IFontRenderer;

public class FontRendererWrapper {

    private static IFontRenderer fontRenderer = DefaultFontRenderer.INSTANCE;

    public static void setFontRenderer(IFontRenderer fontRenderer) {
        FontRendererWrapper.fontRenderer = fontRenderer;
    }

    public static IFontRenderer getFontRenderer() {
        return FontRendererWrapper.fontRenderer;
    }

    public static void drawString(String text, float x, float y, int color) {
        fontRenderer.drawString(text, x, y, color);
    }

    public static void drawStringWithShadow(String text, float x, float y, int color) {
        fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        fontRenderer.drawCenteredString(text, x, y, color);
    }

    public static int getFontHeight() {
        return fontRenderer.getFontHeight();
    }

    public static float getStringHeight(String text) {
        return fontRenderer.getStringHeight(text);
    }

    public static float getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

}