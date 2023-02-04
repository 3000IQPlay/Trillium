package dev._3000IQPlay.trillium.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

public interface Util {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;
    RenderManager rendermgr = mc.getRenderManager();
}