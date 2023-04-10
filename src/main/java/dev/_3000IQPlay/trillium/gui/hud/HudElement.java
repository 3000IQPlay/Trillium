package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.hud.HudEditorGui;
import dev._3000IQPlay.trillium.gui.clickui.ClickUI;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.PositionSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class HudElement extends Module {

    int height;
    int width;
    int dragX, dragY = 0;
    private boolean mousestate = false;
    float x1 = 0;
    float y1 = 0;

    public HudElement(String name, String description,int width, int height) {
        super(name, description, Module.Category.HUD, true, false, false);
        this.height = height;
        this.width = width;
    }

    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f, 0.5f)));

    public int normaliseX() {
        return (int) ((Mouse.getX() / 2f));
    }

    public int normaliseY() {
        ScaledResolution sr = new ScaledResolution(mc);
        return (((-Mouse.getY() + sr.getScaledHeight()) + sr.getScaledHeight()) / 2);
    }

    public boolean isHovering() {
        return normaliseX() > x1 && normaliseX() < x1 + width && normaliseY() > y1 && normaliseY() < y1 + height;
    }

    public void onRender2D(Render2DEvent e) {
        y1 = e.scaledResolution.getScaledHeight() * pos.getValue().getY();
        x1 = e.scaledResolution.getScaledWidth() * pos.getValue().getX();

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui || mc.currentScreen instanceof ClickUI) {
            if (Mouse.isButtonDown(0) && mousestate) {
                pos.getValue().setX((float) (normaliseX() - dragX) / e.scaledResolution.getScaledWidth());
                pos.getValue().setY((float) (normaliseY() - dragY) / e.scaledResolution.getScaledHeight());
            }
        }
        if (Mouse.isButtonDown(0)) {
            if (!mousestate && isHovering()) {
                dragX = (int) (normaliseX() - (pos.getValue().getX() * e.scaledResolution.getScaledWidth()));
                dragY = (int) (normaliseY() - (pos.getValue().getY() * e.scaledResolution.getScaledHeight()));
                mousestate = true;
            }
        } else {
            mousestate = false;
        }
    }

    public float getPosX(){
        return x1;
    }

    public float getPosY(){
        return y1;
    }

    public float getX(){
        return pos.getValue().x;
    }

    public float getY(){
        return pos.getValue().y;
    }

    public void setHeight(int h){
        this.height = h;
    }
}