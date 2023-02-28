package dev._3000IQPlay.trillium.modules.movement;

import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AntiVoid
        extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MotionStop));
    public Setting<Integer> distance = this.register(new Setting<Integer>("Distance", 10, 1, 256));
    public Setting<Integer> height = this.register(new Setting<Integer>("Height", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(10), v -> this.mode.getValue() == Mode.Packet));
	public Setting<Boolean> ss = this.register(new Setting<Boolean>("SilentSwich", true, v -> this.mode.getValue() == Mode.ObiClutch));
	public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false, v -> this.mode.getValue() == Mode.ObiClutch));
    public Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.Motion || this.mode.getValue() == Mode.Glide));

    public AntiVoid() {
        super("AntiVoid", "Prevents u from falling in the void", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onToggle() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
    }

    @Override
    public void onUpdate() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (AntiVoid.mc.player.noClip || AntiVoid.mc.player.posY > (double)this.distance.getValue().intValue() || AntiVoid.mc.player.isRiding()) {
            return;
        }
        RayTraceResult trace = AntiVoid.mc.world.rayTraceBlocks(AntiVoid.mc.player.getPositionVector(), new Vec3d(AntiVoid.mc.player.posX, 0.0, AntiVoid.mc.player.posZ), false, false, false);
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
            switch (this.mode.getValue()) {
				case ObiClutch: {
                    int slot = getObiSlot();
                    if (slot != -1) {
                        BlockUtils.placeBlock(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ), EnumHand.MAIN_HAND, this.ss.getValue(), this.rotate.getValue(), false);
                    }
					break;
                }
                case MotionStop: {
                    AntiVoid.mc.player.setVelocity(0.0, 0.0, 0.0);
                    AntiVoid.mc.player.motionY = 0.0;
                    break;
                }
                case Motion: {
                    AntiVoid.mc.player.motionY = this.speed.getValue().floatValue();
                    break;
                }
                case Glide: {
                    AntiVoid.mc.player.motionY *= (double)this.speed.getValue().floatValue();
                    break;
                }
                case Packet: {
                    mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(AntiVoid.mc.player.posX, (double)this.height.getValue().intValue(), AntiVoid.mc.player.posZ, AntiVoid.mc.player.onGround));
                }
            }
        }
    }
	
	public int getObiSlot() {
    int slot = -1;
    for (int i = 0; i <= 8; i++) {
        ItemStack stack = mc.player.inventory.getStackInSlot(i);
        if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        if (block instanceof BlockObsidian) {
            slot = i;
            break;
        }
    }
    return slot;
}

    enum Mode {
		ObiClutch,
        MotionStop,
        Motion,
        Glide,
        Packet
    }
}