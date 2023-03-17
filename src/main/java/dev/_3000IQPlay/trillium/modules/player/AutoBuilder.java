package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.event.events.Render3DEvent;
import dev._3000IQPlay.trillium.event.events.UpdateWalkingPlayerEvent;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.util.BlockUtils;
import dev._3000IQPlay.trillium.util.InventoryUtil;
import dev._3000IQPlay.trillium.util.RenderUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AutoBuilder
        extends Module {
    private final Setting<Settings> settings = this.register(new Setting<Settings>("Settings", Settings.PATTERN));
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.STAIRS, v -> this.settings.getValue() == Settings.PATTERN));
    private final Setting<Direction> stairDirection = this.register(new Setting<Direction>("Direction", Direction.NORTH, v -> this.mode.getValue() == Mode.STAIRS && this.settings.getValue() == Settings.PATTERN));
    private final Setting<Integer> width = this.register(new Setting<Integer>("StairWidth", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100), v -> this.mode.getValue() == Mode.STAIRS && this.settings.getValue() == Settings.PATTERN));
    private final Setting<Boolean> dynamic = this.register(new Setting<Boolean>("Dynamic", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.FLAT && this.settings.getValue() == Settings.PATTERN));
    private final Setting<Boolean> setPos = this.register(new Setting<Boolean>("ResetPos", Boolean.valueOf(false), v -> this.settings.getValue() == Settings.PATTERN && (this.mode.getValue() == Mode.STAIRS || this.mode.getValue() == Mode.FLAT && this.dynamic.getValue() == false)));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Blocks/Tick", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(8), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Integer> placeDelay = this.register(new Setting<Integer>("PlaceDelay", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(500), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> altRotate = this.register(new Setting<Boolean>("AltRotate", Boolean.valueOf(false), v -> this.rotate.getValue() != false && this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> ground = this.register(new Setting<Boolean>("NoJump", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> noMove = this.register(new Setting<Boolean>("NoMove", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.PLACE));
    private final Setting<Boolean> render = this.register(new Setting<Boolean>("Render", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.RENDER));
    private final Setting<Boolean> box = this.register(new Setting<Boolean>("Box", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() != false));
	private final Setting<ColorSetting> boxColor = this.register(new Setting<ColorSetting>("BoxColor", new ColorSetting(65535), v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() != false));
    private final Setting<ColorSetting> outlineColor = this.register(new Setting<ColorSetting>("OutlineColor", new ColorSetting(-16711681), v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
	private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.settings.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
    private final Setting<Boolean> keepPos = this.register(new Setting<Boolean>("KeepOldPos", Boolean.valueOf(false), v -> this.settings.getValue() == Settings.MISC));
    private final Setting<Updates> updates = this.register(new Setting<Updates>("Update", Updates.TICK, v -> this.settings.getValue() == Settings.MISC));
    private final Setting<Switch> switchMode = this.register(new Setting<Switch>("Switch", Switch.SILENT, v -> this.settings.getValue() == Settings.MISC));
    private final Setting<Boolean> allBlocks = this.register(new Setting<Boolean>("AllBlocks", Boolean.valueOf(true), v -> this.settings.getValue() == Settings.MISC));
    private final Timer timer = new Timer();
    private final List<BlockPos> placepositions = new ArrayList<BlockPos>();
    private BlockPos startPos;
    private int blocksThisTick;
    private int lastSlot;
    private int blockSlot;

    public AutoBuilder() {
        super("AutoBuilder", "Auto Builds.", Module.Category.PLAYER, true, false, false);
    }

    @Override
    public void onTick() {
        if (this.updates.getValue() == Updates.TICK) {
            this.doAutoBuilder();
        }
    }

    @Override
    public void onUpdate() {
        if (this.updates.getValue() == Updates.UPDATE) {
            this.doAutoBuilder();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.updates.getValue() == Updates.WALKING && event.getStage() != 1) {
            this.doAutoBuilder();
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.placepositions == null || !this.render.getValue().booleanValue()) {
            return;
        }
        Color outline = new Color(this.outlineColor.getValue().getRed(), this.outlineColor.getValue().getGreen(), this.outlineColor.getValue().getBlue(), this.outlineColor.getValue().getAlpha());
        Color box = new Color(this.boxColor.getValue().getRed(), this.boxColor.getValue().getGreen(), this.boxColor.getValue().getBlue(), this.boxColor.getValue().getAlpha());
        this.placepositions.forEach(pos -> RenderUtil.drawSexyBox(new AxisAlignedBB(pos), box, outline, this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), 1.0f, 1.0f, 1.0f));
    }

    @Override
    public void onEnable() {
        this.placepositions.clear();
        if (!this.keepPos.getValue().booleanValue() || this.startPos == null) {
            this.startPos = new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).down();
        }
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        this.timer.reset();
    }

    private void doAutoBuilder() {
        if (!this.check()) {
            return;
        }
        for (BlockPos pos : this.placepositions) {
            if (this.blocksThisTick >= this.blocksPerTick.getValue()) {
                this.doSwitch(true);
                return;
            }
            int canPlace = BlockUtils.isPositionPlaceable(pos, false, true);
            if (canPlace == 3) {
                BlockUtils.placeBlockBetter(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                ++this.blocksThisTick;
                continue;
            }
            if (canPlace != 2 || this.mode.getValue() != Mode.STAIRS) continue;
            if (BlockUtils.isPositionPlaceable(pos.down(), false, true) == 3) {
                BlockUtils.placeBlockBetter(pos.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                ++this.blocksThisTick;
                continue;
            }
            switch (this.stairDirection.getValue()) {
                case SOUTH: {
                    if (BlockUtils.isPositionPlaceable(pos.south(), false, true) != 3) break;
                    BlockUtils.placeBlockBetter(pos.south(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                    ++this.blocksThisTick;
                    break;
                }
                case WEST: {
                    if (BlockUtils.isPositionPlaceable(pos.west(), false, true) != 3) break;
                    BlockUtils.placeBlockBetter(pos.west(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                    ++this.blocksThisTick;
                    break;
                }
                case NORTH: {
                    if (BlockUtils.isPositionPlaceable(pos.north(), false, true) != 3) break;
                    BlockUtils.placeBlockBetter(pos.north(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                    ++this.blocksThisTick;
                    break;
                }
                case EAST: {
                    if (BlockUtils.isPositionPlaceable(pos.east(), false, true) != 3) break;
                    BlockUtils.placeBlockBetter(pos.east(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.altRotate.getValue());
                    ++this.blocksThisTick;
                }
            }
        }
        this.doSwitch(true);
    }

    private boolean doSwitch(boolean back) {
        Item i = AutoBuilder.mc.player.getHeldItemMainhand().getItem();
        switch (this.switchMode.getValue()) {
            case NONE: {
                if (i instanceof ItemBlock) {
                    if (this.allBlocks.getValue().booleanValue()) {
                        return true;
                    }
                    return ((ItemBlock)i).getBlock() instanceof BlockObsidian;
                }
                return false;
            }
            case NORMAL: {
                if (back) break;
                InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
                break;
            }
            case SILENT: {
                if (i instanceof ItemBlock && (this.allBlocks.getValue().booleanValue() || ((ItemBlock)i).getBlock() instanceof BlockObsidian) || this.lastSlot == -1) break;
                if (back) {
                    AutoBuilder.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.lastSlot));
                    break;
                }
                AutoBuilder.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.blockSlot));
            }
        }
        return true;
    }

    private boolean check() {
        if (this.setPos.getValue().booleanValue()) {
            this.startPos = new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).down();
            this.setPos.setValue(false);
        }
        this.getPositions();
        if (this.placepositions.isEmpty()) {
            return false;
        }
        if (!this.timer.passedMs(this.placeDelay.getValue().intValue())) {
            return false;
        }
        this.timer.reset();
        this.blocksThisTick = 0;
        this.lastSlot = AutoBuilder.mc.player.inventory.currentItem;
        int n = this.blockSlot = this.allBlocks.getValue() != false ? InventoryUtil.findAnyBlock() : InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (this.ground.getValue().booleanValue() && !AutoBuilder.mc.player.onGround) {
            return false;
        }
        if (this.blockSlot == -1) {
            return false;
        }
        if (this.noMove.getValue().booleanValue() && (AutoBuilder.mc.player.moveForward != 0.0f || AutoBuilder.mc.player.moveStrafing != 0.0f)) {
            return false;
        }
        return this.doSwitch(false);
    }

    private void getPositions() {
        if (this.startPos == null) {
            return;
        }
        this.placepositions.clear();
        for (BlockPos pos : BlockUtils.getSphere(new BlockPos(AutoBuilder.mc.player.posX, Math.ceil(AutoBuilder.mc.player.posY), AutoBuilder.mc.player.posZ).up(), this.range.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0)) {
            if (this.placepositions.contains(pos) || !AutoBuilder.mc.world.isAirBlock(pos)) continue;
            if (this.mode.getValue() == Mode.STAIRS) {
                switch (this.stairDirection.getValue()) {
                    case NORTH: {
                        if (this.startPos.getZ() - pos.getZ() != pos.getY() - this.startPos.getY() || Math.abs(pos.getX() - this.startPos.getX()) >= this.width.getValue() / 2) break;
                        this.placepositions.add(pos);
                        break;
                    }
                    case EAST: {
                        if (pos.getX() - this.startPos.getX() != pos.getY() - this.startPos.getY() || Math.abs(pos.getZ() - this.startPos.getZ()) >= this.width.getValue() / 2) break;
                        this.placepositions.add(pos);
                        break;
                    }
                    case SOUTH: {
                        if (pos.getZ() - this.startPos.getZ() != pos.getY() - this.startPos.getY() || Math.abs(this.startPos.getX() - pos.getX()) >= this.width.getValue() / 2) break;
                        this.placepositions.add(pos);
                        break;
                    }
                    case WEST: {
                        if (this.startPos.getX() - pos.getX() != pos.getY() - this.startPos.getY() || Math.abs(this.startPos.getZ() - pos.getZ()) >= this.width.getValue() / 2) break;
                        this.placepositions.add(pos);
                    }
                }
                continue;
            }
            if (this.mode.getValue() != Mode.FLAT || (double)pos.getY() != (this.dynamic.getValue() != false ? Math.ceil(AutoBuilder.mc.player.posY) - 1.0 : (double)this.startPos.getY())) continue;
            this.placepositions.add(pos);
        }
    }

    public static enum Settings {
        MISC,
        PATTERN,
        PLACE,
        RENDER;

    }

    public static enum Direction {
        WEST,
        SOUTH,
        EAST,
        NORTH;

    }

    public static enum Updates {
        TICK,
        UPDATE,
        WALKING;

    }

    public static enum Switch {
        NONE,
        NORMAL,
        SILENT;

    }

    public static enum Mode {
        STAIRS,
        FLAT;

    }
}