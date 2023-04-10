package dev._3000IQPlay.trillium.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FpsCounter extends HudElement {

    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));

    public FpsCounter() {
        super("Fps", "Renders FPS amount", 50, 10);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        FontRender.drawString6("FPS " + ChatFormatting.WHITE + Minecraft.getDebugFPS(), getPosX(), getPosY(), color.getValue().getRawColor(), false);
    }
}