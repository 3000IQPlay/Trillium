package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.PositionSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.ArmorUtils;
import dev._3000IQPlay.trillium.util.ColorUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class ArmorHud extends Module{
    public ArmorHud() {
        super("ArmorHud", "Displays your armour on the screen", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));

    float x1 =0;
    float y1= 0;

    int dragX, dragY = 0;
    boolean mousestate = false;

    public int normaliseX(){
        return (int) ((Mouse.getX()/2f));
    }
    public int normaliseY(){
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight())/2);
    }

    public boolean isHovering(){
        return normaliseX() > x1 - 10 && normaliseX()< x1 + 100 && normaliseY() > y1 - 5 &&  normaliseY() < y1 + 20;
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  e.scaledResolution.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
                }
            }
        }
        if (Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
        GlStateManager.enableTexture2D();
        int iteration = 0;
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty())
                continue;
            int x = (int) (x1 - 90 + (9 - iteration) * 20 + 2);
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, (int) y1);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, (int) y1, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            mc.fontRenderer.drawStringWithShadow(s, (x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (y1 + 9), 16777215);
            int dmg = (int) ArmorUtils.calculatePercentage(is);
            mc.fontRenderer.drawStringWithShadow(dmg + "", (x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2f), (y1 - 11), ColorUtil.toRGBA(0, 255, 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

}