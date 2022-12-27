package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.MathUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class LowHPScreen extends Module {

    public LowHPScreen() {
        super("LowHPScreen", "LowHPScreen", Category.RENDER, true, false, false);
    }




    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x2250b4b4)));

    int dynamic_alpha = 0;
    int nuyahz = 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent e){

        Color color2 = new Color(color.getValue().getRed(),color.getValue().getGreen(),color.getValue().getBlue(), MathUtil.clamp(dynamic_alpha + 40,0,255));

        if(mc.player.getHealth() < 10) {
            ScaledResolution sr = new ScaledResolution(mc);
            RenderUtil.draw2DGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledWidth(), color2.getRGB(), new Color(0,0,0,0).getRGB(),  color2.getRGB(), new Color(0,0,0,0).getRGB());
            if(mc.player.getHealth() > 9){
                nuyahz = 18;
            } else
            if(mc.player.getHealth() > 8){
                nuyahz = 36;
            } else
            if(mc.player.getHealth() > 7){
                nuyahz = 54;
            } else
            if(mc.player.getHealth() > 6){
                nuyahz = 72;
            } else
            if(mc.player.getHealth() > 5){
                nuyahz = 90;
            } else
            if(mc.player.getHealth() > 4){
                nuyahz = 108;
            } else
            if(mc.player.getHealth() > 3){
                nuyahz = 126;
            } else
            if(mc.player.getHealth() > 2){
                nuyahz = 144;
            } else
            if(mc.player.getHealth() > 1){
                nuyahz = 162;
            } else
            if(mc.player.getHealth() > 0){
                nuyahz = 180;
            }
        }

        if(nuyahz > dynamic_alpha){
            dynamic_alpha = dynamic_alpha + 3;
        }
        if(nuyahz < dynamic_alpha){
            dynamic_alpha = dynamic_alpha - 3;
        }

    }

    //dalpha 180/10
    // 18

}
