package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.event.events.RenderAttackIndicatorEvent;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.exploit.BowKiller;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.util.phobos.IEntityLivingBase;
import dev._3000IQPlay.trillium.util.PaletteHelper;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.util.math.MathHelper.clamp;
import static org.lwjgl.opengl.GL11.*;

public class CoolCrosshair extends Module {
    public final Setting<ColorSetting> color = this.register(new Setting<>("Color", new ColorSetting(0x8800FF00)));
    private final Setting<Boolean> smt = this.register(new Setting<>("Smooth", Boolean.FALSE));
    public Setting<Float> car = this.register(new Setting<>("Otstup", 0.0f, 0.1f, 1.0f));
    public Setting<Float> lwid = this.register(new Setting<>("OtstupTwo", 0.0f, 0.1f, 1.0f));
    public Setting<Float> rounded2 = this.register(new Setting<>("Round2", 0.0f, 0.5f, 20.0f));
    int status = 0;
    int santi = 0;
    float animation = 0;

    public CoolCrosshair() {
        super("CoolCrosshair", "Cool shit", Module.Category.HUD, true, false, false);
    }

    public static void drawPartialCircle(float x, float y, float radius, int startAngle, int endAngle, float thickness, Color colour, boolean smooth) {
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        if (startAngle > endAngle) {
            int temp = startAngle;
            startAngle = endAngle;
            endAngle = temp;
        }
        if (startAngle < 0)
            startAngle = 0;
        if (endAngle > 360)
            endAngle = 360;
        if (smooth) {
            GL11.glEnable(GL_LINE_SMOOTH);
        } else {
            GL11.glDisable(GL_LINE_SMOOTH);
        }
        GL11.glLineWidth(thickness);
        GL11.glColor4f(colour.getRed() / 255.0F, colour.getGreen() / 255.0F, colour.getBlue() / 255.0F, colour.getAlpha() / 255.0F);
        GL11.glBegin(GL_LINE_STRIP);
        float ratio = 0.01745328F;
        for (int i = startAngle; i <= endAngle; i = i + 1) {
            float radians = (i - 90) * ratio;
            GL11.glVertex2f(x + (float) Math.cos(radians) * radius, y + (float) Math.sin(radians) * radius);
        }
        GL11.glEnd();
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public void onRenderAttackIndicator(RenderAttackIndicatorEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onUpdate() {
        if (BowKiller.delayTimer.getPassedTimeMs() < Trillium.moduleManager.getModuleByClass(BowKiller.class).delay.getValue() * 1000) {
            if (animation < 20) {
                animation += 1f;
            }
        }

        if (santi < status) {
            santi = santi + 60;
        }
        if (status < santi) {
            santi = santi - 360;
        }
        if (santi < 0) {
            santi = 0;
        }

    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        super.onRender2D(event);
		
		if (Trillium.moduleManager.getModuleByClass(BowKiller.class).isDisabled()) return;

        float x1 = (float) (event.scaledResolution.getScaledWidth_double() / 2F);
        float y1 = (float) (event.scaledResolution.getScaledHeight_double() / 2F);


        boolean blend = GL11.glIsEnabled(GL_BLEND);
        GL11.glEnable(GL_BLEND);

        if (BowKiller.delayTimer.getPassedTimeMs() > Trillium.moduleManager.getModuleByClass(BowKiller.class).delay.getValue() * 1000 || mc.player.getHeldItemMainhand().getItem() != Items.BOW) {
            animation = 0f;
            status = getCooledAttackStrength() == 0 ? 0 : (int) (360 / (1f / getCooledAttackStrength()));
            drawPartialCircle(x1, y1, rounded2.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() - car.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() + car.getValue(), 0, 360, lwid.getValue(), color.getValue().withAlpha(color.getValue().getAlpha() > 210 ? color.getValue().getAlpha() : color.getValue().getAlpha() + 40).getColorObject(), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() - car.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
            drawPartialCircle(x1, y1, rounded2.getValue() + car.getValue(), 0, status, lwid.getValue(), PaletteHelper.astolfo(false, 1), smt.getValue());
        } else {

            if (animation < 20) {
                animation += 1f;
            }
            RoundedShader.drawRound(x1 - animation, y1 - 3f, animation * 2, 6, 4, new Color(0x0A0A0A));
            RenderUtil.glScissor(x1 - animation, y1 - 3f, x1 + animation * 2, x1 + 6, event.scaledResolution);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            if (BowKiller.delayTimer.getPassedTimeMs() > Trillium.moduleManager.getModuleByClass(BowKiller.class).delay.getValue() * 666f) {
                FontRender.drawCentString5("charging.  ", x1, y1 - 0.5f, -1);
            } else if (BowKiller.delayTimer.getPassedTimeMs() > Trillium.moduleManager.getModuleByClass(BowKiller.class).delay.getValue() * 333f) {
                FontRender.drawCentString5("charging.. ", x1, y1 - 0.5f, -1);
            } else {
                FontRender.drawCentString5("charging...", x1, y1 - 0.5f, -1);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }


        if (!blend) {
            GL11.glDisable(GL_BLEND);
        }
    }

    private float getCooledAttackStrength() {
        return clamp(((float) ((IEntityLivingBase) mc.player).getTicksSinceLastSwing()) / getCooldownPeriod(), 0.0F, 1.0F);
    }

    public float getCooldownPeriod() {
        return (float) (1.0 / mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0);
    }
}