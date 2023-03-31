package dev._3000IQPlay.trillium.gui.hud;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.AttackEvent;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.combat.Aura;
import dev._3000IQPlay.trillium.modules.combat.AutoCrystal;
import dev._3000IQPlay.trillium.modules.render.NameTags;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.PositionSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.MathUtil;
import dev._3000IQPlay.trillium.util.RoundedShader;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import java.util.Objects;


public class TargetHud extends Module {
	private static TargetHud INSTANCE = new TargetHud();

    public TargetHud() {
        super("TargetHud", "TargetHud", Category.HUD, true, false, false);
		this.setInstance();
    }
	
	private Setting<colorModeEn> colorMode = register(new Setting("RectColorType", colorModeEn.Sky));
	public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("MainColorSpeed", 18, 2, 54));
	public Setting<ColorSetting> rectC1 = this.register(new Setting<ColorSetting>("MainColor1", new ColorSetting(0x4ea1fd)));
	public Setting<ColorSetting> rectC2 = this.register(new Setting<ColorSetting>("MainColor2", new ColorSetting(0x4efd9a)));
	
    private final Setting<PositionSetting> pos = this.register(new Setting<>("Position", new PositionSetting(0.5f,0.5f)));
    private final Timer timer = new Timer();
    private Entity target;
    private Entity lastTarget;
    private float displayHealth;
    private float health;
    private final java.util.ArrayList<Particles> particles = new java.util.ArrayList();

    private boolean sentParticles;
    private double scale = 1;
    private final Timer timeUtil = new Timer();
	
	public static TargetHud getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TargetHud();
        }
        return INSTANCE;
    }
	
	private void setInstance() {
        INSTANCE = this;
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        Gui.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    float ticks;

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
        return normaliseX() > posX + 38 + 2 && normaliseX()< posX + 129 && normaliseY() > posY - 34 &&  normaliseY() < posY + 14;
    }
	
	public Color getColor(int count) {
        int index = (int) (count);
        switch (colorMode.getValue()) {
            case Sky:
                return ColorUtil.skyRainbow((int)30 - this.colorSpeed.getValue(), index);
            case LightRainbow:
                return ColorUtil.rainbow((int)30 - this.colorSpeed.getValue(), index, .6f, 1, 1);

            case Rainbow:
                return ColorUtil.rainbow((int)30 - this.colorSpeed.getValue(), index, 1f, 1, 1);

            case Fade:
                return ColorUtil.fade((int)30 - this.colorSpeed.getValue(), index, rectC1.getValue().getColorObject(), 1);

            case DoubleColor:
                return ColorUtil.interpolateColorsBackAndForth((int)30 - this.colorSpeed.getValue(), index,
                        rectC1.getValue().getColorObject(), rectC2.getValue().getColorObject(), true);
            case Analogous:
                int val = 1;
                Color analogous = ColorUtil.getAnalogousColor(rectC2.getValue().getColorObject())[val];
                return ColorUtil.interpolateColorsBackAndForth((int)30 - this.colorSpeed.getValue(), index, rectC1.getValue().getColorObject(), analogous, true);
            default:
                return rectC1.getValue().getColorObject();
        }
    }

    float posX = 0;
    float posY = 0;

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {

        ScaledResolution sr = new ScaledResolution(mc);
        posX = sr.getScaledWidth() * pos.getValue().getX();
        posY  = sr.getScaledHeight() * pos.getValue().getY();
		
		if (Aura.target != null) {
            if (Aura.target instanceof EntityPlayer) {
                target = (EntityPlayer) Aura.target;
            } else {
                target = null;
            }
        } else if (Trillium.moduleManager.getModuleByClass(AutoCrystal.class).getTarget() != null) {
            target = Trillium.moduleManager.getModuleByClass(AutoCrystal.class).getTarget();
		}

        if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof HudEditorGui){
            target = mc.player;

            if(isHovering()){
                if(Mouse.isButtonDown(0) && mousestate){
                    pos.getValue().setX( (float) (normaliseX() - dragX) /  sr.getScaledWidth());
                    pos.getValue().setY( (float) (normaliseY() - dragY) / sr.getScaledHeight());
                }
            }
        } else if(target == mc.player){
            target = null;
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


        final float nameWidth = 38;

        if (timer.passedMs(9)) {
            if (target != null && (target.getDistance(mc.player) > 10 || mc.world.getEntityByID(Objects.requireNonNull(target).getEntityId()) == null)) {
                scale = Math.max(0, scale - timeUtil.getPassedTimeMs() / 8E+13 - (1 - scale) / 10);
                particles.clear();
                timer.reset();
            } else {
                scale = Math.min(1, scale + timeUtil.getPassedTimeMs() / 4E+14 + (1 - scale) / 10);
            }
        }

        if (target == null || !(target instanceof EntityPlayer)) {
            particles.clear();
            return;
        }

        if (scale == 0) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate((posX + 38 + 2 + 129 / 2f) * (1 - scale), (posY - 34 + 48 / 2f) * (1 - scale), 0);
        GlStateManager.scale(scale, scale, 0);

        final EntityPlayer en = (EntityPlayer) target;
        final double dist = mc.player.getDistance(target);

        final String name = ((EntityPlayer) target).getName();

        //Background
        final ItemStack renderOffhand = ((EntityPlayer) target).getHeldItemOffhand().copy();

        RoundedShader.drawGradientHorizontal((float)posX + 38.0f + 2.0f, (float)posY - 34.0f, 140.0f, 48.0f, 8.0f, new Color(0, 0, 0, 255), new Color(0, 0, 0, 255));
		RoundedShader.drawGradientHorizontal((float)posX + 38.0f + 2.0f, (float)posY - 34.0f, 140.0f, 48.0f, 8.0f, new Color(0, 0, 0, 255), new Color(0, 0, 0, 255));
		RoundedShader.drawGradientHorizontal((float)posX + 38.0f + 2.0f, (float)posY - 34.0f, 140.0f, 48.0f, 8.0f, new Color(0, 0, 0, 255), new Color(0, 0, 0, 255));
		RoundedShader.drawGradientHorizontal((float)posX + 38.0f + 2.0f, (float)posY - 34.0f, 140.0f, 48.0f, 8.0f, new Color(0, 0, 0, 255), new Color(0, 0, 0, 255));
		RoundedShader.drawGradientHorizontal((float)posX + 38.0f - 1, (float)posY - 37.0f, 146.0f, 54.0f, 8.0f, ColorUtil.applyOpacity(TargetHud.getInstance().getColor(200), 0.7f).getRGB(), ColorUtil.applyOpacity(TargetHud.getInstance().getColor(0), 0.7f).getRGB());
        renderItemStack(renderOffhand, (int)posX + 38 + 2 + 140 - 22, (int)posY - 27);

        GlStateManager.popMatrix();

        final int scaleOffset = (int) (((EntityPlayer) target).hurtTime * 0.35f);

        for (final Particles p : particles) {
            if (p.opacity > 4) p.render2D();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((posX + 38 + 2 + 129 / 2f) * (1 - scale), (posY - 34 + 48 / 2f) * (1 - scale), 0);
        GlStateManager.scale(scale, scale, 0);

        if (target instanceof AbstractClientPlayer) {
            final double offset = -(((AbstractClientPlayer) target).hurtTime * 23);
            Particles.color(new Color(255, (int) (255 + offset), (int) (255 + offset)));
            try {
                renderPlayerModelTexture(posX + 38 + 6 + scaleOffset / 2f, posY - 34 + 5 + scaleOffset / 2f, 3, 3, 3, 3, 30 - scaleOffset, 30 - scaleOffset, 24, 24.5f, (AbstractClientPlayer) en);

            } catch (Exception ignored){

            }
            renderPlayerModelTexture(posX + 38 + 6 + scaleOffset / 2f, posY - 34 + 5 + scaleOffset / 2f, 15, 3, 3, 3, 30 - scaleOffset, 30 - scaleOffset, 24, 24.5f, (AbstractClientPlayer) en);
            Particles.color(Color.WHITE);
        }

        final double fontHeight = 7;

        FontRender.drawString6("Distance: " + MathUtil.round(dist, 1), (int) (posX + 38 + 6 + 30 + 3), (int) (posY - 34 + 8 + 15 + 2), -1, false);

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Particles.scissor(posX + 38 + 6 + 30 + 3, posY - 34 + 5 + 15 - fontHeight, 91, 30);

        FontRender.drawString6("Name: " + name, (int) (posX + 38 + 6 + 30 + 3), (int) (posY - 34 + 8 + 15 - fontHeight), -1, false);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        if (!String.valueOf(((EntityPlayer) target).getHealth()).equals("NaN"))
            health = Math.min(20, ((EntityPlayer) target).getHealth());

        if (String.valueOf(displayHealth).equals("NaN")) {
            displayHealth = (float) (Math.random() * 20);
        }

        if ((dist > 10) || target.isDead) {
            health = 0;
        }

        final int speed = 6;
        if (timer.passedMs(1000 / 60)) {
            displayHealth = (displayHealth * (speed - 1) + health) / speed;

            ticks += 0.1f;

            for (final Particles p : particles) {
                p.updatePosition();

                if (p.opacity < 1) particles.remove(p);
            }

            timer.reset();
        }


        float offset = 6;
        final float drawBarPosX = posX + nameWidth;

        if (displayHealth > 0.1)
            for (int i = 0; i < displayHealth * 4; i++) {
				int color = -1;
                color = Particles.mixColors(this.rectC1.getValue().getColorObject(), this.rectC2.getValue().getColorObject(), (Math.sin(ticks + posX * 0.4f + i * 0.6f / 14f) + 1) * 0.5f).hashCode();
                Gui.drawRect((int) (drawBarPosX + offset), (int) (posY + 5), (int) (drawBarPosX + 1 + offset * 1.25), (int) (posY + 10), color);
                offset += 1;
            }

        if ((((EntityPlayer) target).hurtTime == 9 && !sentParticles) || (lastTarget != null && ((EntityPlayer) lastTarget).hurtTime == 9 && !sentParticles)) {

            for (int i = 0; i <= 15; i++) {
                final Particles p = new Particles();
                final Color c;
                c = Particles.mixColors(this.rectC1.getValue().getColorObject(), this.rectC2.getValue().getColorObject(), (Math.sin(ticks + posX * 0.4f + i) + 1) * 0.5f);
                p.init(posX + 55, posY - 15, ((Math.random() - 0.5) * 2) * 1.4, ((Math.random() - 0.5) * 2) * 1.4, Math.random() * 4, c);
                particles.add(p);
            }

            sentParticles = true;
        }

        if (((EntityPlayer) target).hurtTime == 8) sentParticles = false;

        if (!(dist > 20|| target.isDead)) {
            FontRender.drawString6(MathUtil.round(displayHealth, 1) + "", (int) (drawBarPosX + 2 + offset * 1.25), (int) (posY + 2.5f), -1, false);
        }

        if (lastTarget != target) {
            lastTarget = target;
        }

        final java.util.ArrayList<Particles> removeList = new java.util.ArrayList<>();
        for (final Particles p : particles) {
            if (p.opacity <= 1) {
                removeList.add(p);
            }
        }

        for (final Particles p : removeList) {
            particles.remove(p);
        }
        GlStateManager.popMatrix();
        timeUtil.reset();
    }
	
    private void renderItemStack(final ItemStack stack, final int x, final int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        NameTags.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        NameTags.mc.getRenderItem().renderItemAndEffectIntoGUI(stack,  x,  y);
        NameTags.mc.getRenderItem().renderItemOverlays(NameTags.mc.fontRenderer,  stack,  x,  y);
        NameTags.mc.getRenderItem().zLevel = 0.0f;
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f,  0.5f,  0.5f);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f,  2.0f,  2.0f);
        GlStateManager.popMatrix();
    }
	
	public static enum colorModeEn {
        Sky,
        LightRainbow,
        Rainbow,
        Fade,
        DoubleColor,
        Analogous;
    }
}