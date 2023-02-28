package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render2DEvent;
import dev._3000IQPlay.trillium.gui.clickui.ClickUI;
import dev._3000IQPlay.trillium.gui.clickui.ColorUtil;
import dev._3000IQPlay.trillium.gui.clickui.Colors;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.Timer;
import dev._3000IQPlay.trillium.util.Util;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    private long fadeinn;
    private long fadeinnn;
    private int n;
    public int i = 85;
    private final Timer timer = new Timer();
    private final Setting<colorModeEn> colorMode = register(new Setting("ColorMode", colorModeEn.Analogous));
	public Setting<GM> gradientMode = register(new Setting("GradientMode", GM.Horizontal));
	public Setting<Boolean> darkBackGround = this.register(new Setting<Boolean>("DarkBackGround", true));
	public Setting<Boolean> showBinds = this.register(new Setting<Boolean>("ShowBinds", true));
	public Setting<Boolean> scroll = this.register(new Setting<Boolean>("Scroll", true));
    public Setting<Integer> scrollval = this.register(new Setting<Integer>("Scroll Speed", 10, 1, 30, v -> this.scroll.getValue()));
	public Setting<Integer> fadeintimeout = this.register(new Setting<Integer>("FadeInTimeout", 512, 0, 2048));
    public Setting<Float> fadeintimespeed = this.register(new Setting<Float>("FadeInSpeed", 0.5f, 0.1f, 5.0f));
    public Setting<Integer> colorSpeed = this.register(new Setting<Integer>("ColorSpeed", 18, 2, 54));
	public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("HoverAlpha", 170, 0, 255));
	public Setting<Boolean> gradientBG = this.register(new Setting<Boolean>("GradientBG", true));
	
	public final Setting<ColorSetting> gradientLB = this.register(new Setting<>("GLeftBotom", new ColorSetting(-8453889), v -> this.gradientBG.getValue()));
    public final Setting<ColorSetting> gradientLT = this.register(new Setting<>("GLeftTop", new ColorSetting(-16711681), v -> this.gradientBG.getValue()));
	public final Setting<ColorSetting> gradientRB = this.register(new Setting<>("GRightBottom", new ColorSetting(-16711808), v -> this.gradientBG.getValue()));
    public final Setting<ColorSetting> gradientRT = this.register(new Setting<>("GRightTop", new ColorSetting(-14024449), v -> this.gradientBG.getValue()));
	
	public final Setting<ColorSetting> mainColor = this.register(new Setting<>("MainColor", new ColorSetting(-8453889)));
	public final Setting<ColorSetting> hcolor1 = this.register(new Setting<>("MainColor1", new ColorSetting(-8453889)));
    public final Setting<ColorSetting> acolor = this.register(new Setting<>("MainColor2", new ColorSetting(-16711681)));
	public final Setting<ColorSetting> mainColor3 = this.register(new Setting<>("MainColor3", new ColorSetting(-16711808)));
    public final Setting<ColorSetting> mainColor2 = this.register(new Setting<>("MainColor4", new ColorSetting(-14024449)));
	
    public final Setting<ColorSetting> slidercolor = this.register(new Setting<>("SliderColor", new ColorSetting(-16733441)));
	
    public final Setting<ColorSetting> gcolor1 = this.register(new Setting<>("gcolor1", new ColorSetting(-4597637)));
    public final Setting<ColorSetting> gcolor2 = this.register(new Setting<>("gcolor2", new ColorSetting(-16777216)));
	
    public final Setting<ColorSetting> topColor = this.register(new Setting<>("TopColor", new ColorSetting(-115042915)));
    public final Setting<ColorSetting> downColor = this.register(new Setting<>("DownColor", new ColorSetting(-114219739)));

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
        int index = count;
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
                        hcolor1.getValue().getColorObject(), Colors.ALTERNATE_COLOR, true);
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

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        float mouseposx = (float) Mouse.getX() / 100;
        float mouseposy = (float) Mouse.getY() / 100;
        fadeinn = (long) (timer.getPassedTimeMs() / fadeintimespeed.getValue());
		if (fadeinn < fadeintimeout.getValue()) {
			fadeinnn = fadeinn;
		}
    }

    @Override
    public void onDisable() {
        timer.reset();
        if (ClickGui.mc.entityRenderer.getShaderGroup() != null) {
            ClickGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }
    }

    @Override
    public void onLoad() {
        mainColor.getValue().getColorObject();
        Trillium.commandManager.setPrefix(Trillium.moduleManager.getModuleByClass(MainSettings.class).prefix.getValue());
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
	
	public enum GM {
        Horizontal,
        Vertical
    }

    public enum colorModeEn {
        Sky,
        LightRainbow,
        Rainbow,
        Fade,
        DoubleColor,
        Analogous
    }
}