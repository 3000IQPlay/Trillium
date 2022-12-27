package dev._3000IQPlay.trillium.gui.hud;

import com.jhlabs.image.GaussianFilter;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.util.*;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class WaterMark extends Module {
    public WaterMark() {
        super("WaterMark", "WaterMark", Module.Category.HUD, true, false, false);
    }

    int i = 0;
    public Timer timer = new Timer();

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){


                RenderUtil.drawSmoothRect(4f, 4f, Util.fr.getStringWidth("Trillium") + 4 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс ") + 140, 18f, new Color(35, 35, 40, 230).getRGB());

                if (timer.passedMs(350)) {
                    ++i;
                    timer.reset();
                }

                if (i == 24) {
                    i = 0;
                }

                String w1 = "_";
                String w2 = "T_";
                String w3 = "Tr_";
                String w4 = "Thi_";
                String w5 = "Tril_";
                String w6 = "Trill_";
                String w7 = "Trilli_";
                String w8 = "Trilliu_";
                String w9 = "Trillium";
                String w10 = "Trillium";
                String w11 = "Trillium";
                String w12 = "Trilliu_";
                String w13 = "Trilli_";
                String w14 = "Trill_";
                String w15 = "Tril_";
                String w16 = "Tri_";
                String w17 = "Tr_";
                String w18 = "T_";
                String w19 = "_";
                String text = "";
                if (i == 0) {
                    text = w1;
                }
                if (i == 1) {
                    text = w2;
                }
                if (i == 2) {
                    text = w3;
                }
                if (i == 3) {
                    text = w4;
                }
                if (i == 4) {
                    text = w5;
                }
                if (i == 5) {
                    text = w6;
                }
                if (i == 6) {
                    text = w7;
                }
                if (i == 7) {
                    text = w8;
                }
                if (i == 8) {
                    text = w9;
                }
                if (i == 9) {
                    text = w10;
                }
                if (i == 10) {
                    text = w11;
                }
                if (i == 11) {
                    text = w12;
                }
                if (i == 12) {
                    text = w13;
                }
                if (i == 13) {
                    text = w14;
                }
                if (i == 14) {
                    text = w15;
                }
                if (i == 15) {
                    text = w16;
                }
                if (i == 16) {
                    text = w17;
                }
                if (i == 17) {
                    text = w18;
                }
                if (i == 18) {
                    text = w19;
                }

                Util.fr.drawStringWithShadow(text, 9f, 7, -1);
                Util.fr.drawStringWithShadow("|  " + mc.player.getName(), Util.fr.getStringWidth(w13) + 20, 7, -1);
                Util.fr.drawStringWithShadow("|  " + Trillium.serverManager.getPing() + " мс", Util.fr.getStringWidth(w13) + 35 + Util.fr.getStringWidth(mc.player.getName()), 7, -1);
                try {
                    Util.fr.drawStringWithShadow("|  " + (Minecraft.getMinecraft().currentServerData.serverIP), Util.fr.getStringWidth(w13) + 38 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс "), 7, -1);
                } catch (Exception ew) {
                    Util.fr.drawStringWithShadow("|  " + ("SinglePlayer"), Util.fr.getStringWidth(w13) + 38 + Util.fr.getStringWidth(mc.player.getName()) + Util.fr.getStringWidth(" 9999 мс "), 7, -1);
                }
    }



    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }


    public static void setColor(int color) {
        GL11.glColor4ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF), (byte) (color >> 24 & 0xFF));
    }
    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, Color color) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        setColor(color.getRGB());
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture((int)x, (int) y, 0, 0, (int)width, (int)height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }





}
