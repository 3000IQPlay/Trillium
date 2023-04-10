package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ArmorHud extends HudElement {

    public ArmorHud() {
        super("ArmorHud", "Displays your armour on the screen" , 100, 20);
    }

    public static float calculatePercentage(ItemStack stack) {
        float durability = stack.getMaxDamage() - stack.getItemDamage();
        return (durability / (float) stack.getMaxDamage()) * 100F;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        GlStateManager.enableTexture2D();

        int iteration = 0;

        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty())
                continue;
            int x = (int) (getPosX() - 90 + (9 - iteration) * 20 + 2);
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, (int) getPosY());
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, (int) getPosY(), "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            mc.fontRenderer.drawStringWithShadow(s, (x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (getPosY() + 9), 16777215);
            int dmg = (int) calculatePercentage(is);
            mc.fontRenderer.drawStringWithShadow(dmg + "", (x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2f), (getPosY() - 11), new Color(0, 255, 0).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
}