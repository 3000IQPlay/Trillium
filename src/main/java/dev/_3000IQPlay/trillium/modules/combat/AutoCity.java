package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.util.BlockUtils;
import dev._3000IQPlay.trillium.util.EntityUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class AutoCity
        extends Module {
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 1, 10));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> oneFifteen = this.register(new Setting<Boolean>("1.15", false));
    public final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    public final Setting<Boolean> crystalCheck = this.register(new Setting<Boolean>("Crystal Check", true));
    public final Setting<Boolean> pickOnly = this.register(new Setting<Boolean>("Pickaxe Check", true));
    public final Setting<Boolean> silentPlace = this.register(new Setting<Boolean>("Silent Place", true));
    public final Setting<Boolean> prePlaceCrystal = this.register(new Setting<Boolean>("PrePlace Crystals", false));
    public Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", Boolean.valueOf(true), v -> this.render.getValue()));
    public Setting<Boolean> cSync = this.register(new Setting<Object>("Color Sync", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Integer> cRed = this.register(new Setting<Object>("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cGreen = this.register(new Setting<Object>("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cBlue = this.register(new Setting<Object>("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> cAlpha = this.register(new Setting<Object>("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> red = this.register(new Setting<Integer>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> green = this.register(new Setting<Integer>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", Integer.valueOf(155), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public Setting<Boolean> box = this.register(new Setting<Boolean>("Box", Boolean.valueOf(true), v -> this.render.getValue()));
    public final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    public final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.render.getValue()));
    public BlockPos renderPos = null;
    public EntityPlayer target;
    public Timer timer = new Timer();

    public AutoCity() {
        super("AutoCity", "Automatically mines city blocks of opponents", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (AutoCity.nullCheck()) {
            return;
        }
        this.target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
        if (this.target == null) {
            return;
        }
        Double dist = this.range.getValue().doubleValue();
        Vec3d vec = this.target.getPositionVector();
        if (this.pickOnly.getValue().booleanValue() && !(AutoCity.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
            return;
        }
        if (AutoCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.add(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.add(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.add(0.0, 0.0, -1.0));
            BlockPos targetXCrystal = new BlockPos(vec.add(2.0, 0.0, 0.0));
            BlockPos targetXMinusCrystal = new BlockPos(vec.add(-2.0, 0.0, 0.0));
            BlockPos targetZCrystal = new BlockPos(vec.add(0.0, 0.0, 2.0));
            BlockPos targetZMinusCrystal = new BlockPos(vec.add(0.0, 0.0, -2.0));
            if (!this.isPlayerOccupied() && !this.crystalCheck.getValue().booleanValue()) {
                if (AutoCity.isBlockValid(targetX)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                }
                if (!AutoCity.isBlockValid(targetX) && AutoCity.isBlockValid(targetXMinus)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && AutoCity.isBlockValid(targetZ)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && !AutoCity.isBlockValid(targetZ) && AutoCity.isBlockValid(targetZMinus)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && !AutoCity.isBlockValid(targetZ) && !AutoCity.isBlockValid(targetZMinus) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
            if (this.crystalCheck.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && AutoCity.isBlockValid(targetX)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetX, EnumFacing.DOWN));
                    this.renderPos = targetX;
                } else if (this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && AutoCity.isBlockValid(targetXMinus)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetXMinus, EnumFacing.DOWN));
                    this.renderPos = targetXMinus;
                } else if (this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && AutoCity.isBlockValid(targetZ)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZ, EnumFacing.DOWN));
                    this.renderPos = targetZ;
                } else if (this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) && AutoCity.isBlockValid(targetZMinus)) {
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    AutoCity.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetZMinus, EnumFacing.DOWN));
                    this.renderPos = targetZMinus;
                } else {
                    if (AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                        this.renderPos = null;
                        return;
                    }
                    this.renderPos = null;
                    return;
                }
            }
            if (this.prePlaceCrystal.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue())) {
                    BlockUtils.placeCrystalOnBlock(targetXCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue())) {
                    BlockUtils.placeCrystalOnBlock(targetXMinusCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue())) {
                    BlockUtils.placeCrystalOnBlock(targetZCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue())) {
                    BlockUtils.placeCrystalOnBlock(targetZMinusCrystal, EnumHand.MAIN_HAND, true, false, this.silentPlace.getValue());
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
        }
    }

    @Override
    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (this.pickOnly.getValue().booleanValue() && !(AutoCity.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
            return;
        }
        if (AutoCity.nullCheck()) {
            return;
        }
        this.target = (EntityPlayer)EntityUtil.getTarget(true, false, false, false, false, 10.0, EntityUtil.toMode("Closest"));
        if (this.target == null) {
            return;
        }
        Double dist = this.range.getValue().doubleValue();
        Vec3d vec = this.target.getPositionVector();
        if (AutoCity.mc.player.getPositionVector().distanceTo(vec) <= dist) {
            BlockPos targetX = new BlockPos(vec.add(1.0, 0.0, 0.0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1.0, 0.0, 0.0));
            BlockPos targetZ = new BlockPos(vec.add(0.0, 0.0, 1.0));
            BlockPos targetZMinus = new BlockPos(vec.add(0.0, 0.0, -1.0));
            BlockPos targetXCrystal = new BlockPos(vec.add(2.0, 0.0, 0.0));
            BlockPos targetXMinusCrystal = new BlockPos(vec.add(-2.0, 0.0, 0.0));
            BlockPos targetZCrystal = new BlockPos(vec.add(0.0, 0.0, 2.0));
            BlockPos targetZMinusCrystal = new BlockPos(vec.add(0.0, 0.0, -2.0));
            if (!this.isPlayerOccupied() && !this.crystalCheck.getValue().booleanValue()) {
                if (AutoCity.isBlockValid(targetX)) {
                    RenderUtil.drawBoxESP(targetX, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!AutoCity.isBlockValid(targetX) && AutoCity.isBlockValid(targetXMinus)) {
                    RenderUtil.drawBoxESP(targetXMinus, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && AutoCity.isBlockValid(targetZ)) {
                    RenderUtil.drawBoxESP(targetZ, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && !AutoCity.isBlockValid(targetZ) && AutoCity.isBlockValid(targetZMinus)) {
                    RenderUtil.drawBoxESP(targetZMinus, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!AutoCity.isBlockValid(targetX) && !AutoCity.isBlockValid(targetXMinus) && !AutoCity.isBlockValid(targetZ) && !AutoCity.isBlockValid(targetZMinus) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
            if (this.crystalCheck.getValue().booleanValue() && this.target != null && this.renderPos != null) {
                RenderUtil.drawBoxESP(this.renderPos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            }
            if (this.prePlaceCrystal.getValue().booleanValue() && this.target != null) {
                if (this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetXCrystal, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetXMinusCrystal, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetZCrystal, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue())) {
                    RenderUtil.drawBoxESP(targetZMinusCrystal, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.outline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
                }
                if (!this.canPlaceCrystal(targetXCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetXMinusCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZCrystal, this.oneFifteen.getValue()) && !this.canPlaceCrystal(targetZMinusCrystal, this.oneFifteen.getValue()) || AutoCity.mc.player.getPositionVector().distanceTo(vec) > dist) {
                    return;
                }
            }
        }
    }

    public boolean canPlaceCrystal(BlockPos blockPos, boolean oneFifteen) {
        return oneFifteen ? AutoCity.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && AutoCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || AutoCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK : AutoCity.mc.world.getBlockState(blockPos).getBlock() instanceof BlockAir && AutoCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ())).getBlock() instanceof BlockAir && AutoCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.OBSIDIAN || AutoCity.mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isBlockValid(BlockPos pos) {
        IBlockState blockState = AutoCity.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)AutoCity.mc.world, pos) != -1.0f;
    }

    public boolean isPlayerOccupied() {
        return AutoCity.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && AutoCity.mc.player.isHandActive();
    }
}

