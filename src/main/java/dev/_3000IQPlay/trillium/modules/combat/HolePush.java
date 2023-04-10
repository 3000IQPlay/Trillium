package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.ColorSetting;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HolePush extends Module {
    public final Setting<Float> targetRange = this.register(new Setting<>("Target Range", 8.0f, 0.1f, 15.0f));
    public final Setting<Float> placeRange = this.register(new Setting<>("Place Range", 5.0f, 0.1f, 6.0f));
    public final Setting<Integer> placeDelay = this.register(new Setting<>("Place Delay", 100, 0, 1000));
    public final Setting<RotationMode> rotationMode = this.register(new Setting<>("Rotation", RotationMode.Packet));
    public final Setting<Boolean> rotateBack = this.register(new Setting<>("Rotate Back", false));
    public final Setting<Boolean> inLiquids = this.register(new Setting<>("In Liquids", false));
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", true));
	public final Setting<Boolean> extraPackets = this.register(new Setting<>("Extra Packet", true));
    public final Setting<Boolean> mineRedstone = this.register(new Setting<>("Mine Redstone", false));
    public final Setting<MineMode> mineMode = this.register(new Setting<>("Mine", MineMode.Packet));
    public final Setting<Boolean> consistent = this.register(new Setting<>("Consistent", false, v -> !this.consistent.getValue()));
    public final Setting<Boolean> silentSwitch = this.register(new Setting<>("Silent Switch", true));
    public Side side = null;
    public EntityPlayer entityPlayer = null;
    public BlockPos entityPlayerPos = null, placedRedstonePos = null, placedPistonPos = null;
    public Timer timer = new Timer();
    public boolean rotated = false, mined = false;
	
	public HolePush() {
        super("HolePush", "Pushes HoleFags out of their hole", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        entityPlayer = getUntrappedClosestEntityPlayer(targetRange.getValue(), true);
        if (entityPlayer == null) {
            Command.sendMessage("No safe Targets found, disabling HolePush!");
            return;
        }
        entityPlayerPos = EntityUtil.getPlayerPos(entityPlayer).up();
        side = getSide(entityPlayerPos);
        if (side == null) {
            Command.sendMessage("No possible place sides found, disabling HolePush!");
            return;
        }
        rotated = false;
        mined = false;
        placedRedstonePos = null;
    }

    @Override
    public void onTick() {
        if (!timer.passed(placeDelay.getValue()))
            return;
        entityPlayer = getUntrappedClosestEntityPlayer(targetRange.getValue(), false);
        if (entityPlayer == null) {
            Command.sendMessage("No safe Targets found, disabling HolePush!");
            return;
        }
        BlockPos pistonPos = getPistonPos(entityPlayerPos, side);
        BlockPos redstonePos = getRedstonePos(entityPlayerPos, side);
        if (!HolePush.isSafe(entityPlayer)) {
            Command.sendMessage("Target no longer safe, disabling HolePush!");
            return;
        }
        if (!placedPiston(entityPlayerPos)) {
            float rotationYaw = mc.player.rotationYaw;
            switch(rotationMode.getValue()) {
                case Packet:
                    rotatePacket(side);
                    break;
                case Vanilla:
                    rotateVanilla(side);
                    break;
                case TickWait:
                    if (!rotated) {
                        rotateVanilla(side);
                        rotated = true;
                        return;
                    }
                    break;
            }
            int slot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.PISTON));
            if (slot == -1) {
                Command.sendMessage("No Pistons found, disabling HolePush.");
                return;
            }
            BlockUtils.placeBlock(pistonPos, EnumHand.MAIN_HAND, false, packet.getValue(), this.extraPackets.getValue(), slot);
            placedPistonPos = pistonPos;
            if (rotateBack.getValue() && rotationMode.getValue() != RotationMode.Packet)
				mc.player.rotationYaw = rotationYaw;
            rotated = false;
            return;
        }
        if (!isPistonTriggered(pistonPos) && redstonePos != null) {
            float rotationYaw = mc.player.rotationYaw;
            switch(rotationMode.getValue()) {
                case Packet:
                    rotatePacket(side);
                    break;
                case Vanilla:
                    rotateVanilla(side);
                    break;
                case TickWait:
                    if (!rotated) {
                        rotateVanilla(side);
                        rotated = true;
                        return;
                    }
                    break;
            }
            int slot = InventoryUtil.getItemHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
            if (slot == -1) {
                Command.sendMessage("No redstone blocks found, disabling HolePush.");
                return;
            }
            BlockUtils.placeBlock(redstonePos, EnumHand.MAIN_HAND, false, packet.getValue(), this.extraPackets.getValue(), slot);
            if (rotateBack.getValue() && rotationMode.getValue() != RotationMode.Packet)
				mc.player.rotationYaw = rotationYaw;
            placedRedstonePos = redstonePos;
            rotated = false;
            mined = false;
            return;
        }
        if (isPistonTriggered(pistonPos) && mineRedstone.getValue() && !mined) {
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE))
                return;
            switch(mineMode.getValue()) {
                case Packet:
				    int currentItemP = mc.player.inventory.currentItem;
				    int slotP = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
				    if (mc.player.inventory.currentItem != slotP && slotP != -1 && silentSwitch.getValue()) {
				        mc.player.connection.sendPacket(new CPacketHeldItemChange(slotP));
					}
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, placedRedstonePos, EnumFacing.UP));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, placedRedstonePos, EnumFacing.UP));
                    mined = !consistent.getValue();
					if (silentSwitch.getValue() && slotP != -1) {
						mc.player.connection.sendPacket(new CPacketHeldItemChange(currentItemP));
                        mc.player.inventory.currentItem = currentItemP;
                        mc.playerController.updateController();
                    }
                    return;
                case Click:
                    mc.playerController.onPlayerDamageBlock(placedRedstonePos, mc.player.getHorizontalFacing());
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mined = !consistent.getValue();
                    return;
                case Vanilla:
                    int currentItem = mc.player.inventory.currentItem;
                    int slot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                    if (mc.player.inventory.currentItem != slot && slot != -1 && silentSwitch.getValue()) {
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    }
                    mc.playerController.onPlayerDamageBlock(placedRedstonePos, mc.objectMouseOver.sideHit);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (silentSwitch.getValue() && slot != -1) {
						mc.player.connection.sendPacket(new CPacketHeldItemChange(currentItem));
                        mc.player.inventory.currentItem = currentItem;
                        mc.playerController.updateController();
                    }
            }
        }
    }
	
	public static boolean isSafe(EntityPlayer target) {
        BlockPos playerPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY), Math.floor(target.posZ));
        return (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.BEDROCK);
    }
	
    public EntityPlayer getUntrappedClosestEntityPlayer(float range, boolean pistonCheck) {
        TreeMap<Float, EntityPlayer> treeMap = new TreeMap<>();
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (!entityPlayer.equals(mc.player) && !Trillium.friendManager.isFriend(entityPlayer.getName()) && mc.player.getDistance(entityPlayer) < range && HolePush.isSafe(entityPlayer) && canPlace(EntityUtil.getPlayerPos(entityPlayer).up().up()) && (!pistonCheck || mc.world.getBlockState(EntityUtil.getPlayerPos(entityPlayer).up()).getBlock().equals(Blocks.AIR))) {
                treeMap.put(mc.player.getDistance(entityPlayer), entityPlayer);
            }
        }
        if (!treeMap.isEmpty())
            return treeMap.firstEntry().getValue();
        return null;
    }

    public BlockPos getPistonPos(BlockPos pos, Side side) {
        switch(side) {
            case North:
                return pos.north();
            case East:
                return pos.east();
            case South:
                return pos.south();
            case West:
                return pos.west();
        }
        return null;
    }

    public BlockPos getRedstonePos(BlockPos pos, Side side) {
        switch(side) {
            case North:
                if (canPlace(pos.north().north()))
                    return pos.north().north();
                if (canPlace(pos.north().up()))
                    return pos.north().up();
                if (canPlace(pos.north().east()))
                    return pos.north().east();
                if (canPlace(pos.north().west()))
                    return pos.north().west();
                break;
            case East:
                if (canPlace(pos.east().east()))
                    return pos.east().east();
                if (canPlace(pos.east().up()))
                    return pos.east().up();
                if (canPlace(pos.east().north()))
                    return pos.east().north();
                if (canPlace(pos.east().south()))
                    return pos.east().south();
                break;
            case South:
                if (canPlace(pos.south().south()))
                    return pos.south().south();
                if (canPlace(pos.south().up()))
                    return pos.south().up();
                if (canPlace(pos.south().east()))
                    return pos.south().east();
                if (canPlace(pos.south().west()))
                    return pos.south().west();
                break;
            case West:
                if (canPlace(pos.west().west()))
                    return pos.west().west();
                if (canPlace(pos.west().up()))
                    return pos.west().up();
                if (canPlace(pos.west().north()))
                    return pos.west().north();
                if (canPlace(pos.west().south()))
                    return pos.west().south();
                break;
        }
        return null;
    }

    public boolean isPistonTriggered(BlockPos pos) {
        return isRedstone(pos.north()) || isRedstone(pos.east()) || isRedstone(pos.south()) || isRedstone(pos.west()) || isRedstone(pos.up());
    }

    public boolean isRedstone(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_BLOCK);
    }

    public boolean isPiston(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON);
    }

    public boolean canPlace(BlockPos pos) {
        ArrayList<Entity> intersecting = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).stream().filter(entity -> !(entity instanceof EntityEnderCrystal)).collect(Collectors.toCollection(ArrayList::new));
        return intersecting.isEmpty() && (mc.player.getDistanceSq(pos) < (placeRange.getValue() * placeRange.getValue())) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.getValue() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA)))));
    }

    public boolean canPlacePiston(BlockPos pos) {
        return (mc.player.getDistanceSq(pos) < (placeRange.getValue() * placeRange.getValue())) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && (mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.getValue() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA)))));
    }

    public void rotatePacket(Side side) {
        switch(side) {
            case North:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180, mc.player.rotationPitch, mc.player.onGround));
                break;
            case East:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, mc.player.rotationPitch, mc.player.onGround));
                break;
            case South:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, mc.player.rotationPitch, mc.player.onGround));
                break;
            case West:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90, mc.player.rotationPitch, mc.player.onGround));
                break;
        }
    }

    public void rotateVanilla(Side side) {
        switch(side) {
            case North:
                mc.player.rotationYaw = 180;
                break;
            case East:
                mc.player.rotationYaw = -90;
                break;
            case South:
                mc.player.rotationYaw = 0;
                break;
            case West:
                mc.player.rotationYaw = 90;
                break;
        }
    }

    public Side getSide(BlockPos pos) {
        if (canPlacePiston(pos.north()) && canPlace(pos.south()) && canPlace(pos.south().up()) && (canPlace(pos.north().north()) || canPlace(pos.north().east()) || canPlace(pos.north().west()) || canPlace(pos.north().up())))
            return Side.North;
        if (canPlacePiston(pos.east()) && canPlace(pos.west()) && canPlace(pos.west().up()) && (canPlace(pos.east().east()) || canPlace(pos.east().north()) || canPlace(pos.east().south()) || canPlace(pos.east().up())))
            return Side.East;
        if (canPlacePiston(pos.south()) && canPlace(pos.north()) && canPlace(pos.north().up()) && (canPlace(pos.south().south()) || canPlace(pos.south().east()) || canPlace(pos.south().west()) || canPlace(pos.south().up())))
            return Side.South;
        if (canPlacePiston(pos.west()) && canPlace(pos.east()) && canPlace(pos.east().up()) && (canPlace(pos.west().west()) || canPlace(pos.west().north()) || canPlace(pos.west().east()) || canPlace(pos.west().up())))
            return Side.West;
        return null;
    }

    public boolean placedPiston(BlockPos pos) {
        switch(side) {
            case North:
                return isPiston(pos.north());
            case East:
                return isPiston(pos.east());
            case South:
                return isPiston(pos.south());
            case West:
                return isPiston(pos.west());
        }
        return false;
    }
	
	public enum MineMode {
		Packet,
		Click,
		Vanilla;
	}
	
	public enum RotationMode {
		Packet,
		Vanilla,
		TickWait;
	}

    public enum Side {
        North,
        East,
        South,
        West
    }
}