package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.event.events.EventPreMotion;
import dev._3000IQPlay.trillium.util.BlockUtils;
import dev._3000IQPlay.trillium.util.RotationHelper;
import dev._3000IQPlay.trillium.util.SilentRotationUtil;
import dev._3000IQPlay.trillium.util.Timer;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class AutoFarm extends Module {
    private boolean isActive;
    private int oldSlot;
    public static boolean isEating;
    ArrayList<BlockPos> crops = new ArrayList();
    ArrayList<BlockPos> check = new ArrayList();
    Timer timerHelper = new Timer();
    Timer timerHelper2 = new Timer();
    private Setting<FarmModa> farmMode = this.register(new Setting<>("AutoFarm Mode", FarmModa.Harvest));
    public Setting<Float> delay = this.register(new Setting<>("Delay (Seconds)", 2.0f, 0.0f, 10.0f));
    public Setting<Float> radius = this.register(new Setting<>("Farm Radius", 4.0f, 1.0f, 7.0f));
	public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));
    public Setting<Boolean> autoEat = this.register(new Setting<>("Auto Eat", true));
    public Setting<Float> feed = this.register(new Setting<>("On Hunger", 15.0f, 1.0f, 20.0f, v -> this.autoEat.getValue()));
	
	public AutoFarm() {
        super("AutoFarm", "Farms shit for yo lazy ass", Module.Category.PLAYER, true, false, false);
    }

    public static boolean doesHaveSeeds() {
        for (int i = 0; i < 9; ++i) {
            mc.player.inventory.getStackInSlot(i);
            if (!(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemSeeds)) continue;
            return true;
        }
        return false;
    }

    public static int searchSeeds() {
        for (int i = 0; i < 45; ++i) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (!(itemStack.getItem() instanceof ItemSeeds)) continue;
            return i;
        }
        return -1;
    }

    public static int getSlotWithSeeds() {
        for (int i = 0; i < 9; ++i) {
            mc.player.inventory.getStackInSlot(i);
            if (!(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemSeeds)) continue;
            return i;
        }
        return 0;
    }

    @Override
    public void onEnable() {
        crops.clear();
        check.clear();
        super.onEnable();
    }

    private boolean isOnCrops() {
        for (double x = mc.player.boundingBox.minX; x < mc.player.boundingBox.maxX; x += (double) 0.01f) {
            for (double z = mc.player.boundingBox.minZ; z < mc.player.boundingBox.maxZ; z += (double) 0.01f) {
                Block block = mc.world.getBlockState(new BlockPos(x, mc.player.posY - 0.1, z)).getBlock();
                if (block instanceof BlockFarmland || block instanceof BlockSoulSand || block instanceof BlockSand || block instanceof BlockAir)
                    continue;
                return false;
            }
        }
        return true;
    }

    private boolean IsValidBlockPos(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFarmland || state.getBlock() instanceof BlockSand || state.getBlock() instanceof BlockSoulSand) {
            return mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (autoEat.getValue()) {
            if (isFood()) {
                if (isFood() && (float) mc.player.getFoodStats().getFoodLevel() <= feed.getValue()) {
                    isActive = true;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                } else if (isActive) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    isActive = false;
                }
            } else {
                if (isEating && !mc.player.isHandActive()) {
                    if (oldSlot != -1) {
                        mc.player.inventory.currentItem = oldSlot;
                        oldSlot = -1;
                    }
                    isEating = false;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    return;
                }
                if (isEating) {
                    return;
                }
                if (isValid(mc.player.getHeldItemOffhand(), mc.player.getFoodStats().getFoodLevel())) {
                    mc.player.setActiveHand(EnumHand.OFF_HAND);
                    isEating = true;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    mc.rightClickMouse();
                } else {
                    for (int i = 0; i < 9; ++i) {
                        if (!isValid(mc.player.inventory.getStackInSlot(i), mc.player.getFoodStats().getFoodLevel())) continue;
                        oldSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = i;
                        isEating = true;
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        mc.rightClickMouse();
                        return;
                    }
                }
            }
        }
    }

    private boolean itemCheck(Item item) {
        return item != Items.ROTTEN_FLESH && item != Items.SPIDER_EYE && item != Items.POISONOUS_POTATO && (item != Items.FISH || new ItemStack(Items.FISH).getItemDamage() != 3);
    }

    private boolean isValid(ItemStack stack, int food) {
        return stack.getItem() instanceof ItemFood && feed.getValue() - (float) food >= (float) ((ItemFood) stack.getItem()).getHealAmount(stack) && itemCheck(stack.getItem());
    }

    private boolean isFood() {
        return mc.player.getHeldItemOffhand().getItem() instanceof ItemFood;
    }

    @SubscribeEvent
    public void onPreMotion(EventPreMotion event) {
        BlockPos pos;
        if (mc.player == null && mc.world == null) {
            return;
        }
        if (farmMode.getValue() == FarmModa.Plant && !doesHaveSeeds() && searchSeeds() != -1) {
            mc.playerController.windowClick(0, searchSeeds(), 1, ClickType.QUICK_MOVE, mc.player);
        }
    }

    @SubscribeEvent
    public void onPre(EventPreMotion e) {
		if (farmMode.getValue() == FarmModa.Harvest) {
			ArrayList<BlockPos> blockPositions = getBlocks(radius.getValue(), radius.getValue(), radius.getValue());
			for (BlockPos pos : blockPositions) {
				BlockCrops crop;
				IBlockState state = BlockUtils.getState(pos);
				if (!isCheck(Block.getIdFromBlock(state.getBlock()))) continue;
				if (!isCheck(0)) {
					check.add(pos);
				}
				Block block = mc.world.getBlockState(pos).getBlock();
				BlockPos downPos = pos.down(1);
				if (!(block instanceof BlockCrops) || (crop = (BlockCrops) block).canGrow(mc.world, pos, state, true) || !timerHelper.passedMs((double)delay.getValue() * 100.0) || pos == null) continue;
				mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
				mc.player.swingArm(EnumHand.MAIN_HAND);
				if (doesHaveSeeds()) {
					oldSlot = mc.player.inventory.currentItem;
					mc.player.connection.sendPacket(new CPacketHeldItemChange(getSlotWithSeeds()));
					mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(downPos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
					mc.player.swingArm(EnumHand.MAIN_HAND);
					mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
				}
				timerHelper.reset();
			}
		} else if (farmMode.getValue() == FarmModa.Plant) {
			BlockPos pos = BlockUtils.getSphere(AutoFarm.getPlayerPosLocal(), radius.getValue(), 6, false, true, 0).stream().filter(this::IsValidBlockPos).min(Comparator.comparing(blockPos -> AutoFarm.getDistanceOfEntityToBlock(mc.player, blockPos))).orElse(null);
			Vec3d vec = new Vec3d(0.0, 0.0, 0.0);
			if (timerHelper.passedMs((double)delay.getValue() * 100.0) && isOnCrops() && pos != null && doesHaveSeeds()) {
				oldSlot = mc.player.inventory.currentItem;
				mc.player.connection.sendPacket(new CPacketHeldItemChange(getSlotWithSeeds()));
				mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.VALUES[0].getOpposite(), vec, EnumHand.MAIN_HAND);
				mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
				timerHelper.reset();
			}
		}
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive e) {
		if (e.getPacket() instanceof SPacketBlockChange) {
			SPacketBlockChange p = (SPacketBlockChange) e.getPacket();
			if (isEnabled(Block.getIdFromBlock(p.getBlockState().getBlock()))) {
				crops.add(p.getBlockPosition());
			}
		} else if (e.getPacket() instanceof SPacketMultiBlockChange) {
			SPacketMultiBlockChange p = (SPacketMultiBlockChange) e.getPacket();
			for (SPacketMultiBlockChange.BlockUpdateData dat : p.getChangedBlocks()) {
				if (!isEnabled(Block.getIdFromBlock(dat.getBlockState().getBlock()))) continue;
				crops.add(dat.getPos());
			}
		}
    }
	
	public static double getDistanceOfEntityToBlock(Entity entity, BlockPos pos) {
        return getDistance(entity.posX, entity.posY, entity.posZ, pos.getX(), pos.getY(), pos.getZ());
    }
	
	public static double getDistance(double x, double y, double z, double x1, double y1, double z1) {
        double posX = x - x1;
        double posY = y - y1;
        double posZ = z - z1;
        return MathHelper.sqrt(posX * posX + posY * posY + posZ * posZ);
    }
	
	public static BlockPos getPlayerPosLocal() {
        if (mc.player == null) {
            return BlockPos.ORIGIN;
        }
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private boolean isCheck(int id) {
        int check = 0;
        if (id != 0) {
            check = 59;
        }
        if (id == 0) {
            return false;
        }
        return id == check;
    }

    private boolean isEnabled(int id) {
        int check = 0;
        if (id != 0) {
            check = 59;
        }
        if (id == 0) {
            return false;
        }
        return id == check;
    }

    private ArrayList<BlockPos> getBlocks(float x, float y, float z) {
        BlockPos min = new BlockPos(mc.player.posX - (double) x, mc.player.posY - (double) y, mc.player.posZ - (double) z);
        BlockPos max = new BlockPos(mc.player.posX + (double) x, mc.player.posY + (double) y, mc.player.posZ + (double) z);
        return BlockUtils.getAllInBox(min, max);
    }
	
	public static enum FarmModa {
		Harvest,
		Plant;
	}
}