package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.event.events.ConnectToServerEvent;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.clickui.ClickUI;
import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.PNGtoResourceLocation;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_BLEND;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    private int n;
    public int i = 85;
    private final Timer timer = new Timer();
    private Setting<colorModeEn> colorMode = register(new Setting("ColorMode", colorModeEn.Analogous));
	public Setting<GM> gradientMode = register(new Setting("GradientMode", GM.Horizontal));
	public Setting<Boolean> darkBackGround = this.register(new Setting<Boolean>("DarkBackGround", true));
	public Setting<Boolean> showBinds = this.register(new Setting<Boolean>("ShowBinds", true));
	public Setting<Boolean> scroll = this.register(new Setting<Boolean>("Scroll", true));
    public Setting<Integer> scrollval = this.register(new Setting<Integer>("Scroll Speed", 10, 1, 30, v -> this.scroll.getValue()));
    public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("ColorSpeed", 18, 2, 54));
	public Setting<Boolean> gradientBG = this.register(new Setting<Boolean>("GradientBG", true));
	
	public final Setting<ColorSetting> gradientLB = this.register(new Setting<>("GLeftBotom", new ColorSetting(-8453889), v -> this.gradientBG.getValue()));
    public final Setting<ColorSetting> gradientLT = this.register(new Setting<>("GLeftTop", new ColorSetting(-16711681), v -> this.gradientBG.getValue()));
	public final Setting<ColorSetting> gradientRB = this.register(new Setting<>("GRightBottom", new ColorSetting(-16711808), v -> this.gradientBG.getValue()));
    public final Setting<ColorSetting> gradientRT = this.register(new Setting<>("GRightTop", new ColorSetting(-14024449), v -> this.gradientBG.getValue()));
	
	public final Setting<ColorSetting> hcolor1 = this.register(new Setting<>("MainColor", new ColorSetting(-8453889), v -> this.colorMode.getValue() == colorModeEn.Fade || this.colorMode.getValue() == colorModeEn.DoubleColor || this.colorMode.getValue() == colorModeEn.Analogous));
	public final Setting<ColorSetting> hcolor2 = this.register(new Setting<>("MainColor2", new ColorSetting(-16711808), v -> this.colorMode.getValue() == colorModeEn.DoubleColor));
    public final Setting<ColorSetting> acolor = this.register(new Setting<>("AnalogousColor", new ColorSetting(-16711681), v -> this.colorMode.getValue() == colorModeEn.Analogous));

    public String[] myString;
    public ClickGui() {
        super("ClickGui", "Important module", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
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
                return ColorUtil.fade((int)30 - this.colorSpeed.getValue(), index, hcolor1.getValue().getColorObject(), 1);

            case DoubleColor:
                return ColorUtil.interpolateColorsBackAndForth((int)30 - this.colorSpeed.getValue(), index,
                        hcolor1.getValue().getColorObject(), hcolor2.getValue().getColorObject(), true);
            case Analogous:
                int val = 1;
                Color analogous = ColorUtil.getAnalogousColor(acolor.getValue().getColorObject())[val];
                return ColorUtil.interpolateColorsBackAndForth((int)30 - this.colorSpeed.getValue(), index, hcolor1.getValue().getColorObject(), analogous, true);
            default:
                return hcolor1.getValue().getColorObject();
        }
    }

    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(ClickUI.getClickGui());
        timer.reset();
    }
	
    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        timer.reset();
        if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
            ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @Override
    public void onTick() {
        this.disable();
        timer.reset();
    }
	
    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
	
	public static enum GM {
        Horizontal,
        Vertical;
    }

    public enum colorModeEn {
        Sky,
        LightRainbow,
        Rainbow,
        Fade,
        DoubleColor,
        Analogous;
    }
}