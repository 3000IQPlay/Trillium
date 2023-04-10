package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PacketStats extends HudElement {
    public PacketStats() {
        super("PacketStats", "Renders current packet stats", 100, 50);
    }

    public final Setting<ColorSetting> shadowColor = this.register(new Setting<>("ShadowColor", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> color2 = this.register(new Setting<>("Color", new ColorSetting(0xFF101010)));
    public final Setting<ColorSetting> textColor = this.register(new Setting<>("TextColor", new ColorSetting(0xBEBEBE)));
    public final Setting<ColorSetting> color3 = this.register(new Setting<>("Color2", new ColorSetting(0xC59B9B9B)));

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        GlStateManager.pushMatrix();
        RenderUtil.drawBlurredShadow(getPosX(), getPosY(), 80, 40, 20, shadowColor.getValue().getColorObject());
        RoundedShader.drawRound(getPosX(), getPosY(), 80, 40, 7f, color2.getValue().getColorObject());
        RoundedShader.drawRound(getPosX() + 2, getPosY() + 13, 76, 1, 0.5f, color3.getValue().getColorObject());
        FontRender.drawCentString6("PacketStats", getPosX() + 40, getPosY() + 5, textColor.getValue().getColor());
        FontRender.drawString5("In: " + packets_in * 4 + " p/s", getPosX() + 3, getPosY() + 20, textColor.getValue().getColor());
        FontRender.drawString5("Out: " + packets_out * 4 + " p/s", getPosX() + 3, getPosY() + 30, textColor.getValue().getColor());
        GlStateManager.popMatrix();
    }

    int counter_in;
    int counter_out;

    int packets_in;
    int packets_out;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e){
        counter_in++;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e){
        counter_out++;
    }

    @Override
    public void onUpdate(){
        if(mc.player.ticksExisted % 5 == 0){
            packets_in = counter_in;
            packets_out = counter_out;
            counter_out = 0;
            counter_in = 0;
        }
    }
}