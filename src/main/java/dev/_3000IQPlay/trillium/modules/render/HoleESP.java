package dev._3000IQPlay.trillium.modules.render;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.BlockUtils;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.RotationUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HoleESP
        extends Module {
	public Setting<Boolean> renderBedrockHoles = this.register(new Setting<Boolean>("RenderBedrock", true));
	public Setting<Boolean> renderObsidianHoles = this.register(new Setting<Boolean>("RenderObsidian", true));
	public Setting<Boolean> ownHole = this.register(new Setting<Boolean>("OwnHole", false));
	private final Setting<Integer> rangeXZ = this.register(new Setting<>("RangeXZ", 8, 1, 25));
    private final Setting<Integer> rangeY = this.register(new Setting<>("RangeY", 5, 1, 25));
	private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
	private final Setting<Integer> bBoxTopAlpha = this.register(new Setting<Object>("BedrockBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Integer> bBoxBottomAlpha = this.register(new Setting<Object>("BedrockBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderBedrockHoles.getValue()));
	private final Setting<Integer> oBoxTopAlpha = this.register(new Setting<Object>("ObsidianBoxTopAlpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue()));
	private final Setting<Integer> oBoxBottomAlpha = this.register(new Setting<Object>("ObsidianBoxBottomAlpha", Integer.valueOf(110), Integer.valueOf(0), Integer.valueOf(255), v -> this.renderObsidianHoles.getValue()));
	public Setting<Double> bHeight = this.register(new Setting<Double>("BedrockHeight", 0.0, -2.0, 2.0, v -> this.renderBedrockHoles.getValue()));
	public Setting<Double> oHeight = this.register(new Setting<Double>("ObsidianHeight", 0.0, -2.0, 2.0, v -> this.renderObsidianHoles.getValue()));
	
	private final Setting<ColorSetting> btfC = this.register(new Setting<ColorSetting>("BedrockTopFill", new ColorSetting(8847615), v -> this.renderBedrockHoles.getValue()));
	private final Setting<ColorSetting> btoC = this.register(new Setting<ColorSetting>("BedrockTopOutline", new ColorSetting(-7929601), v -> this.renderBedrockHoles.getValue()));
	private final Setting<ColorSetting> bbfC = this.register(new Setting<ColorSetting>("BedrockBottomFill", new ColorSetting(65535), v -> this.renderBedrockHoles.getValue()));
	private final Setting<ColorSetting> bboC = this.register(new Setting<ColorSetting>("BedrockBottomOutline", new ColorSetting(-16711681), v -> this.renderBedrockHoles.getValue()));
	
	private final Setting<ColorSetting> otfC = this.register(new Setting<ColorSetting>("ObsidianTopFill", new ColorSetting(8847615), v -> this.renderObsidianHoles.getValue()));
	private final Setting<ColorSetting> otoC = this.register(new Setting<ColorSetting>("OnsidianTopOutline", new ColorSetting(-7929601), v -> this.renderObsidianHoles.getValue()));
	private final Setting<ColorSetting> obfC = this.register(new Setting<ColorSetting>("OutlineBottomFill", new ColorSetting(16711680), v -> this.renderObsidianHoles.getValue()));
	private final Setting<ColorSetting> oboC = this.register(new Setting<ColorSetting>("OutlineBottomOutline", new ColorSetting(-65536), v -> this.renderObsidianHoles.getValue()));

    private List<BlockPos> obiHoles = new ArrayList<>();
    private List<BlockPos> bedrockHoles = new ArrayList<>();
	private int currentAlpha = 0;

    public HoleESP() {
        super("HoleESP", "Shows safe spots", Module.Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;
        obiHoles.clear();
        bedrockHoles.clear();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(mc.player.getPosition().add(-rangeXZ.getValue(), -rangeY.getValue(), -rangeXZ.getValue()), mc.player.getPosition().add(rangeXZ.getValue(), rangeY.getValue(), rangeXZ.getValue()));

        for (BlockPos pos : blocks) {
			if (pos.equals(new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) && !this.ownHole.getValue().booleanValue() || !RotationUtil.isInFov(pos)) {
			    continue;
		   }
            if (!(
                    mc.world.getBlockState(pos).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement() &&
                            mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial().blocksMovement()
            )) {

                if (BlockUtils.validObi(pos) && renderObsidianHoles.getValue()) {
                    this.obiHoles.add(pos);
                }

                if (BlockUtils.validBedrock(pos) && renderBedrockHoles.getValue()) {
                    this.bedrockHoles.add(pos);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;
        for (BlockPos pos : this.bedrockHoles) {
            RenderUtil.drawBoxESP(pos, new Color(this.btfC.getValue().getRed(), this.btfC.getValue().getGreen(), this.btfC.getValue().getBlue(), this.btfC.getValue().getAlpha()), true, new Color(this.btoC.getValue().getRed(), this.btoC.getValue().getGreen(), this.btoC.getValue().getBlue(), this.btoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxTopAlpha.getValue(), true, this.bHeight.getValue(), true, true, true, false, this.currentAlpha);
			RenderUtil.drawBoxESP(pos, new Color(this.bbfC.getValue().getRed(), this.bbfC.getValue().getGreen(), this.bbfC.getValue().getBlue(), this.bbfC.getValue().getAlpha()), true, new Color(this.bboC.getValue().getRed(), this.bboC.getValue().getGreen(), this.bboC.getValue().getBlue(), this.bboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.bBoxBottomAlpha.getValue(), true, this.bHeight.getValue(), true, true, false, true, this.currentAlpha);
        }

        for (BlockPos pos : this.obiHoles) {
            RenderUtil.drawBoxESP(pos, new Color(this.otfC.getValue().getRed(), this.otfC.getValue().getGreen(), this.otfC.getValue().getBlue(), this.otfC.getValue().getAlpha()), true, new Color(this.otoC.getValue().getRed(), this.otoC.getValue().getGreen(), this.otoC.getValue().getBlue(), this.otoC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxTopAlpha.getValue(), true, this.oHeight.getValue(), true, true, true, false, this.currentAlpha);
			RenderUtil.drawBoxESP(pos, new Color(this.obfC.getValue().getRed(), this.obfC.getValue().getGreen(), this.obfC.getValue().getBlue(), this.obfC.getValue().getAlpha()), true, new Color(this.oboC.getValue().getRed(), this.oboC.getValue().getGreen(), this.oboC.getValue().getBlue(), this.oboC.getValue().getAlpha()), this.lineWidth.getValue().floatValue(), true, true, this.oBoxBottomAlpha.getValue(), true, this.oHeight.getValue(), true, true, false, true, this.currentAlpha);
        }
    }
}