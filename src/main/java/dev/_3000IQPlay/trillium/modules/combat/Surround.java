package dev._3000IQPlay.trillium.modules.combat;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.modules.movement.Step;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.BlockUtils;
import dev._3000IQPlay.trillium.util.DamageUtil;
import dev._3000IQPlay.trillium.util.EntityUtil;
import dev._3000IQPlay.trillium.util.InventoryUtil;
import dev._3000IQPlay.trillium.util.RotationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class Surround
        extends Module {
    public final Setting<Integer> blocksPerPlace = this.register(new Setting<Integer>("Blocks Per Tick", 16, 0, 20));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 200));
    public final Setting<Boolean> dynamicEntityExtend = this.register(new Setting<Boolean>("Dynamic Entity Extend", true));
    public final Setting<Sensitivity> sensitivity = this.register(new Setting<Sensitivity>("Sensitivity", Sensitivity.High));
	public final Setting<AntiCity> antiCity = this.register(new Setting<AntiCity>("AntiCity", AntiCity.Smart));
	public final Setting<CenterType> centerType = this.register(new Setting<CenterType>("CenterType", CenterType.None));
    public final Setting<Boolean> antiAnvil = this.register(new Setting<Boolean>("Anti Anvil", true));
	public final Setting<Boolean> floor = this.register(new Setting<Boolean>("Floor", true));
    public final Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public final Setting<Boolean> extraPacket = this.register(new Setting<Boolean>("Extra Packet", false));
	public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
	public final Setting<Boolean> normalizeAngle = this.register(new Setting<Boolean>("Normalize Angle", false, v -> this.rotate.getValue()));
    public final Setting<Boolean> blockClear = this.register(new Setting<Boolean>("Block Clear", true));
    public final Setting<Integer> blockClearRange = this.register(new Setting<Integer>("Block Clear Range", 5, 1, 10, v -> this.blockClear.getValue()));
    public final Setting<CrystalClear> crystalClear = this.register(new Setting<CrystalClear>("Crystal Clear", CrystalClear.Toggle));
    public final Setting<Integer> crystalClearRange = this.register(new Setting<Integer>("Crystal Clear Range", 4, 1, 10));
    public final Setting<Boolean> swingCrystalClear = this.register(new Setting<Boolean>("Swing Crystal Clear", true));
    public final Setting<Boolean> packetCrystal = this.register(new Setting<Boolean>("Packet Crystal", true));
    public final Setting<Boolean> rotateCrystal = this.register(new Setting<Boolean>("Rotate Crystal", false));
    public final Setting<Float> maxSelfDamage = this.register(new Setting<Float>("Max Self Damage", Float.valueOf(18.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    public final Setting<Boolean> yMotionDisable = this.register(new Setting<Boolean>("Y Motion Disable", true));
    public final Setting<Boolean> stepDisable = this.register(new Setting<Boolean>("Step Disable", true));
    public EnumFacing facing = null;
    public BlockPos startPos;
    public int placedBlocks = 0;

    public Surround() {
        super("Surround", "Automatically surrounds yourself in obsidian to prevent crystal damage", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.startPos = new BlockPos(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posZ);
        BlockPos centerPos = new BlockPos((double)Surround.getPlayerPosFloored().getX() + 0.5, (double)Surround.getPlayerPosFloored().getY(), (double)Surround.getPlayerPosFloored().getZ() + 0.5);
        switch (this.centerType.getValue()) {
            case Instant: {
                Trillium.movementManager.setPositionPacket((double)this.startPos.getX() + 0.5, this.startPos.getY(), (double)this.startPos.getZ() + 0.5, true, true, true);
            }
            case Motion: {
                Trillium.movementManager.setMotion(((double)this.startPos.getX() + 0.5 - Surround.mc.player.posX) / 2.0, Surround.mc.player.motionY, ((double)this.startPos.getZ() + 0.5 - Surround.mc.player.posZ) / 2.0);
            }
		}
        if (this.crystalClear.getValue() == CrystalClear.Toggle) {
            for (Entity entity : Surround.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || !(Surround.mc.player.getDistance(entity.posX, entity.posY, entity.posZ) < (double)this.crystalClearRange.getValue().intValue()) || EntityUtil.isSafe(Surround.mc.player)) continue;
                if (this.rotateCrystal.getValue().booleanValue()) {
                    RotationUtil.faceVector(new Vec3d(entity.posX, entity.posY, entity.posZ), this.normalizeAngle.getValue());
                }
                Vec3d vec3d = new Vec3d(entity.posX, entity.posY, entity.posZ);
                if (!(DamageUtil.calculateDamage(vec3d, (Entity)Surround.mc.player) < this.maxSelfDamage.getValue().floatValue())) continue;
                Surround.attackEntity(entity, this.packetCrystal.getValue(), this.swingCrystalClear.getValue());
            }
        }
    }
	
	public static BlockPos getPlayerPosFloored() {
        return new BlockPos(Math.floor(Surround.mc.player.posX), Math.floor(Surround.mc.player.posY), Math.floor(Surround.mc.player.posZ));
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @Override
    public void onUpdate() {
        BlockPos pos2;
        BlockPos pos;
        if (Surround.fullNullCheck()) {
            return;
        }
        if (this.yMotionDisable.getValue().booleanValue() && Surround.mc.player.posY != (double)this.startPos.getY()) {
            this.disable();
        }
        if (this.stepDisable.getValue().booleanValue() && Step.getInstance().isEnabled()) {
            this.disable();
        }
        if (this.crystalClear.getValue() == CrystalClear.Always) {
            for (Entity entity : Surround.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || !(Surround.mc.player.getDistance(entity.posX, entity.posY, entity.posZ) < (double)this.crystalClearRange.getValue().intValue()) || EntityUtil.isSafe(Surround.mc.player)) continue;
                if (this.rotateCrystal.getValue().booleanValue()) {
                    RotationUtil.faceVector(new Vec3d(entity.posX, entity.posY, entity.posZ), this.normalizeAngle.getValue());
                }
                Vec3d vec3d = new Vec3d(entity.posX, entity.posY, entity.posZ);
                if (!(DamageUtil.calculateDamage(vec3d, (Entity)Surround.mc.player) < this.maxSelfDamage.getValue().floatValue())) continue;
                Surround.attackEntity(entity, this.packetCrystal.getValue(), this.swingCrystalClear.getValue());
            }
        }
        this.placedBlocks = 0;
        ArrayList<BlockPos> blocks = Surround.getPos(0.0, 0.0, 0.0, Surround.mc.player);
        if (blocks.size() == 1) {
            pos = blocks.get(0);
            BlockPos[] surroundOffset = new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()};
            BlockPos floorPos = new BlockPos(Surround.mc.player.posX, Surround.mc.player.posY - 1.0, Surround.mc.player.posZ);
            if (this.canPlaceOnBlock(floorPos) && this.floor.getValue().booleanValue()) {
                this.placeBlock(floorPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            for (BlockPos posA : surroundOffset) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posA)) {
                    this.doClearBlocks(posA);
                }
                if (this.canPlaceOnBlock(posA.down()) && !this.intersectsWithEntity(posA.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posA.down());
                }
                if (this.canPlaceOnBlock(posA) && !this.intersectsWithEntity(posA)) {
                    this.placeBlock(posA, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posA);
                }
                if (!this.areNeighbouringBlocks(posA)) {
                    if (this.canPlaceOnBlock(posA.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posA.down()) && !this.canPlaceOnBlock(posA.down()) && this.canPlaceOnBlock(posA) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posA);
                    }
                }
                if ((BlockUtils.getBlockDamage(posA) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posA) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posA);
            }
        }
        if (blocks.size() == 2) {
            pos = blocks.get(0);
            pos2 = blocks.get(1);
            BlockPos[] surroundOffset = new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()};
            BlockPos[] surroundOffset2 = new BlockPos[]{pos2.north(), pos2.east(), pos2.south(), pos2.west()};
            if (this.canPlaceOnBlock(pos.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            if (this.canPlaceOnBlock(pos2.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos2.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            for (BlockPos posA : surroundOffset) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posA)) {
                    this.doClearBlocks(posA);
                }
                if (this.canPlaceOnBlock(posA.down()) && !this.intersectsWithEntity(posA.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posA.down());
                }
                if (this.canPlaceOnBlock(posA) && !this.intersectsWithEntity(posA)) {
                    this.placeBlock(posA, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posA);
                }
                if (!this.areNeighbouringBlocks(posA)) {
                    if (this.canPlaceOnBlock(posA.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posA.down()) && !this.canPlaceOnBlock(posA.down()) && this.canPlaceOnBlock(posA) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posA);
                    }
                }
                if ((BlockUtils.getBlockDamage(posA) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posA) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posA);
            }
            for (BlockPos posB : surroundOffset2) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posB)) {
                    this.doClearBlocks(posB);
                }
                if (this.canPlaceOnBlock(posB.down()) && !this.intersectsWithEntity(posB.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posB.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posB.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posB.down());
                }
                if (this.canPlaceOnBlock(posB) && !this.intersectsWithEntity(posB)) {
                    this.placeBlock(posB, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posB) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posB);
                }
                if (!this.areNeighbouringBlocks(posB)) {
                    if (this.canPlaceOnBlock(posB.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posB.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posB.down()) && !this.canPlaceOnBlock(posB.down()) && this.canPlaceOnBlock(posB) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posB);
                    }
                }
                if ((BlockUtils.getBlockDamage(posB) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posB) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posB);
            }
        }
        if (blocks.size() == 4) {
            pos = blocks.get(0);
            pos2 = blocks.get(1);
            BlockPos pos3 = blocks.get(2);
            BlockPos pos4 = blocks.get(3);
            BlockPos[] surroundOffset = new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()};
            BlockPos[] surroundOffset2 = new BlockPos[]{pos2.north(), pos2.east(), pos2.south(), pos2.west()};
            BlockPos[] surroundOffset3 = new BlockPos[]{pos3.north(), pos3.east(), pos3.south(), pos3.west()};
            BlockPos[] surroundOffset4 = new BlockPos[]{pos4.north(), pos4.east(), pos4.south(), pos4.west()};
            if (this.canPlaceOnBlock(pos.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            if (this.canPlaceOnBlock(pos2.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos2.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            if (this.canPlaceOnBlock(pos3.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos3.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            if (this.canPlaceOnBlock(pos4.down()) && this.floor.getValue().booleanValue()) {
                this.placeBlock(pos4.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
            }
            for (BlockPos posA : surroundOffset) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posA)) {
                    this.doClearBlocks(posA);
                }
                if (this.canPlaceOnBlock(posA.down()) && !this.intersectsWithEntity(posA.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posA.down());
                }
                if (this.canPlaceOnBlock(posA) && !this.intersectsWithEntity(posA)) {
                    this.placeBlock(posA, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posA) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posA);
                }
                if (!this.areNeighbouringBlocks(posA)) {
                    if (this.canPlaceOnBlock(posA.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posA.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posA.down()) && !this.canPlaceOnBlock(posA.down()) && this.canPlaceOnBlock(posA) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posA);
                    }
                }
                if ((BlockUtils.getBlockDamage(posA) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posA) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posA);
            }
            for (BlockPos posB : surroundOffset2) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posB)) {
                    this.doClearBlocks(posB);
                }
                if (this.canPlaceOnBlock(posB.down()) && !this.intersectsWithEntity(posB.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posB.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posB.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posB.down());
                }
                if (this.canPlaceOnBlock(posB) && !this.intersectsWithEntity(posB)) {
                    this.placeBlock(posB, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posB) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posB);
                }
                if (!this.areNeighbouringBlocks(posB)) {
                    if (this.canPlaceOnBlock(posB.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posB.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posB.down()) && !this.canPlaceOnBlock(posB.down()) && this.canPlaceOnBlock(posB) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posB);
                    }
                }
                if ((BlockUtils.getBlockDamage(posB) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posB) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posB);
            }
            for (BlockPos posC : surroundOffset3) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posC)) {
                    this.doClearBlocks(posC);
                }
                if (this.canPlaceOnBlock(posC.down()) && !this.intersectsWithEntity(posC.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posC.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posC.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posC.down());
                }
                if (this.canPlaceOnBlock(posC) && !this.intersectsWithEntity(posC)) {
                    this.placeBlock(posC, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posC) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posC);
                }
                if (!this.areNeighbouringBlocks(posC)) {
                    if (this.canPlaceOnBlock(posC.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posC.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posC.down()) && !this.canPlaceOnBlock(posC.down()) && this.canPlaceOnBlock(posC) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posC);
                    }
                }
                if ((BlockUtils.getBlockDamage(posC) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posC) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posC);
            }
            for (BlockPos posD : surroundOffset4) {
                if (this.blockClear.getValue().booleanValue() && this.areBlocksEasyToBreak(posD)) {
                    this.doClearBlocks(posD);
                }
                if (this.canPlaceOnBlock(posD.down()) && !this.intersectsWithEntity(posD.down()) && this.floor.getValue().booleanValue()) {
                    this.placeBlock(posD.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posD.down()) && this.dynamicEntityExtend.getValue().booleanValue() && this.floor.getValue().booleanValue()) {
                    this.doDynamicExtend(posD.down());
                }
                if (this.canPlaceOnBlock(posD) && !this.intersectsWithEntity(posD)) {
                    this.placeBlock(posD, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                } else if (this.intersectsWithEntity(posD) && this.dynamicEntityExtend.getValue().booleanValue()) {
                    this.doDynamicExtend(posD);
                }
                if (!this.areNeighbouringBlocks(posD)) {
                    if (this.canPlaceOnBlock(posD.down()) && this.floor.getValue().booleanValue()) {
                        this.placeBlock(posD.down(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    }
                    if (!this.areNeighbouringBlocks(posD.down()) && !this.canPlaceOnBlock(posD.down()) && this.canPlaceOnBlock(posD) || !this.floor.getValue().booleanValue()) {
                        this.doHelpingBlocks(posD);
                    }
                }
                if ((BlockUtils.getBlockDamage(posD) == 0.0f || this.antiCity.getValue() != AntiCity.Smart) && (!this.antiAnvil.getValue().booleanValue() || !(this.getBlock(posD) instanceof BlockAnvil))) continue;
                this.doBlockExtend(posD);
            }
        }
    }

    public void doClearBlocks(BlockPos pos) {
        EnumFacing side;
        if (this.blockClear.getValue().booleanValue() && Surround.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < (double)this.blockClearRange.getValue().intValue() && (side = BlockUtils.getFirstFacing(pos)) != null) {
            this.facing = side;
            Surround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            Surround.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }

    public boolean areBlocksEasyToBreak(BlockPos pos) {
        return this.getBlock(pos) == Blocks.REDSTONE_WIRE || this.getBlock(pos) == Blocks.REDSTONE_TORCH || this.getBlock(pos) == Blocks.UNLIT_REDSTONE_TORCH || this.getBlock(pos) == Blocks.TORCH || this.getBlock(pos) == Blocks.GRASS || this.getBlock(pos) == Blocks.TALLGRASS || this.getBlock(pos) == Blocks.DEADBUSH || this.getBlock(pos) == Blocks.TRIPWIRE || this.getBlock(pos) == Blocks.WHEAT;
    }

    public void doHelpingBlocks(BlockPos pos) {
        if (this.canPlaceOnBlock(pos.north()) && !this.intersectsWithSelf(pos.north())) {
            this.placeBlock(pos.north(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (this.canPlaceOnBlock(pos.east()) && !this.intersectsWithSelf(pos.east())) {
            this.placeBlock(pos.east(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (this.canPlaceOnBlock(pos.south()) && !this.intersectsWithSelf(pos.south())) {
            this.placeBlock(pos.south(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
        if (this.canPlaceOnBlock(pos.west()) && !this.intersectsWithSelf(pos.west())) {
            this.placeBlock(pos.west(), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
    }

    public void doBlockExtend(BlockPos pos) {
        BlockPos[] dynamicBlockOffset;
        for (BlockPos posA : dynamicBlockOffset = new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()}) {
            if (!this.canPlaceOnBlock(posA) || this.intersectsWithEntity(posA)) continue;
            this.placeBlock(posA, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
        }
    }

    public void doDynamicExtend(BlockPos pos) {
        if (this.intersectsWithEntity(pos) && !this.intersectsWithSelf(pos)) {
            BlockPos[] dynamicOffset;
            for (BlockPos posA : dynamicOffset = new BlockPos[]{pos.north(), pos.east(), pos.south(), pos.west()}) {
                BlockPos[] dynamicOffset2;
                if (this.canPlaceOnBlock(posA) && !this.intersectsWithEntity(posA)) {
                    this.placeBlock(posA, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                    continue;
                }
                if (!this.intersectsWithEntity(posA)) continue;
                for (BlockPos posB : dynamicOffset2 = new BlockPos[]{posA.north(), posA.east(), posA.south(), posA.west()}) {
                    BlockPos[] dynamicOffset3;
                    if (this.canPlaceOnBlock(posB) && !this.intersectsWithEntity(posB)) {
                        this.placeBlock(posB, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                        continue;
                    }
                    if (!this.intersectsWithEntity(posB)) continue;
                    for (BlockPos posC : dynamicOffset3 = new BlockPos[]{posB.north(), posB.east(), posB.south(), posB.west()}) {
                        BlockPos[] dynamicOffset4;
                        if (this.canPlaceOnBlock(posC) && !this.intersectsWithEntity(posC)) {
                            this.placeBlock(posC, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                            continue;
                        }
                        if (!this.intersectsWithEntity(posC)) continue;
                        for (BlockPos posD : dynamicOffset4 = new BlockPos[]{posC.north(), posC.east(), posC.south(), posC.west()}) {
                            BlockPos[] dynamicOffset5;
                            if (this.canPlaceOnBlock(posD) && !this.intersectsWithEntity(posD)) {
                                this.placeBlock(posD, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                                continue;
                            }
                            if (!this.intersectsWithEntity(posD)) continue;
                            for (BlockPos posE : dynamicOffset5 = new BlockPos[]{posD.north(), posD.east(), posD.south(), posD.west()}) {
                                if (!this.canPlaceOnBlock(posE) || this.intersectsWithEntity(posE)) continue;
                                this.placeBlock(posE, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), true);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean canPlaceOnBlock(BlockPos pos) {
        return this.getBlock(pos) instanceof BlockAir || this.getBlock(pos) instanceof BlockSnow || this.getBlock(pos) instanceof BlockLiquid || this.getBlock(pos) instanceof BlockVine || this.getBlock(pos) instanceof BlockSnow || this.getBlock(pos) instanceof BlockTallGrass || this.getBlock(pos) instanceof BlockFire || this.getBlock(pos) instanceof BlockDynamicLiquid || this.getBlock(pos) instanceof BlockStaticLiquid;
    }

    public Block getBlock(BlockPos pos) {
        return Surround.mc.world.getBlockState(pos).getBlock();
    }

    public boolean intersectsWithSelf(BlockPos pos) {
        for (Entity entity : Surround.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity != Surround.mc.player || !new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) continue;
            return true;
        }
        return false;
    }

    public boolean intersectsWithEntity(BlockPos pos) {
        for (Entity entity : Surround.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity == null || EntityUtil.isDead(entity) || !entity.preventEntitySpawning || entity instanceof EntityPlayer && !BlockUtils.isBlocking(pos, (EntityPlayer)entity)) continue;
            return true;
        }
        return false;
    }

    public boolean areNeighbouringBlocks(BlockPos pos) {
        return !(this.getBlock(pos.up()) instanceof BlockAir) || !(this.getBlock(pos.down()) instanceof BlockAir) || !(this.getBlock(pos.north()) instanceof BlockAir) || !(this.getBlock(pos.east()) instanceof BlockAir) || !(this.getBlock(pos.south()) instanceof BlockAir) || !(this.getBlock(pos.west()) instanceof BlockAir);
    }

    public void placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean packet, boolean sneaking) {
        int oldSlot = Surround.mc.player.inventory.currentItem;
        int obsidianSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int enderChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (this.placedBlocks < this.blocksPerPlace.getValue()) {
            if (this.sensitivity.getValue() == Sensitivity.High) {
                if (enderChestSlot != -1) {
                    Surround.mc.player.inventory.currentItem = enderChestSlot;
                }
                if (obsidianSlot != -1) {
                    Surround.mc.player.inventory.currentItem = obsidianSlot;
                }
            }
            if (this.sensitivity.getValue() == Sensitivity.Low) {
                if (enderChestSlot != -1) {
                    Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(enderChestSlot));
                }
                if (obsidianSlot != -1) {
                    Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(obsidianSlot));
                }
            }
            if (enderChestSlot != -1 || obsidianSlot != -1) {
                BlockUtils.placeBlock(blockPos, hand, rotate, packet, this.extraPacket.getValue(), sneaking);
            }
            if (this.sensitivity.getValue() == Sensitivity.High) {
                Surround.mc.player.inventory.currentItem = oldSlot;
            }
            if (this.sensitivity.getValue() == Sensitivity.Low) {
                Surround.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            }
            ++this.placedBlocks;
        }
    }
	
	public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            Surround.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            Surround.mc.playerController.attackEntity(Surround.mc.player, entity);
        }
        if (swingArm) {
            Surround.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
	
	public static ArrayList<BlockPos> getPos(double posX, double posY, double posZ, Entity entity) {
        ArrayList<BlockPos> block = new ArrayList<BlockPos>();
        if (entity != null) {
            AxisAlignedBB bb = entity.ridingEntity != null ? entity.ridingEntity.getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(posX, posY, posZ) : entity.getEntityBoundingBox().contract(0.01, 1.0, 0.01).offset(posX, posY, posZ);
            int y = (int)bb.minY;
            int x = (int)Math.floor(bb.minX);
            while ((double)x < Math.floor(bb.maxX) + 1.0) {
                int z = (int)Math.floor(bb.minZ);
                while ((double)z < Math.floor(bb.maxZ) + 1.0) {
                    block.add(new BlockPos(x, y, z));
                    ++z;
                }
                ++x;
            }
        }
        return block;
    }

    public static enum AntiCity {
        Smart,
        None;
    }

    public static enum CrystalClear {
        Toggle,
        Always;
    }

    public static enum Sensitivity {
        High,
        Low;
    }
	
	public static enum CenterType {
        None,
        Instant,
        Motion;
    }
}