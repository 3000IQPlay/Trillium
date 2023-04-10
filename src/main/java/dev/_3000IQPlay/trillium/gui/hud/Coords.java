package dev._3000IQPlay.trillium.gui.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.gui.hud.HudElement;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coords extends HudElement {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));

    public Coords() {
        super("Coords", "Display cordiantes", 100, 10);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent e) {
        super.onRender2D(e);
        boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Nether");
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
        FontRender.drawString6(coordinates, getPosX(), getPosY(), color.getValue().getRawColor(), false);
    }
}