package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

public class BlockHighlight
        extends Module {
	private final Setting<Integer> boxTopAlpha = this.register(new Setting<Object>("BoxTopAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255)));
	private final Setting<Integer> boxBottomAlpha = this.register(new Setting<Object>("BoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255)));
	private final Setting<ColorSetting> tfC = this.register(new Setting<ColorSetting>("TopFill", new ColorSetting(0x8700ff)));
	private final Setting<ColorSetting> toC = this.register(new Setting<ColorSetting>("TopOutline", new ColorSetting(0x8700ff)));
	private final Setting<ColorSetting> bfC = this.register(new Setting<ColorSetting>("BottomFill", new ColorSetting(0x00ffff)));
	private final Setting<ColorSetting> boC = this.register(new Setting<ColorSetting>("BottomOutline", new ColorSetting(0x00ffff)));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
	private int currentAlpha = 0;

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
            RenderUtil.drawBoxESP(blockpos, new Color(this.tfC.getValue().getRed(), this.tfC.getValue().getGreen(), this.tfC.getValue().getBlue(), this.tfC.getValue().getAlpha()), true, new Color(this.toC.getValue().getRed(), this.toC.getValue().getGreen(), this.toC.getValue().getBlue(), this.toC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.boxTopAlpha.getValue(), true, 0.0, true, true, true, false, this.currentAlpha);
	        RenderUtil.drawBoxESP(blockpos, new Color(this.bfC.getValue().getRed(), this.bfC.getValue().getGreen(), this.bfC.getValue().getBlue(), this.bfC.getValue().getAlpha()), true, new Color(this.boC.getValue().getRed(), this.boC.getValue().getGreen(), this.boC.getValue().getBlue(), this.boC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.boxBottomAlpha.getValue(), true, 0.0, true, true, false, true, this.currentAlpha);
        }
    }
}