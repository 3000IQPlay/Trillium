package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.util.PaletteHelper;
import dev._3000IQPlay.trillium.util.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.awt.*;

// Unfinished Module

public class ChinaHat
        extends Module {
	public Setting<HatMode> chinaHatMode = this.register(new Setting<HatMode>("ColorMode", HatMode.Astolfo));
	public Setting<ColorSetting> colorC = this.register(new Setting<ColorSetting>("HatColor", new ColorSetting(0), v -> this.chinaHatMode.getValue() == HatMode.Custom));
	public Setting<Float> hatAlpha = this.register(new Setting<Float>("HatAlpha", 0.5f, 0.2f, 1.0f, v -> this.chinaHatMode.getValue() != HatMode.Custom));
	public Setting<Float> rotateSpeed = this.register(new Setting<Float>("RotateSpeed", 2.0f, 0.0f, 5.0f));
	public Setting<Integer> segments = this.register(new Setting<Integer>("Segments", 50, 6, 50));
    public Setting<Float> height = this.register(new Setting<Float>("Height", 0.3f, 0.1f, 1.0f));
    public Setting<Float> radius = this.register(new Setting<Float>("Radius", 0.7f, 0.3f, 1.5f));
	public Setting<Boolean> renderFriends = this.register(new Setting<Boolean>("DrawOnFriends", true));
	public Setting<Boolean> renderEnemies = this.register(new Setting<Boolean>("DrawOnEnemies", false));
    public Setting<Boolean> drawThePlayer = this.register(new Setting<Boolean>("DrawThePlayer", true));
    public Setting<Boolean> onlyThirdPerson = this.register(new Setting<Boolean>("OnlyThirdPerson", true, v -> this.drawThePlayer.getValue()));
	
	public ChinaHat() {
        super("ChinaHat", "Experiums ChinaHat Rewriten", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if ((drawThePlayer.getValue().booleanValue() && !(this.onlyThirdPerson.getValue().booleanValue() && mc.gameSettings.thirdPersonView == 0)) || (Trillium.friendManager.isFriend(mc.player.getName()) && this.renderFriends.getValue()) || (!Trillium.friendManager.isFriend(mc.player.getName()) && this.renderEnemies.getValue())) {
            drawChinaHatFor(mc.player);
        }
    }
	
	public void drawChinaHatFor(EntityLivingBase event) {
        if (ChinaHat.mc.player.getHealth() <= 0.0f) {
            return;
        }
        GlStateManager.pushMatrix();
        int color = 0;
        switch (this.chinaHatMode.getValue()) {
            case Custom: {
                break;
            }
            case Astolfo: {
                color = PaletteHelper.astolfo(5000.0f, 1).getRGB();
                break;
            }
        }
        double x = ChinaHat.mc.player.lastTickPosX + (ChinaHat.mc.player.posX - ChinaHat.mc.player.lastTickPosX) * (double)ChinaHat.mc.timer.renderPartialTicks - mc.renderManager.renderPosX;
        double y = ChinaHat.mc.player.lastTickPosY + (ChinaHat.mc.player.posY - ChinaHat.mc.player.lastTickPosY) * (double)ChinaHat.mc.timer.renderPartialTicks - mc.renderManager.renderPosY;
        double z = ChinaHat.mc.player.lastTickPosZ + (ChinaHat.mc.player.posZ - ChinaHat.mc.player.lastTickPosZ) * (double)ChinaHat.mc.timer.renderPartialTicks - mc.renderManager.renderPosZ;
        float f = ChinaHat.mc.player.getEyeHeight() + 0.35f;
        float f2 = ChinaHat.mc.player.isSneaking() ? 0.25f : 0.0f;
        float center = 0.5f;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated((x -= 0.5) + (double)center, (y += (double)(f - f2)) + (double)ChinaHat.mc.player.height + (double)this.height.getValue() + (double)center - (double)(ChinaHat.mc.player.isSneaking() ? 0.23D : 0.0D), (z -= 0.5) + (double)center);
        GL11.glRotated((entity.ticksExisted + ChinaHat.mc.timer.renderPartialTicks) * this.rotateSpeed.getValue(), 0.0, 1.0, 0.0);
        GL11.glTranslated(-(x + (double)center), -(y + (double)center), -(z + (double)center));
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        if (this.chinaHatMode.getValue() == HatMode.Custom) {
            RenderHelper.color(new Color(this.colorC.getValue().getRed() / 255.0f, this.colorC.getValue().getGreen() / 255.0f, this.colorC.getValue().getBlue() / 255.0f, this.colorC.getValue().getAlpha() / 255.0f));
        } else {
            GlStateManager.color((float)new Color(color).getRed() / 255.0f, (float)new Color(color).getGreen() / 255.0f, (float)new Color(color).getBlue() / 255.0f, this.hatAlpha.getValue());
        }
        RenderHelper.drawCone(this.radius.getValue(), this.height.getValue(), (int)this.segments.getValue(), true);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GlStateManager.popMatrix();
    }
	
	public static enum HatMode {
		Custom,
		Astolfo;
	}
}