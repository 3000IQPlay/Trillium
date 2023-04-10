package dev._3000IQPlay.trillium.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpeedMeter extends HudElement {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Boolean> bps = this.register(new Setting<>("BPS", false));
	float x1 =0;
    float y1= 0;
	
    public SpeedMeter() {
        super("SpeedMeter", "Draws your current speed", 50,10);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        double mx = mc.player.motionX;
        double mz = mc.player.motionZ;
        double motion = Math.hypot(mx, mz) * 20;
        String str ="";
        if(!bps.getValue()) {
            str = "Speed " + ChatFormatting.WHITE + Trillium.speedManager.getSpeedKpH() + " km/h";
        } else {
            str = String.format("Speed %.1f", motion);
        }
		FontRender.drawString6(str, getPosX(), getPosY(), color.getValue().getRawColor(),true);
    }
}