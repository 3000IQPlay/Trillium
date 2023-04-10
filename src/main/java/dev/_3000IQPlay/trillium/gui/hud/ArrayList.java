package dev._3000IQPlay.trillium.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.PaletteHelper;
import dev._3000IQPlay.trillium.util.RenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static dev._3000IQPlay.trillium.util.RenderUtil.TwoColoreffect;

public class ArrayList extends HudElement {
    private final Setting<Mode> mode = register(new Setting<>("Mode", Mode.ColorText));
    private final Setting<ColorSetting> color = register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Float> rainbowSpeed = register(new Setting("Speed", 10.0f, 1.0f, 20.0f));
    private final Setting<Float> saturation = register(new Setting("Saturation", 0.5f, 0.1f, 1.0f));
    private final Setting<Integer> gste = register(new Setting("GS", 30, 1, 50));
    private final Setting<Boolean> glow = register(new Setting<>("glow", false));
    private final Setting<cMode> cmode = register(new Setting<>("ColorMode", cMode.Rainbow));
    private final Setting<Boolean> hrender = register(new Setting<>("HideHud", true));
    private final Setting<Boolean> hhud = register(new Setting<>("HideRender", true));
    private final Setting<ColorSetting> color2 = this.register(new Setting<>("Color2", new ColorSetting(237176633)));
    private final Setting<ColorSetting> color3 = this.register(new Setting<>("RectColor", new ColorSetting(-16777216)));
    private final Setting<ColorSetting> color4 = this.register(new Setting<>("SideRectColor", new ColorSetting(-16777216)));

    public ArrayList() {
		super("ArrayList", "Displays enabled modules in array", 50, 30);
	}

    boolean reverse;

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
        float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);

        int stringWidth;
        reverse = getPosX() > (float) (e.getScreenWidth() / 2);
        int offset = 0;
        int offset2 = 0;

        int yTotal = 0;
        for (int i = 0; i < Trillium.moduleManager.sortedModules.size(); ++i) {
            yTotal += FontRender.getFontHeight6() + 3;
        }
        setHeight(yTotal);

        // Если режим - ЦветнойТекст, то мы рендерим сначала эффект свечения, а затем плитки
        if(mode.getValue() == Mode.ColorText) {
            for (int k = 0; k < Trillium.moduleManager.sortedModules.size(); k++) {
                Module module = Trillium.moduleManager.sortedModules.get(k);
                if (!module.isDrawn()) {continue;}
                if (hrender.getValue() && module.getCategory() == Category.RENDER) {continue;}
                if (hhud.getValue() && module.getCategory() == Category.HUD) {continue;}
                Color color1 = null;
                if (cmode.getValue() == cMode.Rainbow) {
                    color1 = PaletteHelper.astolfo(offset2, yTotal, saturation.getValue(), rainbowSpeed.getValue());
                } else if (cmode.getValue() == cMode.DoubleColor) {
                    color1 = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + offset2 * ((20f - rainbowSpeed.getValue()) / 200));
                } else {
                    color1 = new Color(color.getValue().getColor()).darker();
                }
                if (!reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) RenderHelper.drawBlurredShadow(getPosX() - 3, getPosY() + (float) offset2 - 1, (float) stringWidth + 4.0f, 9.0f, gste.getValue(), color1);
                }
                if (reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) RenderHelper.drawBlurredShadow(getPosX() - (float) stringWidth - 3, getPosY() + (float) offset2 - 1, stringWidth + 4, 9f, gste.getValue(), color1);
                }
                offset2 += 8;
            }
        }
        //

        for (int k = 0; k < Trillium.moduleManager.sortedModules.size(); k++) {
            Module module = Trillium.moduleManager.sortedModules.get(k);
            if (!module.isDrawn()) {
                continue;
            }
            if(hrender.getValue() && module.getCategory() == Category.RENDER){
                continue;
            }
            if(hhud.getValue() && module.getCategory() == Category.HUD){
                continue;
            }
            Color color1 = null;

            if(cmode.getValue() == cMode.Rainbow){
                color1 = PaletteHelper.astolfo(offset, yTotal, saturation.getValue(), rainbowSpeed.getValue());
            } else if(cmode.getValue() == cMode.DoubleColor){
                color1 = TwoColoreffect(color.getValue().getColorObject(), color2.getValue().getColorObject(), Math.abs(System.currentTimeMillis() / 10) / 100.0 + offset * ((20f - rainbowSpeed.getValue()) / 200) );
            } else {
                color1 = new Color(color.getValue().getColor()).darker();
            }

            if(mode.getValue() == Mode.ColorRect) {
                if (!reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) {
                        RenderHelper.drawBlurredShadow(getPosX() - 3, getPosY() + (float) offset - 1, (float) stringWidth + 4.0f, 9.0f, gste.getValue(), color1);
                    }
                    drawRect(getPosX(), getPosY() + (float) offset, getPosX() + (float) stringWidth + 1.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                    drawRect(getPosX() - 2.0f, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, color4.getValue().getColor());
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() + 3.0f, getPosY() + 2.0f + (float) offset, -1, false);
                }
                if (reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    if (glow.getValue()) {
                        RenderHelper.drawBlurredShadow(getPosX() - (float) stringWidth - 3, getPosY() + (float) offset - 1, stringWidth + 4, 9f, gste.getValue(), color1);
                    }
                    drawRect(getPosX() - (float) stringWidth, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                    drawRect(getPosX() + 1f, getPosY() + (float) offset, getPosX() + 4.0f, getPosY() + (float) offset + 8.0f, color4.getValue().getColor());
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() - stringWidth + 2.0f, getPosY() + 2.0f + (float) offset, -1, false);
                }
            } else {
                if (!reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    drawRect(getPosX(), getPosY() + (float) offset, getPosX() + (float) stringWidth + 1.0f, getPosY() + (float) offset + 8.0f, color3.getValue().getColor());
                    drawRect(getPosX() - 2.0f, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() + 3.0f, getPosY() + 2.0f + (float) offset, color1.getRGB(), false);
                }
                if (reverse) {
                    stringWidth = FontRender.getStringWidth6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "")) + 3;
                    drawRect(getPosX() - (float) stringWidth, getPosY() + (float) offset, getPosX() + 1.0f, getPosY() + (float) offset + 8.0f, color3.getValue().getColor());
                    drawRect(getPosX() + 1f, getPosY() + (float) offset, getPosX() + 4.0f, getPosY() + (float) offset + 8.0f, color1.getRGB());
                    FontRender.drawString6(module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : ""), getPosX() - stringWidth + 2.0f, getPosY() + 2.0f + (float) offset, color1.getRGB(), false);
                }
            }
            offset += 8;
        }
    }

    private enum cMode {
        Rainbow, Custom,DoubleColor
    }

    private enum Mode {
        ColorText, ColorRect
    }
}