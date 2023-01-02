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

import java.awt.*;

public class ArmorHud extends Module{
    public ArmorHud() {
        super("ArmorHud", "fps", Module.Category.HUD, true, false, false);
    }

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));


    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));



    float x1 =0;
    float y1= 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){
        ScaledResolution sr = new ScaledResolution(mc);

        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();
        renderArmorHUD(true);
		if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }

                RenderUtil.drawRect2(x1 - 10,y1 ,x1 + 50, y1 + 10, new Color(0x73A9A9A9, true).getRGB());
            }
        }
		if (Mouse.isButtonDown(0) && isHovering()){
            if(!mousestate){
                dragX = (int) (normaliseX() - (pos.getValue().getX() * sr.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * sr.getScaledHeight()));
            }
            mousestate = true;
        } else {
            mousestate = false;
        }
    }
	
    public void renderArmorHUD(boolean percent) {
        ScaledResolution sr = new ScaledResolution(mc);
        y1 = sr.getScaledHeight() * pos.getValue().getY();
        x1 = sr.getScaledWidth() * pos.getValue().getX();
        GlStateManager.enableTexture2D();
        int iteration = 0;
        int y = (int) (y1 - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0));
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty())
                continue;
            int x = (int) (x1 - 90 + (9 - iteration) * 20 + 2);
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            mc.fontRenderer.drawStringWithShadow(s, (x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (y + 9), 16777215);
            if (percent) {
                int dmg = (int) ArmorUtils.calculatePercentage(is);
                mc.fontRenderer.drawStringWithShadow(dmg + "", (x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int) (0 * 255.0F), (int) (1 * 255.0F), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
	
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
        return normaliseX() > x1 - 10 && normaliseX()< x1 + 50 && normaliseY() > y1 &&  normaliseY() < y1 + 10;
    }
}
