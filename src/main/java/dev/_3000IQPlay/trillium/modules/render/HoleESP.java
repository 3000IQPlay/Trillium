package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Random;

public class HoleESP
        extends Module {
    private static HoleESP INSTANCE = new HoleESP();
	public Setting<Page> page = this.register(new Setting<Page>("Page", Page.Render));
	public Setting<Boolean> renderBedrockHoles = this.register(new Setting<Boolean>("RenderBedrock", true, v -> this.page.getValue() == Page.Render));
	public Setting<Boolean> renderObsidianHoles = this.register(new Setting<Boolean>("RenderObsidian", true, v -> this.page.getValue() == Page.Render));
	private final Setting<Integer> holes = this.register(new Setting<Integer>("Holes", 5, 1, 500, v -> this.page.getValue() == Page.Render));
	public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false, v -> this.page.getValue() == Page.Render));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.page.getValue() == Page.Render));
	private final Setting<Integer> bBoxTopAlpha = this.register(new Setting<Object>("BedrockBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<Integer> bBoxBottomAlpha = this.register(new Setting<Object>("BedrockBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<Integer> oBoxTopAlpha = this.register(new Setting<Object>("ObsidianBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<Integer> oBoxBottomAlpha = this.register(new Setting<Object>("ObsidianBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	public Setting<Double> bHeight = this.register(new Setting<Double>("BedrockHeight", 0.0, -2.0, 2.0, v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	public Setting<Double> oHeight = this.register(new Setting<Double>("ObsidianHeight", 0.0, -2.0, 2.0, v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	
	private final Setting<ColorSetting> btfC = this.register(new Setting<ColorSetting>("BedrockTopFill", new ColorSetting(0x8700ff), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> btoC = this.register(new Setting<ColorSetting>("BedrockTopOutline", new ColorSetting(0x8700ff00), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> bbfC = this.register(new Setting<ColorSetting>("BedrockBottomFill", new ColorSetting(0x00ffff), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> bboC = this.register(new Setting<ColorSetting>("BedrockBottomOutline", new ColorSetting(0x00ffff), v -> this.renderBedrockHoles.getValue() && this.page.getValue() == Page.Render));
	
	private final Setting<ColorSetting> otfC = this.register(new Setting<ColorSetting>("ObsidianTopFill", new ColorSetting(0x8700ff), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> otoC = this.register(new Setting<ColorSetting>("OnsidianTopOutline", new ColorSetting(0x8700ff00), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> obfC = this.register(new Setting<ColorSetting>("OutlineBottomFill", new ColorSetting(0xff0000), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	private final Setting<ColorSetting> oboC = this.register(new Setting<ColorSetting>("OutlineBottomOutline", new ColorSetting(0xff0000), v -> this.renderObsidianHoles.getValue() && this.page.getValue() == Page.Render));
	
	
	public Setting<Float> holeRange = this.register(new Setting<Float>("HoleRange", Float.valueOf(6.0f), Float.valueOf(1.0f), Float.valueOf(256.0f), v -> this.page.getValue() == Page.Manager));
    public Setting<Integer> holeUpdates = this.register(new Setting<Integer>("HoleUpdates", 100, 0, 1000, v -> this.page.getValue() == Page.Manager));
    public Setting<Integer> holeSync = this.register(new Setting<Integer>("HoleSync", 10000, 1, 10000, v -> this.page.getValue() == Page.Manager));
    public Setting<ThreadMode> holeThread = this.register(new Setting<ThreadMode>("HoleThread", ThreadMode.WHILE, v -> this.page.getValue() == Page.Manager));
	
	private int currentAlpha = 0;

    public HoleESP() {
        super("HoleESP", "Shows safe spots", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
		int drawnHoles = 0;
	   	for (BlockPos pos : Trillium.holeManager.getSortedHoles()) {
			if (drawnHoles >= this.holes.getValue()) {
			    break;
			}
		    if (pos.equals(new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.ownHole.getValue().booleanValue() || !RotationUtil.isInFov(pos)) {
				continue;
			}
		    if (this.renderBedrockHoles.getValue() && Trillium.holeManager.isSafe(pos)) {
	            RenderUtil.drawBoxESP(pos, new Color(this.btfC.getValue().getRed(), this.btfC.getValue().getGreen(), this.btfC.getValue().getBlue(), this.btfC.getValue().getAlpha()), true, new Color(this.btoC.getValue().getRed(), this.btoC.getValue().getGreen(), this.btoC.getValue().getBlue(), this.btoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxTopAlpha.getValue(), true, this.bHeight.getValue(), true, true, true, false, this.currentAlpha);
				RenderUtil.drawBoxESP(pos, new Color(this.bbfC.getValue().getRed(), this.bbfC.getValue().getGreen(), this.bbfC.getValue().getBlue(), this.bbfC.getValue().getAlpha()), true, new Color(this.bboC.getValue().getRed(), this.bboC.getValue().getGreen(), this.bboC.getValue().getBlue(), this.bboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxBottomAlpha.getValue(), true, this.bHeight.getValue(), true, true, false, true, this.currentAlpha);
	        } else if (this.renderObsidianHoles.getValue()) {
	    	    RenderUtil.drawBoxESP(pos, new Color(this.otfC.getValue().getRed(), this.otfC.getValue().getGreen(), this.otfC.getValue().getBlue(), this.otfC.getValue().getAlpha()), true, new Color(this.otoC.getValue().getRed(), this.otoC.getValue().getGreen(), this.otoC.getValue().getBlue(), this.otoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxTopAlpha.getValue(), true, this.oHeight.getValue(), true, true, true, false, this.currentAlpha);
			    RenderUtil.drawBoxESP(pos, new Color(this.obfC.getValue().getRed(), this.obfC.getValue().getGreen(), this.obfC.getValue().getBlue(), this.obfC.getValue().getAlpha()), true, new Color(this.oboC.getValue().getRed(), this.oboC.getValue().getGreen(), this.oboC.getValue().getBlue(), this.oboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxBottomAlpha.getValue(), true, this.oHeight.getValue(), true, true, false, true, this.currentAlpha);
			}
			++drawnHoles;
        }
    }
	
	public enum ThreadMode {
        POOL,
        WHILE,
        NONE
    }
	
	public static enum Page {
		Render,
		Manager;
	}
}
